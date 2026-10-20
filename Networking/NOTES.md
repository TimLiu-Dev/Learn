# Notes

## Teaching preferences (stated by user, 2026-10-18)
- Teach like a CS student with basic programming experience.
- One concept per session; mixed review session at the end of each section.
- "Why" before "how".
- Hands-on with real tools (Wireshark, dig, curl, ping, traceroute, netstat) wherever possible.
- Code in Kotlin and Java.
- Never give exercise answers unless asked.
- Mark interview-relevant topics.
- Capstone: HTTP server from raw sockets in Kotlin/Java.

## Environment (checked 2026-10-18)
- macOS, OpenJDK 26, IntelliJ IDEA, Android Studio, Gradle (Homebrew).
- Available: curl, dig, nc, tcpdump, traceroute. Not installed: Wireshark/tshark, standalone kotlinc.

## Course conventions (decided 2026-10-18)
- Every session, including reviews, has a sequential number; `lessons/NNNN-slug.html` matches it. Course home: `index.html`.
- "No answers" rule applied as: hands-on observation questions (`li.observe`) are never answered on the page — the user brings them to chat. Quizzes give instant right/wrong feedback (the skill's feedback loop). Free-recall key points sit behind an explicit reveal button (= the user asking).
- Spaced retrieval: every lesson from session 2 opens with a warm-up recalling earlier sessions.
- Layer colours are fixed course-wide (app purple, transport blue, network green, link orange, physical grey).
- Terminology: network-layer unit = "packet" (avoid "IP datagram"); TCP segment; UDP datagram; link frame.
- Code: single-file Java runnable with `java File.java` (JDK 26) plus a Kotlin equivalent.

## Status
- 2026-10-18: user asked to generate all of Parts 0–1 (sessions 1–12) without answering the setup questions yet (Kotlin vs Java, interview timeline, prior knowledge). Defaults: Kotlin + Java side by side; no reordering.
- 2026-10-19: sessions 1–12, 4 reference sheets, components (quiz, steps, recall, delay-calc, subnet-trainer, fill-in) written. No learning records yet; the user hasn't started. Write the first records when they bring answers from session 1.
- Unverified across Part 1: Wireshark UI labels (Wireshark wasn't installed while writing; filters checked against dfref); which DHCP messages macOS sends on "Renew DHCP Lease"; Kurose & Ross section numbers. Watch for mismatches when the user reports what they see.
- User's network facts observed while writing (do NOT reveal; useful for judging their answers): /24 home LAN, MAC is locally administered, IPv6 shows link-local + ULA only (no global), CDN-served university sites ~20 ms.
