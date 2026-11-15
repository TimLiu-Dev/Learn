terraform {
  required_version = ">= 1.9.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.75"
    }
  }

  # Remote encrypted state backend with locking.
  # Bootstrap bucket/table once per account or migrate to Terraform 1.10+ native use_lockfile = true.
  backend "s3" {
    bucket         = "cloudpulse-tfstate-us-east-1"
    key            = "foundation/production/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "cloudpulse-tfstate-lock"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Platform    = "CloudPulse"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

data "aws_caller_identity" "current" {}

locals {
  name_prefix = "cloudpulse-${var.environment}"

  interface_endpoints = {
    ecr_api        = "com.amazonaws.${var.aws_region}.ecr.api"
    ecr_dkr        = "com.amazonaws.${var.aws_region}.ecr.dkr"
    logs           = "com.amazonaws.${var.aws_region}.logs"
    secretsmanager = "com.amazonaws.${var.aws_region}.secretsmanager"
    sts            = "com.amazonaws.${var.aws_region}.sts"
  }
}

# =============================================================================
# 1. Multi-AZ VPC (Public / App Private / Data Isolated)
# =============================================================================
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${local.name_prefix}-vpc"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${local.name_prefix}-igw"
  }
}

resource "aws_subnet" "public" {
  count                   = length(var.availability_zones)
  vpc_id                  = aws_vpc.main.id
  availability_zone       = var.availability_zones[count.index]
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, count.index) # 10.20.0.0/24, 10.20.1.0/24
  map_public_ip_on_launch = false

  tags = {
    Name = "${local.name_prefix}-public-${var.availability_zones[count.index]}"
    Tier = "Public"
  }
}

resource "aws_subnet" "private" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.main.id
  availability_zone = var.availability_zones[count.index]
  cidr_block        = cidrsubnet(var.vpc_cidr, 6, count.index + 1) # 10.20.4.0/22, 10.20.8.0/22

  tags = {
    Name = "${local.name_prefix}-private-${var.availability_zones[count.index]}"
    Tier = "AppPrivate"
  }
}

resource "aws_subnet" "isolated" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.main.id
  availability_zone = var.availability_zones[count.index]
  cidr_block        = cidrsubnet(var.vpc_cidr, 6, count.index + 3) # 10.20.12.0/22, 10.20.16.0/22

  tags = {
    Name = "${local.name_prefix}-isolated-${var.availability_zones[count.index]}"
    Tier = "DataIsolated"
  }
}

resource "aws_eip" "nat" {
  count  = var.environment == "production" ? length(var.availability_zones) : 1
  domain = "vpc"

  tags = {
    Name = "${local.name_prefix}-nat-eip-${count.index}"
  }
}

resource "aws_nat_gateway" "nat" {
  count         = length(aws_eip.nat)
  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = aws_subnet.public[count.index].id
  depends_on    = [aws_internet_gateway.igw]

  tags = {
    Name = "${local.name_prefix}-nat-${count.index}"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${local.name_prefix}-public-rt"
  }
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table" "private" {
  count  = length(var.availability_zones)
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat[var.environment == "production" ? count.index : 0].id
  }

  tags = {
    Name = "${local.name_prefix}-private-rt-${count.index}"
  }
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private[count.index].id
}

resource "aws_route_table" "isolated" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${local.name_prefix}-isolated-rt"
  }
}

resource "aws_route_table_association" "isolated" {
  count          = length(aws_subnet.isolated)
  subnet_id      = aws_subnet.isolated[count.index].id
  route_table_id = aws_route_table.isolated.id
}

# =============================================================================
# 2. VPC Gateway Endpoints (Free) + PrivateLink Interface Endpoints
# =============================================================================
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.aws_region}.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = concat(aws_route_table.private[*].id, [aws_route_table.isolated.id])

  tags = {
    Name = "${local.name_prefix}-s3-gateway-ep"
  }
}

resource "aws_vpc_endpoint" "dynamodb" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.aws_region}.dynamodb"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = concat(aws_route_table.private[*].id, [aws_route_table.isolated.id])

  tags = {
    Name = "${local.name_prefix}-dynamodb-gateway-ep"
  }
}

resource "aws_security_group" "vpce" {
  name        = "${local.name_prefix}-vpce-sg"
  description = "Allow HTTPS (443) from VPC workloads to PrivateLink Interface Endpoints"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "HTTPS from VPC"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }

  tags = {
    Name = "${local.name_prefix}-vpce-sg"
  }
}

resource "aws_vpc_endpoint" "interface" {
  for_each            = local.interface_endpoints
  vpc_id              = aws_vpc.main.id
  service_name        = each.value
  vpc_endpoint_type   = "Interface"
  subnet_ids          = aws_subnet.private[*].id
  security_group_ids  = [aws_security_group.vpce.id]
  private_dns_enabled = true

  tags = {
    Name = "${local.name_prefix}-${each.key}-ep"
  }
}

# =============================================================================
# 3. GitHub Actions OIDC Federation (Zero Static Access Keys!)
# =============================================================================
resource "aws_iam_openid_connect_provider" "github" {
  count = var.create_oidc_provider ? 1 : 0

  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1", "1c58a3a8518e8759bf075b76b750d4f2df264fcd"]
}

locals {
  github_oidc_provider_arn = var.create_oidc_provider ? aws_iam_openid_connect_provider.github[0].arn : "arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/token.actions.githubusercontent.com"
}

data "aws_iam_policy_document" "github_oidc_trust" {
  statement {
    sid     = "AllowGitHubActionsOidc"
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [local.github_oidc_provider_arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    # Restrict strictly to this repository's main branch, PRs, or production environment
    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values = [
        "repo:${var.github_org}/${var.github_repo}:ref:refs/heads/main",
        "repo:${var.github_org}/${var.github_repo}:pull_request",
        "repo:${var.github_org}/${var.github_repo}:environment:production",
      ]
    }
  }
}

resource "aws_iam_role" "github_deploy" {
  name                 = "${local.name_prefix}-github-oidc-deploy"
  assume_role_policy   = data.aws_iam_policy_document.github_oidc_trust.json
  max_session_duration = 3600
}

data "aws_iam_policy_document" "github_deploy_permissions" {
  # Allow assuming CDK bootstrap roles for cdk synth/diff/deploy
  statement {
    sid     = "AssumeCdkBootstrapRoles"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    resources = [
      "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/cdk-hnb659fds-*",
    ]
  }

  # Allow ECR image push & ECS deployment verification / rollback
  statement {
    sid    = "EcrImagePush"
    effect = "Allow"
    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:CompleteLayerUpload",
      "ecr:InitiateLayerUpload",
      "ecr:PutImage",
      "ecr:UploadLayerPart",
      "ecr:BatchGetImage",
    ]
    resources = ["*"]
  }

  statement {
    sid    = "EcsCanaryAndRollbackGuard"
    effect = "Allow"
    actions = [
      "ecs:DescribeServices",
      "ecs:DescribeTaskDefinition",
      "ecs:UpdateService",
      "cloudformation:DescribeStacks",
      "cloudformation:RollbackStack",
      "cloudwatch:DescribeAlarms",
      "cloudwatch:GetMetricStatistics",
    ]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "github_deploy" {
  name   = "${local.name_prefix}-github-oidc-policy"
  role   = aws_iam_role.github_deploy.id
  policy = data.aws_iam_policy_document.github_deploy_permissions.json
}

# =============================================================================
# 4. Outputs
# =============================================================================
output "vpc_id" {
  value       = aws_vpc.main.id
  description = "CloudPulse Multi-AZ VPC ID"
}

output "github_deploy_role_arn" {
  value       = aws_iam_role.github_deploy.arn
  description = "Set this ARN as GitHub Actions repository variable AWS_DEPLOY_ROLE_ARN"
}

output "private_subnet_ids" {
  value       = aws_subnet.private[*].id
  description = "App private subnet IDs"
}

output "isolated_subnet_ids" {
  value       = aws_subnet.isolated[*].id
  description = "Data isolated subnet IDs"
}
