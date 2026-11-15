variable "aws_region" {
  description = "AWS region for CloudPulse bootstrap & network foundation"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment name"
  type        = string
  default     = "production"

  validation {
    condition     = contains(["staging", "production"], var.environment)
    error_message = "environment must be either 'staging' or 'production'."
  }
}

variable "vpc_cidr" {
  description = "CIDR block for the Multi-AZ VPC"
  type        = string
  default     = "10.20.0.0/16"
}

variable "availability_zones" {
  description = "Two Availability Zones for Multi-AZ high availability"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "github_org" {
  description = "GitHub organization or username allowed to assume the OIDC deploy role"
  type        = string
}

variable "github_repo" {
  description = "GitHub repository name allowed to assume the OIDC deploy role"
  type        = string
  default     = "capstone-2-aws-saas-platform"
}

variable "create_oidc_provider" {
  description = "Set false if the GitHub OIDC provider already exists in this AWS account"
  type        = bool
  default     = true
}
