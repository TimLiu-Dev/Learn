# Learning Record: Roadmap Expansion for Junior Android Dev to Senior AI Engineer (2027)

Expanded [MISSION.md](../MISSION.md) and the workspace curriculum from 9 Modules (25 Lessons) to 12 Modules (33 Lessons) after auditing `https://roadmap.sh/ai-engineer` against the learner's Junior Android Developer -> Senior AI Engineer (2027) goal.

## Status
active

## Evidence
- Learner confirmed `add all` after reviewing the gap analysis comparing the existing 25 coding-agent lessons against `roadmap.sh/ai-engineer` and 2027 mobile/Android market positioning.
- Prior coding-agent modules (`0001`–`0025`) provide deep coverage of ReAct, MCP, Agent Skills, Tree-sitter, PageRank Repo Maps, Hybrid Search, Prefix Caching, Sandboxing, Reflexion, Worktrees, SWE-bench, OpenTelemetry, Kotlin/Gradle, MCTS, and Event Sourcing, but lacked generalist LLM foundations, FastAPI/SSE deployment, vector DB HNSW + cross-encoder RAG evals, indirect prompt injection security, LLM-as-a-Judge + vLLM quantization serving, fine-tuning vs. RAG tradeoffs, and multimodal/on-device Android AI.

## Implications
- **Recommended Learning Order**: Start at **Module 0 (`0026`–`0027`)** to bridge Kotlin Coroutines/Retrofit and Android mental models into Python tokenization/sampling, `asyncio`, SSE streaming, and FastAPI/Docker; continue through **Modules 1–9 (`0001`–`0025`)**; complete **Module 10 (`0028`–`0031`)** for production RAG/Reranking/Ragas, OWASP LLM01 security, LLM-as-a-Judge + vLLM serving, and Prompting vs. RAG vs. LoRA/DPO; finish with **Module 11 (`0032`–`0033`)** for Multimodal Compose Vision Agents and On-Device Android `Gemini Nano` / AICore / `LiteRT` routing.
- Avoid fluff from `roadmap.sh`: skip heavy LangChain wrapper churn, multi-vendor Vector DB comparison shopping (standardize on HNSW concepts used by `pgvector`/Qdrant), and from-scratch pre-training.
