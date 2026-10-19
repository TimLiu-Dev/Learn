# Networking Curriculum (draft v1 — awaiting feedback)

Interview frequency: ★★ very common · ★ common · (blank) mostly for understanding/debugging.
Ratings are informed judgment, not data. Tools are introduced where they first earn their place (🔧).

## Part 0 — Foundations
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 1 | Packets & packet switching; latency vs bandwidth vs throughput | ★ | 🔧 `ping` |
| 2 | Layering & encapsulation: TCP/IP (4–5 layers) vs OSI (7) — why layers exist | ★★ | 🔧 install Wireshark, dissect one packet's layers |
| R0 | Review | | |

## Part 1 — Getting a packet from A to B (link + internet layers)
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 3 | MAC addresses, Ethernet/Wi-Fi frames, switches | | 🔧 `ifconfig`, Wireshark frame headers |
| 4 | IPv4 addresses and the IP packet; TTL; ICMP | ★ | Wireshark: IP header fields |
| 5 | Subnets & CIDR (subnet math) | ★ | compute your own network from `ifconfig` |
| 6 | ARP: from IP address to MAC address | ★ | 🔧 `arp -a`, capture an ARP exchange |
| 7 | DHCP: how your machine gets an address | ★ | renew a lease, watch DORA in Wireshark |
| 8 | Routing: routing tables, default gateway, longest-prefix match | ★ | 🔧 `netstat -rn`, `traceroute` |
| 9 | NAT and private address ranges | ★ | compare your LAN IP with your public IP |
| 10 | IPv6: why, address format, SLAAC, dual stack | | `ping6`, find your IPv6 addresses |
| R1 | Mixed review: Parts 0–1 | | |

## Part 2 — Transport: TCP & UDP
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 11 | Ports & sockets: multiplexing, the 5-tuple | ★ | 🔧 `lsof -i`, `netstat -an` |
| 12 | UDP: the minimal transport | ★ | Kotlin UDP echo client/server |
| 13 | TCP connections: 3-way handshake, teardown, TIME_WAIT | ★★ | capture a handshake in Wireshark |
| 14 | TCP reliability: sequence numbers, ACKs, retransmission | ★ | Wireshark TCP stream graphs |
| 15 | TCP flow & congestion control; Nagle; head-of-line blocking | ★ | throttled transfer experiment |
| 16 | TCP vs UDP: choosing a transport | ★★ | design exercise |
| 17 | TCP sockets in Kotlin/Java: echo server, and why streams need framing | ★ | 🔧 `nc`; Kotlin TCP client/server |
| R2 | Mixed review: Parts 0–2 (interleaved) | | |

## Part 3 — Naming & the Web
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 18 | DNS: hierarchy, recursive vs iterative resolution | ★★ | 🔧 `dig +trace` |
| 19 | DNS records, caching & TTLs; "it's always DNS" | ★ | Mess With DNS playground |
| 20 | HTTP/1.1 message anatomy; speaking HTTP by hand | ★★ | 🔧 `curl -v`, `nc` to a real server |
| 21 | HTTP semantics: methods, status codes, idempotency, caching headers | ★★ | curl exercises |
| 22 | Statelessness, cookies & sessions (vs tokens) | ★ | inspect cookies with curl + browser devtools |
| 23 | Same-origin policy & CORS *(added)* | ★ | trigger and fix a CORS error |
| 24 | HTTP/2: binary framing, multiplexing, header compression | ★ | `curl --http2`, devtools protocol column |
| 25 | HTTP/3 & QUIC: why HTTP moved to UDP | ★ | `curl --http3`, capture QUIC |
| 26 | Real-time: polling, long polling, SSE, WebSockets | ★★ | watch a WebSocket upgrade |
| R3 | Mixed review: Parts 0–3 | | |

## Part 4 — Security
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 27 | Crypto essentials: symmetric, asymmetric, hashes, signatures, key exchange | ★ | |
| 28 | Certificates & PKI: chains of trust, CAs | ★ | 🔧 `openssl s_client` |
| 29 | TLS 1.3 handshake & HTTPS | ★★ | decrypt your own TLS in Wireshark via SSLKEYLOGFILE |
| 30 | Common attacks: MITM, ARP/DNS spoofing, SYN flood/DDoS, replay; CSRF/XSS & cookie flags | ★ | spot attack signatures in captures |
| R4 | Mixed review: Parts 0–4 | | |

## Part 5 — Infrastructure
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 31 | Firewalls: stateless vs stateful, security groups | | macOS firewall / `pf` look |
| 32 | Proxies: forward vs reverse | ★ | run a local reverse proxy |
| 33 | Load balancers: L4 vs L7, algorithms, health checks, sticky sessions | ★★ | balance two local servers |
| 34 | CDNs & edge caching | ★★ | read CDN cache headers with curl |
| 35 | VPNs & tunnels *(added, brief)* | | |
| R5 | Mixed review: Parts 0–5 | | |

## Part 6 — Putting it together
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 36 | "What happens when you type a URL and press Enter" — you write the full answer | ★★ | trace every step with tools |
| 37 | Debugging networked software: refused vs timeout vs reset, timeouts/retries, a layer-by-layer method *(added)* | ★ | diagnose staged failures |
| 38 | Project 1: accept a connection, parse the request line | | Kotlin/Java, raw `ServerSocket` |
| 39 | Project 2: headers, bodies, Content-Length, responses & status codes | | |
| 40 | Project 3: routing & static files | | |
| 41 | Project 4: concurrency (threads → virtual threads), keep-alive | ★ | load-test with curl/`ab` |
| 42 | Project 5: compare with a framework (e.g. Ktor/Javalin) — what it does for you | | |
| R6 | Final cumulative review | | |
