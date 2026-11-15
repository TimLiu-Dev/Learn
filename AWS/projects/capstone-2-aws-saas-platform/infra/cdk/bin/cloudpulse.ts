#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { AwsSolutionsChecks, NagSuppressions } from 'cdk-nag';
import { SaasPlatformStack } from '../lib/saas-platform-stack';

const app = new cdk.App();

const environmentName =
  (app.node.tryGetContext('env') as 'staging' | 'production' | undefined) ?? 'production';
const imageTag = app.node.tryGetContext('imageTag') as string | undefined;

const stack = new SaasPlatformStack(app, `CloudPulse-${environmentName}`, {
  environmentName,
  imageTag,
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION ?? 'us-east-1',
  },
});

// Shift-left compliance check against AWS Solutions security rules
cdk.Aspects.of(app).add(new AwsSolutionsChecks({ verbose: true }));

// Documented, intentional suppressions with compensating controls
NagSuppressions.addStackSuppressions(stack, [
  {
    id: 'AwsSolutions-ELB2',
    reason: 'ALB access logging bucket omitted in capstone template to avoid cyclic S3 bucket policy boilerplate; enable in org landing zone.',
  },
  {
    id: 'AwsSolutions-EC23',
    reason: 'Public ALB accepts 0.0.0.0/0 HTTP/HTTPS ingress and is fronted by AWS WAFv2 rate-limiting and managed rule sets.',
  },
  {
    id: 'AwsSolutions-COG2',
    reason: 'MFA is configured as OPTIONAL for self-service B2B onboarding; enterprise tenants enforce MFA via Cognito groups.',
  },
  {
    id: 'AwsSolutions-IAM4',
    reason: 'AWSLambdaVPCAccessExecutionRole managed policy is attached automatically by CDK for VPC-attached Lambda ENI lifecycle.',
  },
  {
    id: 'AwsSolutions-IAM5',
    reason: 'KMS and CloudWatch Logs actions require wildcard resource scope constrained by EncryptionContext and account/region conditions.',
  },
]);
