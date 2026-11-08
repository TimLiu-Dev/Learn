# Delivery of Part 2 (Transport Mechanics) and Part 3 (Professional Developer Track)

## Scope Completed
- **Part 2: Transport Layer Mechanics (Sessions 13–20)**
  - 13: Ports & Sockets (5-tuple, ephemeral ranges, `lsof -i`, `netstat`)
  - 14: UDP Mechanics (8-byte header, connectionless datagrams, single-file Java datagram socket)
  - 15: TCP Lifecycle (3-way handshake, 4-way FIN teardown, TIME_WAIT, 2MSL)
  - 16: TCP Reliability (Sequence numbers, cumulative ACKs, SACK, Fast Retransmit)
  - 17: Flow & Congestion Control (rwnd, cwnd, BDP formula, Reno AIMD vs Cubic vs BBR)
  - 18: Transport Dilemmas (Nagle's algorithm, Delayed ACKs, `TCP_NODELAY`, transport HoL blocking)
  - 19: Sockets in Code (Raw Java `ServerSocket`, virtual threads, delimiter vs length-prefix framing)
  - 20: Mixed Review: Transport Layer Mastery (Interview gauntlet, diagnostic synthesis)

- **Part 3: Professional Developer Track (Sessions 21–35)**
  - 21: DNS Architecture (13 root clusters, TLDs, recursive vs iterative, `dig +trace`)
  - 22: DNS in Practice (Records A/AAAA/CNAME/TXT, zone apex RFC 1912 limits, TTL cache bugs)
  - 23: HTTP/1.1 Anatomy (Request/response structure, CRLF boundaries, `Keep-Alive`, chunked encoding)
  - 24: HTTP Semantics (Safe vs idempotent methods, status families, 401 vs 403, 502 vs 504, ETags/304)
  - 25: Cookies & State (Statelessness, `HttpOnly`, `Secure`, `SameSite` Strict/Lax/None, Session vs JWT)
  - 26: Browser Security & CORS (SOP definition, CORS response headers, preflight `OPTIONS`)
  - 27: HTTP/2 (Binary framing, stream multiplexing, HPACK compression, domain sharding obsolescence)
  - 28: HTTP/3 & QUIC (QUIC over UDP, stream independence, 0-RTT/1-RTT, connection ID migration)
  - 29: Real-Time Web (Short/long polling, SSE `text/event-stream`, WebSocket 101 Upgrade, WebRTC)
  - 30: Applied Cryptography (AES-GCM, RSA vs ECC, ECDHE key exchange, Perfect Forward Secrecy)
  - 31: PKI & Certificates (Digital signatures, X.509 chains, Root/Intermediate/Leaf, OCSP Stapling)
  - 32: TLS 1.3 Handshake (1-RTT handshake, encrypted certs, SNI extension, `SSLKEYLOGFILE` decrypt)
  - 33: High-Performance RPC (REST vs gRPC, Protobuf binary tags & varints, 4 streaming modes)
  - 34: Resilient Client Architecture (Connect vs Read timeouts, connection pooling, Exponential Backoff with Jitter, Circuit Breakers)
  - 35: Mixed Review: Web Protocols & Developer Networking (Staff/Principal architecture gauntlet)

## Quality & Pedagogical Verification
- Every lesson adheres to the benchmark standard established in Session 1:
  - Tufte-inspired typography, custom SVGs, interactive quizzes with balanced answer lengths.
  - Active recall prompts with hidden revealable model answers.
  - Hands-on terminal CLI instructions (`curl`, `dig`, `openssl`, `nc`, `lsof`).
  - Zero answer giveaways in observation steps.
  - Unbroken two-way navigation chain from Session 1 to Session 35 and back to `index.html`.
