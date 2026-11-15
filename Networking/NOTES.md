# Learning Notes &amp; Conventions

## Environment (macOS)
- **Java / Kotlin Toolchain:** OpenJDK 25 (`/opt/homebrew/opt/openjdk@25/bin/javac -Xlint:all`), IntelliJ / Android Studio / Gradle. All interactive browser labs compile locally via `LabRunner.java` (port 8765) and are batch-verified with `python3 scripts/validate_labs.py`.
- **Linux &amp; Emulation (Part 2):** Docker / OrbStack on Apple Silicon for `iproute2`, `ip netns`, `nftables`, and Containerlab + FRRouting (`quay.io/frrouting/frr`).
- **Diagnostic CLI Stack:** `curl`, `dig`, `nc`, `tcpdump`, `traceroute`, `netstat`, `lsof`, `openssl`, `arp`, `ndp`, `Wireshark`.

## Teaching Conventions (Immutable Course Style)
1. **Why Before How:** Every concept opens with the concrete operational failure or real-world dilemma that forced the protocol/tool into existence.
2. **Layer Terminological Discipline:**
   - **Application:** message / HTTP request / RPC
   - **Transport:** TCP *segment* / UDP *datagram*
   - **Internet / Network:** IP *packet*
   - **Link:** Ethernet *frame*
3. **Color-Coded Layers (`assets/course.css`):** Application (`l5`/`app`), Transport (`l4`/`transport`), Internet (`l3`/`network`), Link (`l2`/`link`), Physical (`l1`).
4. **No KaTeX / No Markdown Leakage:** `course.css` renders plain HTML + semantic tags (`<code>`, `<var>`, `<sup>`, Unicode math) — never put raw `$...$` or markdown backticks inside HTML prose or `data-why` attributes.
5. **Quiz Integrity (`assets/quiz.js`):** 0-indexed `data-answer` must strictly match `"Correct!"` on the true option and `"Incorrect: ..."` on distractors, with balanced option lengths so length never clues the answer.

## Course Version History
- **v1–v2:** Initial 72-session syllabus (`0001`–`0072`) + 7 permanent reference sheets + Java lab validation (`LabRunner.java`).
- **v3 (2026-11-15 Two-Part 2027 Senior NetEng Overhaul):**
  - Audited against `roadmap.sh/network-engineer` for transitioning from Junior Android Developer to Senior Software Dev (Part 1) + Senior Network Engineer (Part 2).
  - Removed all `◇ Optional Deep-Dive` badges from `0036`, `0039`, `0040`, `0041`, `0048`, `0049` — elevated all 6 to **★ Core NetEng** inside Part 2.
  - Added 15 new sessions (`0073`–`0087`) covering Android/Mobile edge (`0073`), Reverse Proxies &amp; Graceful Draining (`0074`), Linux `iproute2`/`netns`/`nftables` (`0075`), Containerlab/FRR (`0076`), FHRP/Anycast GW (`0077`), IS-IS/BFD/ECMP/SR (`0078`), MP-BGP EVPN Spine-Leaf (`0079`), 802.1X/RADIUS/TACACS+/DAI/ZTNA (`0080`), IP Multicast (`0081`), YANG/NETCONF/RESTCONF/gNMI (`0082`), Python NetDevOps (`0083`), Ansible (`0084`), Terraform/Batfish GitOps (`0085`), Automation Outage Autopsy (`0086`), and **Capstone 2 (`0087`)**.
  - Upgraded Capstone 1 (`0069`) with reverse-proxy `/api/*` forwarding, `/healthz`, and `SIGTERM` graceful draining.
  - Added permanent reference sheet `reference/netdevops-and-automation.html`.
  - Audited and fixed 129 scrambled/mismatched `data-why` quiz explanations across 49 legacy lessons plus HTML cleanup.
