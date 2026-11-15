import * as path from 'path';
import {
  Duration,
  RemovalPolicy,
  Stack,
  StackProps,
  CfnOutput,
  Tags,
} from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as apigwv2 from 'aws-cdk-lib/aws-apigatewayv2';
import * as apigwv2Authorizers from 'aws-cdk-lib/aws-apigatewayv2-authorizers';
import * as apigwv2Integrations from 'aws-cdk-lib/aws-apigatewayv2-integrations';
import * as cloudwatch from 'aws-cdk-lib/aws-cloudwatch';
import * as cwActions from 'aws-cdk-lib/aws-cloudwatch-actions';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecr from 'aws-cdk-lib/aws-ecr';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as kms from 'aws-cdk-lib/aws-kms';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as sfn from 'aws-cdk-lib/aws-stepfunctions';
import * as tasks from 'aws-cdk-lib/aws-stepfunctions-tasks';
import * as sns from 'aws-cdk-lib/aws-sns';
import * as sqs from 'aws-cdk-lib/aws-sqs';
import * as wafv2 from 'aws-cdk-lib/aws-wafv2';

export interface SaasPlatformStackProps extends StackProps {
  readonly environmentName: 'staging' | 'production';
  readonly imageTag?: string;
}

export class SaasPlatformStack extends Stack {
  public readonly apiUrl: CfnOutput;
  public readonly albDnsName: CfnOutput;

  constructor(scope: Construct, id: string, props: SaasPlatformStackProps) {
    super(scope, id, props);

    const isProd = props.environmentName === 'production';
    const removalPolicy = isProd ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY;

    Tags.of(this).add('Platform', 'CloudPulse');
    Tags.of(this).add('Environment', props.environmentName);
    Tags.of(this).add('ManagedBy', 'AWS-CDK');

    // =========================================================================
    // 1. KMS Customer-Managed Key (CMK) with Automatic Annual Rotation
    // =========================================================================
    const dataKey = new kms.Key(this, 'PlatformDataKey', {
      alias: `alias/cloudpulse-${props.environmentName}-data`,
      description: 'CMK for CloudPulse data at rest (RDS, DynamoDB, SQS, Logs, SNS)',
      enableKeyRotation: true,
      removalPolicy,
    });

    // Allow CloudWatch Logs in this region to encrypt log groups with the CMK
    dataKey.addToResourcePolicy(
      new iam.PolicyStatement({
        sid: 'AllowCloudWatchLogsEncryption',
        principals: [new iam.ServicePrincipal(`logs.${this.region}.amazonaws.com`)],
        actions: [
          'kms:Encrypt*',
          'kms:Decrypt*',
          'kms:ReEncrypt*',
          'kms:GenerateDataKey*',
          'kms:Describe*',
        ],
        resources: ['*'],
        conditions: {
          ArnLike: {
            'kms:EncryptionContext:aws:logs:arn': `arn:aws:logs:${this.region}:${this.account}:*`,
          },
        },
      }),
    );

    // =========================================================================
    // 2. Multi-AZ VPC + Gateway Endpoints + PrivateLink Interface Endpoints
    // =========================================================================
    const vpc = new ec2.Vpc(this, 'CloudPulseVpc', {
      vpcName: `cloudpulse-${props.environmentName}-vpc`,
      ipAddresses: ec2.IpAddresses.cidr('10.20.0.0/16'),
      maxAzs: 2,
      natGateways: isProd ? 2 : 1,
      subnetConfiguration: [
        {
          name: 'Public',
          subnetType: ec2.SubnetType.PUBLIC,
          cidrMask: 24,
        },
        {
          name: 'AppPrivate',
          subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS,
          cidrMask: 22,
        },
        {
          name: 'DataIsolated',
          subnetType: ec2.SubnetType.PRIVATE_ISOLATED,
          cidrMask: 22,
        },
      ],
    });

    const vpcFlowLogGroup = new logs.LogGroup(this, 'VpcFlowLogGroup', {
      logGroupName: `/aws/vpc/cloudpulse-${props.environmentName}-flowlogs`,
      retention: logs.RetentionDays.ONE_MONTH,
      encryptionKey: dataKey,
      removalPolicy,
    });

    vpc.addFlowLog('VpcFlowLog', {
      destination: ec2.FlowLogDestination.toCloudWatchLogs(vpcFlowLogGroup),
      trafficType: ec2.FlowLogTrafficType.REJECT,
    });

    // Gateway Endpoints ($0/hr, $0/GB) keep S3 & DynamoDB traffic off NAT Gateways
    vpc.addGatewayEndpoint('S3GatewayEndpoint', {
      service: ec2.GatewayVpcEndpointAwsService.S3,
      subnets: [
        { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
        { subnetType: ec2.SubnetType.PRIVATE_ISOLATED },
      ],
    });

    vpc.addGatewayEndpoint('DynamoDbGatewayEndpoint', {
      service: ec2.GatewayVpcEndpointAwsService.DYNAMODB,
      subnets: [
        { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
        { subnetType: ec2.SubnetType.PRIVATE_ISOLATED },
      ],
    });

    // PrivateLink Interface Endpoints for AWS control-plane & container pulls
    const endpointSg = new ec2.SecurityGroup(this, 'VpcEndpointSg', {
      vpc,
      description: 'Allow HTTPS from VPC workloads to PrivateLink Interface Endpoints',
      allowAllOutbound: false,
    });
    endpointSg.addIngressRule(
      ec2.Peer.ipv4(vpc.vpcCidrBlock),
      ec2.Port.tcp(443),
      'HTTPS from VPC private subnets',
    );

    const interfaceEndpoints: Array<[string, ec2.InterfaceVpcEndpointAwsService]> = [
      ['EcrApiEndpoint', ec2.InterfaceVpcEndpointAwsService.ECR],
      ['EcrDkrEndpoint', ec2.InterfaceVpcEndpointAwsService.ECR_DOCKER],
      ['LogsEndpoint', ec2.InterfaceVpcEndpointAwsService.CLOUDWATCH_LOGS],
      ['SecretsEndpoint', ec2.InterfaceVpcEndpointAwsService.SECRETS_MANAGER],
      ['StsEndpoint', ec2.InterfaceVpcEndpointAwsService.STS],
    ];

    for (const [endpointId, service] of interfaceEndpoints) {
      vpc.addInterfaceEndpoint(endpointId, {
        service,
        subnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
        securityGroups: [endpointSg],
        privateDnsEnabled: true,
      });
    }

    // =========================================================================
    // 3. Identity: Cognito User Pool (OIDC / JWT with Tenant Isolation Claim)
    // =========================================================================
    const userPool = new cognito.UserPool(this, 'TenantUserPool', {
      userPoolName: `cloudpulse-${props.environmentName}-users`,
      selfSignUpEnabled: false,
      signInAliases: { email: true },
      autoVerify: { email: true },
      mfa: cognito.Mfa.OPTIONAL,
      mfaSecondFactor: { sms: false, otp: true },
      passwordPolicy: {
        minLength: 12,
        requireLowercase: true,
        requireUppercase: true,
        requireDigits: true,
        requireSymbols: true,
      },
      customAttributes: {
        tenant_id: new cognito.StringAttribute({ mutable: false }),
        role: new cognito.StringAttribute({ mutable: true }),
      },
      accountRecovery: cognito.AccountRecovery.EMAIL_ONLY,
      removalPolicy,
    });

    const userPoolClient = userPool.addClient('WebSpaClient', {
      userPoolClientName: 'cloudpulse-web-oidc',
      generateSecret: false,
      oAuth: {
        flows: { authorizationCodeGrant: true },
        scopes: [cognito.OAuthScope.OPENID, cognito.OAuthScope.EMAIL, cognito.OAuthScope.PROFILE],
        callbackUrls: ['https://app.cloudpulse.example.com/callback'],
        logoutUrls: ['https://app.cloudpulse.example.com/logout'],
      },
      accessTokenValidity: Duration.minutes(30),
      idTokenValidity: Duration.minutes(30),
      refreshTokenValidity: Duration.days(14),
    });

    // =========================================================================
    // 4. Data Tier: DynamoDB (Hot Telemetry) + Aurora Serverless v2 & RDS Proxy
    // =========================================================================
    const telemetryTable = new dynamodb.Table(this, 'TelemetryTable', {
      tableName: `cloudpulse-${props.environmentName}-telemetry`,
      partitionKey: { name: 'PK', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'SK', type: dynamodb.AttributeType.STRING },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
      encryption: dynamodb.TableEncryption.CUSTOMER_MANAGED,
      encryptionKey: dataKey,
      pointInTimeRecoverySpecification: { pointInTimeRecoveryEnabled: true },
      stream: dynamodb.StreamViewType.NEW_AND_OLD_IMAGES,
      timeToLiveAttribute: 'ttl',
      removalPolicy,
    });

    telemetryTable.addGlobalSecondaryIndex({
      indexName: 'GSI1',
      partitionKey: { name: 'GSI1PK', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'GSI1SK', type: dynamodb.AttributeType.STRING },
      projectionType: dynamodb.ProjectionType.ALL,
    });

    const dbSecurityGroup = new ec2.SecurityGroup(this, 'AuroraSg', {
      vpc,
      description: 'Aurora PostgreSQL Serverless v2 isolated security group',
      allowAllOutbound: false,
    });

    const proxySecurityGroup = new ec2.SecurityGroup(this, 'RdsProxySg', {
      vpc,
      description: 'RDS Proxy security group',
      allowAllOutbound: true,
    });

    dbSecurityGroup.addIngressRule(
      proxySecurityGroup,
      ec2.Port.tcp(5432),
      'PostgreSQL from RDS Proxy only',
    );

    const auroraCluster = new rds.DatabaseCluster(this, 'LedgerCluster', {
      clusterIdentifier: `cloudpulse-${props.environmentName}-ledger`,
      engine: rds.DatabaseClusterEngine.auroraPostgres({
        version: rds.AuroraPostgresEngineVersion.of('16.4', '16'),
      }),
      credentials: rds.Credentials.fromGeneratedSecret('cloudpulse_admin', {
        secretName: `cloudpulse/${props.environmentName}/aurora-admin`,
        encryptionKey: dataKey,
      }),
      defaultDatabaseName: 'cloudpulse',
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_ISOLATED },
      securityGroups: [dbSecurityGroup],
      serverlessV2MinCapacity: 0.5,
      serverlessV2MaxCapacity: isProd ? 8 : 2,
      writer: rds.ClusterInstance.serverlessV2('writer'),
      readers: [
        rds.ClusterInstance.serverlessV2('reader', {
          scaleWithWriter: true,
        }),
      ],
      storageEncrypted: true,
      storageEncryptionKey: dataKey,
      iamAuthentication: true,
      backup: {
        retention: isProd ? Duration.days(14) : Duration.days(3),
      },
      deletionProtection: isProd,
      removalPolicy,
    });

    const rdsProxy = auroraCluster.addProxy('LedgerProxy', {
      secrets: [auroraCluster.secret!],
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_ISOLATED },
      securityGroups: [proxySecurityGroup],
      iamAuth: true,
      requireTls: true,
      idleClientTimeout: Duration.minutes(30),
      maxConnectionsPercent: 90,
    });

    // =========================================================================
    // 5. Async Messaging & Saga State Machine (SQS + DLQ + EventBridge + SFN)
    // =========================================================================
    const ingestionDlq = new sqs.Queue(this, 'IngestionDlq', {
      queueName: `cloudpulse-${props.environmentName}-ingestion-dlq`,
      encryption: sqs.QueueEncryption.KMS,
      encryptionMasterKey: dataKey,
      enforceSSL: true,
      retentionPeriod: Duration.days(14),
    });

    const ingestionQueue = new sqs.Queue(this, 'IngestionQueue', {
      queueName: `cloudpulse-${props.environmentName}-ingestion`,
      encryption: sqs.QueueEncryption.KMS,
      encryptionMasterKey: dataKey,
      enforceSSL: true,
      visibilityTimeout: Duration.seconds(60),
      deadLetterQueue: {
        queue: ingestionDlq,
        maxReceiveCount: 3,
      },
    });

    const domainEventBus = new events.EventBus(this, 'DomainEventBus', {
      eventBusName: `cloudpulse-${props.environmentName}-events`,
    });

    const sagaHandlerFn = new lambda.Function(this, 'SagaStepHandler', {
      functionName: `cloudpulse-${props.environmentName}-saga-handler`,
      runtime: lambda.Runtime.NODEJS_20_X,
      architecture: lambda.Architecture.ARM_64,
      handler: 'index.handler',
      code: lambda.Code.fromAsset(path.join(__dirname, '../../../services/saga')),
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      timeout: Duration.seconds(15),
      memorySize: 256,
      environment: {
        TABLE_NAME: telemetryTable.tableName,
        RDS_PROXY_ENDPOINT: rdsProxy.endpoint,
      },
    });

    telemetryTable.grantReadWriteData(sagaHandlerFn);
    dataKey.grantEncryptDecrypt(sagaHandlerFn);
    rdsProxy.grantConnect(sagaHandlerFn, 'cloudpulse_app');
    proxySecurityGroup.addIngressRule(
      sagaHandlerFn.connections.securityGroups[0],
      ec2.Port.tcp(5432),
      'PostgreSQL via RDS Proxy from Saga Lambda',
    );

    const sagaFailed = new sfn.Fail(this, 'SagaFailed', {
      cause: 'Tenant onboarding saga failed and executed compensating actions',
      error: 'TenantOnboardingSagaError',
    });

    const sagaSucceeded = new sfn.Succeed(this, 'SagaSucceeded');

    const releaseQuota = new tasks.LambdaInvoke(this, 'ReleaseTenantQuota', {
      lambdaFunction: sagaHandlerFn,
      payload: sfn.TaskInput.fromObject({
        action: 'RELEASE_QUOTA',
        'tenantId.$': '$.detail.tenantId',
        'requestId.$': '$.detail.requestId',
      }),
      resultPath: '$.compensation.quota',
    }).next(sagaFailed);

    const refundBilling = new tasks.LambdaInvoke(this, 'RefundBillingAuthorization', {
      lambdaFunction: sagaHandlerFn,
      payload: sfn.TaskInput.fromObject({
        action: 'REFUND_BILLING',
        'tenantId.$': '$.detail.tenantId',
        'requestId.$': '$.detail.requestId',
        'billingAuthId.$': '$.billingResult.Payload.billingAuthId',
      }),
      resultPath: '$.compensation.billing',
    }).next(releaseQuota);

    const publishProvisioned = new tasks.EventBridgePutEvents(this, 'PublishProvisionedEvent', {
      entries: [
        {
          eventBus: domainEventBus,
          source: 'cloudpulse.onboarding',
          detailType: 'TenantProvisioned',
          detail: sfn.TaskInput.fromObject({
            'tenantId.$': '$.detail.tenantId',
            'requestId.$': '$.detail.requestId',
            status: 'ACTIVE',
          }),
        },
      ],
    }).next(sagaSucceeded);

    const provisionSchema = new tasks.LambdaInvoke(this, 'ProvisionTenantSchema', {
      lambdaFunction: sagaHandlerFn,
      payload: sfn.TaskInput.fromObject({
        action: 'PROVISION_SCHEMA',
        'tenantId.$': '$.detail.tenantId',
        'requestId.$': '$.detail.requestId',
        'simulateDbFailure.$': '$.detail.simulateDbFailure',
      }),
      resultPath: '$.schemaResult',
    })
      .addCatch(refundBilling, { resultPath: '$.error' })
      .next(publishProvisioned);

    const authorizeBilling = new tasks.LambdaInvoke(this, 'AuthorizeBilling', {
      lambdaFunction: sagaHandlerFn,
      payload: sfn.TaskInput.fromObject({
        action: 'AUTHORIZE_BILLING',
        'tenantId.$': '$.detail.tenantId',
        'planTier.$': '$.detail.planTier',
        'requestId.$': '$.detail.requestId',
        'simulateBillingFailure.$': '$.detail.simulateBillingFailure',
      }),
      resultPath: '$.billingResult',
    })
      .addCatch(releaseQuota, { resultPath: '$.error' })
      .next(provisionSchema);

    const reserveQuota = new tasks.LambdaInvoke(this, 'ReserveTenantQuota', {
      lambdaFunction: sagaHandlerFn,
      payload: sfn.TaskInput.fromObject({
        action: 'RESERVE_QUOTA',
        'tenantId.$': '$.detail.tenantId',
        'planTier.$': '$.detail.planTier',
        'requestId.$': '$.detail.requestId',
      }),
      resultPath: '$.quotaResult',
    }).addCatch(sagaFailed, { resultPath: '$.error' });

    const sagaLogGroup = new logs.LogGroup(this, 'SagaLogGroup', {
      logGroupName: `/aws/vendedlogs/states/cloudpulse-${props.environmentName}-saga`,
      retention: logs.RetentionDays.ONE_MONTH,
      encryptionKey: dataKey,
      removalPolicy,
    });

    const onboardingSaga = new sfn.StateMachine(this, 'TenantOnboardingSaga', {
      stateMachineName: `cloudpulse-${props.environmentName}-onboarding-saga`,
      stateMachineType: sfn.StateMachineType.STANDARD,
      definitionBody: sfn.DefinitionBody.fromChainable(reserveQuota.next(authorizeBilling)),
      tracingEnabled: true,
      logs: {
        destination: sagaLogGroup,
        level: sfn.LogLevel.ERROR,
      },
    });

    const sagaTargetDlq = new sqs.Queue(this, 'SagaEventDlq', {
      queueName: `cloudpulse-${props.environmentName}-saga-target-dlq`,
      encryption: sqs.QueueEncryption.KMS,
      encryptionMasterKey: dataKey,
      enforceSSL: true,
    });

    new events.Rule(this, 'TenantOnboardingRule', {
      eventBus: domainEventBus,
      eventPattern: {
        source: ['cloudpulse.controlplane'],
        detailType: ['TenantProvisioningRequested'],
      },
      targets: [
        new targets.SfnStateMachine(onboardingSaga, {
          deadLetterQueue: sagaTargetDlq,
          retryAttempts: 3,
        }),
      ],
    });

    // =========================================================================
    // 6. Compute: ECS Fargate (Graviton ARM64) + Execution/Task Role Split
    // =========================================================================
    const ecrRepository = new ecr.Repository(this, 'ApiRepository', {
      repositoryName: `cloudpulse-${props.environmentName}-api`,
      imageScanOnPush: true,
      encryption: ecr.RepositoryEncryption.KMS,
      encryptionKey: dataKey,
      lifecycleRules: [{ maxImageCount: 20 }],
      removalPolicy,
    });

    const ecsCluster = new ecs.Cluster(this, 'CloudPulseCluster', {
      clusterName: `cloudpulse-${props.environmentName}-cluster`,
      vpc,
      containerInsights: true,
    });

    const ecsLogGroup = new logs.LogGroup(this, 'ApiLogGroup', {
      logGroupName: `/aws/ecs/cloudpulse-${props.environmentName}-api`,
      retention: logs.RetentionDays.ONE_MONTH,
      encryptionKey: dataKey,
      removalPolicy,
    });

    // Execution Role: used ONLY by the ECS agent to pull images, fetch secrets, write logs
    const executionRole = new iam.Role(this, 'EcsExecutionRole', {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      description: 'ECS Agent execution role for pulling ECR images and reading startup secrets',
    });
    ecrRepository.grantPull(executionRole);
    ecsLogGroup.grantWrite(executionRole);
    auroraCluster.secret!.grantRead(executionRole);
    dataKey.grantDecrypt(executionRole);

    // Task Role: used ONLY by application code at runtime (least-privilege)
    const taskRole = new iam.Role(this, 'EcsTaskRole', {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      description: 'Runtime IAM Task Role for CloudPulse API containers',
    });
    telemetryTable.grantReadWriteData(taskRole);
    ingestionQueue.grantSendMessages(taskRole);
    domainEventBus.grantPutEventsTo(taskRole);
    rdsProxy.grantConnect(taskRole, 'cloudpulse_app');
    dataKey.grantEncryptDecrypt(taskRole);

    const taskDefinition = new ecs.FargateTaskDefinition(this, 'ApiTaskDef', {
      cpu: 512,
      memoryLimitMiB: 1024,
      runtimePlatform: {
        cpuArchitecture: ecs.CpuArchitecture.ARM64,
        operatingSystemFamily: ecs.OperatingSystemFamily.LINUX,
      },
      executionRole,
      taskRole,
    });

    const apiContainer = taskDefinition.addContainer('ApiContainer', {
      containerName: 'cloudpulse-api',
      image: ecs.ContainerImage.fromEcrRepository(ecrRepository, props.imageTag ?? 'latest'),
      logging: ecs.LogDrivers.awsLogs({
        streamPrefix: 'api',
        logGroup: ecsLogGroup,
      }),
      portMappings: [{ containerPort: 8080, protocol: ecs.Protocol.TCP }],
      environment: {
        APP_ENV: props.environmentName,
        TELEMETRY_TABLE: telemetryTable.tableName,
        INGESTION_QUEUE_URL: ingestionQueue.queueUrl,
        EVENT_BUS_NAME: domainEventBus.eventBusName,
        RDS_PROXY_ENDPOINT: rdsProxy.endpoint,
        COGNITO_USER_POOL_ID: userPool.userPoolId,
      },
      healthCheck: {
        command: ['CMD-SHELL', 'curl -f http://localhost:8080/health || exit 1'],
        interval: Duration.seconds(15),
        timeout: Duration.seconds(5),
        retries: 3,
        startPeriod: Duration.seconds(20),
      },
    });

    const ecsServiceSg = new ec2.SecurityGroup(this, 'EcsServiceSg', {
      vpc,
      description: 'Allow traffic only from ALB to ECS Fargate tasks',
      allowAllOutbound: true,
    });

    proxySecurityGroup.addIngressRule(
      ecsServiceSg,
      ec2.Port.tcp(5432),
      'PostgreSQL via RDS Proxy from ECS Fargate',
    );

    const fargateService = new ecs.FargateService(this, 'ApiFargateService', {
      serviceName: `cloudpulse-${props.environmentName}-api`,
      cluster: ecsCluster,
      taskDefinition,
      desiredCount: isProd ? 2 : 1,
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
      assignPublicIp: false,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      securityGroups: [ecsServiceSg],
      circuitBreaker: { enable: true, rollback: true },
      healthCheckGracePeriod: Duration.seconds(30),
    });

    // =========================================================================
    // 7. Edge & Ingress: ALB + WAFv2 + API Gateway HTTP API (Cognito JWT)
    // =========================================================================
    const albSg = new ec2.SecurityGroup(this, 'AlbSg', {
      vpc,
      description: 'ALB security group allowing HTTP/HTTPS ingress',
      allowAllOutbound: true,
    });
    albSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80), 'HTTP ingress');

    const alb = new elbv2.ApplicationLoadBalancer(this, 'PlatformAlb', {
      loadBalancerName: `cloudpulse-${props.environmentName}-alb`,
      vpc,
      internetFacing: true,
      vpcSubnets: { subnetType: ec2.SubnetType.PUBLIC },
      securityGroups: [albSg],
      dropInvalidHeaderFields: true,
    });

    ecsServiceSg.addIngressRule(
      albSg,
      ec2.Port.tcp(apiContainer.containerPort),
      'App port 8080 from ALB',
    );

    const httpListener = alb.addListener('HttpListener', {
      port: 80,
      protocol: elbv2.ApplicationProtocol.HTTP,
    });

    const apiTargetGroup = httpListener.addTargets('EcsApiTargets', {
      port: 8080,
      protocol: elbv2.ApplicationProtocol.HTTP,
      targets: [fargateService],
      deregistrationDelay: Duration.seconds(30),
      healthCheck: {
        path: '/health',
        healthyHttpCodes: '200',
        interval: Duration.seconds(15),
        timeout: Duration.seconds(5),
        healthyThresholdCount: 2,
        unhealthyThresholdCount: 3,
      },
    });

    // WAFv2 Regional WebACL attached directly to ALB
    const webAcl = new wafv2.CfnWebACL(this, 'PlatformWebAcl', {
      name: `cloudpulse-${props.environmentName}-waf`,
      scope: 'REGIONAL',
      defaultAction: { allow: {} },
      visibilityConfig: {
        cloudWatchMetricsEnabled: true,
        metricName: `CloudPulseWaf-${props.environmentName}`,
        sampledRequestsEnabled: true,
      },
      rules: [
        {
          name: 'AWSManagedRulesCommonRuleSet',
          priority: 1,
          overrideAction: { none: {} },
          statement: {
            managedRuleGroupStatement: {
              vendorName: 'AWS',
              name: 'AWSManagedRulesCommonRuleSet',
            },
          },
          visibilityConfig: {
            cloudWatchMetricsEnabled: true,
            metricName: 'CommonRuleSet',
            sampledRequestsEnabled: true,
          },
        },
        {
          name: 'AWSManagedRulesSQLiRuleSet',
          priority: 2,
          overrideAction: { none: {} },
          statement: {
            managedRuleGroupStatement: {
              vendorName: 'AWS',
              name: 'AWSManagedRulesSQLiRuleSet',
            },
          },
          visibilityConfig: {
            cloudWatchMetricsEnabled: true,
            metricName: 'SQLiRuleSet',
            sampledRequestsEnabled: true,
          },
        },
        {
          name: 'RateLimitPerIp',
          priority: 3,
          action: { block: {} },
          statement: {
            rateBasedStatement: {
              limit: 2000,
              aggregateKeyType: 'IP',
            },
          },
          visibilityConfig: {
            cloudWatchMetricsEnabled: true,
            metricName: 'RateLimitPerIp',
            sampledRequestsEnabled: true,
          },
        },
      ],
    });

    new wafv2.CfnWebACLAssociation(this, 'AlbWebAclAssociation', {
      resourceArn: alb.loadBalancerArn,
      webAclArn: webAcl.attrArn,
    });

    // API Gateway HTTP API with Cognito JWT Authorizer -> VPC Link -> ALB
    const vpcLink = new apigwv2.VpcLink(this, 'ApiVpcLink', {
      vpc,
      subnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
    });

    const jwtIssuer = `https://cognito-idp.${this.region}.amazonaws.com/${userPool.userPoolId}`;
    const jwtAuthorizer = new apigwv2Authorizers.HttpJwtAuthorizer(
      'CognitoJwtAuthorizer',
      jwtIssuer,
      {
        jwtAudience: [userPoolClient.userPoolClientId],
      },
    );

    const httpApi = new apigwv2.HttpApi(this, 'CloudPulseHttpApi', {
      apiName: `cloudpulse-${props.environmentName}-api`,
      defaultAuthorizer: jwtAuthorizer,
    });

    httpApi.addRoutes({
      path: '/v1/{proxy+}',
      methods: [apigwv2.HttpMethod.ANY],
      integration: new apigwv2Integrations.HttpAlbIntegration('AlbProxyIntegration', httpListener, {
        vpcLink,
      }),
    });

    // =========================================================================
    // 8. ECS Auto Scaling (CPU + RequestCountPerTarget)
    // =========================================================================
    const scalableTarget = fargateService.autoScaleTaskCount({
      minCapacity: isProd ? 2 : 1,
      maxCapacity: isProd ? 10 : 4,
    });

    scalableTarget.scaleOnCpuUtilization('CpuTargetScaling', {
      targetUtilizationPercent: 60,
      scaleInCooldown: Duration.seconds(120),
      scaleOutCooldown: Duration.seconds(60),
    });

    scalableTarget.scaleOnRequestCount('AlbRequestScaling', {
      requestsPerTarget: 1000,
      targetGroup: apiTargetGroup,
      scaleInCooldown: Duration.seconds(120),
      scaleOutCooldown: Duration.seconds(60),
    });

    // =========================================================================
    // 9. Observability: CloudWatch SLO Alarms & Unified Operations Dashboard
    // =========================================================================
    const sloAlertTopic = new sns.Topic(this, 'SloAlertTopic', {
      topicName: `cloudpulse-${props.environmentName}-slo-alerts`,
      masterKey: dataKey,
    });

    const alb5xxErrorRate = new cloudwatch.MathExpression({
      expression: 'IF(reqs > 0, (err5xx / reqs) * 100, 0)',
      usingMetrics: {
        err5xx: alb.metrics.httpCodeTarget(elbv2.HttpCodeTarget.TARGET_5XX_COUNT, {
          period: Duration.minutes(1),
          statistic: 'Sum',
        }),
        reqs: alb.metrics.requestCount({
          period: Duration.minutes(1),
          statistic: 'Sum',
        }),
      },
      label: 'Target 5xx Error Rate (%)',
      period: Duration.minutes(1),
    });

    const sloErrorRateAlarm = new cloudwatch.Alarm(this, 'Slo5xxRateAlarm', {
      alarmName: `cloudpulse-${props.environmentName}-slo-5xx-rate`,
      alarmDescription: 'Availability SLO breach: Target 5xx rate > 1% for 3 consecutive minutes',
      metric: alb5xxErrorRate,
      threshold: 1,
      evaluationPeriods: 3,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    const sloLatencyAlarm = new cloudwatch.Alarm(this, 'SloP99LatencyAlarm', {
      alarmName: `cloudpulse-${props.environmentName}-slo-p99-latency`,
      alarmDescription: 'Latency SLO breach: ALB p99 target response time > 500ms',
      metric: alb.metrics.targetResponseTime({
        period: Duration.minutes(1),
        statistic: 'p99',
      }),
      threshold: 0.5,
      evaluationPeriods: 3,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    const dlqDepthAlarm = new cloudwatch.Alarm(this, 'DlqMessagesVisibleAlarm', {
      alarmName: `cloudpulse-${props.environmentName}-dlq-nonempty`,
      alarmDescription: 'Poison-pill or downstream failure: messages landed in Ingestion DLQ',
      metric: ingestionDlq.metricApproximateNumberOfMessagesVisible({
        period: Duration.minutes(1),
        statistic: 'Maximum',
      }),
      threshold: 1,
      evaluationPeriods: 1,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD,
    });

    const sagaFailedAlarm = new cloudwatch.Alarm(this, 'SagaFailedAlarm', {
      alarmName: `cloudpulse-${props.environmentName}-saga-failed`,
      alarmDescription: 'Tenant onboarding Step Functions Saga failed',
      metric: onboardingSaga.metricFailed({
        period: Duration.minutes(5),
        statistic: 'Sum',
      }),
      threshold: 1,
      evaluationPeriods: 1,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD,
    });

    const auroraAcuAlarm = new cloudwatch.Alarm(this, 'AuroraHighAcuAlarm', {
      alarmName: `cloudpulse-${props.environmentName}-aurora-acu-high`,
      alarmDescription: 'Aurora Serverless v2 ACU utilization > 85%',
      metric: auroraCluster.metric('ACUUtilization', {
        period: Duration.minutes(5),
        statistic: 'Average',
      }),
      threshold: 85,
      evaluationPeriods: 2,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
    });

    for (const alarm of [
      sloErrorRateAlarm,
      sloLatencyAlarm,
      dlqDepthAlarm,
      sagaFailedAlarm,
      auroraAcuAlarm,
    ]) {
      alarm.addAlarmAction(new cwActions.SnsAction(sloAlertTopic));
    }

    const dashboard = new cloudwatch.Dashboard(this, 'SloDashboard', {
      dashboardName: `CloudPulse-${props.environmentName}-SLO`,
    });

    dashboard.addWidgets(
      new cloudwatch.GraphWidget({
        title: 'API Request Volume & 5xx Error Rate (%)',
        left: [alb.metrics.requestCount({ period: Duration.minutes(1), statistic: 'Sum' })],
        right: [alb5xxErrorRate],
        width: 12,
      }),
      new cloudwatch.GraphWidget({
        title: 'ALB Target Latency (p50 / p95 / p99)',
        left: [
          alb.metrics.targetResponseTime({ period: Duration.minutes(1), statistic: 'p50' }),
          alb.metrics.targetResponseTime({ period: Duration.minutes(1), statistic: 'p95' }),
          alb.metrics.targetResponseTime({ period: Duration.minutes(1), statistic: 'p99' }),
        ],
        width: 12,
      }),
    );

    dashboard.addWidgets(
      new cloudwatch.GraphWidget({
        title: 'ECS Fargate CPU & Memory Utilization',
        left: [fargateService.metricCpuUtilization({ period: Duration.minutes(1) })],
        right: [fargateService.metricMemoryUtilization({ period: Duration.minutes(1) })],
        width: 8,
      }),
      new cloudwatch.GraphWidget({
        title: 'Aurora Serverless v2 ACU & Connections',
        left: [auroraCluster.metric('ServerlessDatabaseCapacity', { period: Duration.minutes(1) })],
        right: [auroraCluster.metricDatabaseConnections({ period: Duration.minutes(1) })],
        width: 8,
      }),
      new cloudwatch.GraphWidget({
        title: 'SQS Ingestion Queue vs DLQ Depth',
        left: [ingestionQueue.metricApproximateNumberOfMessagesVisible()],
        right: [ingestionDlq.metricApproximateNumberOfMessagesVisible()],
        width: 8,
      }),
    );

    dashboard.addWidgets(
      new cloudwatch.GraphWidget({
        title: 'DynamoDB Consumed RCU / WCU',
        left: [telemetryTable.metricConsumedReadCapacityUnits()],
        right: [telemetryTable.metricConsumedWriteCapacityUnits()],
        width: 12,
      }),
      new cloudwatch.GraphWidget({
        title: 'Onboarding Saga Executions (Succeeded vs Failed)',
        left: [onboardingSaga.metricSucceeded({ period: Duration.minutes(5) })],
        right: [onboardingSaga.metricFailed({ period: Duration.minutes(5) })],
        width: 12,
      }),
    );

    // =========================================================================
    // 10. Stack Outputs
    // =========================================================================
    this.apiUrl = new CfnOutput(this, 'HttpApiEndpoint', {
      value: httpApi.apiEndpoint,
      description: 'API Gateway HTTP API endpoint protected by Cognito JWT Authorizer',
    });

    this.albDnsName = new CfnOutput(this, 'AlbDnsName', {
      value: alb.loadBalancerDnsName,
      description: 'Application Load Balancer DNS name protected by WAFv2',
    });

    new CfnOutput(this, 'EcrRepositoryUri', {
      value: ecrRepository.repositoryUri,
      description: 'ECR Repository URI for ARM64 container builds',
    });

    new CfnOutput(this, 'EcsClusterName', {
      value: ecsCluster.clusterName,
    });

    new CfnOutput(this, 'EcsServiceName', {
      value: fargateService.serviceName,
    });

    new CfnOutput(this, 'IngestionDlqUrl', {
      value: ingestionDlq.queueUrl,
    });

    new CfnOutput(this, 'RdsProxyEndpoint', {
      value: rdsProxy.endpoint,
    });
  }
}
