# Parameterized Multi-Tenant VPC + KMS Encrypted RDS PostgreSQL (Terraform Baseline)
terraform {
  required_version = ">= 1.6.0"
}

variable "customer_slug" {
  type        = string
  description = "Tenant/customer slug isolating infrastructure state without code forks."
}

variable "vpc_cidr" {
  type    = string
  default = "10.42.0.0/16"
}

resource "aws_vpc" "customer_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name    = "aegisops-${var.customer_slug}-vpc"
    Managed = "terraform"
  }
}

resource "aws_kms_key" "tenant_data_key" {
  description             = "Customer-managed KMS key for ${var.customer_slug} RDS and S3 ingestion"
  enable_key_rotation     = true
  deletion_window_in_days = 30
}

resource "aws_db_instance" "aegis_postgres" {
  identifier              = "aegisops-${var.customer_slug}-pg"
  engine                  = "postgres"
  engine_version          = "16.3"
  instance_class          = "db.t4g.medium"
  allocated_storage       = 50
  storage_encrypted       = true
  kms_key_id              = aws_kms_key.tenant_data_key.arn
  publicly_accessible     = false # NEVER expose customer ontology DB to public internet
  backup_retention_period = 14
  deletion_protection     = true
}
