# Networking Curriculum (v2)

Interview frequency: ★★ very common · ★ common · (blank) mostly for understanding/debugging.
Ratings are informed judgment, not data. Tools are introduced where they first earn their place (🔧).
Every session, including reviews, has a sequential number; lesson files are `lessons/NNNN-<slug>.html` with the same number.

## Part 0 — Foundations
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 1 | Packets & packet switching; latency vs bandwidth vs throughput | ★ | 🔧 `ping` |
| 2 | Layering & encapsulation: TCP/IP vs OSI — why layers exist | ★★ | 🔧 install Wireshark, dissect one packet's layers |
| 3 | Review: Part 0 | | |

## Part 1 — Getting a packet from A to B (link + internet layers)
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 4 | MAC addresses, Ethernet/Wi-Fi frames, switches | | 🔧 `ifconfig`, Wireshark frame headers |
| 5 | IPv4 addresses and the IP packet; TTL; ICMP | ★ | Wireshark: IP header fields |
| 6 | Subnets & CIDR (subnet math) | ★ | compute your own network; subnet trainer |
| 7 | ARP: from IP address to MAC address | ★ | 🔧 `arp -a`, capture an ARP exchange |
| 8 | DHCP: how your machine gets an address | ★ | renew a lease, watch it in Wireshark |
| 9 | Routing: routing tables, default gateway, longest-prefix match | ★ | 🔧 `netstat -rn`, `traceroute` |
| 10 | NAT and private address ranges | ★ | compare your LAN IP with your public IP |
| 11 | IPv6: why, address format, SLAAC, NDP, dual stack | | `ping6`, `ndp`, first Kotlin/Java snippet |
| 12 | Mixed review: Parts 0–1 | | |

## Part 2 — Transport: TCP & UDP
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 13 | Ports & sockets: multiplexing, the 5-tuple | ★ | 🔧 `lsof -i`, `netstat -an` |
| 14 | UDP: the minimal transport | ★ | Kotlin UDP echo client/server |
| 15 | TCP connections: 3-way handshake, teardown, TIME_WAIT | ★★ | capture a handshake in Wireshark |
| 16 | TCP reliability: sequence numbers, ACKs, retransmission | ★ | Wireshark TCP stream graphs |
| 17 | TCP flow & congestion control; Nagle; head-of-line blocking | ★ | throttled transfer experiment |
| 18 | TCP vs UDP: choosing a transport | ★★ | design exercise |
| 19 | TCP sockets in Kotlin/Java: echo server, and why streams need framing | ★ | 🔧 `nc`; Kotlin TCP client/server |
| 20 | Mixed review: Parts 0–2 | | |

## Part 3 — Naming & the Web
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 21 | DNS: hierarchy, recursive vs iterative resolution | ★★ | 🔧 `dig +trace` |
| 22 | DNS records, caching & TTLs; "it's always DNS" | ★ | Mess With DNS playground |
| 23 | HTTP/1.1 message anatomy; speaking HTTP by hand | ★★ | 🔧 `curl -v`, `nc` to a real server |
| 24 | HTTP semantics: methods, status codes, idempotency, caching headers | ★★ | curl exercises |
| 25 | Statelessness, cookies & sessions (vs tokens) | ★ | cookies with curl + devtools |
| 26 | Same-origin policy & CORS | ★ | trigger and fix a CORS error |
| 27 | HTTP/2: binary framing, multiplexing, header compression | ★ | `curl --http2`, devtools |
| 28 | HTTP/3 & QUIC: why HTTP moved to UDP | ★ | `curl --http3`, capture QUIC |
| 29 | Real-time: polling, long polling, SSE, WebSockets | ★★ | watch a WebSocket upgrade |
| 30 | Mixed review: Parts 0–3 | | |

## Part 4 — Security
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 31 | Crypto essentials: symmetric, asymmetric, hashes, signatures, key exchange | ★ | |
| 32 | Certificates & PKI: chains of trust, CAs | ★ | 🔧 `openssl s_client` |
| 33 | TLS 1.3 handshake & HTTPS | ★★ | decrypt your own TLS in Wireshark |
| 34 | Common attacks: MITM, ARP/DNS spoofing, SYN flood/DDoS, replay; CSRF/XSS & cookie flags | ★ | attack signatures in captures |
| 35 | Mixed review: Parts 0–4 | | |

## Part 5 — Infrastructure
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 36 | Firewalls: stateless vs stateful, security groups | | macOS `pf` look |
| 37 | Proxies: forward vs reverse | ★ | run a local reverse proxy |
| 38 | Load balancers: L4 vs L7, algorithms, health checks, sticky sessions | ★★ | balance two local servers |
| 39 | CDNs & edge caching (and anycast) | ★★ | read CDN cache headers with curl |
| 40 | VPNs & tunnels (brief) | | |
| 41 | Mixed review: Parts 0–5 | | |

## Part 6 — Putting it together
| # | Session | Interview | Hands-on |
|---|---------|-----------|----------|
| 42 | "What happens when you type a URL and press Enter" — you write the full answer | ★★ | trace every step with tools |
| 43 | Debugging networked software: refused vs timeout vs reset; a layer-by-layer method | ★ | diagnose staged failures |
| 44 | Project 1: accept a connection, parse the request line | | Kotlin/Java, raw `ServerSocket` |
| 45 | Project 2: headers, bodies, Content-Length, responses & status codes | | |
| 46 | Project 3: routing & static files | | |
| 47 | Project 4: concurrency (threads → virtual threads), keep-alive | ★ | load-test |
| 48 | Project 5: compare with a framework (Ktor/Javalin) — what it does for you | | |
| 49 | Final cumulative review | | |

---

# Detailed plan: Parts 0–1

Shared conventions for every lesson:
- **Terminology** (Kurose & Ross style, with one deliberate choice): application *message*; TCP *segment*; UDP *datagram*; network-layer **packet** (alias "IP datagram" — avoid in this workspace); link-layer *frame*. *Host* = end system; *router* = layer-3 forwarder; *switch* = layer-2 forwarder. *Latency* = one-way delay; *RTT* = round trip. *Bandwidth* = link capacity (bits/s); *throughput* = rate actually achieved.
- **Layer colours** are fixed across all lessons: application, transport, network, link, physical (see `assets/course.css`).
- Lesson shape: win → warm-up recall (from session 2 on) → why → concept → try it (real tools) → check yourself (quiz) → primary source → ask your teacher.
- Hands-on questions whose answers depend on the learner's own machine are never answered in the lesson — bring them to chat.

### 1 · Packets & delay
- **Win:** predict from physics the minimum RTT to a city, then use `ping` to catch a server that can't be where you assumed.
- **Key facts:** data is split into packets (header + payload); packet switching shares links, circuit switching reserves them; routers store-and-forward; delay = processing + queuing + transmission (L/R) + propagation (d/s); fibre ≈ 2×10⁸ m/s ≈ 200 km/ms; bandwidth vs throughput (bottleneck); bits vs bytes (×8); full queues → packet loss.
- **Hands-on:** `ping` loopback, gateway, 1.1.1.1, two distant universities; compute physical minimum RTT; explain the contradiction.

### 2 · Layers & encapsulation
- **Win:** install Wireshark, capture one HTTP request, and name each header and what its layer is responsible for.
- **Key facts:** why layers (separation of concerns, swap Wi-Fi for Ethernet without touching apps); 5-layer Internet model vs 7-layer OSI (presentation, session) vs RFC 1122's 4 layers; per-layer responsibility and address (app: —, transport: port, network: IP address, link: MAC address); encapsulation and PDU names; hosts implement all layers, routers up to network, switches up to link; why "L4"/"L7" use OSI numbers.
- **Hands-on:** `brew install --cask wireshark-app`, capture while `curl http://example.com`, dissect Frame / Ethernet II / IPv4 / TCP / HTTP.

### 3 · Review: Part 0
- Interleaved retrieval across 1–2: free recall, quiz, one delay calculation, one "which layer?" sorting exercise.

### 4 · MAC addresses & frames
- **Win:** read an Ethernet frame header and explain why a MAC address only matters for one hop.
- **Key facts:** 48-bit MAC, OUI, I/G and U/L bits (randomised private Wi-Fi addresses), Ethernet II fields (dst, src, EtherType 0x0800/0x86DD/0x0806, payload, FCS), MTU 1500, broadcast ff:ff:ff:ff:ff:ff, switches learn MAC→port and flood unknowns; frame MACs change each hop while IP addresses stay.

### 5 · IPv4 & ICMP
- **Win:** read every IPv4 header field in Wireshark and explain what TTL and ICMP are for.
- **Key facts:** 32-bit dotted-decimal; best-effort, connectionless; header fields; protocol numbers 1/6/17; TTL decrement and Time Exceeded; ICMP echo (ping), destination unreachable, time exceeded; MTU, DF bit and fragmentation (briefly); IPv4 exhaustion as the hook for NAT and IPv6.

### 6 · Subnets & CIDR
- **Win:** for any address/prefix, compute network, broadcast and host range, and decide whether two hosts share a subnet.
- **Key facts:** prefix + host bits; /n and netmask (macOS shows hex); bitwise AND; 2^(32−n) addresses; classful history → CIDR; "same subnet?" decides direct delivery vs gateway.
- **Component:** `assets/subnet-trainer.js` (random problems, instant checking) — reused in session 12.

### 7 · ARP
- **Win:** explain and watch how your Mac learns the MAC address for an IP on its subnet.
- **Key facts:** broadcast request / unicast reply (RFC 826); cache and expiry; only for on-subnet targets — off-subnet traffic ARPs for the gateway; no authentication (sets up ARP spoofing in session 34); IPv6 uses NDP instead.

### 8 · DHCP
- **Win:** watch your Mac get its configuration and name each DHCP message and what it carries.
- **Key facts:** a host needs IP, mask, gateway and DNS before it can talk; UDP 67/68; Discover/Offer/Request/Ack; leases, renewal; options; 169.254.0.0/16 self-assigned address as a debugging clue.

### 9 · Routing
- **Win:** read your routing table, predict any packet's next hop, and trace the hops to a server.
- **Key facts:** forwarding vs routing; destination prefix → next hop; longest-prefix match; default route; hop-by-hop; OSPF/BGP named only; traceroute via TTL + ICMP Time Exceeded; `*` ≠ loss.

### 10 · NAT
- **Win:** explain why the internet sees a different IP from your laptop, and how replies find their way back.
- **Key facts:** RFC 1918 ranges; NAPT translation table (IP + port); unsolicited inbound fails → port forwarding; CGNAT 100.64.0.0/10; NAT ≠ firewall; peer-to-peer pain (STUN/TURN named).

### 11 · IPv6
- **Win:** read and compress IPv6 addresses, classify the ones on your Mac, and explain how IPv6 replaces NAT and ARP.
- **Key facts:** 128-bit; :: rules (RFC 5952); /64 LANs; link-local fe80::/10, ULA fc00::/7, global 2000::/3, ::1, multicast (no broadcast); SLAAC + Router Advertisements; temporary addresses; NDP; 40-byte header, hop limit, no router fragmentation; dual stack and Happy Eyeballs.
- **Code:** first single-file Java/Kotlin program listing and classifying interface addresses.

### 12 · Review: Parts 0–1
- Interleaved: subnet trainer, packet-journey ordering (DHCP → ARP → routing → NAT), "which tool would you run?" debugging scenarios, free recall.
