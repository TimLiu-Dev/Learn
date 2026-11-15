// Minimal, dependency-free Node.js 20 ESM Saga step handlers for CloudPulse
// tenant onboarding orchestration. Each action is idempotent via tenantId + requestId.

import { DynamoDBClient, UpdateItemCommand } from '@aws-sdk/client-dynamodb';

const ddb = new DynamoDBClient({});
const TABLE_NAME = process.env.TABLE_NAME;

export const handler = async (event) => {
  const { action, tenantId, planTier, requestId } = event;

  if (!tenantId || !requestId) {
    throw new Error('Missing required tenantId or requestId');
  }

  switch (action) {
    case 'RESERVE_QUOTA': {
      await ddb.send(
        new UpdateItemCommand({
          TableName: TABLE_NAME,
          Key: {
            PK: { S: `TENANT#${tenantId}` },
            SK: { S: 'METADATA#QUOTA' },
          },
          UpdateExpression:
            'SET #status = :reserved, planTier = :tier, lastRequestId = :reqId',
          ConditionExpression:
            'attribute_not_exists(lastRequestId) OR lastRequestId <> :reqId',
          ExpressionAttributeNames: { '#status': 'status' },
          ExpressionAttributeValues: {
            ':reserved': { S: 'QUOTA_RESERVED' },
            ':tier': { S: planTier ?? 'PRO' },
            ':reqId': { S: requestId },
          },
        }),
      );
      return { ...event, quotaReserved: true };
    }

    case 'RELEASE_QUOTA': {
      await ddb.send(
        new UpdateItemCommand({
          TableName: TABLE_NAME,
          Key: {
            PK: { S: `TENANT#${tenantId}` },
            SK: { S: 'METADATA#QUOTA' },
          },
          UpdateExpression: 'SET #status = :released, releasedAt = :now',
          ExpressionAttributeNames: { '#status': 'status' },
          ExpressionAttributeValues: {
            ':released': { S: 'QUOTA_RELEASED' },
            ':now': { S: new Date().toISOString() },
          },
        }),
      );
      return { ...event, quotaReleased: true };
    }

    case 'AUTHORIZE_BILLING': {
      if (event.simulateBillingFailure) {
        throw new Error('BillingAuthorizationDeclined: card authorization failed');
      }
      return {
        ...event,
        billingAuthId: `auth_${requestId}`,
        billingAuthorized: true,
      };
    }

    case 'REFUND_BILLING': {
      return {
        ...event,
        billingRefunded: true,
        refundedAuthId: event.billingAuthId ?? null,
      };
    }

    case 'PROVISION_SCHEMA': {
      if (event.simulateDbFailure) {
        throw new Error('SchemaProvisioningFailed: Aurora RDS Proxy timeout');
      }
      // In production, connects to Aurora via RDS Proxy using IAM auth token
      // and executes idempotent CREATE SCHEMA IF NOT EXISTS tenant_<id>.
      return {
        ...event,
        schemaName: `tenant_${tenantId.replace(/[^a-zA-Z0-9_]/g, '_')}`,
        schemaProvisioned: true,
      };
    }

    default:
      throw new Error(`Unsupported saga action: ${action}`);
  }
};
