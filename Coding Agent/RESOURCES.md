# AI Coding Agents Resources

## Knowledge

- [Paper: _ReAct: Synergizing Reasoning and Acting in Language Models_ — Yao et al. (Princeton / Google Brain, 2022)](https://arxiv.org/abs/2210.03629)
  Foundational text establishing the interleaved Thought -> Action -> Observation loop. Use for: core agent execution state machines and stop sequence handling.
- [Paper: _Reflexion: Language Agents with Verbal Reinforcement Learning_ — Shinn et al. (Northeastern / MIT, 2023)](https://arxiv.org/abs/2303.11366)
  Defines episodic memory, self-reflection on failed test cases, and heuristic self-healing. Use for: automated code repair loops and test traceback digestion.
- [Paper: _SWE-bench: Can Language Models Resolve Real-World GitHub Issues?_ — Jimenez et al. (Princeton / Chicago, ICLR 2024)](https://www.swebench.com/)
  The industry-standard benchmark for AI software engineers. Use for: agent evaluation harnesses, pass@k metrics, and environment reproducibility.
- [Specification: _Model Context Protocol (MCP)_ — Anthropic (2024)](https://modelcontextprotocol.io/)
  Open standard for connecting AI models to external tools, data sources, and dev environments. Use for: building interoperable agent tool architectures.
- [Architecture & Benchmarks: _Aider: AI Pair Programming in Your Terminal_ — Paul Gauthier (2023–2025)](https://aider.chat/docs/repomap.html)
  Pioneered repository maps via Tree-sitter AST symbol tags and PageRank relevance scoring. Use for: token-efficient codebase representation.
- [Documentation: _Tree-sitter: An Incremental Parsing System for Programming Tools_ — Max Brunsfeld et al.](https://tree-sitter.github.io/tree-sitter/)
  Fast, incremental AST generator for multi-language symbol extraction. Use for: code chunking, symbol graph generation, and syntax-aware diffs.
- [Guide: _Prompt Caching & KV Cache Prefix Reuse in Multi-Turn Agents_ — Anthropic & vLLM](https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching)
  Defines how static prompt prefixes (system instructions, repo maps, tool schemas) are retained in GPU KV memory, cutting latency by 80% and token costs by 90%. Use for: cost-efficient agent scaling.
- [Specification: _Agent Skills Open Standard_ — Anthropic / Agentic AI Foundation](https://agentskills.io/)
  Defines progressive-disclosure `SKILL.md` bundles with lightweight YAML metadata indexes and on-demand instruction/script loading. Use for: context-efficient agent capabilities.
- [Paper: _From Local to Global: A Graph RAG Approach to Query-Focused Summarization_ — Edge et al. (Microsoft Research, 2024)](https://arxiv.org/abs/2404.16130)
  Extracts hierarchical knowledge and relationship graphs. Use for: GraphRAG concepts across multi-package call and import graphs.
- [Paper: _GraphCoder: Enhancing Repository-Level Code Completion via Code Context Graph-based Retrieval and Language Model_ — Liu et al. (2024)](https://arxiv.org/abs/2406.07003)
  Combines Control/Data Dependence Code Context Graphs with coarse-to-fine retrieval for repository-level code context, paired with Cormack et al. (SIGIR 2009) Reciprocal Rank Fusion. Use for: hybrid code retrieval.
- [Guide: _Claude Platform Tool Use & Structured Function Calling Reference_ — Anthropic](https://platform.claude.com/docs/en/agents-and-tools/tool-use/overview)
  Official protocol mechanics for JSON schema tool calling and streaming execution. Use for: client-side tool dispatch engines.
- [Paper: _Automated Program Repair in the Era of Large Pre-trained Language Models_ — Xia, Wei, Zhang (ICSE 2023)](https://arxiv.org/abs/2210.14179)
  Establishes LLM-driven test-failure-guided program repair loops across real codebases (`DOI: 10.1109/ICSE48619.2023.00129`). Use for: Gradle/Maven JVM self-healing compiler-test feedback loops.
- [Specification: _OpenTelemetry Semantic Conventions for Generative AI Systems_ — CNCF OpenTelemetry](https://github.com/open-telemetry/semantic-conventions-genai)
  Standardized `gen_ai.*` span attributes, token usage telemetry, and MCP/agent trace hierarchies. Use for: agent observability and runaway loop detection.
- [Paper: _Scaling LLM Test-Time Compute Optimally can be More Effective than Scaling Parameters_ — Snell et al. (UC Berkeley / Google DeepMind, 2024)](https://arxiv.org/abs/2408.03314)
  Establishes how trajectory space search (MCTS, rollouts, PRMs) outperforms 14x larger models using greedy decoding on coding benchmarks. Use for: reasoning agents and trajectory search.
- [Architecture & Standards: _Temporal Durable Execution Systems & Martin Kleppmann Event Sourcing Patterns_](https://docs.temporal.io/encyclopedia/temporal-system-architecture)
  Defines immutable event-sourced replay architecture for long-running workflows and non-deterministic tool caching. Use for: zero-loss crash resilience.
- [Curriculum Roadmap: _AI Engineer Roadmap (`roadmap.sh/ai-engineer`)_ — Kamran Ahmed et al.](https://roadmap.sh/ai-engineer)
  Industry baseline skill map covering LLM APIs, embeddings, vector databases, RAG, AI safety/ethics, Evals, and Dockerized FastAPI deployment. Use for: cross-checking generalist Senior AI Engineer breadth.
- [Paper: _Neural Machine Translation of Rare Words with Subword Units_ — Sennrich, Haddow, Birch (ACL 2016)](https://arxiv.org/abs/1508.07909)
  Introduces Byte-Pair Encoding (BPE) subword tokenization solving open-vocabulary representation. Use for: tokenizer mechanics, token counting, and code-token inflation analysis.
- [Paper: _The Curious Case of Neural Text Degeneration_ — Holtzman et al. (ICLR 2020)](https://arxiv.org/abs/1904.09751)
  Diagnoses greedy/beam text degeneration and introduces Nucleus (`top_p`) sampling. Use for: sampling hyperparameter tuning (`temperature`, `top_p`) across deterministic coding vs. exploratory tasks.
- [Documentation: _FastAPI StreamingResponse & Async SSE APIs_ — Sebastián Ramírez](https://fastapi.tiangolo.com/advanced/custom-response/)
  Official pattern for wrapping async Python generators in Server-Sent Events (`text/event-stream`). Use for: low-latency token-streaming LLM backend gateways.
- [Documentation: _Qdrant HNSW Indexing & Vector Search Fundamentals_ — Qdrant](https://qdrant.tech/courses/day2/hnsw-layers/)
  Explains Hierarchical Navigable Small World (HNSW) graph construction, `m`, `ef_construct`, and recall/latency tuning. Use for: production vector indexing beyond brute-force scan.
- [Documentation: _Sentence-Transformers Cross-Encoders & Ragas Evaluation Framework_ — SBERT / Ragas](https://www.sbert.net/docs/pretrained_models/cross-encoders.html)
  Two-stage retrieval combining fast bi-encoder vector lookup with full cross-attention reranking, plus [Ragas](https://docs.ragas.io/) faithfulness/context-recall RAG metrics. Use for: high-precision RAG pipelines.
- [Security Standard: _OWASP Top 10 for LLM Applications: LLM01 Prompt Injection_ — OWASP GenAI Security Project](https://genai.owasp.org/llmrisk/llm01-prompt-injection/)
  Taxonomy and defense-in-depth mitigations for direct jailbreaks and indirect prompt injection inside untrusted data/MCP responses. Use for: agent trust boundaries, sanitization, and PII/secret redaction.
- [Paper: _Judging LLM-as-a-Judge with MT-Bench and Chatbot Arena_ — Zheng et al. (2023)](https://arxiv.org/abs/2306.05685)
  Establishes pairwise grading, position/verbosity bias controls, and calibration against human preferences. Use for: non-deterministic agent quality evaluation harnesses.
- [Paper: _Efficient Memory Management for Large Language Model Serving with PagedAttention (vLLM)_ — Kwon et al. (SOSP 2023)](https://arxiv.org/abs/2309.06180)
  Virtual-memory KV-cache paging enabling continuous batching and high-throughput open-weights inference. Use for: GPU VRAM sizing, quantization (`FP16`/`Q8`/`Q4`), and self-hosting economics.
- [Papers: _LoRA: Low-Rank Adaptation_ (Hu et al., 2021) & _Direct Preference Optimization (DPO)_ (Rafailov et al., 2023)](https://arxiv.org/abs/2106.09685)
  Parameter-efficient adapter fine-tuning ([arXiv:2106.09685](https://arxiv.org/abs/2106.09685)) and reward-model-free alignment ([arXiv:2305.18290](https://arxiv.org/abs/2305.18290)). Use for: Prompting vs. RAG vs. Fine-Tuning architecture decisions.
- [Documentation: _Anthropic Claude Vision & Visual Tokenization Guide_ — Anthropic](https://platform.claude.com/)
  Covers multimodal image block formatting, tile-based visual token budgets, and UI inspection workflows. Use for: Jetpack Compose / Android emulator visual debugging agents.
- [Documentation: _Android AI, Gemini Nano (AICore) & Google AI Edge LiteRT_ — Google Android Developers](https://developer.android.com/ai/agents)
  Official Android guides for on-device `Gemini Nano` via system `AICore` ([developer.android.com/ai](https://developer.android.com/ai)) and custom model deployment via [LiteRT](https://ai.google.dev/edge/litert). Use for: offline-first, privacy-preserving hybrid mobile AI routing.

## Wisdom (Communities)

- [Latent Space Discord & Podcast](https://www.latent.space/)
  Premier engineering forum for AI Engineers building autonomous code and agent workflows. Use for: frontier architecture debates, benchmarks, and hiring trends.
- [AI Engineer World's Fair / Summit Community](https://www.ai.engineer/)
  High-signal conference community focused on production LLM systems and software engineering agents. Use for: portfolio critique and industry standards.
- [r/LocalLLaMA](https://reddit.com/r/LocalLLaMA)
  Active community testing open-weights coding models (Qwen-Coder, DeepSeek-Coder, Llama-Code) and local agent harnesses. Use for: offline model benchmarking and agent runtime tuning.
- [Aider & OpenDevin / All-Hands GitHub Discussions](https://github.com/All-Hands-AI/OpenHands)
  Open-source engineering hub actively iterating on SWE-bench harnesses, worktree isolation, and coding agent sandboxes. Use for: sandbox safety and diff patching edge cases.

## Gaps
- Standardized benchmarks for multi-file refactoring beyond single-commit SWE-bench tasks (to be monitored in future sessions).
