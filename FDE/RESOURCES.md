# Senior Forward Deployed Engineer (2027) Resources

## Knowledge

### Operating Model, Discovery & Career Calibration
- [Article: "Dev versus Delta: Demystifying engineering roles at Palantir" — Palantir Engineering Blog](https://blog.palantir.com/dev-versus-delta-demystifying-engineering-roles-at-palantir-ad44c2a6e87)
  Primary source distinguishing core Product Developers ("many customers, one capability") from Forward Deployed Engineers / Deltas ("one customer, many capabilities"). Use for: Phase 1 operating philosophy and avoiding the consulting trap.
- [Essay: "Reflections on Palantir" — Nabeel Qureshi](https://nabeelqu.co/reflections-on-palantir)
  First-hand account by a former Palantir FDE lead on working with ambiguity, customer discovery, rapid prototyping under enterprise chaos, and shipping production outcomes. Use for: Phase 1 & Phase 7 senior FDE judgment.
- [Article: "Services-Led Growth" — Andreessen Horowitz (a16z)](https://a16z.com/services-led-growth/)
  Strategic analysis of why AI companies deploy FDEs ("trading margin for moat") and how field hacks must generalize back into core software primitives. Use for: Phase 1 productization boundaries and executive framing.
- [Newsletter: "What Are Forward Deployed Engineers, and Why Are They in Demand?" — The Pragmatic Engineer](https://newsletter.pragmaticengineer.com/p/forward-deployed-engineers)
  Industry breakdown of FDE demand surge, compensation, and common pitfalls where companies mislabel sales support as FDE. Use for: career positioning, job vetting, and interview calibration.
- [Skill Tree: Forward Deployed Engineer Roadmap — roadmap.sh](https://roadmap.sh/forward-deployed-engineer)
  Community-maintained skill map covering full-stack delivery, cloud/IaC/K8s, SSO, SOW/UAT delivery artifacts, and applied AI deployment breadth. Use for: cross-checking skill coverage across Phases 1–7.

### Enterprise Identity, Infra & Security
- [Docs: OpenID Connect — How Connect Works](https://openid.net/developers/how-connect-works/)
  Primary specification overview for enterprise SSO authentication tokens, claims, and identity provider flows. Use for: Unit `0010` bridging mobile OAuth to enterprise SSO.
- [Docs: OpenFGA Documentation](https://openfga.dev/docs) & [SpiceDB / AuthZed Docs](https://authzed.com/docs)
  Canonical implementations of relationship-based access control (ReBAC) for document, folder, and multi-tenant enterprise ACL propagation. Use for: Unit `0010` & `0017`.
- [Docs: HashiCorp Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
  Official Infrastructure-as-Code reference for repeatable customer VPC/IAM provisioning. Use for: Unit `0012` & Capstone environment setup.
- [Docs: Kubernetes — Debug Running Pods](https://kubernetes.io/docs/tasks/debug/debug-application/debug-running-pod/)
  Official troubleshooting runbook for inspecting crash loops, probes, logs, and network isolation inside customer clusters. Use for: Unit `0012`.
- [Docs: OpenTelemetry Observability Primer](https://opentelemetry.io/docs/concepts/observability-primer/) & [OTel GenAI Semantic Conventions](https://github.com/open-telemetry/semantic-conventions-genai)
  Vendor-neutral fundamentals and GenAI span/metric conventions across distributed pipelines and LLM calls. Use for: Unit `0013` & Capstone telemetry.
- [Standard: OWASP Top 10 for Large Language Model Applications](https://genai.owasp.org/llm-top-10/)
  Industry threat taxonomy covering prompt injection, sensitive data disclosure, excessive agency, and unsafe tool execution. Use for: Units `0011`, `0021`, `0026`.

### Applied AI, Protocols, Inference & Evals
- [Article: "Contextual Retrieval in AI Systems" — Anthropic](https://www.anthropic.com/news/contextual-retrieval)
  Canonical guide showing how contextual chunk enrichment paired with BM25 + embeddings + reranking slashes enterprise retrieval failure rates. Use for: Units `0015`–`0016`.
- [Docs: vLLM Documentation](https://docs.vllm.ai/)
  Primary reference for high-throughput self-hosted LLM inference inside private/air-gapped VPCs. Use for: Unit `0014` inference hosting & cost/latency tradeoffs.
- [Engineering Guide: "Building Effective Agents" — Anthropic Research](https://www.anthropic.com/research/building-effective-agents)
  Canonical taxonomy separating composable workflows (chaining, routing, parallelization, evaluator-optimizer) from autonomous agent loops. Use for: Unit `0020`.
- [Article: "Agents" — Chip Huyen](https://huyenchip.com/2025/01/07/agents.html)
  Systems perspective on agent planning, tool execution, memory, latency budgets, and production failure modes. Use for: Phases 4–5.
- [Specs: Model Context Protocol (MCP) Architecture](https://modelcontextprotocol.io/docs/concepts/architecture) & [Agent2Agent (A2A) Protocol Spec](https://github.com/a2aproject/A2A)
  Official open standards for vertical agent-to-tool bindings (MCP) and horizontal cross-framework agent interoperability (A2A Agent Cards & async tasks). Use for: Units `0022` & Capstone.
- [Guide: "Your AI Product Needs Evals" — Hamel Husain](https://hamel.dev/blog/posts/evals/)
  Industry benchmark on axial error analysis, golden datasets from real failures, and calibrated LLM-as-judge suites. Use for: Phase 6 (`0024`–`0026`) & Capstone CI gates.

### Enterprise Data, Postgres, Cloud Networking & Webhooks
- [Docs: PostgreSQL Index Types (B-Tree, GIN, GiST, HNSW)](https://www.postgresql.org/docs/current/indexes-types.html)
  Official reference for composite multi-tenant indexes, `CONCURRENTLY` index builds, and keyset pagination performance. Use for: Unit `0010` & Capstone schema.
- [Docs: AWS VPC Fundamentals & Subnets](https://docs.aws.amazon.com/vpc/latest/userguide/what-is-amazon-vpc.html) & [IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
  Primary cloud architecture docs covering private subnets, PrivateLink, corporate CA TLS, and least-privilege workload IAM roles. Use for: Unit `0011` & Capstone VPC infra.
- [Repo: IBM Docling — Document Layout & Table Parser](https://github.com/docling-project/docling)
  Open-source layout parser for multi-column PDFs and structured Markdown table extraction prior to Contextual Chunking. Use for: Unit `0017`.
- [Docs: GitHub — Validating Webhook Deliveries](https://docs.github.com/en/webhooks/using-webhooks/validating-webhook-deliveries) & [Stripe — Webhook Signatures](https://stripe.com/docs/webhooks/signatures)
  Canonical guides for HMAC-SHA256 signature validation, raw-body hashing, constant-time comparison, and replay timestamp windows. Use for: Unit `0005` & Capstone webhooks.
- [Docs: DuckDB Window Functions](https://duckdb.org/docs/sql/window_functions) & [GitHub: pgvector](https://github.com/pgvector/pgvector)
  Official references for analytical window queries (deduplication, drift audits) and hybrid PostgreSQL vector similarity indexing. Use for: Units `0009`, `0016`, `0017`, & Capstone.
- [Guide: Anthropic Prompt Engineering Overview](https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering/overview)
  Primary production documentation on structured XML/JSON prompts, grounding, refusal calibration, and evaluation iteration. Use for: Units `0018`–`0020` & Phase 6.

## Wisdom (Communities)

- [Subreddit: r/FDEngineering](https://www.reddit.com/r/FDEngineering/)
  Peer community of Forward Deployment Engineers sharing field war stories, debugging tactics, interview breakdowns, and travel/burnout trade-offs. Use for: sanity-checking real-world FDE job postings and deployment culture.
- [GitHub: global-fde/awesome-fde-resources](https://github.com/global-fde/awesome-fde-resources)
  Curated open-source index of FDE case studies, tooling patterns, and career playbooks. Use for: discovering real-world enterprise integration examples.
- [GitHub: pierpaolo28/Awesome-FDE-Roadmap](https://github.com/pierpaolo28/Awesome-FDE-Roadmap)
  Community-maintained technical roadmap covering data pipelines, enterprise infrastructure, and AI agent deployment. Use for: cross-checking skill gaps against peers.
- [Network: FDE.community](https://fde.community/)
  Practitioner community for FDEs and enterprise AI builders. Use for: regional meetups, peer mock discovery interviews, and architecture feedback.

## Gaps

- **Palantir Decomp / OpenAI / Anthropic Mock Rubrics:** Curate clean first-hand engineering interview accounts before authoring Unit `0028` (or supplement with `FDE.community` peer mock rubrics).
