#!/usr/bin/env python3
"""
inject_part1.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 1 lessons (0005 to 0012).
"""

import os
import re

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0005": {
        "title": "IPv4 Header Checksum & TTL Decrement Engine",
        "key": "0005-ipv4-checksum",
        "desc": "Implement RFC 791 16-bit one's complement checksum calculation and TTL decrement in <code>calcChecksum(int[] words16)</code> and <code>decrementTtl(int[] headerWords, int ttlIndex)</code>.",
        "starter": """public class Solution {
    // 1. Calculate RFC 791 16-bit one's complement checksum over 16-bit header words
    public static int calcChecksum(int[] words16) {
        // TODO: implement
        return 0;
    }

    // 2. Decrement TTL: if TTL <= 1, return false (drop / Time Exceeded)
    // Otherwise decrement TTL by 1, update headerWords[ttlIndex], and return true
    public static boolean decrementTtl(int[] headerWords, int ttlIndex) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static int calcChecksum(int[] words16) {
        long sum = 0;
        for (int w : words16) {
            sum += (w & 0xFFFF);
        }
        while ((sum >> 16) > 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (int) (~sum & 0xFFFF);
    }

    public static boolean decrementTtl(int[] headerWords, int ttlIndex) {
        int ttlAndProto = headerWords[ttlIndex];
        int ttl = (ttlAndProto >> 8) & 0xFF;
        if (ttl <= 1) return false;
        ttl--;
        headerWords[ttlIndex] = (ttl << 8) | (ttlAndProto & 0xFF);
        return true;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Standard RFC 791 20-byte IPv4 header words
        int[] hdr = {0x4500, 0x003C, 0x1C46, 0x4000, 0x4006, 0x0000, 0xAC10, 0x0A63, 0xAC10, 0x0A0C};
        int csum = Solution.calcChecksum(hdr);
        if (csum == 0xB1E6) {
            System.out.println("PASS: Test 1: RFC 791 checksum = 0xB1E6");
            passed++;
        } else {
            System.out.printf("FAIL: Test 1: expected 0xB1E6, got 0x%04X%n", csum);
        }

        // Test 2: Decrement TTL from 64 to 63
        int[] hdr2 = {0x4500, 0x003C, 0x1C46, 0x4000, 0x4006, 0xB1E6, 0xAC10, 0x0A63, 0xAC10, 0x0A0C};
        boolean ok = Solution.decrementTtl(hdr2, 4); // index 4 has TTL=0x40 (64) and proto=0x06
        int newTtl = (hdr2[4] >> 8) & 0xFF;
        if (ok && newTtl == 63) {
            System.out.println("PASS: Test 2: TTL decremented to 63");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: TTL decrement failed");
        }

        // Test 3: TTL = 1 dropped (Time to Live Exceeded)
        hdr2[4] = 0x0106;
        boolean okDrop = Solution.decrementTtl(hdr2, 4);
        if (!okDrop) {
            System.out.println("PASS: Test 3: TTL=1 packet safely dropped");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: TTL=1 should be dropped");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Sum all 16-bit words into a 32-bit integer or long.",
            "Fold 32-bit overflow carries back into the lower 16 bits: while ((sum >> 16) > 0) sum = (sum & 0xFFFF) + (sum >> 16).",
            "Take the one's complement (~sum & 0xFFFF) to produce the final 16-bit checksum.",
            "TTL is the high byte of the 5th 16-bit word (bits 15..8). If ttl <= 1, return false."
        ],
        "example_java": """public class Ipv4Checksum {
    public static int calcChecksum(int[] words16) {
        long sum = 0;
        for (int w : words16) sum += (w & 0xFFFF);
        while ((sum >> 16) > 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (int) (~sum & 0xFFFF);
    }

    public static void main(String[] args) {
        int[] hdr = {0x4500, 0x003C, 0x1C46, 0x4000, 0x4006, 0x0000, 0xAC10, 0x0A63, 0xAC10, 0x0A0C};
        System.out.printf("IPv4 Checksum: 0x%04X%n", calcChecksum(hdr));
    }
}""",
        "example_kt": """object Ipv4Checksum {
    fun calcChecksum(words16: IntArray): Int {
        var sum = 0L
        for (w in words16) sum += (w and 0xFFFF)
        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return (sum.inv() and 0xFFFF).toInt()
    }
}

fun main() {
    val hdr = intArrayOf(0x4500, 0x003C, 0x1C46, 0x4000, 0x4006, 0x0000, 0xAC10.toInt(), 0x0A63, 0xAC10.toInt(), 0x0A0C)
    println("IPv4 Checksum: 0x%04X".format(Ipv4Checksum.calcChecksum(hdr)))
}"""
    },
    "0006": {
        "title": "Subnet Mask & CIDR Range Calculator",
        "key": "0006-cidr-calc",
        "desc": "Implement <code>getNetworkIp(ip, prefix)</code>, <code>getBroadcastIp(ip, prefix)</code>, and <code>getUsableHostCount(prefix)</code>.",
        "starter": """public class Solution {
    // 1. Calculate Network Address (e.g. "192.168.1.155", 24 -> "192.168.1.0")
    public static String getNetworkIp(String ip, int prefix) {
        // TODO: implement
        return "";
    }

    // 2. Calculate Directed Broadcast Address (e.g. "192.168.1.155", 24 -> "192.168.1.255")
    public static String getBroadcastIp(String ip, int prefix) {
        // TODO: implement
        return "";
    }

    // 3. Return total usable host count: 2^(32-prefix) - 2 (0 for /31 and /32)
    public static long getUsableHostCount(int prefix) {
        // TODO: implement
        return 0L;
    }
}""",
        "solution": """public class Solution {
    public static long ipToLong(String ip) {
        String[] p = ip.trim().split("\\\\.");
        return (Long.parseLong(p[0]) << 24) | (Long.parseLong(p[1]) << 16) | (Long.parseLong(p[2]) << 8) | Long.parseLong(p[3]);
    }

    public static String longToIp(long n) {
        return String.format("%d.%d.%d.%d", (n >> 24) & 0xFF, (n >> 16) & 0xFF, (n >> 8) & 0xFF, n & 0xFF);
    }

    public static String getNetworkIp(String ip, int prefix) {
        long mask = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;
        return longToIp(ipToLong(ip) & mask);
    }

    public static String getBroadcastIp(String ip, int prefix) {
        long mask = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;
        long hostMask = ~mask & 0xFFFFFFFFL;
        return longToIp((ipToLong(ip) & mask) | hostMask);
    }

    public static long getUsableHostCount(int prefix) {
        if (prefix >= 31) return 0;
        return (1L << (32 - prefix)) - 2;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: /24 subnet
        String net1 = Solution.getNetworkIp("192.168.1.155", 24);
        String bcast1 = Solution.getBroadcastIp("192.168.1.155", 24);
        if ("192.168.1.0".equals(net1) && "192.168.1.255".equals(bcast1)) {
            System.out.println("PASS: Test 1: /24 net=192.168.1.0 bcast=192.168.1.255");
            passed++;
        } else {
            System.out.printf("FAIL: Test 1: got net=%s, bcast=%s%n", net1, bcast1);
        }

        // Test 2: /26 subnet (10.0.0.75/26 -> net 10.0.0.64, bcast 10.0.0.127)
        String net2 = Solution.getNetworkIp("10.0.0.75", 26);
        String bcast2 = Solution.getBroadcastIp("10.0.0.75", 26);
        if ("10.0.0.64".equals(net2) && "10.0.0.127".equals(bcast2)) {
            System.out.println("PASS: Test 2: /26 net=10.0.0.64 bcast=10.0.0.127");
            passed++;
        } else {
            System.out.printf("FAIL: Test 2: got net=%s, bcast=%s%n", net2, bcast2);
        }

        // Test 3: Usable host counts
        if (Solution.getUsableHostCount(24) == 254 && Solution.getUsableHostCount(26) == 62 && Solution.getUsableHostCount(32) == 0) {
            System.out.println("PASS: Test 3: Usable host calculations (/24=254, /26=62, /32=0)");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Host count calculation mismatch");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Convert IPv4 dotted-decimal string into a 32-bit unsigned long using bit shifts: (o1 << 24) | (o2 << 16) | (o3 << 8) | o4.",
            "Subnet mask = (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL.",
            "Network address = ipLong & mask; Broadcast address = (ipLong & mask) | (~mask & 0xFFFFFFFFL).",
            "Usable hosts = (1L << (32 - prefix)) - 2 (subtracting 2 for network ID and broadcast)."
        ],
        "example_java": """public class CidrMath {
    public static long ipToLong(String ip) {
        String[] p = ip.trim().split("\\\\.");
        return (Long.parseLong(p[0]) << 24) | (Long.parseLong(p[1]) << 16) | (Long.parseLong(p[2]) << 8) | Long.parseLong(p[3]);
    }

    public static String longToIp(long n) {
        return String.format("%d.%d.%d.%d", (n >> 24) & 0xFF, (n >> 16) & 0xFF, (n >> 8) & 0xFF, n & 0xFF);
    }

    public static void main(String[] args) {
        long ip = ipToLong("10.0.0.75");
        long mask = (0xFFFFFFFFL << (32 - 26)) & 0xFFFFFFFFL;
        System.out.println("Network IP: " + longToIp(ip & mask));
    }
}""",
        "example_kt": """object CidrMath {
    fun ipToLong(ip: String): Long {
        val p = ip.trim().split(".").map { it.toLong() }
        return (p[0] shl 24) or (p[1] shl 16) or (p[2] shl 8) or p[3]
    }

    fun longToIp(n: Long): String =
        "${(n shr 24) and 0xFF}.${(n shr 16) and 0xFF}.${(n shr 8) and 0xFF}.${n and 0xFF}"
}

fun main() {
    val ip = CidrMath.ipToLong("10.0.0.75")
    val mask = (0xFFFFFFFFL shl (32 - 26)) and 0xFFFFFFFFL
    println("Network IP: ${CidrMath.longToIp(ip and mask)}")
}"""
    },
    "0007": {
        "title": "ARP Cache & Gratuitous ARP Parser",
        "key": "0007-arp-engine",
        "desc": "Implement ARP table learning, IP-to-MAC resolution, and Gratuitous ARP detection.",
        "starter": """import java.util.*;

public class Solution {
    private final Map<String, String> cache = new HashMap<>();

    // 1. Store sender IP -> MAC mapping (MAC stored in uppercase)
    public void processArp(String senderIp, String senderMac) {
        // TODO: implement
    }

    // 2. Lookup MAC by IP (return null if not found)
    public String lookup(String ip) {
        // TODO: implement
        return null;
    }

    // 3. True if sender IP equals target IP (Gratuitous ARP IP conflict check / announcement)
    public static boolean isGratuitous(String senderIp, String targetIp) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private final Map<String, String> cache = new HashMap<>();

    public void processArp(String senderIp, String senderMac) {
        if (senderIp != null && senderMac != null) {
            cache.put(senderIp.trim(), senderMac.trim().toUpperCase());
        }
    }

    public String lookup(String ip) {
        return ip != null ? cache.get(ip.trim()) : null;
    }

    public static boolean isGratuitous(String senderIp, String targetIp) {
        return senderIp != null && senderIp.trim().equals(targetIp != null ? targetIp.trim() : "");
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution arp = new Solution();
        arp.processArp("192.168.1.1", "00:11:22:33:44:55");
        arp.processArp("192.168.1.100", "aa:bb:cc:dd:ee:ff");

        // Test 1: Lookup
        if ("00:11:22:33:44:55".equalsIgnoreCase(arp.lookup("192.168.1.1"))) {
            System.out.println("PASS: Test 1: ARP table lookup succeeded");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: ARP lookup failed");
        }

        // Test 2: Miss returns null
        if (arp.lookup("192.168.1.200") == null) {
            System.out.println("PASS: Test 2: ARP miss returns null");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Expected null for unknown IP");
        }

        // Test 3: Gratuitous ARP
        boolean g1 = Solution.isGratuitous("192.168.1.50", "192.168.1.50");
        boolean g2 = Solution.isGratuitous("192.168.1.50", "192.168.1.1");
        if (g1 && !g2) {
            System.out.println("PASS: Test 3: Gratuitous ARP detection");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Gratuitous ARP check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Use a HashMap<String, String> mapping IP string to MAC string.",
            "Always normalize MAC addresses with .toUpperCase().",
            "Gratuitous ARP occurs when a host broadcasts an ARP Request where sender IP == target IP to detect IP conflicts or update switch CAM tables after failover."
        ],
        "example_java": """import java.util.*;

public class ArpCache {
    private final Map<String, String> table = new HashMap<>();

    public void learn(String ip, String mac) { table.put(ip, mac.toUpperCase()); }
    public String resolve(String ip) { return table.get(ip); }

    public static void main(String[] args) {
        ArpCache cache = new ArpCache();
        cache.learn("192.168.1.1", "00:50:56:c0:00:01");
        System.out.println("Resolved Gateway MAC: " + cache.resolve("192.168.1.1"));
    }
}""",
        "example_kt": """class ArpCache {
    private val table = mutableMapOf<String, String>()

    fun learn(ip: String, mac: String) { table[ip] = mac.uppercase() }
    fun resolve(ip: String): String? = table[ip]
}

fun main() {
    val cache = ArpCache()
    cache.learn("192.168.1.1", "00:50:56:c0:00:01")
    println("Resolved Gateway MAC: ${cache.resolve("192.168.1.1")}")
}"""
    },
    "0008": {
        "title": "DHCP Protocol Options Parser",
        "key": "0008-dhcp-parser",
        "desc": "Parse RFC 2131 DHCP packet byte streams, verify Magic Cookie (0x63825363), and extract Option 53 (DHCP Message Type).",
        "starter": """public class Solution {
    // 1. Verify 4-byte Magic Cookie at byte offset 236: 0x63, 0x82, 0x53, 0x63
    public static boolean hasMagicCookie(byte[] packet) {
        // TODO: implement
        return false;
    }

    // 2. Extract Option 53 (Message Type: 1=Discover, 2=Offer, 3=Request, 5=Ack)
    // Return message type byte value, or -1 if not found or cookie invalid
    public static int getMessageType(byte[] packet) {
        // TODO: implement
        return -1;
    }
}""",
        "solution": """public class Solution {
    public static boolean hasMagicCookie(byte[] packet) {
        if (packet == null || packet.length < 240) return false;
        return (packet[236] == (byte) 0x63 &&
                packet[237] == (byte) 0x82 &&
                packet[238] == (byte) 0x53 &&
                packet[239] == (byte) 0x63);
    }

    public static int getMessageType(byte[] packet) {
        if (!hasMagicCookie(packet)) return -1;
        int i = 240;
        while (i < packet.length) {
            int opt = packet[i] & 0xFF;
            if (opt == 255) break; // End option
            if (opt == 0) { i++; continue; } // Pad option
            if (i + 1 >= packet.length) break;
            int len = packet[i + 1] & 0xFF;
            if (opt == 53 && len == 1 && i + 2 < packet.length) {
                return packet[i + 2] & 0xFF;
            }
            i += 2 + len;
        }
        return -1;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Build sample DHCP packet (245 bytes)
        byte[] pkt = new byte[250];
        pkt[236] = 0x63; pkt[237] = (byte)0x82; pkt[238] = 0x53; pkt[239] = 0x63; // Magic Cookie
        pkt[240] = 53; pkt[241] = 1; pkt[242] = 5; // Option 53 = DHCP ACK (5)
        pkt[243] = (byte)255; // End option

        // Test 1: Magic Cookie
        if (Solution.hasMagicCookie(pkt)) {
            System.out.println("PASS: Test 1: DHCP Magic Cookie verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Magic Cookie not detected");
        }

        // Test 2: Message Type = 5 (ACK)
        int type = Solution.getMessageType(pkt);
        if (type == 5) {
            System.out.println("PASS: Test 2: Extracted Option 53 Message Type = 5 (DHCP ACK)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 5, got " + type);
        }

        // Test 3: Invalid cookie returns -1
        pkt[236] = 0x00;
        if (Solution.getMessageType(pkt) == -1) {
            System.out.println("PASS: Test 3: Invalid magic cookie cleanly rejected");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Invalid cookie should return -1");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "DHCP header options begin immediately after the 236-byte fixed BOOTP header.",
            "The magic cookie is exactly 4 bytes: 99, 130, 83, 99 (0x63, 0x82, 0x53, 0x63).",
            "DHCP options follow TLV (Type-Length-Value) encoding: Option 255 marks End; Option 0 is 1-byte padding; all other options have 1-byte length followed by value bytes."
        ],
        "example_java": """public class DhcpInspector {
    public static boolean checkCookie(byte[] pkt) {
        return pkt.length >= 240 && pkt[236] == 0x63 && pkt[237] == (byte)0x82 && pkt[238] == 0x53 && pkt[239] == 0x63;
    }

    public static void main(String[] args) {
        byte[] b = new byte[240];
        b[236] = 0x63; b[237] = (byte)0x82; b[238] = 0x53; b[239] = 0x63;
        System.out.println("Cookie valid: " + checkCookie(b));
    }
}""",
        "example_kt": """object DhcpInspector {
    fun checkCookie(pkt: ByteArray): Boolean =
        pkt.size >= 240 && pkt[236] == 0x63.toByte() && pkt[237] == 0x82.toByte() && pkt[238] == 0x53.toByte() && pkt[239] == 0x63.toByte()
}

fun main() {
    val b = ByteArray(240)
    b[236] = 0x63.toByte(); b[237] = 0x82.toByte(); b[238] = 0x53.toByte(); b[239] = 0x63.toByte()
    println("Cookie valid: ${DhcpInspector.checkCookie(b)}")
}"""
    },
    "0009": {
        "title": "Longest Prefix Match (LPM) Router Engine",
        "key": "0009-lpm-routing",
        "desc": "Implement a real IP routing table lookup algorithm using Longest Prefix Match (LPM).",
        "starter": """import java.util.*;

public class Solution {
    // 1. Add route entry with CIDR prefix (e.g. "10.0.0.0/8", "eth0" or "0.0.0.0/0", "gateway")
    public void addRoute(String cidr, String nextHop) {
        // TODO: implement
    }

    // 2. Perform Longest Prefix Match lookup for destination IP
    // Return matching nextHop interface, or "DROP" if no matching route
    public String route(String destIp) {
        // TODO: implement
        return "DROP";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private static class Route {
        long network;
        long mask;
        int prefix;
        String nextHop;
    }

    private final List<Route> routes = new ArrayList<>();

    public void addRoute(String cidr, String nextHop) {
        String[] parts = cidr.trim().split("/");
        long ip = ipToLong(parts[0]);
        int prefix = Integer.parseInt(parts[1]);
        long mask = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;

        Route r = new Route();
        r.network = ip & mask;
        r.mask = mask;
        r.prefix = prefix;
        r.nextHop = nextHop;
        routes.add(r);
    }

    public String route(String destIp) {
        long dip = ipToLong(destIp);
        Route best = null;
        for (Route r : routes) {
            if ((dip & r.mask) == r.network) {
                if (best == null || r.prefix > best.prefix) {
                    best = r;
                }
            }
        }
        return best != null ? best.nextHop : "DROP";
    }

    private static long ipToLong(String ip) {
        String[] p = ip.trim().split("\\\\.");
        return (Long.parseLong(p[0]) << 24) | (Long.parseLong(p[1]) << 16) | (Long.parseLong(p[2]) << 8) | Long.parseLong(p[3]);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution rtr = new Solution();
        rtr.addRoute("0.0.0.0/0", "DefaultGateway");
        rtr.addRoute("10.0.0.0/8", "CorporateBackbone");
        rtr.addRoute("10.20.0.0/16", "DatacenterVLAN");
        rtr.addRoute("10.20.30.0/24", "DatabaseRack");

        // Test 1: Most specific /24 match beats /16, /8, /0
        String h1 = rtr.route("10.20.30.55");
        if ("DatabaseRack".equals(h1)) {
            System.out.println("PASS: Test 1: Most specific /24 route selected");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected DatabaseRack, got " + h1);
        }

        // Test 2: Fallback to /16
        String h2 = rtr.route("10.20.99.12");
        if ("DatacenterVLAN".equals(h2)) {
            System.out.println("PASS: Test 2: /16 route selected over /8");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected DatacenterVLAN, got " + h2);
        }

        // Test 3: Fallback to default route 0.0.0.0/0
        String h3 = rtr.route("8.8.8.8");
        if ("DefaultGateway".equals(h3)) {
            System.out.println("PASS: Test 3: Default gateway selected for public IP");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected DefaultGateway, got " + h3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Store routes as (networkLong, maskLong, prefixInt, nextHopString).",
            "An IP matches a route if (destIpLong & route.mask) == route.network.",
            "Longest Prefix Match rule: among all matching routes, the route with the highest prefix length (e.g. /24 > /16 > /8 > /0) wins!"
        ],
        "example_java": """import java.util.*;

public class RouterLpm {
    public static void main(String[] args) {
        System.out.println("Longest Prefix Match (LPM) ensures most specific route wins.");
    }
}""",
        "example_kt": """object RouterLpm {
    fun main() {
        println("Longest Prefix Match (LPM) ensures most specific route wins.")
    }
}"""
    },
    "0010": {
        "title": "Port Address Translation (PAT) State Table",
        "key": "0010-pat-table",
        "desc": "Implement a stateful NAT/PAT engine translating private IP:port to public external ports.",
        "starter": """import java.util.*;

public class Solution {
    // 1. Translate outbound private IP:port -> return assigned unique external port
    public int translateOutbound(String privateIp, int privatePort) {
        // TODO: implement
        return -1;
    }

    // 2. Translate inbound packet on externalPort -> return original "privateIp:privatePort" (or null)
    public String translateInbound(int externalPort) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private static class Entry {
        String privateIp;
        int privatePort;
        int externalPort;
    }

    private int nextExtPort = 40000;
    private final Map<String, Entry> outTable = new HashMap<>();
    private final Map<Integer, Entry> inTable = new HashMap<>();

    public int translateOutbound(String privateIp, int privatePort) {
        String key = privateIp.trim() + ":" + privatePort;
        if (outTable.containsKey(key)) {
            return outTable.get(key).externalPort;
        }
        Entry e = new Entry();
        e.privateIp = privateIp.trim();
        e.privatePort = privatePort;
        e.externalPort = nextExtPort++;

        outTable.put(key, e);
        inTable.put(e.externalPort, e);
        return e.externalPort;
    }

    public String translateInbound(int externalPort) {
        Entry e = inTable.get(externalPort);
        return e != null ? e.privateIp + ":" + e.privatePort : null;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution pat = new Solution();

        // Test 1: Outbound translation assigns external port
        int ext1 = pat.translateOutbound("192.168.1.10", 5000);
        int ext2 = pat.translateOutbound("192.168.1.20", 5000);
        if (ext1 > 0 && ext2 > 0 && ext1 != ext2) {
            System.out.println("PASS: Test 1: Two hosts on same internal port get distinct external ports");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Port collision or invalid port");
        }

        // Test 2: Inbound reverse lookup
        String target1 = pat.translateInbound(ext1);
        if ("192.168.1.10:5000".equals(target1)) {
            System.out.println("PASS: Test 2: Inbound packet correctly demultiplexed to host 1");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 192.168.1.10:5000, got " + target1);
        }

        // Test 3: Unsolicited inbound probe dropped (null)
        if (pat.translateInbound(65530) == null) {
            System.out.println("PASS: Test 3: Unsolicited inbound packet blocked by NAT state table");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Unsolicited inbound packet should be blocked");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Maintain two HashMaps: one for outbound mapping (\"ip:port\" -> Entry) and one for inbound (externalPort -> Entry).",
            "Assign sequential ephemeral ports starting from 40000.",
            "If an outbound session already exists for that private IP:port, reuse its assigned external port!"
        ],
        "example_java": """import java.util.*;

public class PatExample {
    public static void main(String[] args) {
        System.out.println("PAT maps thousands of private sockets to one public IP via port rewriting.");
    }
}""",
        "example_kt": """object PatExample {
    fun main() {
        println("PAT maps thousands of private sockets to one public IP via port rewriting.")
    }
}"""
    },
    "0011": {
        "title": "IPv6 Full Expansion & Solicited-Node Multicast",
        "key": "0011-ipv6-util",
        "desc": "Implement IPv6 128-bit address expansion and Solicited-Node Multicast generation.",
        "starter": """public class Solution {
    // 1. Expand abbreviated IPv6 address (e.g. "2001:db8::1" -> "2001:0db8:0000:0000:0000:0000:0000:0001")
    public static String expand(String ipv6) {
        // TODO: implement
        return "";
    }

    // 2. Return RFC 4291 Solicited-Node Multicast address: "ff02::1:ff" + last 6 hex chars of address formatted as "ff02::1:ffXX:XXXX"
    public static String getSolicitedNodeMulticast(String ipv6) {
        // TODO: implement
        return "";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static String expand(String ipv6) {
        String clean = ipv6.trim().toLowerCase();
        String[] parts = clean.split("::", -1);
        List<String> groups = new ArrayList<>();
        if (parts.length == 2) {
            String[] left = parts[0].isEmpty() ? new String[0] : parts[0].split(":");
            String[] right = parts[1].isEmpty() ? new String[0] : parts[1].split(":");
            int missing = 8 - (left.length + right.length);
            Collections.addAll(groups, left);
            for (int i = 0; i < missing; i++) groups.add("0000");
            Collections.addAll(groups, right);
        } else {
            Collections.addAll(groups, clean.split(":"));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String g = groups.get(i);
            while (g.length() < 4) g = "0" + g;
            if (i > 0) sb.append(":");
            sb.append(g);
        }
        return sb.toString();
    }

    public static String getSolicitedNodeMulticast(String ipv6) {
        String full = expand(ipv6).replaceAll(":", "");
        String last6 = full.substring(full.length() - 6);
        return "ff02::1:ff" + last6.substring(0, 2) + ":" + last6.substring(2);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Expansion of ::
        String exp1 = Solution.expand("2001:db8::1");
        if ("2001:0db8:0000:0000:0000:0000:0000:0001".equals(exp1)) {
            System.out.println("PASS: Test 1: Expanded 2001:db8::1");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 2001:0db8:0000:0000:0000:0000:0000:0001, got " + exp1);
        }

        // Test 2: Expansion of loopback ::1
        String exp2 = Solution.expand("::1");
        if ("0000:0000:0000:0000:0000:0000:0000:0001".equals(exp2)) {
            System.out.println("PASS: Test 2: Expanded ::1");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: got " + exp2);
        }

        // Test 3: Solicited-Node Multicast
        String snm = Solution.getSolicitedNodeMulticast("fe80::200:5eff:fe00:101");
        if ("ff02::1:ff00:0101".equalsIgnoreCase(snm)) {
            System.out.println("PASS: Test 3: RFC 4291 Solicited-Node Multicast ff02::1:ff00:0101");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected ff02::1:ff00:0101, got " + snm);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Split by \"::\" to find left and right parts. The number of missing 16-bit blocks is 8 - (left.length + right.length).",
            "Left-pad every block with zeros until it has exactly 4 hex characters.",
            "The Solicited-Node Multicast address prefix is ff02::1:ff followed by the lowest 24 bits (last 6 hex characters of expanded address)."
        ],
        "example_java": """public class Ipv6Inspector {
    public static void main(String[] args) {
        System.out.println("IPv6 addresses have 128 bits, eliminating NAT.");
    }
}""",
        "example_kt": """object Ipv6Inspector {
    fun main() {
        println("IPv6 addresses have 128 bits, eliminating NAT.")
    }
}"""
    },
    "0012": {
        "title": "Multi-Hop Router Packet Trace Engine",
        "key": "0012-hop-engine",
        "desc": "Simulate packet forwarding across multiple router hops: TTL decrement, drop on expiry, and Layer 2 MAC address rewrites.",
        "starter": """public class Solution {
    public static class HopResult {
        public boolean dropped;
        public int newTtl;
        public String srcMac;
        public String dstMac;
    }

    // Process one router hop:
    // If ttl <= 1, mark dropped = true and newTtl = 0
    // Otherwise dropped = false, newTtl = ttl - 1, srcMac = egressSrcMac, dstMac = nextHopMac
    public static HopResult forwardHop(String inSrcMac, String inDstMac, int ttl, String egressSrcMac, String nextHopMac) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class HopResult {
        public boolean dropped;
        public int newTtl;
        public String srcMac;
        public String dstMac;
    }

    public static HopResult forwardHop(String inSrcMac, String inDstMac, int ttl, String egressSrcMac, String nextHopMac) {
        HopResult res = new HopResult();
        if (ttl <= 1) {
            res.dropped = true;
            res.newTtl = 0;
            return res;
        }
        res.dropped = false;
        res.newTtl = ttl - 1;
        res.srcMac = egressSrcMac;
        res.dstMac = nextHopMac;
        return res;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Normal hop
        Solution.HopResult h1 = Solution.forwardHop("A-MAC", "R1-IN-MAC", 64, "R1-OUT-MAC", "R2-IN-MAC");
        if (h1 != null && !h1.dropped && h1.newTtl == 63 && "R1-OUT-MAC".equals(h1.srcMac) && "R2-IN-MAC".equals(h1.dstMac)) {
            System.out.println("PASS: Test 1: TTL decremented to 63, MAC rewritten");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Normal forwarding failed");
        }

        // Test 2: Second hop
        Solution.HopResult h2 = Solution.forwardHop("R1-OUT-MAC", "R2-IN-MAC", 63, "R2-OUT-MAC", "B-MAC");
        if (h2 != null && !h2.dropped && h2.newTtl == 62 && "R2-OUT-MAC".equals(h2.srcMac) && "B-MAC".equals(h2.dstMac)) {
            System.out.println("PASS: Test 2: Second hop forwarded to destination B");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Second hop failed");
        }

        // Test 3: TTL expiry
        Solution.HopResult h3 = Solution.forwardHop("R1-OUT-MAC", "R2-IN-MAC", 1, "R2-OUT-MAC", "B-MAC");
        if (h3 != null && h3.dropped) {
            System.out.println("PASS: Test 3: Packet with TTL=1 dropped with ICMP Time Exceeded");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: TTL=1 should be dropped");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check if ttl <= 1: if so, set dropped = true and return.",
            "Otherwise, set newTtl = ttl - 1.",
            "The router rewrites Layer 2 MAC addresses on the outgoing frame: srcMac becomes the router's outgoing interface, dstMac becomes nextHopMac. Layer 3 IPs remain untouched!"
        ],
        "example_java": """public class MultiHopTrace {
    public static void main(String[] args) {
        System.out.println("Layer 2 MACs change at every hop; Layer 3 IPs remain end-to-end.");
    }
}""",
        "example_kt": """object MultiHopTrace {
    fun main() {
        println("Layer 2 MACs change at every hop; Layer 3 IPs remain end-to-end.")
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

    # Don't double inject
    if 'class="code-practice"' in html:
        print(f"Skipping {num}: already has code practice")
        return

    # Build injection block
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
    # Insert before <section>\s*<h2>Check yourself</h2>
    target_pattern = r'(<section>\s*<h2>Check yourself</h2>)'
    if not re.search(target_pattern, html):
        target_pattern = r'(<h2>Check yourself</h2>)'

    new_html = re.sub(target_pattern, block + "\n\\1", html, count=1)

    # Ensure scripts are loaded
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
