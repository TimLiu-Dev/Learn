# Mission: Computer Networking

## Why
Understand how data moves between computers well enough to build, debug, and reason about networked software — and to answer networking questions confidently in software engineering interviews.

## Success looks like
- Explain "what happens when I type a URL and press Enter" from DNS lookup down to packets on the wire, and back up to the rendered response
- Open a Wireshark capture and point out a TCP handshake, a DNS query, a TLS handshake, and an HTTP request
- Diagnose a broken connection (DNS failure vs. refused vs. timeout vs. reset vs. TLS error) using ping, traceroute, dig, curl, and netstat/lsof
- Write a TCP and UDP client/server in Kotlin or Java and explain what each socket call does on the network
- Build a working HTTP/1.1 server from raw sockets, and explain what a web framework does on top of that
- Choose between TCP/UDP, HTTP/1.1/2/3, polling/WebSockets, L4/L7 load balancing — and justify the choice

## Constraints
- CS-student level with basic programming experience
- Code in Kotlin and Java; working on macOS (JDK 26, IntelliJ, curl, dig, tcpdump, traceroute available; Wireshark not yet installed)
- One concept per session; a mixed review session at the end of each section
- Hands-on with real tools wherever possible; "why" before "how"
- No exercise answers unless asked

## Out of scope (proposed — confirm)
- Network-engineer operations: configuring routers/switches, CCNA-style material, BGP/OSPF internals beyond the concept
- Physical layer and radio details (signal encoding, Wi-Fi physics)
- Email protocols (SMTP/IMAP), FTP, and other legacy application protocols
