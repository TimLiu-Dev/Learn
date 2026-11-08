# Computer Networking Resources

## Knowledge

### Textbooks and foundational courses
- [Book: _Computer Networking: A Top-Down Approach_ by Kurose & Ross — author site with lecture videos and slides](https://gaia.cs.umass.edu/kurose_ross/index.php)
  The standard university text; teaches from the application layer down, which matches how a programmer meets networks. Use for: layering, TCP internals, routing, the "why" behind every layer.
- [Kurose & Ross Wireshark Labs](https://gaia.cs.umass.edu/kurose_ross/wireshark.php)
  Free guided labs (HTTP, DNS, TCP, UDP, IP, ARP, DHCP, TLS). Use for: hands-on exercises that match the textbook chapters.
- [Kurose & Ross Interactive Problems](https://gaia.cs.umass.edu/kurose_ross/interactive/)
  Auto-generated end-of-chapter problems with worked solutions (delay, layering, subnetting, longest-prefix match, NAT, TCP, DNS…). Use for: extra spaced practice in review sessions.
- [Book: _Computer Networks: A Systems Approach_ by Peterson & Davie (free online)](https://book.systemsapproach.org/)
  Rigorous, open-licensed second opinion with more systems/implementation depth. Use for: congestion control, IP/routing details, QUIC.
- [Book: _High Performance Browser Networking_ by Ilya Grigorik (free online)](https://hpbn.co/)
  Performance-focused tour of TCP, UDP, TLS, HTTP/1.1, HTTP/2, WebSockets, from a web engineer at Google. Use for: latency, HTTP versions, TLS cost, WebSockets/SSE.
- [Book: _Routing TCP/IP, Volumes 1 & 2_ by Jeff Doyle (Cisco Press)](https://www.ciscopress.com/)
  The definitive bible of enterprise and ISP routing. Use for: OSPF link-state databases, BGP attributes, route reflection, and traffic engineering.
- [Beej's Guide to Network Programming](https://beej.us/guide/bgnet/)
  The classic sockets programming guide (C, but the concepts map 1:1 onto Java/Kotlin sockets). Use for: what socket calls actually do — bind, listen, accept, connect, send/recv.
- [Oracle Java Tutorial: All About Sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html)
  Official Java socket tutorial. Use for: `Socket`/`ServerSocket` API basics in the code lessons.

### Specifications (primary sources)
- [RFC 9293 — TCP](https://www.rfc-editor.org/rfc/rfc9293) — the current consolidated TCP spec. Use for: handshake, states, sequence numbers.
- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110) — methods, status codes, headers, shared by all HTTP versions.
- [RFC 9113 — HTTP/2](https://www.rfc-editor.org/rfc/rfc9113) · [RFC 9114 — HTTP/3](https://www.rfc-editor.org/rfc/rfc9114) · [RFC 9000 — QUIC](https://www.rfc-editor.org/rfc/rfc9000)
- [RFC 8446 — TLS 1.3](https://www.rfc-editor.org/rfc/rfc8446)
- Link/internet layer (Part 1): [RFC 791 — IPv4](https://www.rfc-editor.org/rfc/rfc791) · [RFC 792 — ICMP](https://www.rfc-editor.org/rfc/rfc792) · [RFC 826 — ARP](https://www.rfc-editor.org/rfc/rfc826) · [RFC 2131 — DHCP](https://www.rfc-editor.org/rfc/rfc2131) · [RFC 4632 — CIDR](https://www.rfc-editor.org/rfc/rfc4632) · [RFC 1918 — private addresses](https://www.rfc-editor.org/rfc/rfc1918) · [RFC 3022 — NAT](https://www.rfc-editor.org/rfc/rfc3022) · [RFC 8200 — IPv6](https://www.rfc-editor.org/rfc/rfc8200) · [RFC 4291 — IPv6 addressing](https://www.rfc-editor.org/rfc/rfc4291) · [RFC 4861 — NDP](https://www.rfc-editor.org/rfc/rfc4861) · [RFC 5952 — IPv6 text form](https://www.rfc-editor.org/rfc/rfc5952) · [RFC 1122 — host requirements](https://www.rfc-editor.org/rfc/rfc1122)
- Enterprise & ISP Routing (Part 4): [RFC 2328 — OSPF v2](https://www.rfc-editor.org/rfc/rfc2328) · [RFC 4271 — BGP-4](https://www.rfc-editor.org/rfc/rfc4271) · [RFC 7348 — VXLAN](https://www.rfc-editor.org/rfc/rfc7348) · [RFC 4301 — IPsec](https://www.rfc-editor.org/rfc/rfc4301)
- [RFC 6265 — Cookies](https://www.rfc-editor.org/rfc/rfc6265) · [RFC 6455 — WebSocket](https://www.rfc-editor.org/rfc/rfc6455)

### Cloud & Modern Infrastructure Documentation
- [AWS VPC Documentation & User Guide](https://docs.aws.amazon.com/vpc/latest/userguide/what-is-amazon-vpc.html)
  Authoritative reference for VPC CIDR blocks, route tables, Internet Gateways, NAT Gateways, Security Groups vs NACLs, and Transit Gateway architecture.
- [Kubernetes Official Networking Documentation](https://kubernetes.io/docs/concepts/services-networking/)
  Covers the IP-per-Pod network model, CNI plugin specifications, kube-proxy, Services (ClusterIP, NodePort, LoadBalancer), and Ingress controllers.
- [Cilium & eBPF Documentation](https://docs.cilium.io/)
  How modern Linux kernel eBPF replaces iptables for high-scale container routing, observability, and network security policies.

### Reference docs and explainers
- [MDN Web Docs: HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP)
  The best practitioner reference for HTTP, cookies, CORS, caching. Use for: everything in the web section.
- [The Illustrated TLS 1.3 Connection](https://tls13.xargs.org/) · [The Illustrated QUIC Connection](https://quic.xargs.org/)
  Every byte of a real handshake, annotated. Use for: TLS and HTTP/3 lessons.
- [Cloudflare Learning Center](https://www.cloudflare.com/learning/)
  Clear explainers on Anycast, BGP, DNS, CDNs, DDoS, and load balancing.
- [Wireshark User's Guide](https://www.wireshark.org/docs/wsug_html_chunked/)
  Official manual. Use for: capture filters, display filters, following streams.
- [Julia Evans (jvns.ca)](https://jvns.ca/) and [Zines: _Networking! ACK!_](https://wizardzines.com/zines/networking/)
  Short, tool-first explanations of networking concepts. Use for: intuition and debugging mindset.

### Interview preparation & Capstone
- [alex/what-happens-when (GitHub)](https://github.com/alex/what-happens-when)
  Deep answer to the classic "what happens when you type google.com" interview question. Use for: the capstone synthesis lesson.

## Wisdom (Communities)
- [Network Engineering Stack Exchange](https://networkengineering.stackexchange.com/)
  Moderated Q&A; strict about on-topic questions (enterprise networking, protocols). Use for: protocol-behaviour questions.
- [Server Fault](https://serverfault.com/)
  Q&A for sysadmin/ops problems. Use for: "my server/proxy/load balancer behaves weirdly" questions.
- [Wireshark Q&A](https://ask.wireshark.org/)
  Official Wireshark community. Use for: "what am I looking at in this capture?"
- [r/networking](https://www.reddit.com/r/networking/)
  Large, professional-network-engineer audience. Use for: reading how practitioners think.

## Gaps
- No Kotlin-first networking text found yet; using Java resources (the APIs are identical in Kotlin).
- Section numbers in Kurose & Ross (8th edition) may vary across reprints. Systems Approach and RFC citations were verified against official sources.
