# Computer Networking: Master Curriculum (v3)

A comprehensive, field-wide syllabus structured across:
- **Part 0: Shared Foundations** (Sessions 1–3) — Delay, packets, and protocol layers [Live 🟢]
- **Part 1: Layer 2 & 3 Core** (Sessions 4–12) — MAC, IPv4/IPv6, CIDR, ARP, DHCP, Routing, NAT [Live 🟢]
- **Part 2: Transport Mechanics** (Sessions 13–20) — Ports, UDP, TCP handshakes, reliability, flow/congestion [Live 🟢]
- **Part 3: Track A — Professional Developers** (Sessions 21–35) — HTTP/1/2/3, TLS 1.3, WebSockets, gRPC, resilience [Live 🟢]
- **Part 4: Track B — Professional Network Engineers** (Sessions 36–50) — Switching, VLANs, STP, OSPF, BGP, firewalls, IPsec, QoS [Live 🟢]
- **Part 5: Track C — Cloud & Modern Infrastructure** (Sessions 51–65) — SDN, VPCs, subnets, route tables, TGW, ALBs, K8s CNI/eBPF [Live 🟢]
- **Part 6: Capstones & Outage Forensics** (Sessions 66–72) — The URL question, debugging playbook, raw socket server, outage autopsies [Live 🟢]

---

## Part 0 — Shared Foundations (Live 🟢)
| # | Session | Focus / Topic | Interview | Hands-on / Tool |
|---|---------|---------------|-----------|-----------------|
| 01 | [Packets & Delay](lessons/0001-packets-and-delay.html) | Packet switching, store-and-forward, 4 delays, speed-of-light limits | ★ | 🔧 `ping` RTT physics |
| 02 | [Layers & Encapsulation](lessons/0002-layers-and-encapsulation.html) | OSI 7-layer vs TCP/IP 4-layer models, headers & payloads | ★★ | 🔧 Wireshark packet dissection |
| 03 | [Review: Foundations](lessons/0003-review-foundations.html) | Synthesis of delay math and protocol encapsulation | | Retrieval & diagnostic checks |

## Part 1 — Layer 2 & 3 Core Substrate (Live 🟢)
| # | Session | Focus / Topic | Interview | Hands-on / Tool |
|---|---------|---------------|-----------|-----------------|
| 04 | [MAC Addresses & Frames](lessons/0004-mac-addresses-and-frames.html) | Link layer, Ethernet frames, MAC unicast/multicast/broadcast | | 🔧 `ifconfig`, frame headers |
| 05 | [IPv4 & ICMP](lessons/0005-ipv4-and-icmp.html) | IP packet format, TTL, checksum, ICMP echo/unreachable | ★ | Wireshark TTL decrement |
| 06 | [Subnets & CIDR](lessons/0006-subnets-and-cidr.html) | Subnet masks, CIDR prefix math, host ranges, broadcast address | ★ | Subnet math trainer |
| 07 | [ARP](lessons/0007-arp.html) | Resolving IP to MAC on local LAN, ARP cache, gratuitous ARP | ⚡ | 🔧 `arp -a`, packet capture |
| 08 | [DHCP](lessons/0008-dhcp.html) | Dynamic address leasing, DORA exchange, DHCP options | ★ | Lease renewal capture |
| 09 | [Routing Fundamentals](lessons/0009-routing.html) | Default gateways, routing tables, longest-prefix match (LPM) | ★ | 🔧 `netstat -rn`, `traceroute` |
| 10 | [NAT & PAT](lessons/0010-nat.html) | RFC 1918 private IPs, Port Address Translation, state tables | ★ | Comparing private LAN vs public IP |
| 11 | [IPv6](lessons/0011-ipv6.html) | 128-bit hex notation, SLAAC, NDP neighbor solicitation, dual-stack | | 🔧 `ping6`, `ndp -a` |
| 12 | [Review: Link & Internet](lessons/0012-review-link-and-internet.html) | Comprehensive packet trace evaluation across L2 and L3 | | Diagnostic synthesis |

---

## Part 2 — Transport Layer Mechanics: TCP, UDP, & Sockets (Complete 🟢)
| # | Session | Focus / Topic | Importance | Hands-on / Artifact |
|---|---------|---------------|------------|---------------------|
| 13 | [Ports & Multiplexing](lessons/0013-ports-and-sockets.html) | Ephemeral vs well-known ports, the 5-tuple, demultiplexing | ★ | 🔧 `lsof -i`, `netstat -an` |
| 14 | [UDP: Minimal Transport](lessons/0014-udp.html) | Connectionless datagrams, low latency, use cases (VoIP, DNS) | | Single-file Java Datagram socket |
| 15 | [TCP Connection Lifecycle](lessons/0015-tcp-connections.html) | 3-way handshake (SYN, SYN-ACK, ACK), FIN/RST, TIME_WAIT (2MSL) | ★★ ⚡ | Wireshark handshake capture |
| 16 | [TCP Reliability](lessons/0016-tcp-reliability.html) | Sequence numbers, cumulative ACKs, SACK, retransmission timeouts | ⚡ | Wireshark TCP stream graphs |
| 17 | [TCP Flow & Congestion](lessons/0017-tcp-flow-and-congestion.html) | Sliding window (rwnd), congestion window (cwnd), Reno vs Cubic vs BBR | ★ | Throttled transfer experiment |
| 18 | [Transport Dilemmas](lessons/0018-tcp-dilemmas.html) | Nagle's algorithm (`TCP_NODELAY`), delayed ACKs, HoL blocking | ⚡ | Reproducing Nagle latency spike |
| 19 | [Sockets in Code](lessons/0019-tcp-sockets-in-code.html) | Berkeley sockets API, `ServerSocket`, stream framing strategies | | Java virtual thread TCP echo server |
| 20 | [Mixed Review: Transport](lessons/0020-review-transport.html) | Comprehensive transport protocol audit | | Diagnostic review lab |

---

## Part 3 — Track A: Professional Software Developers (Complete 🟢)
| # | Session | Focus / Topic | Importance | Hands-on / Artifact |
|---|---------|---------------|------------|---------------------|
| 21 | [DNS Architecture](lessons/0021-dns-architecture.html) | Root servers, TLDs, authoritative nameservers, iterative vs recursive | ★★ | 🔧 `dig +trace` root walk |
| 22 | [DNS in Production](lessons/0022-dns-in-practice.html) | Records (A, AAAA, CNAME, MX, TXT), TTL caching, stale cache bugs | ⚡ | Stale DNS diagnosis |
| 23 | [HTTP/1.1 Protocol Anatomy](lessons/0023-http1-anatomy.html) | Headers, methods, bodies, persistent connections (`Keep-Alive`) | ★★ | 🔧 `nc` manual request, `curl -v` |
| 24 | [HTTP Semantics](lessons/0024-http-semantics.html) | Idempotency, safe methods, status codes (2xx, 3xx, 4xx, 5xx), caching | ★★ | ETag & `Cache-Control` probes |
| 25 | [Web State & Cookies](lessons/0025-cookies-and-state.html) | Statelessness, Cookie flags (HttpOnly, Secure, SameSite), JWTs | | DevTools cookie auditing |
| 26 | [Browser Security & CORS](lessons/0026-cors-and-sop.html) | Same-Origin Policy (SOP), Preflight `OPTIONS`, CORS headers | ⚡ | Triggering & fixing CORS |
| 27 | [HTTP/2 Deep Dive](lessons/0027-http2.html) | Binary framing, stream multiplexing, HPACK header compression | ★ | 🔧 `curl --http2` multiplexing |
| 28 | [HTTP/3 & QUIC](lessons/0028-http3-and-quic.html) | UDP-based transport, 0-RTT handshakes, surviving IP migration | ★ | 🔧 `curl --http3` & QUIC capture |
| 29 | [Real-Time Web Protocols](lessons/0029-realtime-web.html) | Polling vs SSE vs WebSockets vs WebRTC | ★★ | WebSocket upgrade trace |
| 30 | [Applied Cryptography](lessons/0030-crypto-foundations.html) | Symmetric vs asymmetric ciphers, hashing, digital signatures | | 🔧 `openssl` ECDH keygen |
| 31 | [PKI & Certificates](lessons/0031-pki-and-certificates.html) | X.509 chains of trust, CAs, intermediate authorities, OCSP | ⚡ | 🔧 `openssl s_client` cert chain |
| 32 | [TLS 1.3 Handshake](lessons/0032-tls13-handshake.html) | ECDHE key exchange, Perfect Forward Secrecy (PFS), HTTPS encryption | ★★ | Decrypting TLS session keys |
| 33 | [High-Performance RPC](lessons/0033-grpc-and-rpc.html) | REST vs gRPC, Protocol Buffers binary framing & streaming | | Protobuf serialization bench |
| 34 | [Resilient Client Architecture](lessons/0034-client-resilience.html) | Connection pooling, exponential backoff, jitter, circuit breakers | ⚡ | Simulating thundering herd |
| 35 | [Mixed Review: Web & Dev](lessons/0035-review-developer-track.html) | Developer networking security and performance audit | | End-to-end web lab |

---

## Part 4 — Track B: Professional Network Engineers (Complete 🟢)
> *Note for Software Engineers:* Sessions marked **◇ Optional** cover CCNA-level hardware/ISP mechanics (optics, L3 SVIs, STP, OSPF, router QoS, SNMP) that you can skim or treat as enrichment while keeping VLANs (38), BGP/RPKI (42–44), Firewalls (45), VPNs (46), and VXLAN (47) as core required.

| # | Session | Focus / Topic | Importance | Hands-on / Artifact |
|---|---------|---------------|------------|---------------------|
| 36 | [Physical Media & Optics](lessons/0036-physical-media-and-optics.html) | Copper Cat6a, Single-mode vs Multi-mode fiber, SFP+, LACP trunking | ◇ Optional | Optical link budget calculation |
| 37 | [Enterprise Switching](lessons/0037-enterprise-switching.html) | CAM tables, MAC learning, flooding, aging, broadcast storms | ★ | MAC table inspection |
| 38 | [VLANs & 802.1Q](lessons/0038-vlans-and-8021q.html) | Segmenting broadcast domains, Access vs Trunk ports, Native VLAN | ★ | Wireshark 802.1Q tag dissection |
| 39 | [Inter-VLAN Routing](lessons/0039-inter-vlan-routing.html) | Router-on-a-stick vs Layer 3 Switch Switched Virtual Interfaces (SVIs) | ◇ Optional | Routing across tagged trunks |
| 40 | [Spanning Tree Protocol](lessons/0040-spanning-tree-protocol.html) | STP (802.1D) & RSTP (802.1w), Bridge ID, Root bridge, loop prevention | ◇ Optional | BPDU dissection & election trace |
| 41 | [Enterprise IGP: OSPF](lessons/0041-ospf-routing.html) | Link-State Advertisements (LSAs), Dijkstra SPF, Area 0, DR/BDR election | ◇ Optional | OSPF adjacency analysis |
| 42 | [BGP Fundamentals](lessons/0042-bgp-fundamentals.html) | Autonomous Systems (ASNs), Path-Vector protocol, TCP port 179 | ★★ | BGP Looking Glass route check |
| 43 | [BGP Attributes & Peering](lessons/0043-bgp-attributes-and-peering.html) | eBGP vs iBGP, Route Reflectors, AS-PATH, Local Pref, MED, Transit | ★ | BGP path selection lab |
| 44 | [Internet Routing Security](lessons/0044-routing-security-and-rpki.html) | BGP route hijacking, route leaks, RPKI ROA verification | ⚡ | Verifying ROA cryptographic validity |
| 45 | [Perimeter Firewalls](lessons/0045-perimeter-firewalls.html) | Stateless ACLs vs stateful inspection, connection tracking, DMZ | ★ | macOS `pf` state table lab |
| 46 | [Enterprise VPNs](lessons/0046-enterprise-vpns.html) | IPsec architecture (AH, ESP, IKEv2), Site-to-site tunnels, WireGuard | ⚡ | IPsec ESP packet capture |
| 47 | [Overlay Encapsulation](lessons/0047-overlay-encapsulation-vxlan.html) | GRE tunnels, VXLAN encapsulation (VNI), Data center network fabric | ★ | Wireshark VXLAN decode |
| 48 | [Quality of Service (QoS)](lessons/0048-quality-of-service.html) | DSCP marking, queuing (LLQ, WRED), traffic policing vs shaping | ◇ Optional | Rate limiting & shaping test |
| 49 | [Network Telemetry](lessons/0049-network-telemetry.html) | SNMP v3, Syslog, NetFlow, sFlow, Streaming Telemetry | ◇ Optional | NetFlow record decoding |
| 50 | [Mixed Review: NetEng](lessons/0050-review-network-engineering.html) | Enterprise & ISP architecture troubleshooting | ★ Master | Complex topology audit |

---

## Part 5 — Track C: Cloud & Modern Infrastructure (Complete 🟢)
| # | Session | Focus / Topic | Importance | Hands-on / Artifact |
|---|---------|---------------|------------|---------------------|
| 51 | [SDN Fundamentals](lessons/0051-sdn-fundamentals.html) | Decoupling Control Plane & Data Plane, OpenFlow, virtual overlays | ★ | Flow table inspection |
| 52 | [Cloud VPC Architecture](lessons/0052-cloud-vpc-architecture.html) | CIDR design, Public/Private subnets, Multi-AZ high availability | ★★ | Multi-tier VPC architecture lab |
| 53 | [Cloud Egress & Gateways](lessons/0053-cloud-egress-and-gateways.html) | Internet Gateways (IGW), Route Tables, Managed NAT Gateways | ⚡ | Tracing NAT gateway egress |
| 54 | [Cloud Security & Isolation](lessons/0054-security-groups-and-nacls.html) | Security Groups (Stateful) vs NACLs (Stateless), Ephemeral port drops | ★★ | Ephemeral port drop triage |
| 55 | [VPC Interconnects](lessons/0055-vpc-interconnects-and-tgw.html) | VPC Peering (non-transitive limits) vs AWS Transit Gateway (TGW) | ★ | Hub-and-spoke route propagation |
| 56 | [Private SaaS Connectivity](lessons/0056-privatelink-and-vpc-endpoints.html) | AWS PrivateLink, Interface Endpoints, avoiding public internet | ⚡ | Consuming private VPC endpoints |
| 57 | [Hybrid Cloud Interconnect](lessons/0057-hybrid-cloud-interconnect.html) | DirectConnect / ExpressRoute, redundant BGP over IPsec failover | | Hybrid cloud connectivity design |
| 58 | [Global Anycast & CDNs](lessons/0058-anycast-and-cdns.html) | Anycast IP routing to closest PoP, CloudFront/Fastly edge caching | ★★ | Anycast latency & cache header audit |
| 59 | [Cloud Load Balancing](lessons/0059-cloud-load-balancing-alb-nlb.html) | Application Load Balancer (L7) vs Network Load Balancer (L4) | ★★ | ALB vs NLB performance benchmark |
| 60 | [Kubernetes Network Model](lessons/0060-kubernetes-network-model.html) | Pod IP-per-Pod, flat cluster network, namespaces & veth pairs | ★★ | Linux network namespace audit |
| 61 | [Container Interfaces (CNI)](lessons/0061-kubernetes-cni-plugins.html) | Calico (BGP/overlay) vs AWS VPC CNI (native ENI) vs Cilium | ⚡ | Overlay vs native routing test |
| 62 | [Kubernetes Services](lessons/0062-kubernetes-services-and-kubeproxy.html) | ClusterIP, NodePort, LoadBalancer, kube-proxy (iptables vs IPVS) | | Deciphering kube-proxy DNAT rules |
| 63 | [eBPF Modern Networking](lessons/0063-ebpf-networking-and-cilium.html) | How Cilium bypasses iptables in the Linux kernel via eBPF | ⚡ Modern | eBPF map inspection |
| 64 | [Service Mesh Architecture](lessons/0064-service-mesh-and-envoy.html) | Envoy sidecars, Istio control plane, mutual TLS (mTLS) enforcement | | mTLS certificate validation |
| 65 | [Mixed Review: Cloud](lessons/0065-review-cloud-and-modern-infra.html) | Cloud & distributed networking architecture audit | | Full infrastructure design review |

---

## Part 6 — Capstones & Outage Forensics (Complete 🟢)
| # | Session | Focus / Topic | Importance | Hands-on / Artifact |
|---|---------|---------------|------------|---------------------|
| 66 | [The URL Question](lessons/0066-the-url-question.html) | "What happens when you type a URL and hit Enter?" — Full end-to-end trace | ★★★ | Full multi-tool trace |
| 67 | [Diagnostic Playbook](lessons/0067-network-diagnostic-playbook.html) | Systematic layer-by-layer triage: refused vs timeout vs reset vs TLS | ⚡ | Troubleshooting staged outages |
| 68 | [Project: Raw Socket HTTP Server](lessons/0068-project-raw-socket-http-server-1.html) | Building an HTTP/1.1 server from raw `ServerSocket` in Kotlin/Java (Part 1) | | Byte parser & status codes |
| 69 | [Project: Concurrency & Static Files](lessons/0069-project-raw-socket-http-server-2.html) | Virtual threads, Keep-Alive, streaming static files, load testing (Part 2) | ★ | High concurrency load test |
| 70 | [Outage Autopsy: Facebook 2021](lessons/0070-outage-autopsy-facebook-2021.html) | BGP route withdrawal, DNS unreachability, cascading data center lockout | ⚡ Case Study | Dissecting BGP route flapping |
| 71 | [Outage Autopsy: MTU Black Holes](lessons/0071-outage-autopsy-mtu-black-holes.html) | Path MTU Discovery failure when ICMP is dropped; AWS AZ partition | ⚡ Case Study | Reproducing MTU black hole hang |
| 72 | [Final Master Challenge](lessons/0072-final-master-challenge.html) | Comprehensive cumulative networking architecture & diagnostic exam | ★ Master | End-to-end certification |
