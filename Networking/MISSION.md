# Learning Mission — From Junior Android Dev to Senior Network Engineer (2027)

## Target Outcome
Transition systematically from **Junior Android Developer** to **Senior Software Developer &amp; Senior Network Engineer in 2027** across a two-part, 89-session interactive curriculum calibrated against [`roadmap.sh/network-engineer`](https://roadmap.sh/network-engineer) and modern production systems:

1. **Part 1 — Senior Software Developer &amp; Systems Networking (Sessions 0001–0035, 0073–0074, 0066–0069 · 41 sessions):**
   - Packet physics, TCP/IP, CIDR, ARP, DHCP, routing, NAT, IPv6, TCP sockets &amp; congestion control, DNS, HTTP/1.1–HTTP/3, TLS 1.3, gRPC, client resilience, mobile/Android edge networking (`OkHttp`, Happy Eyeballs RFC 8305, QUIC migration, cert pinning), and L7 reverse proxies (`X-Forwarded-For`, anti-smuggling, `SIGTERM` draining).
   - **Capstone 1 (Sessions 0068–0069):** Raw-Socket HTTP/1.1 Server, Threading, Reverse Proxy Forwarding (`/api/*`), `/healthz`, and Zero-502 Graceful Draining in Java 25.

2. **Part 2 — Senior Network Engineer, Cloud &amp; NetDevOps (Sessions 0075–0089, 0036–0065, 0070–0072 · 48 sessions):**
   - Linux `iproute2`/`netns`/`nftables`, Containerlab + FRRouting, optics/LACP, VLANs/STP/RSTP, FHRP/Anycast GW, OSPF, IS-IS/BFD/ECMP/Segment Routing, BGP + RPKI, VXLAN + MP-BGP EVPN Clos fabrics, stateful/NGFW firewalls &amp; IDS/IPS, IPsec/WireGuard &amp; SD-WAN, QoS, SNMP/gNMI telemetry, 802.1X/RADIUS/TACACS+/DAI, Enterprise Wireless (802.11ax/be, WPA3, 802.11k/v/r), IP Multicast, Cloud VPC/TGW/K8s/Cilium eBPF, YANG/NETCONF/RESTCONF/gNMI, Python (Netmiko/NAPALM/Nornir), Ansible, Terraform/Batfish GitOps, and 3 Real-World Outage Autopsies.
   - **Capstone 2 (Session 0087) + Final Exam (Session 0072):** Automated Multi-Site Hybrid Spine-Leaf (EVPN-VXLAN + BFD + IPsec) &amp; Cloud VPC/TGW Fabric with chaos failover verification.

## Learner Profile &amp; Environment
- **Background:** Junior Android / JVM developer comfortable with Kotlin, Java, HTTP clients, and REST APIs; building deep L1–L7 hardware, RF, OS kernel, routing, and automation mastery.
- **OS &amp; Toolchain:** macOS (Apple Silicon) with OpenJDK 25 (`javac -Xlint:all`), Kotlin/Android concepts, Docker/OrbStack (for Linux `netns` &amp; Containerlab/FRR labs), `curl`, `dig`, `nc`, `tcpdump`, `traceroute`, `lsof`, `netstat`, `openssl`, `Wireshark`.
- **Method:** Interactive browser course (`index.html`), hands-on macOS CLI diagnostics, Java 25 verified coding labs (`scripts/validate_labs.py`), zero optional skipped fundamentals—all 89 lessons are core required.
