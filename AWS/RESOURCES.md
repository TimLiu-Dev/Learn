# Senior AWS Engineering Resources

## Knowledge

- [roadmap.sh — AWS Developer Roadmap](https://roadmap.sh/aws)
  Opinionated baseline map of AWS services (IAM, VPC, EC2, S3, Route 53, CloudWatch, CloudFront, RDS, DynamoDB, ElastiCache, ECS/EKS, Lambda, EventBridge, API Gateway, SQS/SNS, IaC, KMS). Use for: curriculum coverage baseline and gap checks.
- [AWS Well-Architected Framework Documentation](https://docs.aws.amazon.com/wellarchitected/latest/framework/welcome.html)
  Official six-pillar architectural standard (Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, Sustainability). Use for: senior design tradeoffs and architecture reviews.
- [Amazon Builders' Library](https://aws.amazon.com/builders-library/)
  Deep engineering essays by AWS Principal Engineers (e.g., *Timeouts, retries, and backoff with jitter*, *Avoiding fallback in distributed systems*, *Shuffle sharding*). Use for: distributed systems reliability and production failure modes.
- [AWS Elastic Load Balancing — Application Load Balancer Docs](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/application-load-balancers.html)
  Primary specification for ALB connection idle timeouts, client keep-alive, target health checks, and HTTP/2 behavior. Use for: request lifecycle and 502/504 troubleshooting.
- [AWS Skill Builder](https://skillbuilder.aws/)
  Official AWS courses and labs (e.g., *AWS Well-Architected Foundations*). Use for: structured AWS service familiarity and hands-on sandbox labs.
- [MDN Web Docs — HTTP & TLS](https://developer.mozilla.org/en-US/docs/Web/HTTP)
  Clear specification-aligned reference on HTTP/1.1, HTTP/2, headers, caching, and TLS handshakes. Use for: Part 1 networking and protocol fundamentals.

## Wisdom (Communities)

- [AWS re:Post](https://repost.aws/)
  Official AWS Q&A community moderated by AWS Heroes and Solutions Architects. Use for: real-world troubleshooting edge cases and service limits.
- [r/aws](https://www.reddit.com/r/aws/)
  High-signal practitioner forum for architecture critiques, FinOps horror stories (e.g., surprise NAT Gateway bills), and job-market expectations. Use for: industry norms and peer review.
- [Local AWS User Groups (Meetup)](https://aws.amazon.com/developer/community/usergroups/)
  In-person/virtual engineering talks and networking. Use for: practicing system design explanations aloud and finding 2027 job referrals.

## Gaps

- Verify exact local Docker/Node/Python/Terraform CLI availability in later hands-on Part 1 & Part 2 capstone lessons when we reach them.
