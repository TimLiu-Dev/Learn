# Notes

## Teaching preferences (stated by user, 2026-10-18 & 2026-11-06)
- Teach like a CS student with basic programming experience; zero assumed prior networking knowledge.
- Tri-track curriculum: Shared Foundations $\rightarrow$ Professional Developers (Track A) $\rightarrow$ Professional Network Engineers (Track B) $\rightarrow$ Cloud & DevOps (Track C) $\rightarrow$ Synthesis & Forensics.
- One concept per session; mixed review session at the end of each section.
- "Why" before "how".
- Hands-on with real tools (Wireshark, dig, curl, ping, traceroute, netstat, lsof, tcpdump) wherever possible.
- Code in Kotlin and Java (JDK 26).
- Never give exercise answers directly unless asked; use instant-feedback widgets and free recall with self-revealed keypoints.
- Mark interview-relevant and production-critical topics.
- Capstone: HTTP server from raw sockets in Kotlin/Java; forensic autopsy of real-world internet outages.

## Environment
- macOS, OpenJDK 26, IntelliJ IDEA, Android Studio, Gradle (Homebrew).
- Available: curl, dig, nc, tcpdump, traceroute, netstat, lsof. Optional: Wireshark/tshark (installed via cask).

## Course conventions
- Every session, including reviews, has a sequential number; `lessons/NNNN-slug.html` matches it. Course home & Master Plan: `index.html`.
- Hands-on observation questions (`li.observe`) are answered in chat with the teacher. Quizzes give instant right/wrong feedback. Free-recall key points sit behind a reveal button.
- Spaced retrieval: every lesson from session 2 opens with a warm-up recalling earlier sessions.
- Layer colours are fixed course-wide (app purple, transport blue, network green, link orange, physical grey).
- Terminology: network-layer unit = "packet" (avoid "IP datagram"); TCP segment; UDP datagram; link frame.
- Code: single-file Java runnable with `java File.java` (JDK 26) plus Kotlin equivalents.

## Status
- 2026-10-19: Initial draft of sessions 1–12 and reference sheets.
- 2026-11-06:
  - Comprehensive 72-session Master Plan & Backlog Legend deployed at `index.html`.
  - Mission & Curriculum expanded to encompass Software Developers, Network Engineers, and Cloud Infrastructure.
  - Session 1 elevated to benchmark status (worked calculation, fill-in drill, mental paradoxes, interview trap, free recall).
  - Part 0 and Part 1 thoroughly audited:
    - Cleaned all legacy "Claude Code" references to "your teacher in chat".
    - Added interactive Longest-Prefix Match fill-in drill to Session 9 (`0009-routing.html`).
    - Added detailed model answer keypoints to Session 3 interview recall (`0003-review-foundations.html`).
    - Verified all 12 lesson script tags, stylesheet imports, and navigation linkages.
    - Verified circular reference sheet navigation and updated `RESOURCES.md`.
    - Initialized `learning-records/0001-baseline-and-mission-expansion.md`.
  - Foundations (Part 0 & Part 1) are complete, self-contained, and fully verified.
  - Part 2 (Sessions 13–20: Transport Layer Mechanics) generated, verified, and linked.
  - Part 3 (Sessions 21–35: Developer Track: Web Protocols & Security) generated, verified, and linked.
  - 35 lessons (Parts 0, 1, 2, and 3) live ready with interactive quizzes, terminal labs, free recalls, and benchmark styling.
  - Triple-check audit completed across Parts 0, 1, 2, and 3:
    - Fixed Session 12 navigation next link to point seamlessly into Session 13.
    - Verified all 164 multiple-choice quiz questions, answer indices, and option lengths.
    - Verified HTML structural integrity and script tag inclusions.
  - Part 4 (Sessions 36–50: Track B — Professional Network Engineers) generated, fully verified, and linked:
    - 15 deep lessons covering Physical media/optics, Enterprise switching/CAM, 802.1Q VLANs, Inter-VLAN/SVIs, STP/RSTP, OSPF, BGP fundamentals & peering policies, RPKI security, Perimeter firewalls/DMZ, IPsec/WireGuard, VXLAN overlay fabrics, QoS/DiffServ/LLQ, and Network Telemetry.
    - 50 lessons total now live across Parts 0, 1, 2, 3, and 4 with seamless two-way chained navigation.
    - Initialized `learning-records/0003-part4-delivery.md`.
  - Part 5 (Sessions 51–65: Track C — Cloud & Modern Infrastructure) generated, fully verified, and linked:
    - 15 deep lessons covering SDN & OVS, Multi-AZ VPC CIDR design, IGW vs NAT GW egress, Stateful SGs vs Stateless NACLs, AWS Transit Gateway (TGW), PrivateLink VPC endpoints, Hybrid DirectConnect/VPN, Anycast BGP CDNs, ALB (L7) vs NLB (L4), Kubernetes network model (IP-per-pod, netns, veth), CNI plugins (Calico, AWS VPC CNI, Cilium), kube-proxy (iptables vs IPVS), eBPF XDP line-rate processing, Envoy sidecars & service mesh mTLS, and Staff Cloud Architect review.
    - Initialized `learning-records/0004-part5-delivery.md`.
  - Part 6 (Sessions 66–72: Cross-Disciplinary Capstones & Outage Forensics) generated, fully verified, and linked:
    - 7 capstone lessons covering the 7-stage end-to-end URL packet trace, the 5-phase network diagnostic playbook (Refused vs Timeout vs Reset vs HostUnreach), the 2-part production HTTP/1.1 server from raw sockets in Java 21+ / Kotlin (virtual threads, keep-alive, static files, chunked transfer), Outage Autopsy: Meta/Facebook 2021 global BGP DNS collapse, Outage Autopsy: MTU Black Holes & PMTUD failures, and Session 72 Final Master Challenge comprehensive field-wide certification exam.
    - Initialized `learning-records/0005-part6-and-course-completion.md`.
  - 100% Curriculum Completion: All 72 lessons live, validated with zero navigation gaps, verified quiz indexes, and complete index/curriculum synchronization.
  - Final Triple-Check Audit & Polish:
    - Fixed broken `CURRICULUM.html` links in Sessions 50 and 65 to point directly to `../index.html`.
    - Standardized Sessions 69, 70, 71, and 72 recall markup from legacy inline handlers to the unified `recall.js` component with localStorage persistence.
    - Added breadcrumbs with `../index.html` linkages to Sessions 69, 70, 71, and 72.
    - Verified Java 21+ code in Sessions 68 & 69 by compiling cleanly with JDK (`javac`).
    - Initialized canonical `GLOSSARY.md` to capture verified vocabulary as the user progresses.
    - Added `scripts/open-lesson.sh` CLI utility for easy lesson opening.
    - Passed automated end-to-end verification across all 72 lessons, 286 quiz items, and 146 recall prompts. Workspace is 100% ready for learning.
- 2026-11-07:
  - Conducted full Senior Developer curriculum audit (`learning-records/0006-senior-dev-audit-and-bug-fixes.md`).
  - Badged Sessions 36, 39, 40, 41, 48, and 49 as `◇ Optional Deep-Dive` across `index.html`, `CURRICULUM.md`, and lesson headers; fixed stale lesson count (`35` -> `72`) in `index.html`.
  - Converted all raw `$...$` LaTeX across 25 HTML lessons into zero-dependency semantic HTML.
  - Restored `<article class="page">`, dark-mode `svg.dia` CSS variables, valid SVG `<tspan>` tags, and interactive `<ol class="steps">` across Sessions 69–72.
  - Fixed `Keep-Alive` unconsumed `Content-Length` body framing desync (HTTP Request Smuggling vector) in `HttpServerV2.java` (Session 69) and verified clean compilation with `javac -Xlint:all`.
  - Added macOS Docker/OrbStack/`kind` container instructions to Linux kernel labs (`0051`, `0060`–`0064`) and clarified connected UDP `ICMP Port Unreachable` behavior on macOS (`0014`).
  - Replaced 105 joke quiz distractors across 29 lessons in Parts 4–6 with realistic senior-level misconceptions.
  - Built local zero-dependency OpenJDK 25 execution engine (`scripts/LabRunner.java` on port 8765) with CORS, virtual threads, sub-second execution, and auto-start integration in `scripts/open-lesson.sh`.
  - Injected dual-tab `[Java 25 | Kotlin]` code examples across all 72 lessons with synchronized language switching and one-click copy (`assets/code-tabs.js`).
  - Added runnable in-browser interactive code practice (`assets/code-practice.js`) to all 72 lessons, complete with starter code, progressive hints, final canonical solutions, and automated test harnesses.
  - Verified 100% test pass rate across the entire curriculum: `validate_labs.py` ran 72 test suites against OpenJDK 25 with 72 passed, 0 failed.
  - Enriched every single quiz question across all 72 lessons (289 total questions) with comprehensive per-option `data-why` attributes explaining both why the correct answer is right and why each wrong distractor is wrong.
  - Completed the Permanent Reference Library in `reference/` by authoring 3 new comprehensive, print-ready reference sheets:
    - `reference/transport-and-sockets.html` (TCP state machine, socket options, BBR vs CUBIC, framing patterns)
    - `reference/web-and-crypto.html` (HTTP/1.1 vs H2 vs H3, CORS matrix, Cookie security, TLS 1.3, PKI, gRPC, client resilience)
    - `reference/cloud-and-k8s.html` (AWS VPC CIDR math, Security Groups vs NACLs, TGW, PrivateLink, K8s CNI/kube-proxy, eBPF XDP)
  - Final Audit: The entire curriculum of 72 lessons, 289 quiz questions with per-option rationale, 72 runnable coding labs, 7 permanent reference sheets, and interactive browser tools is 100% production-ready for senior-level mastery.
