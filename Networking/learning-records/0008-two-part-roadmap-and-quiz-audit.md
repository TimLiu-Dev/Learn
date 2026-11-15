# Learning Record 0008 — Two-Part 2027 Senior NetEng Roadmap Upgrade &amp; Quiz Audit

- **Date:** 2026-11-15
- **Benchmark:** [`roadmap.sh/network-engineer`](https://roadmap.sh/network-engineer)
- **Target Profile:** Transition from **Junior Android Developer** to **Senior Software Developer (Part 1)** and **Senior Network Engineer (Part 2)** in 2027.

## 1. Curriculum Restructuring (72 &rarr; 87 Core Sessions)
- **Part 1 — Senior Software Developer &amp; Systems Networking (41 Sessions + Capstone 1):**
  - Core Foundations, Link/Internet, Transport/Sockets, DNS/HTTP1–3/QUIC, TLS 1.3, gRPC, Client Resilience (`0001`–`0035`)
  - **Added `0073`:** Mobile, Android (`OkHttp`, Happy Eyeballs v2 RFC 8305, NAT64/DNS64, QUIC migration, safe pinning)
  - **Added `0074`:** Reverse Proxies, Request Smuggling (`CL.TE`) prevention, Rate Limiting &amp; Zero-502 `SIGTERM` Draining
  - **Upgraded `0069` (Capstone 1 Pt 2):** Expanded Java 25 HTTP/1.1 Server lab &amp; spec with `/api/*` reverse proxy forwarding (`X-Forwarded-For`), `/healthz`, and `SIGTERM` graceful draining
- **Part 2 — Senior Network Engineer, Cloud &amp; NetDevOps (46 Sessions + Capstone 2):**
  - **Promoted 6 Former Optional Sessions to Core NetEng (`★ Core NetEng`):** `0036` (Optics/LACP), `0039` (Inter-VLAN/SVIs), `0040` (STP/RSTP), `0041` (OSPF), `0048` (QoS), `0049` (SNMP/gNMI Telemetry) — zero optional badges remain.
  - **Added `0075`–`0076`:** Linux `iproute2`/`netns`/`nftables` &amp; Containerlab/FRR vs GNS3/EVE-NG
  - **Added `0077`–`0078`:** FHRP (HSRP/VRRP/GLBP + Anycast Gateway) &amp; IS-IS, BFD, 5-tuple ECMP polarization, Segment Routing
  - **Added `0079`:** MP-BGP EVPN (Type-2/3/5) &amp; Clos Spine-Leaf Fabrics (paired right after `0047` VXLAN)
  - **Added `0080`–`0081`:** Campus Access Security (802.1X, RADIUS/TACACS+, DHCP Snooping/DAI, NTP/NTS, ZTNA) &amp; IP Multicast (Class D, IGMP Snooping, PIM-SM RPF)
  - **Added `0082`–`0085`:** NetDevOps (YANG/OpenConfig, NETCONF `confirmed-commit`, RESTCONF, gNMI), Python (Netmiko/NAPALM/Nornir/TextFSM), Ansible Network Automation, Terraform IaC + Batfish/pyATS GitOps
  - **Added `0086`:** Outage Autopsy 3 (Automation Blast Radius, Retry Storms &amp; Canary Gates)
  - **Added `0087`:** **Capstone 2** — Automated Multi-Site Hybrid Spine-Leaf (EVPN-VXLAN + BFD + IPsec) &amp; Cloud VPC/TGW Fabric

## 2. Reference Sheet &amp; Integrity Fixes
- Created `reference/netdevops-and-automation.html` and linked into `index.html`.
- Fixed 129 quiz `data-why` mismatches/scrambles across 49 legacy lessons, eliminated raw `$...$` LaTeX and stray markdown backticks in HTML prose/attributes, fixed broken `<li>` in `0009`, and fixed misplaced quiz headings in `0050`/`0065`.
- Re-linked all 87 `prev`/`next` `<nav class="lesson-nav">` bars cleanly in exact 2-part sequence.
