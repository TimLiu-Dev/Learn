#!/usr/bin/env python3
"""
enrich_part2.py — Injects rich per-option data-why explanations into Part 2 lessons (0013-0020).
"""

import os
import re

LESSONS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "lessons")

PART2_DATA = {
    "0013": [
        # Q0: Ephemeral port selector
        [
            "Incorrect: The destination web server binds to its own listening port (e.g. 443) and does not select client ports.",
            "Correct! The client operating system kernel automatically assigns an unused ephemeral port from the dynamic range (49152–65535) during connect().",
            "Incorrect: Local Wi-Fi routers forward Layer 3 packets and only rewrite ports if performing PAT/NAT at the perimeter.",
            "Incorrect: Recursive DNS servers resolve hostnames to IP addresses; they have zero involvement in TCP transport socket creation."
        ],
        # Q1: Two browser tabs to same server
        [
            "Incorrect: Both connections target the exact same destination IP address.",
            "Incorrect: Both connections use TCP (protocol 6).",
            "Incorrect: Both connections target the same destination service port (e.g. 443).",
            "Correct! The 5-tuple requires uniqueness: while source IP, dest IP, dest port, and protocol are identical, the client OS assigns a unique Source Port to each socket."
        ],
        # Q2: Root privileged port range
        [
            "Correct! Ports 0 to 1023 are Well-Known system ports, reserved on Unix-like OSes to root/superuser to prevent rogue processes from binding trusted services.",
            "Incorrect: Ports 1024 to 49151 are Registered Ports available to unprivileged user applications.",
            "Incorrect: Ports 49152 to 65535 are Dynamic/Ephemeral ports assigned automatically to outbound clients.",
            "Incorrect: TCP/UDP port fields are 16-bit integers; ports above 65535 do not exist in standard headers."
        ],
        # Q3: Modern socket definition
        [
            "Incorrect: An Ethernet cable connects to an RJ-45 physical port, not a transport socket.",
            "Incorrect: A socket is a software construct inside the OS kernel, not a physical hardware ASIC.",
            "Correct! A socket is the operating system abstraction (represented as a file descriptor on Unix) providing an application handle to read/write network bytes.",
            "Incorrect: Sockets manage communication endpoints, not cryptographic encryption keys."
        ]
    ],
    "0014": [
        # Q0: Router queue overflow drops UDP
        [
            "Incorrect: UDP does not interact with TCP; routers do not generate TCP notifications.",
            "Incorrect: UDP has no retransmission queue, timers, or acknowledgments; it cannot resend lost packets.",
            "Correct! UDP is an unreliable datagram protocol: when dropped, the packet is permanently gone unless the application layer detects the gap and recovers.",
            "Incorrect: Standard UDP provides no automatic ACK/NACK signaling to request retransmissions."
        ],
        # Q1: Empty UDP header bytes
        [
            "Incorrect: Four bytes is only two 16-bit fields; a UDP header requires four 16-bit fields.",
            "Correct! The UDP header consists of four 16-bit (2-byte) fields: Source Port, Destination Port, Length, and Checksum, totaling exactly 8 bytes.",
            "Incorrect: 20 bytes is the minimum size of an IPv4 or TCP header.",
            "Incorrect: 32 bytes is typical for a TCP header with multiple options, not a bare UDP header."
        ],
        # Q2: Why DNS defaults to UDP
        [
            "Incorrect: Standard DNS over UDP is unencrypted plaintext (unless DNS-over-HTTPS or DNS-over-TLS is used).",
            "Incorrect: Standard DNS query/response messages easily fit into a single datagram under 512 bytes.",
            "Incorrect: Routers do not prioritize UDP over TCP; both receive standard best-effort forwarding.",
            "Correct! A standard DNS query fits in a single packet. UDP avoids the 1-RTT latency cost of establishing a TCP 3-way handshake before querying."
        ],
        # Q3: UDP data chunk handling
        [
            "Correct! UDP preserves message boundaries: one send() call creates one discrete datagram on the wire, which must be retrieved by one recv() call.",
            "Incorrect: Delivering an unstructured byte stream is the defining property of TCP, not UDP.",
            "Incorrect: UDP does not fragment into fixed-size frames; it encapsulates application chunks directly into IP packets.",
            "Incorrect: UDP does not reorder corrupted segments; corrupted packets failing checksum verification are silently discarded."
        ]
    ],
    "0015": [
        # Q0: TCP 3-way handshake latency
        [
            "Incorrect: Establishing a connection requires exchanging network packets across the physical link.",
            "Correct! The client sends SYN (0.5 RTT) and receives SYN-ACK (0.5 RTT). The client can send data with its final ACK, costing exactly 1 full RTT before payload transmission.",
            "Incorrect: Two RTTs would only occur if TLS 1.2 is added on top of the TCP handshake.",
            "Incorrect: Half an RTT only gets the SYN to the server; the client cannot transmit data until it receives the server's SYN-ACK."
        ],
        # Q1: Why randomize Initial Sequence Numbers (ISNs)
        [
            "Incorrect: The Sequence Number field in the TCP header is always fixed at 32 bits regardless of its starting value.",
            "Incorrect: Random sequence numbers do not compress encrypted payloads.",
            "Incorrect: Sequence numbers are transport-layer fields completely ignored by intermediate IP routers.",
            "Correct! Random ISNs prevent old duplicate packets from previous connections colliding with new sessions and thwart off-path blind TCP spoofing attacks."
        ],
        # Q2: TCP packet sent to inactive port
        [
            "Correct! When a TCP SYN arrives on a port where no process is listening, the operating system kernel immediately replies with a TCP RST (Reset) to terminate the attempt.",
            "Incorrect: FIN is used for orderly graceful connection shutdown by an active application process.",
            "Incorrect: ICMP Redirect is sent by routers to inform a host of a better first-hop gateway, not for closed transport ports.",
            "Incorrect: A server only sends SYN-ACK if a socket is actively listening on that port."
        ],
        # Q3: TIME_WAIT state duration (2MSL)
        [
            "Incorrect: TCP connections do not hold state for 2 hours; that is the default keep-alive probe idle timer.",
            "Correct! The standard TIME_WAIT duration is 2 MSL (Maximum Segment Lifetime), traditionally defined as 1 to 2 minutes (60–120 seconds).",
            "Incorrect: 500 milliseconds is far too short to guarantee lingering duplicate packets have expired across global Internet paths.",
            "Incorrect: 24 hours is unnecessary and would exhaust local kernel memory and port space."
        ]
    ],
    "0016": [
        # Q0: Meaning of TCP ACK number N
        [
            "Incorrect: ACK N does not acknowledge only packet N; TCP acknowledgments are cumulative.",
            "Correct! TCP uses cumulative acknowledgments: ACK N confirms that the receiver has successfully received all contiguous stream bytes up to N-1, and expects byte N next.",
            "Incorrect: Sequence numbers count bytes in the data stream, not application packet message counts.",
            "Incorrect: The ACK field indicates the next byte expected, not the sender's congestion window size."
        ],
        # Q1: Fast Retransmit trigger
        [
            "Incorrect: A single ACK is normal traffic; packet loss cannot be deduced from a single acknowledgment.",
            "Correct! Fast Retransmit (RFC 5681) is triggered when the sender receives 3 duplicate ACKs (4 identical ACKs total), indicating a segment was lost while subsequent data arrived.",
            "Incorrect: Fast Retransmit triggers well before the full RTO timer expires, enabling rapid recovery without stalling.",
            "Incorrect: A TCP RST resets the connection entirely; it does not trigger retransmissions."
        ],
        # Q2: SACK (Selective Acknowledgment) purpose
        [
            "Correct! Without SACK, cumulative ACKs only report the left edge of received data, forcing retransmission of all subsequent packets. SACK informs the sender of isolated discontinuous blocks received.",
            "Incorrect: SACK does not encrypt sequence numbers; encryption is handled by TLS.",
            "Incorrect: SACK reports received sequence blocks, not flow control receiver buffer window sizes.",
            "Incorrect: SACK does not compress TCP options."
        ],
        # Q3: Retransmission Timeout (RTO) computation
        [
            "Incorrect: Fixed static timers cannot adapt to varying network latency (e.g. 1 ms LAN vs 200 ms transpacific link).",
            "Correct! Modern TCP dynamically tracks smoothed round-trip time (SRTT) and round-trip variance (RTTVAR) using Jacobson's algorithm (RFC 6298) to calculate adaptive RTOs.",
            "Incorrect: RTO is computed dynamically by the sender from round-trip measurements, not configured statically by remote DNS records.",
            "Incorrect: Subnet prefix length indicates IP network size and has no relation to round-trip latency."
        ]
    ],
    "0017": [
        # Q0: Flow control vs Congestion control
        [
            "Incorrect: Flow control protects the receiver, while congestion control protects intermediate network routers.",
            "Correct! Flow control prevents the sender from overwhelming the receiving application's buffer; Congestion control prevents the sender from overwhelming network links and router queues.",
            "Incorrect: Both mechanisms are active during both initial connection ramp-up and steady state.",
            "Incorrect: Flow control is governed by the receiver's advertised window (rwnd), while congestion control is governed by the sender's congestion window (cwnd)."
        ],
        # Q1: Effective sending window formula
        [
            "Incorrect: Summing the windows would double the transmission rate and cause severe buffer overflow.",
            "Correct! The sender's usable transmission window is strictly bounded by the minimum of the Receiver Window (rwnd) and Congestion Window (cwnd): min(rwnd, cwnd).",
            "Incorrect: Max(rwnd, cwnd) would violate the receiver's explicit buffer limit whenever cwnd > rwnd.",
            "Incorrect: RTO is a retransmission timeout duration, not a window byte limit."
        ],
        # Q2: TCP Slow Start growth rate
        [
            "Incorrect: Linear increase (1 MSS per RTT) is Congestion Avoidance, not Slow Start.",
            "Correct! During Slow Start, cwnd increases by 1 MSS for every ACK received, resulting in exponential doubling of the congestion window every round-trip time.",
            "Incorrect: Slow Start doubles every RTT ($2^t$), not cubic polynomial growth (which is TCP CUBIC in congestion avoidance).",
            "Incorrect: Senders increase packet transmission rapidly during Slow Start rather than reducing rate."
        ],
        # Q3: TCP Window Scale option necessity
        [
            "Incorrect: Window scale has no effect on ephemeral port ranges.",
            "Correct! The standard TCP header window field is only 16 bits (max 65,535 bytes). High-bandwidth delay networks (BDP > 64KB) require Window Scale (RFC 7323) to scale buffers up to 1 GB.",
            "Incorrect: SYN packets are small control segments that never require window scaling.",
            "Incorrect: Window scaling applies to TCP streaming throughput, not UDP datagram sizes."
        ]
    ],
    "0018": [
        # Q0: Nagle + Delayed ACK pathological interaction
        [
            "Incorrect: Nagle buffers small outbound packets, while Delayed ACK delays returning ACKs up to 200 ms, creating a mutual deadlock.",
            "Incorrect: The connection does not crash with RST; it stalls silently for 40–200 ms on small writes.",
            "Correct! Nagle refuses to send a small chunk until the previous packet is ACKed; Delayed ACK refuses to send an ACK until a second packet arrives or 200 ms elapses, creating a 200 ms lag spike.",
            "Incorrect: The interaction occurs in software timers on clean networks, without requiring physical packet loss."
        ],
        # Q1: How to disable Nagle's algorithm in code
        [
            "Incorrect: SO_KEEPALIVE enables background heartbeat probes on idle sockets.",
            "Incorrect: SO_REUSEADDR allows binding to an IP:port in TIME_WAIT, unrelated to Nagle.",
            "Correct! Setting the socket option TCP_NODELAY to true disables Nagle's algorithm, instructing the kernel to transmit data immediately without buffering.",
            "Incorrect: TCP_QUICKACK instructs the receiver to send immediate ACKs, but disabling Nagle at the sender is done via TCP_NODELAY."
        ],
        # Q2: What causes bufferbloat
        [
            "Incorrect: Bufferbloat is caused by excessively large router buffers, not undersized memory.",
            "Correct! Bufferbloat occurs when intermediate routers have massive queues that fill up with packets. Classic TCP keeps increasing cwnd until drops occur, causing hundreds of milliseconds of latency.",
            "Incorrect: Lossless fiber optic media does not cause bufferbloat; unmanaged queuing in routers does.",
            "Incorrect: Path MTU discovery adjusts segment size, not router queue latency."
        ],
        # Q3: HTTP/2 transport Head-of-Line blocking solution
        [
            "Incorrect: Increasing memory buffers does not resolve transport head-of-line blocking when packets drop.",
            "Incorrect: Single-packet requests cannot support multiplexed parallel streaming.",
            "Correct! HTTP/3 eliminated TCP entirely, adopting QUIC over UDP. Because QUIC implements independent streams at the transport layer, loss on stream A does not stall stream B.",
            "Incorrect: QUIC and HTTP/3 maintain robust acknowledgments and congestion control over UDP."
        ]
    ],
    "0019": [
        # Q0: accept() return value
        [
            "Incorrect: The ServerSocket itself already holds the listening port number.",
            "Correct! accept() blocks until an incoming client connects, then returns a brand-new Socket dedicated to communicating with that specific client over the established 5-tuple.",
            "Incorrect: Byte counters are tracked by socket streams, not returned by accept().",
            "Incorrect: Gateway IP is a routing property, not returned by socket connection calls."
        ],
        # Q1: Why HTTP/1.1 sends Content-Length
        [
            "Incorrect: Content-Length has no bearing on router queue latency.",
            "Incorrect: TLS encryption handles security independently of HTTP Content-Length.",
            "Incorrect: Content-Length reports uncompressed or compressed payload byte length, but its primary purpose is framing.",
            "Correct! Because TCP is an unstructured continuous stream of bytes, the receiver needs Content-Length to know exactly how many payload bytes belong to this message before parsing the next."
        ],
        # Q2: Client method to initiate 3-way handshake
        [
            "Correct! The client invokes connect(), which dispatches the initial SYN segment and blocks until the 3-way handshake completes.",
            "Incorrect: bind() assigns a local IP:port to a socket, but does not initiate a connection.",
            "Incorrect: listen() is a server-side call that transitions a socket into the passive listening state.",
            "Incorrect: accept() is a server-side call that extracts established connections from the listen queue."
        ],
        # Q3: Unescaped delimiter inside payload
        [
            "Incorrect: Routers do not inspect application payload delimiters; packets are forwarded normally.",
            "Incorrect: The operating system manages raw bytes; delimiter parsing is performed in user space without kernel crashes.",
            "Correct! In delimiter framing, if the delimiter character (e.g. '\\n') appears unescaped within user data, the receiver treats it as the end of the message, prematurely slicing the payload.",
            "Incorrect: TCP connection state is unaffected by application payload framing errors."
        ]
    ],
    "0020": [
        # Q0: 5-tuple purpose
        [
            "Incorrect: Queuing delay is measured by active probes or router telemetry, not the 5-tuple.",
            "Correct! The 5-tuple (Source IP, Destination IP, Protocol, Source Port, Destination Port) uniquely identifies every active transport-layer connection across the Internet.",
            "Incorrect: DNS query responses use transaction IDs and UDP ports, not full 5-tuple routing.",
            "Incorrect: Header compression (like HPACK) operates at Layer 7, not on the Layer 4 5-tuple."
        ],
        # Q1: TCP_NODELAY flag effect
        [
            "Incorrect: Ephemeral port reuse is controlled by SO_REUSEADDR and kernel allocation policies.",
            "Incorrect: MSS is determined by MTU and TCP handshake options, unaffected by TCP_NODELAY.",
            "Incorrect: UDP sockets cannot use streaming flags like TCP_NODELAY.",
            "Correct! TCP_NODELAY disables Nagle's buffering algorithm, allowing small packets to be transmitted immediately and breaking the pathological 200 ms stall caused by Delayed ACKs."
        ],
        # Q2: Why TCP enters TIME_WAIT for 2MSL
        [
            "Correct! TIME_WAIT holds the connection socket for 2 Maximum Segment Lifetimes to ensure the final ACK was received and to ensure that lingering delayed duplicate packets die before the port is reused.",
            "Incorrect: TIME_WAIT does not retransmit data; the connection is already closed.",
            "Incorrect: TIME_WAIT is a local transport state and does not involve server reboots.",
            "Incorrect: Window scale options are negotiated only during the initial SYN handshake."
        ],
        # Q3: Why voice/games run on UDP
        [
            "Incorrect: UDP does not provide built-in encryption; DTLS or application SRTP is required.",
            "Incorrect: UDP has no packet loss protection and drops packets when congestion occurs.",
            "Correct! For real-time audio and gaming, old data is useless. TCP's mandatory retransmissions and Head-of-Line blocking introduce unacceptable 200+ ms lag spikes; UDP delivers packets in real time.",
            "Incorrect: TCP can transport any binary audio bytes, but its retransmission latency ruins interactive communication."
        ],
        # Q4: Transport Head-of-Line (HoL) blocking
        [
            "Incorrect: CPU scheduling is separate from transport-layer stream stalling.",
            "Correct! Because TCP enforces in-order byte delivery, if a single packet is lost on the link, the receiver kernel holds all subsequent received packets in memory until the missing packet is retransmitted.",
            "Incorrect: A full router queue causes packet drops, which triggers HoL blocking, but is not the definition of HoL blocking.",
            "Incorrect: DNS query loops are application/name-resolution issues unrelated to transport streams."
        ],
        # Q5: How BBR avoids bufferbloat
        [
            "Correct! Google BBR continuously measures actual bottleneck bandwidth and minimum RTT, pacing outbound packet transmissions to keep network queues completely empty.",
            "Incorrect: Classic loss-based TCP (like Reno/Cubic) waits for packet drops; BBR explicitly avoids filling buffers to the drop point.",
            "Incorrect: BBR does not set arbitrary 1 GB congestion windows; it dynamically calculates delivery rates.",
            "Incorrect: BBR operates within the standard TCP protocol, keeping sliding windows active."
        ]
    ]
}

def enrich_file(num, questions_data):
    matches = [f for f in os.listdir(LESSONS_DIR) if f.startswith(f"{num}-") and f.endswith(".html")]
    if not matches:
        print(f"File not found for {num}")
        return
    filename = matches[0]
    filepath = os.path.join(LESSONS_DIR, filename)

    with open(filepath, "r", encoding="utf-8") as f:
        html = f.read()

    q_matches = list(re.finditer(r'<div class="q" data-answer="(\d+)">([\s\S]*?<div class="q-explain">[\s\S]*?</div>\s*</div>)', html))
    if len(q_matches) != len(questions_data):
        print(f"Warning: {filename} has {len(q_matches)} questions, but {len(questions_data)} data entries provided.")

    new_html = html
    for qi in reversed(range(min(len(q_matches), len(questions_data)))):
        qm = q_matches[qi]
        q_text = qm.group(0)
        options_data = questions_data[qi]

        ol_m = re.search(r'<ol class="options">([\s\S]*?)</ol>', q_text)
        if not ol_m:
            continue

        raw_lis = re.findall(r'<li(?:\s+[^>]*)?>([\s\S]*?)</li>', ol_m.group(1))
        if len(raw_lis) != len(options_data):
            print(f"Warning in {num} Q{qi}: {len(raw_lis)} lis vs {len(options_data)} options_data")
            continue

        new_lis = []
        for oi, raw_li in enumerate(raw_lis):
            why = options_data[oi].replace('"', '&quot;')
            new_lis.append(f'        <li data-why="{why}">{raw_li}</li>')

        new_ol = '<ol class="options">\n' + '\n'.join(new_lis) + '\n      </ol>'
        new_q_text = q_text[:ol_m.start()] + new_ol + q_text[ol_m.end():]
        new_html = new_html[:qm.start()] + new_q_text + new_html[qm.end():]

    with open(filepath, "w", encoding="utf-8") as f:
        f.write(new_html)
    print(f"Enriched {filename}")

def main():
    for num, q_data in sorted(PART2_DATA.items()):
        enrich_file(num, q_data)

if __name__ == "__main__":
    main()
