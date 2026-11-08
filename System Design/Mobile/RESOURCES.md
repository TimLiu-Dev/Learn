# Mobile System Design Resources (2027 Edition)

## Knowledge

- [Google Android Guide to App Architecture](https://developer.android.com/topic/architecture)
  Official standard for Modern Android Architecture. Use for: Unidirectional Data Flow (UDF), UI state modeling, and clean domain/data boundary definitions.
- [Kotlin Coroutines and Flow: Deep Dive & Structured Concurrency](https://kotlinlang.org/docs/coroutines-overview.html)
  Authoritative concurrency mechanics. Use for: non-blocking thread scheduling, structured lifecycle cancellation, cold vs hot stream semantics.
- [Designing Data-Intensive Applications — Martin Kleppmann](https://dataintensive.net/)
  Foundational text on distributed systems. Use for: client-side replication, conflict resolution (CRDTs vs LWW), write-ahead logs, and causal consistency.
- [Meta Engineering Blog: Mobile Architecture & Performance](https://engineering.fb.com/category/open-source/)
  Real-world FAANG scale mobile designs. Use for: prefetching heuristics, memory allocation limits, mobile metric instrumentation, and zero-overhead telemetry.
- [Uber Engineering: Real-Time Location & Network Reliability](https://www.uber.com/blog/engineering/)
  Mission-critical edge resilience. Use for: battery-aware GPS polling algorithms, binary protocol streaming over QUIC, and connection retry backoff state machines.
- [Google AI Edge & LiteRT / MediaPipe Documentation](https://ai.google.dev/edge)
  2027 on-device machine learning standard. Use for: running quantized models (Gemini Nano) locally, NPU vs CPU dispatch, and hybrid client-cloud inference architectures.
- [Apple Developer Documentation: Background Tasks & Swift Concurrency](https://developer.apple.com/documentation/backgroundtasks)
  Official iOS systems reference. Use for: cross-platform parity comparisons against Android WorkManager and Coroutines.
- [AndroidX Media3 & ExoPlayer Developer Guide](https://developer.android.com/media/media3/exoplayer)
  The official standard for video playback and streaming. Use for: player instance pooling, CacheDataSource chunk buffering, and Adaptive Bitrate (ABR) streaming.
- [Android Developers: Keystore & BiometricPrompt Security Architecture](https://developer.android.com/privacy-and-security/keystore)
  Bank-grade mobile client security. Use for: TEE / StrongBox hardware key storage, CryptoObject authentication, and OAuth token refresh synchronization.
- [Meta Engineering: Mobile Experimentation & Feature Flagging](https://engineering.fb.com/2016/09/20/core-infra/under-the-hood-mobile-systems-scale/)
  A/B testing at extreme scale. Use for: local flag rule evaluation, sticky bucketing via consistent hashing, and zero-bias exposure telemetry.
- [Android Developers: Full-Text Search (FTS) in Room SQLite](https://developer.android.com/training/data-storage/room/defining-data#fts)
  Sub-millisecond offline local search. Use for: inverted indexes, Porter stemmer tokenization, and BM25 relevance ranking.
- [Android Developers: Media3 Transformer & On-Device Transcoding](https://developer.android.com/media/media3/transformer)
  Client media compression pipeline. Use for: hardware silicon HEVC transcoding and resumable S3/TUS chunked uploads.
- [Android Developers: Background Audio Playback with MediaSessionService](https://developer.android.com/media/media3/session/background-playback)
  Spotify-grade audio streaming. Use for: Foreground Service MEDIA_PLAYBACK, lock screen controls, and Audio Focus state machines.

## Wisdom (Communities)

- [r/androiddev](https://reddit.com/r/androiddev)
  High-signal developer community with active Google Developer Experts and core Android team members. Use for: platform bug edge cases and real-world trade-off debates.
- [Kotlinlang Public Slack (#android, #coroutines, #kmp)](https://kotlinlang.slack.com/)
  Direct access to JetBrains and Kotlin maintainers. Use for: advanced coroutines patterns and multiplatform boundary decisions.
- [Mobile Native Foundation](https://mobilenativefoundation.org/)
  Industry coalition (Slack, Uber, Pinterest, Robinhood, etc.) discussing large-scale mobile development, build times, CI/CD, and architecture.
