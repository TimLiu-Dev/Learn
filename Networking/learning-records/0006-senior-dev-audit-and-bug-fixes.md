# Senior Developer Curriculum Audit & Course Hardening

## Context & Decisions
- Evaluated all 72 lessons against Senior/Staff Software Engineering expectations.
- User chose to retain all 72 lessons while badging the 6 CCNA-heavy hardware/ISP sessions (`0036` Optics, `0039` Inter-VLAN SVIs, `0040` STP, `0041` OSPF, `0048` Router QoS, `0049` SNMP/NetFlow) as **◇ Optional Deep-Dive** across `index.html`, `CURRICULUM.md`, and lesson headers so software engineers prioritize VLANs (`0038`), BGP/RPKI (`0042`–`0044`), Stateful Firewalls (`0045`), VPNs (`0046`), and VXLAN (`0047`).
- User chose to execute the baseline bug & quality hardening pass first before adding new senior-dev sessions, Kotlin/Java dual code tabs, code quizzes, or extra reference sheets.

## Hardening Completed
- **Zero-dependency Math Rendering:** Converted all raw unrendered `$...$` LaTeX strings across 25 HTML lessons into native semantic HTML (`<var>`, `<sub>`, `<sup>`, HTML math entities).
- **Sessions 69–72 Layout & Dark-Mode Parity:** Restored `<article class="page">`, `.meta`, and `.win` headers; converted hardcoded light-mode hex SVGs to `svg.dia` CSS variables; fixed invalid `<strong>` tags inside SVG `<text>` in Session 70; converted static `.step` divs in Sessions 69 & 71 to interactive `<ol class="steps">`.
- **HTTP Request Smuggling / Framing Desync Fix (`HttpServerV2.java`):** Added `drainRequestBody()` in Session 69 so unhandled `POST`/`PUT` payloads with `Content-Length` do not poison subsequent `Keep-Alive` requests on the same socket, compile-verified cleanly with `javac -Xlint:all`.
- **macOS Terminal Compatibility:** Added Docker/OrbStack/Lima/`kind` Linux container wrappers for kernel labs (`0051`, `0060`–`0064`), macOS `ping -D -s` + `ifconfig` steps in `0071`, and accurate `ICMP Port Unreachable` connected-UDP behavior in `0014`.
- **Senior-Level Quiz Rigor:** Replaced 105 joke/cartoonish distractors across 29 lessons in Parts 4–6 with realistic engineering misconceptions while preserving verified `data-answer` indices.
