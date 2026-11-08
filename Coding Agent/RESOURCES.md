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
- [Paper: _From Local to Global: A Graph RAG Approach to Query-Focused Summarization_ — Edge et al. (Microsoft Research, 2024)](https://arxiv.org/abs/2404.16130)
  Extracts hierarchical knowledge and relationship graphs. Use for: Code Graph RAG across multi-package call and import graphs.
- [Guide: _OpenAI Function Calling & Anthropic Tool Use Reference_](https://docs.anthropic.com/en/docs/build-with-claude/tool-use)
  Official protocol mechanics for JSON schema tool calling and streaming execution. Use for: client-side tool dispatch engines.
- [Paper: _Scaling LLM Test-Time Compute Optimally can be More Effective than Scaling Parameters_ — Snell et al. (UC Berkeley / Google DeepMind, 2024)](https://arxiv.org/abs/2408.03314)
  Establishes how trajectory space search (MCTS, rollouts, PRMs) outperforms 14x larger models using greedy decoding on coding benchmarks. Use for: reasoning agents and trajectory search.
- [Architecture & Standards: _Temporal Durable Execution Systems & Martin Kleppmann Event Sourcing Patterns_](https://docs.temporal.io/encyclopedia/temporal-system-architecture)
  Defines immutable event-sourced replay architecture for long-running workflows and non-deterministic tool caching. Use for: zero-loss crash resilience.

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
