#!/usr/bin/env python3
"""
inject_part2.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 2 Transport lessons (0013 to 0020).
"""

import os
import re

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0013": {
        "title": "5-Tuple Connection Demultiplexer",
        "key": "0013-conn-demux",
        "desc": "Implement socket connection matching using the canonical 5-tuple (srcIP, srcPort, dstIP, dstPort, protocol) and identify ephemeral port ranges.",
        "starter": """import java.util.*;

public class Solution {
    // 1. Return true if port is in modern IANA ephemeral port range (49152..65535) or BSD/Linux (32768..60999)
    public static boolean isEphemeralPort(int port) {
        // TODO: implement
        return false;
    }

    // 2. Format unique connection key: "srcIp:srcPort->dstIp:dstPort[proto]"
    public static String get5TupleKey(String srcIp, int srcPort, String dstIp, int dstPort, String proto) {
        // TODO: implement
        return "";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static boolean isEphemeralPort(int port) {
        return (port >= 32768 && port <= 65535);
    }

    public static String get5TupleKey(String srcIp, int srcPort, String dstIp, int dstPort, String proto) {
        return String.format("%s:%d->%s:%d[%s]", srcIp.trim(), srcPort, dstIp.trim(), dstPort, proto.trim().toUpperCase());
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Ephemeral port check
        if (Solution.isEphemeralPort(54321) && !Solution.isEphemeralPort(80) && !Solution.isEphemeralPort(443)) {
            System.out.println("PASS: Test 1: Ephemeral port range verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Ephemeral check failed");
        }

        // Test 2: 5-Tuple key generation
        String key = Solution.get5TupleKey("192.168.1.50", 52344, "1.1.1.1", 443, "tcp");
        if ("192.168.1.50:52344->1.1.1.1:443[TCP]".equals(key)) {
            System.out.println("PASS: Test 2: 5-tuple canonical key formatted");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 192.168.1.50:52344->1.1.1.1:443[TCP], got " + key);
        }

        // Test 3: System ports (0-1023) are never ephemeral
        if (!Solution.isEphemeralPort(22) && !Solution.isEphemeralPort(53)) {
            System.out.println("PASS: Test 3: Well-known ports recognized as non-ephemeral");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: System port check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Ephemeral ports are assigned dynamically by OS kernels: Linux uses 32768..60999; IANA defines 49152..65535. Check port >= 32768 && port <= 65535.",
            "Format the key with String.format: \"%s:%d->%s:%d[%s]\".",
            "Normalize protocol string with .toUpperCase()."
        ],
        "example_java": """public class DemuxExample {
    public static void main(String[] args) {
        System.out.println("The 5-tuple demultiplexes packets to distinct socket descriptors.");
    }
}""",
        "example_kt": """object DemuxExample {
    fun main() {
        println("The 5-tuple demultiplexes packets to distinct socket descriptors.")
    }
}"""
    },
    "0014": {
        "title": "UDP Datagram Framing & Length Validator",
        "key": "0014-udp-framing",
        "desc": "Construct UDP transport headers (8 bytes: srcPort, dstPort, length, checksum) and validate datagram boundary lengths.",
        "starter": """public class Solution {
    public static final int UDP_HEADER_LEN = 8;

    // 1. Return UDP total length field value (header + payload)
    public static int calcUdpTotalLength(int payloadBytes) {
        // TODO: implement
        return 0;
    }

    // 2. Validate if datagram length exceeds maximum UDP over IPv4 payload (65,535 - 20 - 8 = 65,507 bytes)
    public static boolean isDatagramValidSize(int payloadBytes) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static final int UDP_HEADER_LEN = 8;
    public static final int MAX_UDP_PAYLOAD = 65507;

    public static int calcUdpTotalLength(int payloadBytes) {
        return UDP_HEADER_LEN + payloadBytes;
    }

    public static boolean isDatagramValidSize(int payloadBytes) {
        return payloadBytes >= 0 && payloadBytes <= MAX_UDP_PAYLOAD;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: UDP Length header for 512-byte DNS payload = 520 bytes
        if (Solution.calcUdpTotalLength(512) == 520) {
            System.out.println("PASS: Test 1: UDP length field = 520 bytes");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: UDP length calculation incorrect");
        }

        // Test 2: Maximum IPv4 UDP payload = 65,507 bytes
        if (Solution.isDatagramValidSize(65507) && !Solution.isDatagramValidSize(65508)) {
            System.out.println("PASS: Test 2: Maximum UDP payload boundary (65,507B) enforced");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Max UDP boundary check failed");
        }

        // Test 3: Zero-length payload is legal in UDP (total length = 8 bytes)
        if (Solution.calcUdpTotalLength(0) == 8 && Solution.isDatagramValidSize(0)) {
            System.out.println("PASS: Test 3: Zero-length payload datagram valid");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Zero-length check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "UDP header has exactly 8 bytes: 2B source port, 2B dest port, 2B total length, 2B checksum.",
            "Total UDP length is header (8) + payload bytes.",
            "In IPv4, maximum packet length is 65,535 bytes. Minus 20 bytes IPv4 header and 8 bytes UDP header = 65,507 bytes maximum datagram payload!"
        ],
        "example_java": """public class UdpFramingExample {
    public static void main(String[] args) {
        System.out.println("UDP is message-oriented: 1 send() = 1 packet = 1 receive().");
    }
}""",
        "example_kt": """object UdpFramingExample {
    fun main() {
        println("UDP is message-oriented: 1 send() = 1 packet = 1 receive().")
    }
}"""
    },
    "0015": {
        "title": "TCP Finite State Machine (FSM)",
        "key": "0015-tcp-fsm",
        "desc": "Implement TCP connection state transitions for handshake and teardown: CLOSED -> SYN_SENT -> ESTABLISHED -> FIN_WAIT_1 -> TIME_WAIT.",
        "starter": """public class Solution {
    // Return next state given (currentState, event):
    // "CLOSED" + "SEND_SYN" -> "SYN_SENT"
    // "SYN_SENT" + "RCV_SYN_ACK" -> "ESTABLISHED"
    // "ESTABLISHED" + "SEND_FIN" -> "FIN_WAIT_1"
    // "FIN_WAIT_1" + "RCV_ACK" -> "FIN_WAIT_2"
    // "FIN_WAIT_2" + "RCV_FIN" -> "TIME_WAIT"
    // Return "INVALID" if transition not recognized
    public static String nextState(String currentState, String event) {
        // TODO: implement
        return "INVALID";
    }
}""",
        "solution": """public class Solution {
    public static String nextState(String currentState, String event) {
        String key = currentState.trim().toUpperCase() + "+" + event.trim().toUpperCase();
        return switch (key) {
            case "CLOSED+SEND_SYN" -> "SYN_SENT";
            case "SYN_SENT+RCV_SYN_ACK" -> "ESTABLISHED";
            case "LISTEN+RCV_SYN" -> "SYN_RCVD";
            case "SYN_RCVD+RCV_ACK" -> "ESTABLISHED";
            case "ESTABLISHED+SEND_FIN" -> "FIN_WAIT_1";
            case "FIN_WAIT_1+RCV_ACK" -> "FIN_WAIT_2";
            case "FIN_WAIT_2+RCV_FIN" -> "TIME_WAIT";
            case "ESTABLISHED+RCV_FIN" -> "CLOSE_WAIT";
            case "CLOSE_WAIT+SEND_FIN" -> "LAST_ACK";
            case "LAST_ACK+RCV_ACK" -> "CLOSED";
            default -> "INVALID";
        };
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Client active open 3-way handshake
        String s1 = Solution.nextState("CLOSED", "SEND_SYN");
        String s2 = Solution.nextState(s1, "RCV_SYN_ACK");
        if ("SYN_SENT".equals(s1) && "ESTABLISHED".equals(s2)) {
            System.out.println("PASS: Test 1: Client active open 3-way handshake (CLOSED -> SYN_SENT -> ESTABLISHED)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Handshake transition failed");
        }

        // Test 2: Active close teardown to TIME_WAIT
        String c1 = Solution.nextState("ESTABLISHED", "SEND_FIN");
        String c2 = Solution.nextState(c1, "RCV_ACK");
        String c3 = Solution.nextState(c2, "RCV_FIN");
        if ("FIN_WAIT_1".equals(c1) && "FIN_WAIT_2".equals(c2) && "TIME_WAIT".equals(c3)) {
            System.out.println("PASS: Test 2: Active close (ESTABLISHED -> FIN_WAIT_1 -> FIN_WAIT_2 -> TIME_WAIT)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Active close failed");
        }

        // Test 3: Passive close teardown
        String p1 = Solution.nextState("ESTABLISHED", "RCV_FIN");
        String p2 = Solution.nextState(p1, "SEND_FIN");
        String p3 = Solution.nextState(p2, "RCV_ACK");
        if ("CLOSE_WAIT".equals(p1) && "LAST_ACK".equals(p2) && "CLOSED".equals(p3)) {
            System.out.println("PASS: Test 3: Passive close (ESTABLISHED -> CLOSE_WAIT -> LAST_ACK -> CLOSED)");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Passive close failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Use a modern switch statement matching on \"currentState + '+' + event\".",
            "In active open: CLOSED -> (SEND_SYN) -> SYN_SENT -> (RCV_SYN_ACK) -> ESTABLISHED.",
            "In active close: ESTABLISHED -> (SEND_FIN) -> FIN_WAIT_1 -> (RCV_ACK) -> FIN_WAIT_2 -> (RCV_FIN) -> TIME_WAIT."
        ],
        "example_java": """public class TcpStateExample {
    public static void main(String[] args) {
        System.out.println("TIME_WAIT lasts 2MSL (~60-120s) to prevent lingering delayed packets from corrupting new connections.");
    }
}""",
        "example_kt": """object TcpStateExample {
    fun main() {
        println("TIME_WAIT lasts 2MSL (~60-120s) to prevent lingering delayed packets from corrupting new connections.")
    }
}"""
    },
    "0016": {
        "title": "TCP Cumulative ACK & Sequence Tracker",
        "key": "0016-tcp-ack-tracker",
        "desc": "Implement TCP cumulative sequence number and ACK tracking: sequence advancement, ACK response computation, and out-of-order segment detection.",
        "starter": """public class Solution {
    private long expectedSeq = 0;

    public Solution(long initialSeq) {
        this.expectedSeq = initialSeq;
    }

    // Process incoming segment with sequence number and payload byte length
    // If seq == expectedSeq: advance expectedSeq by len and return new cumulative ACK
    // If seq > expectedSeq: return current expectedSeq (trigger duplicate ACK!)
    public long processSegment(long seq, int len) {
        // TODO: implement
        return 0L;
    }

    public long getExpectedAck() {
        return expectedSeq;
    }
}""",
        "solution": """public class Solution {
    private long expectedSeq;

    public Solution(long initialSeq) {
        this.expectedSeq = initialSeq;
    }

    public long processSegment(long seq, int len) {
        if (seq == expectedSeq) {
            expectedSeq += len;
        }
        return expectedSeq;
    }

    public long getExpectedAck() {
        return expectedSeq;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution tcp = new Solution(1000L);

        // Test 1: In-order segment: seq=1000, len=500 -> ACK = 1500
        long ack1 = tcp.processSegment(1000L, 500);
        if (ack1 == 1500L) {
            System.out.println("PASS: Test 1: Cumulative ACK advanced to 1500");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 1500, got " + ack1);
        }

        // Test 2: Out-of-order packet: seq=2000 arrives, but expecting 1500! -> Duplicate ACK = 1500
        long ack2 = tcp.processSegment(2000L, 500);
        if (ack2 == 1500L) {
            System.out.println("PASS: Test 2: Out-of-order packet generated duplicate ACK 1500");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected dup ACK 1500, got " + ack2);
        }

        // Test 3: Missing packet arrives: seq=1500, len=500 -> ACK advances to 2000
        long ack3 = tcp.processSegment(1500L, 500);
        if (ack3 == 2000L) {
            System.out.println("PASS: Test 3: Repaired segment advanced ACK to 2000");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected 2000, got " + ack3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "TCP ACKs are cumulative: ACK number indicates the next sequence byte the receiver expects to receive.",
            "If seq == expectedSeq, advance expectedSeq += len.",
            "If an out-of-order segment arrives (seq > expectedSeq), TCP returns the current expectedSeq as a duplicate ACK."
        ],
        "example_java": """public class TcpAckExample {
    public static void main(String[] args) {
        System.out.println("Three duplicate ACKs trigger Fast Retransmit without waiting for RTO timeout.");
    }
}""",
        "example_kt": """object TcpAckExample {
    fun main() {
        println("Three duplicate ACKs trigger Fast Retransmit without waiting for RTO timeout.")
    }
}"""
    },
    "0017": {
        "title": "TCP Congestion Control & Window Limiter",
        "key": "0017-congestion-control",
        "desc": "Implement TCP transmission limits using Effective Window = min(cwnd, rwnd), Slow Start exponential growth, and AIMD congestion avoidance.",
        "starter": """public class Solution {
    // 1. Effective window in flight is the minimum of Congestion Window (cwnd) and Receiver Window (rwnd)
    public static long getEffectiveWindow(long cwnd, long rwnd) {
        // TODO: implement
        return 0L;
    }

    // 2. Slow Start: upon ACK, cwnd increases by MSS (exponential growth per RTT)
    public static long onSlowStartAck(long cwnd, int mss) {
        // TODO: implement
        return 0L;
    }

    // 3. Loss event (TCP Reno): ssthresh = cwnd / 2, cwnd resets to 1 * MSS
    public static long onLossCwnd(int mss) {
        // TODO: implement
        return 0L;
    }
}""",
        "solution": """public class Solution {
    public static long getEffectiveWindow(long cwnd, long rwnd) {
        return Math.min(cwnd, rwnd);
    }

    public static long onSlowStartAck(long cwnd, int mss) {
        return cwnd + mss;
    }

    public static long onLossCwnd(int mss) {
        return mss;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Flow control vs Congestion control limiting
        long w1 = Solution.getEffectiveWindow(100000L, 32000L); // cwnd > rwnd
        long w2 = Solution.getEffectiveWindow(16000L, 64000L);  // cwnd < rwnd
        if (w1 == 32000L && w2 == 16000L) {
            System.out.println("PASS: Test 1: Effective Window = min(cwnd, rwnd) correctly enforced");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Effective window calculation incorrect");
        }

        // Test 2: Slow Start growth
        long cwnd1 = Solution.onSlowStartAck(1460L, 1460);
        if (cwnd1 == 2920L) {
            System.out.println("PASS: Test 2: Slow Start cwnd increased by MSS");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Slow start growth failed");
        }

        // Test 3: Loss event reset
        long cwndLoss = Solution.onLossCwnd(1460);
        if (cwndLoss == 1460L) {
            System.out.println("PASS: Test 3: Timeout loss resets cwnd to 1 MSS");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Loss reset failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "The sender can never have more bytes in flight than min(cwnd, rwnd).",
            "Slow start increases cwnd by MSS for every arriving ACK, doubling cwnd every RTT.",
            "Upon a timeout loss event in TCP Reno, cwnd drops to 1 MSS."
        ],
        "example_java": """public class CongestionExample {
    public static void main(String[] args) {
        System.out.println("Flow control (rwnd) protects the receiver; Congestion control (cwnd) protects the network.");
    }
}""",
        "example_kt": """object CongestionExample {
    fun main() {
        println("Flow control (rwnd) protects the receiver; Congestion control (cwnd) protects the network.")
    }
}"""
    },
    "0018": {
        "title": "Nagle's Algorithm & TCP_NODELAY Simulator",
        "key": "0018-nagle-sim",
        "desc": "Implement Nagle's algorithm packet sending logic: send immediately if TCP_NODELAY is enabled, if packet >= MSS, or if all previous packets have been acknowledged.",
        "starter": """public class Solution {
    // Return true if packet should be dispatched immediately onto wire;
    // Return false if packet must be buffered (waiting for ACKs or more data)
    public static boolean shouldSendNow(int bytesToSend, boolean hasUnackedData, int mss, boolean tcpNoDelay) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static boolean shouldSendNow(int bytesToSend, boolean hasUnackedData, int mss, boolean tcpNoDelay) {
        if (tcpNoDelay) return true; // Nagle disabled!
        if (bytesToSend >= mss) return true; // Full segment
        if (!hasUnackedData) return true; // No data in flight
        return false; // Hold packet (Nagle delay)
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Full-size segment (>= MSS) always sends immediately
        if (Solution.shouldSendNow(1460, true, 1460, false)) {
            System.out.println("PASS: Test 1: Full segment sent immediately even with unacked data");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Full segment should send immediately");
        }

        // Test 2: Small write with unacked data is HELD by Nagle
        if (!Solution.shouldSendNow(100, true, 1460, false)) {
            System.out.println("PASS: Test 2: Small write held by Nagle to avoid tinygrams");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Small write should be held");
        }

        // Test 3: TCP_NODELAY disables Nagle: small write sends immediately!
        if (Solution.shouldSendNow(100, true, 1460, true)) {
            System.out.println("PASS: Test 3: TCP_NODELAY sends small write immediately, defeating 200ms delay!");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: TCP_NODELAY should send immediately");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "If tcpNoDelay == true, Nagle is disabled — always send immediately.",
            "If bytesToSend >= mss, it is a full segment — send immediately.",
            "If !hasUnackedData, there is no unacknowledged data in flight — send immediately.",
            "Otherwise, return false to buffer."
        ],
        "example_java": """public class NagleExample {
    public static void main(String[] args) {
        System.out.println("Combining Nagle + Delayed ACKs causes artificial 200ms latency spikes; fixed with socket.setTcpNoDelay(true).");
    }
}""",
        "example_kt": """object NagleExample {
    fun main() {
        println("Combining Nagle + Delayed ACKs causes artificial 200ms latency spikes; fixed with socket.setTcpNoDelay(true).")
    }
}"""
    },
    "0019": {
        "title": "Stream Framing: Delimiter & Length-Prefix Parsers",
        "key": "0019-stream-framing",
        "desc": "Implement delimiter-based framing (e.g. CRLF) to split a continuous TCP byte stream into discrete application messages.",
        "starter": """import java.util.*;

public class Solution {
    // Parse continuous byte stream buffer into discrete messages separated by delimiter (e.g. "\\n")
    // Trailing incomplete data should NOT be emitted as a message
    public static List<String> parseDelimiterMessages(String stream, String delimiter) {
        // TODO: implement
        return Collections.emptyList();
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static List<String> parseDelimiterMessages(String stream, String delimiter) {
        List<String> messages = new ArrayList<>();
        if (stream == null || delimiter == null || delimiter.isEmpty()) return messages;

        int start = 0;
        int idx;
        while ((idx = stream.indexOf(delimiter, start)) != -1) {
            messages.add(stream.substring(start, idx));
            start = idx + delimiter.length();
        }
        return messages;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Complete messages
        List<String> m1 = Solution.parseDelimiterMessages("PING\\nPONG\\n", "\\n");
        if (m1.size() == 2 && "PING".equals(m1.get(0)) && "PONG".equals(m1.get(1))) {
            System.out.println("PASS: Test 1: Extracted 2 complete delimiter messages");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Complete message extraction failed");
        }

        // Test 2: Incomplete trailing packet fragment (not emitted until delimiter arrives!)
        List<String> m2 = Solution.parseDelimiterMessages("MSG1\\r\\nMSG2\\r\\nINCOMPL", "\\r\\n");
        if (m2.size() == 2 && "MSG1".equals(m2.get(0)) && "MSG2".equals(m2.get(1))) {
            System.out.println("PASS: Test 2: Incomplete trailing fragment correctly retained");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Incomplete trailing fragment handling failed");
        }

        // Test 3: No delimiters present in stream
        List<String> m3 = Solution.parseDelimiterMessages("PARTIAL_PAYLOAD", "\\n");
        if (m3.isEmpty()) {
            System.out.println("PASS: Test 3: Zero messages emitted when delimiter absent");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Should return empty list");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Use stream.indexOf(delimiter, start) in a while loop.",
            "Only add substring(start, idx) to the result list when a delimiter is found.",
            "Any characters after the last delimiter represent an in-flight fragment that must wait for more bytes to arrive over TCP."
        ],
        "example_java": """public class FramingExample {
    public static void main(String[] args) {
        System.out.println("TCP has no concept of packet boundaries; application protocols must frame with delimiters or length-prefixes.");
    }
}""",
        "example_kt": """object FramingExample {
    fun main() {
        println("TCP has no concept of packet boundaries; application protocols must frame with delimiters or length-prefixes.")
    }
}"""
    },
    "0020": {
        "title": "Transport Outage Diagnostic Triage",
        "key": "0020-transport-triage",
        "desc": "Implement diagnostic failure classification: ECONNREFUSED vs ETIMEDOUT vs ECONNRESET vs TIME_WAIT port exhaustion.",
        "starter": """public class Solution {
    // Given symptom and TCP flag facts, return one of:
    // "CONNECTION_REFUSED" (RST received during SYN handshake - port closed)
    // "CONNECTION_TIMEOUT" (No response to SYN - dropped by network/firewall)
    // "CONNECTION_RESET"   (RST received mid-stream after ESTABLISHED)
    // "PORT_EXHAUSTION"    (Ephemeral ports exhausted by TIME_WAIT sockets)
    public static String diagnose(boolean isConnecting, boolean rstReceived, boolean timeoutExpired, int timeWaitCount) {
        // TODO: implement
        return "UNKNOWN";
    }
}""",
        "solution": """public class Solution {
    public static String diagnose(boolean isConnecting, boolean rstReceived, boolean timeoutExpired, int timeWaitCount) {
        if (timeWaitCount > 28000) return "PORT_EXHAUSTION";
        if (isConnecting && rstReceived) return "CONNECTION_REFUSED";
        if (isConnecting && timeoutExpired) return "CONNECTION_TIMEOUT";
        if (!isConnecting && rstReceived) return "CONNECTION_RESET";
        return "HEALTHY";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Immediate RST during connect() -> Port closed / Connection Refused
        String d1 = Solution.diagnose(true, true, false, 50);
        if ("CONNECTION_REFUSED".equals(d1)) {
            System.out.println("PASS: Test 1: RST on SYN diagnosed as CONNECTION_REFUSED");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected CONNECTION_REFUSED, got " + d1);
        }

        // Test 2: Timeout during connect() -> Packet dropped by firewall / Connection Timeout
        String d2 = Solution.diagnose(true, false, true, 50);
        if ("CONNECTION_TIMEOUT".equals(d2)) {
            System.out.println("PASS: Test 2: SYN timeout diagnosed as CONNECTION_TIMEOUT");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected CONNECTION_TIMEOUT, got " + d2);
        }

        // Test 3: TIME_WAIT accumulation -> Ephemeral port exhaustion
        String d3 = Solution.diagnose(true, false, false, 32000);
        if ("PORT_EXHAUSTION".equals(d3)) {
            System.out.println("PASS: Test 3: TIME_WAIT accumulation diagnosed as PORT_EXHAUSTION");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected PORT_EXHAUSTION, got " + d3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check timeWaitCount > 28000 first for ephemeral port exhaustion.",
            "If connecting and RST is received immediately, the OS kernel at the destination has no listening socket on that port: CONNECTION_REFUSED.",
            "If connecting and no response arrives before timer expires: CONNECTION_TIMEOUT (packet silently dropped by firewall)."
        ],
        "example_java": """public class TransportTriageExample {
    public static void main(String[] args) {
        System.out.println("Refused = server is alive and actively rejected the connection (RST).");
        System.out.println("Timeout = silent drop by a firewall or routing black hole (no reply).");
    }
}""",
        "example_kt": """object TransportTriageExample {
    fun main() {
        println("Refused = server is alive and actively rejected the connection (RST).")
        println("Timeout = silent drop by a firewall or routing black hole (no reply).")
    }
}"""
    }
}

def inject_lesson(num):
    data = MODULES[num]
    filename = [f for f in os.listdir(LESSONS_DIR) if f.startswith(num + "-")][0]
    filepath = os.path.join(LESSONS_DIR, filename)

    with open(filepath, "r", encoding="utf-8") as f:
        html = f.read()

    if 'class="code-practice"' in html:
        print(f"Skipping {num}: already has code practice")
        return

    hints_json = str(data["hints"]).replace("'", '"')
    block = f"""
  <h2>Code Example: {data['title']} in Java &amp; Kotlin</h2>
  <div class="code-tabs">
    <div class="code-tabs-nav">
      <button type="button" class="tab-btn active" data-lang="java">Java 25</button>
      <button type="button" class="tab-btn" data-lang="kotlin">Kotlin</button>
      <button type="button" class="code-copy-btn" aria-label="Copy code">Copy</button>
    </div>
    <div class="tab-panel active" data-lang="java">
      <pre><code>{data['example_java']}</code></pre>
    </div>
    <div class="tab-panel" data-lang="kotlin">
      <pre><code>{data['example_kt']}</code></pre>
    </div>
  </div>

  <h2>Interactive Code Practice: {data['title']}</h2>
  <div class="code-practice" data-key="{data['key']}">
    <div class="practice-bar">
      <span class="practice-title">Interactive Code Practice · Java 25</span>
      <span class="runner-badge" title="Daemon connection status">checking runner…</span>
    </div>
    <p class="practice-desc">{data['desc']}</p>
    <div class="editor-shell">
      <textarea class="practice-editor" spellcheck="false" autocomplete="off" autocapitalize="off" rows="15">{data['starter']}</textarea>
    </div>
    <div class="practice-actions">
      <button type="button" class="btn btn-run">▶ Run &amp; Check</button>
      <button type="button" class="btn btn-secondary btn-reset">Reset</button>
      <button type="button" class="btn btn-secondary btn-hint">💡 Hint</button>
      <button type="button" class="btn btn-secondary btn-solution">Show Solution</button>
    </div>
    <div class="practice-hints" hidden></div>
    <div class="practice-solution" hidden></div>
    <div class="practice-console" hidden>
      <div class="console-head">Test Output:</div>
      <pre class="console-body"></pre>
    </div>
    <script type="text/plain" class="starter-code">{data['starter']}</script>
    <script type="text/plain" class="test-harness">
{data['harness']}
</script>
    <script type="text/plain" class="solution-code">{data['solution']}</script>
    <script type="application/json" class="hints-data">{hints_json}</script>
  </div>
"""
    target_pattern = r'(<section>\s*<h2>Check yourself</h2>)'
    if not re.search(target_pattern, html):
        target_pattern = r'(<h2>Check yourself</h2>)'

    new_html = re.sub(target_pattern, block + "\n\\1", html, count=1)

    if 'code-tabs.js' not in new_html:
        new_html = new_html.replace('</body>', '<script src="../assets/code-tabs.js"></script>\n<script src="../assets/code-practice.js"></script>\n</body>')

    with open(filepath, "w", encoding="utf-8") as f:
        f.write(new_html)
    print(f"Injected into {filename}")

def main():
    for num in sorted(MODULES.keys()):
        inject_lesson(num)

if __name__ == "__main__":
    main()
