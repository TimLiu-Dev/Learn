# Primary Resources &amp; References

## Core Networking Textbooks &amp; Roadmaps
- **Roadmap Benchmark:** [`roadmap.sh/network-engineer`](https://roadmap.sh/network-engineer)
- **Kurose &amp; Ross:** *Computer Networking: A Top-Down Approach* (Application-down foundation)
- **Richard Stevens / Kevin Fall:** *TCP/IP Illustrated, Vol. 1* (Protocol wire behavior)
- **Peterson &amp; Davie:** *Computer Networks: A Systems Approach* ([book.systemsapproach.org](https://book.systemsapproach.org/))
- **High Performance Browser Networking (Ilya Grigorik):** [hpbn.co](https://hpbn.co/) (Mobile radio, TCP/TLS, HTTP/2–3)

## Authoritative IETF RFCs &amp; IEEE Standards
- **IPv4 / ARP / DHCP / ICMP:** RFC 791, RFC 792, RFC 826, RFC 2131, RFC 3021 (`/31` P2P links)
- **Multicast:** RFC 1112 (Class D &amp; MAC mapping), RFC 3376 (IGMPv3), RFC 7761 (PIM-SM)
- **TCP / QUIC / HTTP:** RFC 9293 (TCP), RFC 9000 (QUIC), RFC 9110–9114 (HTTP Semantics, HTTP/1.1, HTTP/2, HTTP/3)
- **Mobile &amp; Edge:** RFC 8305 (Happy Eyeballs v2), RFC 6146/6147 (NAT64/DNS64)
- **Routing, FHRP &amp; Overlays:** RFC 2328 (OSPFv2), RFC 4271 (BGP-4), RFC 5880 (BFD), RFC 7938 (BGP in Data Centers), RFC 7348 (VXLAN), RFC 8365 (EVPN), RFC 8402 (Segment Routing), RFC 9568 (VRRP v3)
- **Security &amp; AAA:** RFC 8446 (TLS 1.3), IEEE 802.1X, RFC 8907 (TACACS+), RFC 8915 (NTS)
- **NetDevOps Modeling:** RFC 7950 (YANG 1.1), RFC 6241 (NETCONF), RFC 8040 (RESTCONF)

## Linux, Cloud, Kubernetes &amp; NetDevOps Tooling
- **Linux `iproute2` &amp; `nftables`:** `man 8 ip`, `man 8 ip-netns`, `man 8 ss`, [nftables Wiki](https://wiki.nftables.org/)
- **Virtual Labs &amp; NOS:** [Containerlab](https://containerlab.dev/), [FRRouting (FRR) Docs](https://docs.frrouting.org/)
- **Automation &amp; Verification:** [NAPALM](https://napalm.readthedocs.io/), [Nornir](https://nornir.readthedocs.io/), [Ansible Network Docs](https://docs.ansible.com/ansible/latest/network/), [Terraform Docs](https://developer.hashicorp.com/terraform), [Batfish](https://batfish.org/)
- **Cloud &amp; eBPF:** AWS VPC / Transit Gateway Docs, [Cilium eBPF &amp; XDP Reference](https://docs.cilium.io/), [Kubernetes Network Model](https://kubernetes.io/docs/concepts/services-networking/)
- **Engineering Blogs &amp; Zines:** Cloudflare Blog, Julia Evans Network Zines ([wizardzines.com](https://wizardzines.com/)), [Google SRE Book — Addressing Cascading Failures](https://sre.google/sre-book/addressing-cascading-failures/)
