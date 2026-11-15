# Course Curriculum — Two-Part Roadmap (87 Core Sessions)

Designed to take a **Junior Android Developer** to **Senior Software Developer (Part 1)** and **Senior Network Engineer (Part 2)** in **2027**, benchmarked against `roadmap.sh/network-engineer`.

**Key Upgrade:** Zero lessons are marked optional. Previous optional tags on `0036`, `0039`, `0040`, `0041`, `0048`, and `0049` have been promoted to **Core NetEng** in Part 2, paired with 15 new sessions (`0073`–`0087`) covering Mobile/Android edge, L7 reverse proxies, Linux `iproute2`/`netns`/`nftables`, Containerlab/FRR, FHRP, IS-IS/BFD/ECMP, MP-BGP EVPN, 802.1X/AAA, Multicast, YANG/NETCONF/gNMI, Python/Ansible/Terraform NetDevOps, and Capstone 2.

---

## PART 1: Senior Software Developer &amp; Systems Networking (41 Sessions)

### Module 1A: Foundations (Sessions 0001–0003)
| # | Session | Core Focus |
|---|---|---|
| 0001 | [Packets, Delay &amp; Queuing](lessons/0001-packets-and-delay.html) | Transmission vs propagation delay, queuing, bandwidth-delay product |
| 0002 | [Layers &amp; Encapsulation](lessons/0002-layers-and-encapsulation.html) | 5-layer TCP/IP stack, L2 frame / L3 packet / L4 segment |
| 0003 | [Review: Foundations](lessons/0003-review-foundations.html) | End-to-end RTT, delay composition, layer isolation |

### Module 1B: Link &amp; Internet Layers (Sessions 0004–0012)
| # | Session | Core Focus |
|---|---|---|
| 0004 | [MAC Addresses &amp; Ethernet Frames](lessons/0004-mac-addresses-and-frames.html) | OUI, NIC frames, L2 CAM switch learning &amp; flooding |
| 0005 | [IPv4 Packets, TTL &amp; ICMP](lessons/0005-ipv4-and-icmp.html) | IPv4 header, fragmentation flags, TTL, traceroute internals |
| 0006 | [Subnets &amp; CIDR](lessons/0006-subnets-and-cidr.html) | Bitmasks, VLSM, longest-prefix match (LPM) |
| 0007 | [ARP &amp; Local Neighbor Resolution](lessons/0007-arp.html) | Broadcast ARP request, unicast reply, ARP poisoning mechanics |
| 0008 | [DHCP Lease Lifecycle](lessons/0008-dhcp.html) | DORA flow, relay agents (Option 82), lease renewal &amp; APIPA |
| 0009 | [IP Routing &amp; Forwarding](lessons/0009-routing.html) | Next-hop vs direct delivery, FIB lookup, asymmetric paths |
| 0010 | [NAT &amp; PAT](lessons/0010-nat.html) | NAPT state tables, CG-NAT, STUN/TURN traversal |
| 0011 | [IPv6 Addressing &amp; NDP](lessons/0011-ipv6.html) | 128-bit prefixes, SLAAC, EUI-64, Neighbor Discovery &amp; ICMPv6 |
| 0012 | [Review: Link &amp; Internet](lessons/0012-review-link-and-internet.html) | Full hop-by-hop MAC/IP rewrite trace |

### Module 2: Transport &amp; Sockets (Sessions 0013–0020)
| # | Session | Core Focus |
|---|---|---|
| 0013 | [Ports, Multiplexing &amp; Sockets](lessons/0013-ports-and-sockets.html) | 4-tuple demux, ephemeral ports, `TIME_WAIT` &amp; `CLOSE_WAIT` |
| 0014 | [UDP &amp; Connectionless Apps](lessons/0014-udp.html) | Stateless 8B header, game/VoIP/DNS tradeoffs |
| 0015 | [TCP Connections &amp; State Machine](lessons/0015-tcp-connections.html) | 3-way handshake, ISNs, FIN vs RST, listen queues |
| 0016 | [TCP Reliability &amp; Retransmission](lessons/0016-tcp-reliability.html) | RTO/SRTT, SACK, fast retransmit (3 DupACKs) |
| 0017 | [TCP Flow &amp; Congestion Control](lessons/0017-tcp-flow-and-congestion.html) | rwnd vs cwnd, CUBIC, BBR pacing, ECN |
| 0018 | [TCP Dilemmas in Production](lessons/0018-tcp-dilemmas.html) | Nagle vs Delayed ACK, HoL blocking, bufferbloat |
| 0019 | [TCP Sockets in Code](lessons/0019-tcp-sockets-in-code.html) | Java `Socket`/`ServerSocket`, framing, timeouts, virtual threads |
| 0020 | [Review: Transport Layer](lessons/0020-review-transport.html) | Packet trace &amp; congestion diagnostics |

### Module 3A: DNS &amp; HTTP/Web Protocols (Sessions 0021–0029)
| # | Session | Core Focus |
|---|---|---|
| 0021 | [DNS Architecture](lessons/0021-dns-architecture.html) | Root/TLD/Auth hierarchy, CNAME/A/AAAA/MX/SRV, TTLs |
| 0022 | [DNS in Practice &amp; Security](lessons/0022-dns-in-practice.html) | `dig` debugging, DNSSEC, DoH/DoT |
| 0023 | [HTTP/1.1 Anatomy](lessons/0023-http1-anatomy.html) | Methods, framing (`Content-Length`/chunked), Keep-Alive |
| 0024 | [HTTP Semantics &amp; Caching](lessons/0024-http-semantics.html) | Status codes, `ETag`, `Cache-Control`, idempotency |
| 0025 | [Cookies &amp; Session State](lessons/0025-cookies-and-state.html) | `HttpOnly`, `Secure`, `SameSite`, CSRF tokens |
| 0026 | [CORS &amp; Same-Origin Policy](lessons/0026-cors-and-sop.html) | Preflight `OPTIONS`, credentials, origin enforcement |
| 0027 | [HTTP/2 Binary Multiplexing](lessons/0027-http2.html) | Streams, HPACK compression, server push demise, TCP HoL limit |
| 0028 | [HTTP/3 &amp; QUIC](lessons/0028-http3-and-quic.html) | UDP transport, 0-RTT/1-RTT encrypted handshake, independent streams |
| 0029 | [Realtime Web: WebSockets &amp; SSE](lessons/0029-realtime-web.html) | Long polling vs SSE vs WebSockets upgrade |

### Module 3B: TLS, RPC &amp; Developer Resilience (Sessions 0030–0035)
| # | Session | Core Focus |
|---|---|---|
| 0030 | [Cryptographic Foundations](lessons/0030-crypto-foundations.html) | AEAD (AES-GCM/ChaCha20), ECDHE PFS, SHA-256 &amp; HMAC |
| 0031 | [PKI &amp; X.509 Certificates](lessons/0031-pki-and-certificates.html) | CA chains, SAN verification, ACME, OCSP stapling |
| 0032 | [TLS 1.3 Handshake](lessons/0032-tls13-handshake.html) | 1-RTT encrypted handshake, PSK/0-RTT replay risk |
| 0033 | [gRPC &amp; Binary RPC](lessons/0033-grpc-and-rpc.html) | Protobuf over HTTP/2, streaming modes, deadline propagation |
| 0034 | [Client Resilience Engineering](lessons/0034-client-resilience.html) | Connect/read timeouts, exponential backoff + jitter, circuit breakers |
| 0035 | [Review: Developer Track](lessons/0035-review-developer-track.html) | Full HTTPS/RPC latency &amp; failure diagnosis |

### Module 3C: Senior Dev Edge, Playbook &amp; Capstone 1 (Sessions 0073–0074, 0066–0069)
| # | Session | Core Focus |
|---|---|---|
| 0073 | [Mobile, Android &amp; Edge Networking](lessons/0073-mobile-and-edge-networking.html) | Happy Eyeballs v2 (RFC 8305), OkHttp pool, QUIC migration, NAT64/DNS64, cert pinning |
| 0074 | [Reverse Proxies &amp; Graceful Draining](lessons/0074-reverse-proxies-and-graceful-draining.html) | `X-Forwarded-For` security, CL.TE request smuggling, rate limiting, `SIGTERM` draining |
| 0066 | [The Classic URL Question](lessons/0066-the-url-question.html) | Every packet/handshake from pressing Enter to pixels |
| 0067 | [Network Diagnostic Playbook](lessons/0067-network-diagnostic-playbook.html) | Structured L1–L7 isolation methodology |
| 0068 | [Capstone 1 Pt 1: Raw-Socket HTTP/1.1 Server](lessons/0068-project-raw-socket-http-server-1.html) | Byte-level HTTP/1.1 parser, framing &amp; status engine |
| 0069 | [Capstone 1 Pt 2: Concurrency, Reverse Proxy &amp; Drain](lessons/0069-project-raw-socket-http-server-2.html) | Thread pools, `/api/*` L7 forwarding + `XFF`, `/healthz` + `SIGTERM` draining |

---

## PART 2: Senior Network Engineer, Cloud &amp; NetDevOps (46 Sessions)

### Module 4A: Linux Stack &amp; Lab Foundation (Sessions 0075–0076, 0036)
| # | Session | Core Focus |
|---|---|---|
| 0075 | [Linux Networking: `iproute2`, `netns` &amp; `nftables`](lessons/0075-linux-networking-and-iproute2.html) | Network namespaces, `veth` pairs, Linux bridges, VRFs, policy routing, `ss`, `nft` |
| 0076 | [Network Lab Emulation: Containerlab &amp; FRR](lessons/0076-network-lab-emulation-containerlab.html) | Containerlab YAML, FRRouting (`zebra`/`vtysh`), GNS3 &amp; EVE-NG comparison |
| 0036 | [Physical Media, Optics &amp; LACP ★ Core NetEng](lessons/0036-physical-media-and-optics.html) | SMF/MMF optics, dBm loss budgets, SFP28/QSFP28, 802.3ad LACP bundling |

### Module 4B: Enterprise Switching, IGP &amp; FHRP (Sessions 0037–0041, 0077–0078)
| # | Session | Core Focus |
|---|---|---|
| 0037 | [Enterprise Switching Internals](lessons/0037-enterprise-switching.html) | Cut-through vs store-and-forward ASICs, TCAM, broadcast storms |
| 0038 | [VLANs &amp; IEEE 802.1Q Trunking](lessons/0038-vlans-and-8021q.html) | 4B tags, native VLAN traps, double-tagging defense |
| 0039 | [Inter-VLAN Routing &amp; SVIs ★ Core NetEng](lessons/0039-inter-vlan-routing.html) | Router-on-a-stick vs L3 Switch SVIs |
| 0040 | [Spanning Tree Protocol (STP/RSTP) ★ Core NetEng](lessons/0040-spanning-tree-protocol.html) | Root election, BPDUs, PortFast, BPDU Guard |
| 0041 | [OSPF Routing Protocol ★ Core NetEng](lessons/0041-ospf-routing.html) | Dijkstra SPF, LSAs, areas, DR/BDR election |
| 0077 | [FHRP (HSRP, VRRP) &amp; Anycast Gateway](lessons/0077-fhrp-hsrp-vrrp-and-anycast-gw.html) | Virtual MACs + GARP, STP Root alignment, EVPN Anycast GW |
| 0078 | [IS-IS, BFD, ECMP &amp; Segment Routing](lessons/0078-isis-bfd-and-ecmp-resilience.html) | IS-IS L2/TLVs, BFD sub-second failover, 5-tuple ECMP polarization, SR-MPLS/SRv6 |

### Module 4C: BGP, Overlays &amp; Data Center Fabrics (Sessions 0042–0044, 0047, 0079)
| # | Session | Core Focus |
|---|---|---|
| 0042 | [BGP Fundamentals](lessons/0042-bgp-fundamentals.html) | ASNs, eBGP vs iBGP split-horizon, peering &amp; Route Reflectors |
| 0043 | [BGP Path Selection &amp; Traffic Engineering](lessons/0043-bgp-attributes-and-peering.html) | Local-Pref, AS-Path prepending, MED, Communities |
| 0044 | [Routing Security &amp; RPKI](lessons/0044-routing-security-and-rpki.html) | BGP hijacks, route leaks, RPKI ROAs &amp; Route Origin Validation |
| 0047 | [Overlay Encapsulation: VXLAN](lessons/0047-overlay-encapsulation-vxlan.html) | MAC-in-UDP 4789, VTEPs, VNI scaling &amp; jumbo MTU headroom |
| 0079 | [MP-BGP EVPN &amp; Spine-Leaf Clos Fabrics](lessons/0079-bgp-evpn-and-spine-leaf-fabrics.html) | Clos ECMP, EVPN Type-2/3/5 routes, ARP suppression, Symmetric IRB |

### Module 4D: Firewalls, VPNs, QoS, Telemetry, Campus Security &amp; Multicast (Sessions 0045–0046, 0048–0049, 0080–0081, 0050)
| # | Session | Core Focus |
|---|---|---|
| 0045 | [Perimeter Firewalls &amp; Stateful ACLs](lessons/0045-perimeter-firewalls.html) | Conntrack state, DMZ zones, asymmetric drop pathology |
| 0046 | [Enterprise VPNs: IPsec &amp; WireGuard](lessons/0046-enterprise-vpns.html) | IKEv2/ESP transport vs tunnel, WireGuard Cryptokey Routing, MSS clamping |
| 0048 | [Quality of Service (QoS) ★ Core NetEng](lessons/0048-quality-of-service.html) | DSCP markings, shaping vs policing, Priority Queuing + WFQ |
| 0049 | [Network Telemetry (SNMP &amp; gNMI) ★ Core NetEng](lessons/0049-network-telemetry.html) | SNMPv3 vs streaming gNMI, NetFlow/IPFIX, active synthetic probing |
| 0080 | [802.1X, AAA (RADIUS/TACACS+) &amp; ZTNA](lessons/0080-campus-access-security-8021x-aaa.html) | EAPOL port NAC, TACACS+ RBAC, DHCP Snooping + DAI, NTP/NTS, ZTNA/SASE |
| 0081 | [IP Multicast Essentials](lessons/0081-ip-multicast-essentials.html) | Class D `224.0.0.0/4`, 32:1 MAC aliasing, IGMP Snooping, PIM-SM &amp; RPF |
| 0050 | [Review: Core Network Engineering](lessons/0050-review-network-engineering.html) | Enterprise L1–L4 switching, routing &amp; security integration |

### Module 5: Cloud, Kubernetes, eBPF &amp; Edge Infrastructure (Sessions 0051–0065)
| # | Session | Core Focus |
|---|---|---|
| 0051 | [SDN &amp; Control/Data Plane Split](lessons/0051-sdn-fundamentals.html) | Programmable controllers, OpenFlow concepts, intent APIs |
| 0052 | [Cloud VPC Architecture](lessons/0052-cloud-vpc-architecture.html) | Subnets, implicit VPC router, AZs, route tables |
| 0053 | [Cloud Egress &amp; Gateways](lessons/0053-cloud-egress-and-gateways.html) | IGW vs NAT Gateway vs Egress-Only IGW, NAT hairpinning |
| 0054 | [Security Groups &amp; Network ACLs](lessons/0054-security-groups-and-nacls.html) | Stateful SGs vs stateless NACLs + ephemeral return ranges |
| 0055 | [VPC Peering &amp; Transit Gateways](lessons/0055-vpc-interconnects-and-tgw.html) | Non-transitive peering mesh vs hub-and-spoke TGW |
| 0056 | [PrivateLink &amp; VPC Endpoints](lessons/0056-privatelink-and-vpc-endpoints.html) | Private ENIs solving overlapping CIDRs cleanly |
| 0057 | [Hybrid Cloud Interconnects](lessons/0057-hybrid-cloud-interconnect.html) | Direct Connect / Interconnect + BGP failover over IPsec |
| 0058 | [Anycast &amp; Global CDNs](lessons/0058-anycast-and-cdns.html) | Anycast BGP POP routing, CDN cache hit mechanics |
| 0059 | [Cloud Load Balancing: NLB vs ALB](lessons/0059-cloud-load-balancing-alb-nlb.html) | L4 DSR/Maglev vs L7 proxy termination &amp; health checks |
| 0060 | [Kubernetes Network Model](lessons/0060-kubernetes-network-model.html) | Flat Pod IPs, Pod-to-Pod without NAT, pause netns |
| 0061 | [Kubernetes CNI Plugins](lessons/0061-kubernetes-cni-plugins.html) | Overlay (VXLAN/Geneve) vs Native VPC ENI underlays |
| 0062 | [Kubernetes Services &amp; kube-proxy](lessons/0062-kubernetes-services-and-kubeproxy.html) | ClusterIP DNAT, iptables O(N) vs IPVS O(1) vs Ingress/Gateway API |
| 0063 | [eBPF Networking &amp; Cilium](lessons/0063-ebpf-networking-and-cilium.html) | XDP DDoS drop, socket bypass, kube-proxy replacement |
| 0064 | [Service Mesh &amp; Envoy](lessons/0064-service-mesh-and-envoy.html) | Sidecar mTLS/SPIFFE, sidecar tax vs ambient/eBPF |
| 0065 | [Review: Cloud &amp; Modern Infra](lessons/0065-review-cloud-and-modern-infra.html) | End-to-end VPC + K8s + Cilium debugging |

### Module 6A: NetDevOps, Automation &amp; GitOps (Sessions 0082–0085)
| # | Session | Core Focus |
|---|---|---|
| 0082 | [NetDevOps: YANG, NETCONF, RESTCONF &amp; gNMI](lessons/0082-netdevops-yang-netconf-restconf.html) | Typed schemas, OpenConfig, atomic `confirmed-commit` rollback |
| 0083 | [Python Network Automation](lessons/0083-python-network-automation.html) | Netmiko, NAPALM replace vs merge diffs, Nornir concurrency, TextFSM |
| 0084 | [Ansible Network Automation](lessons/0084-ansible-network-automation.html) | Control-node modules, `group_vars`/`host_vars`, Jinja2, `--check --diff` |
| 0085 | [Terraform Networking IaC &amp; GitOps](lessons/0085-terraform-and-gitops-networking.html) | Remote state locking, `prevent_destroy` guards, Batfish/pyATS CI validation |

### Module 6B: Outage Autopsies, Capstone 2 &amp; Final Exam (Sessions 0070–0071, 0086–0087, 0072)
| # | Session | Core Focus |
|---|---|---|
| 0070 | [Outage 1: Facebook Oct 2021 (BGP + DNS)](lessons/0070-outage-autopsy-facebook-2021.html) | Backbone withdrawal, authoritative DNS invisibility, OOB lockouts |
| 0071 | [Outage 2: Path MTU Black Holes](lessons/0071-outage-autopsy-mtu-black-holes.html) | ICMP Fragmentation Needed drops &amp; TLS handshake hang pathology |
| 0086 | [Outage 3: Automation Blast Radius &amp; DNS/Config Cascade](lessons/0086-outage-autopsy-automation-and-dnssec.html) | Zero-canary fleet blasts, retry storms, OOB separation &amp; circuit breakers |
| 0087 | [Capstone 2 (Part 2): Automated Multi-Site Hybrid Fabric](lessons/0087-capstone-hybrid-network-fabric.html) | Containerlab Clos EVPN-VXLAN + BFD + IPsec symmetry + Terraform TGW + chaos failover |
| 0072 | [Final Master Challenge](lessons/0072-final-master-challenge.html) | Comprehensive multi-layer architecture &amp; debugging exam |
