#!/usr/bin/env python3
"""
inject_part5.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 5 Cloud & Modern Infrastructure lessons (0051 to 0065).
"""

import os
import re
import json

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0051": {
        "title": "OpenFlow Flow Table Match-Action Engine",
        "key": "0051-openflow-engine",
        "desc": "Implement an SDN OpenFlow-style match-action flow table. Given a packet with (inPort, srcIp, dstIp, proto, dstPort) and a list of flow entries ordered by descending priority, find the highest-priority matching flow and return its action ('FORWARD:port', 'DROP', or 'TO_CONTROLLER'). Wildcard fields (0 or null or '*') match any value. If no rule matches, return 'DROP'.",
        "starter": """import java.util.*;

public class Solution {
    public static class FlowRule {
        public final int priority;
        public final String dstIpMatch; // specific IP or "*" for wildcard
        public final int dstPortMatch;  // specific port or 0 for wildcard
        public final String action;     // "FORWARD:port", "DROP", "TO_CONTROLLER"

        public FlowRule(int priority, String dstIpMatch, int dstPortMatch, String action) {
            this.priority = priority;
            this.dstIpMatch = dstIpMatch;
            this.dstPortMatch = dstPortMatch;
            this.action = action;
        }
    }

    public static String matchAndExecute(String packetDstIp, int packetDstPort, List<FlowRule> rules) {
        // TODO: Find highest priority rule matching packet and return its action.
        // Return "DROP" if no rule matches.
        return "DROP";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class FlowRule {
        public final int priority;
        public final String dstIpMatch;
        public final int dstPortMatch;
        public final String action;

        public FlowRule(int priority, String dstIpMatch, int dstPortMatch, String action) {
            this.priority = priority;
            this.dstIpMatch = dstIpMatch;
            this.dstPortMatch = dstPortMatch;
            this.action = action;
        }
    }

    public static String matchAndExecute(String packetDstIp, int packetDstPort, List<FlowRule> rules) {
        FlowRule bestMatch = null;
        for (FlowRule rule : rules) {
            boolean ipOk = rule.dstIpMatch.equals("*") || rule.dstIpMatch.equals(packetDstIp);
            boolean portOk = rule.dstPortMatch == 0 || rule.dstPortMatch == packetDstPort;
            if (ipOk && portOk) {
                if (bestMatch == null || rule.priority > bestMatch.priority) {
                    bestMatch = rule;
                }
            }
        }
        return bestMatch != null ? bestMatch.action : "DROP";
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.FlowRule> table = Arrays.asList(
            new Solution.FlowRule(10, "*", 0, "TO_CONTROLLER"),
            new Solution.FlowRule(100, "10.0.0.5", 80, "FORWARD:eth1"),
            new Solution.FlowRule(200, "10.0.0.5", 443, "FORWARD:eth2"),
            new Solution.FlowRule(50, "192.168.1.1", 0, "DROP")
        );

        // Test 1: Match high priority HTTPS
        String a1 = Solution.matchAndExecute("10.0.0.5", 443, table);
        if ("FORWARD:eth2".equals(a1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected FORWARD:eth2, got " + a1);
        }

        // Test 2: Match HTTP rule
        String a2 = Solution.matchAndExecute("10.0.0.5", 80, table);
        if ("FORWARD:eth1".equals(a2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected FORWARD:eth1, got " + a2);
        }

        // Test 3: Fallback to wildcard default (priority 10 TO_CONTROLLER)
        String a3 = Solution.matchAndExecute("172.16.0.1", 9000, table);
        if ("TO_CONTROLLER".equals(a3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected TO_CONTROLLER, got " + a3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Iterate through the rules and check if both dstIp and dstPort match the packet.",
            "Wildcards are indicated by '*' for dstIp and 0 for dstPort.",
            "Track the matching rule with the maximum priority. If multiple rules match, highest priority wins."
        ],
        "java_code": """// OpenFlow Pipeline Simulation in Modern Java
import java.util.*;

public class OpenFlowSwitch {
    public record FlowEntry(int priority, String matchIp, int matchPort, String action) {}

    public static String processPacket(String dstIp, int dstPort, List<FlowEntry> flowTable) {
        return flowTable.stream()
            .filter(r -> (r.matchIp().equals("*") || r.matchIp().equals(dstIp)) &&
                         (r.matchPort() == 0 || r.matchPort() == dstPort))
            .max(Comparator.comparingInt(FlowEntry::priority))
            .map(FlowEntry::action)
            .orElse("DROP");
    }

    public static void main(String[] args) {
        var table = List.of(
            new FlowEntry(10, "*", 0, "CONTROLLER"),
            new FlowEntry(100, "10.0.0.1", 80, "FORWARD:2")
        );
        System.out.println("Action: " + processPacket("10.0.0.1", 80, table)); // FORWARD:2
    }
}""",
        "kt_code": """// OpenFlow Pipeline Simulation in Kotlin
data class FlowEntry(val priority: Int, val matchIp: String, val matchPort: Int, val action: String)

class OpenFlowSwitch {
    fun processPacket(dstIp: String, dstPort: Int, table: List<FlowEntry>): String {
        return table.filter { (it.matchIp == "*" || it.matchIp == dstIp) &&
                              (it.matchPort == 0 || it.matchPort == dstPort) }
            .maxByOrNull { it.priority }
            ?.action ?: "DROP"
    }
}

fun main() {
    val table = listOf(
        FlowEntry(10, "*", 0, "CONTROLLER"),
        FlowEntry(100, "10.0.0.1", 80, "FORWARD:2")
    )
    val action = OpenFlowSwitch().processPacket("10.0.0.1", 80, table)
    println("Action: $action")
}"""
    },
    "0052": {
        "title": "Multi-AZ Cloud VPC Subnet & Usable IP Calculator",
        "key": "0052-vpc-subnet-calc",
        "desc": "Calculate usable IPs for AWS VPC subnets. AWS reserves 5 IP addresses per subnet (.0 Network, .1 VPC Router, .2 Amazon DNS, .3 Future reserved, .255 Broadcast). Also implement a CIDR containment check to verify that a subnet CIDR is fully contained within the VPC CIDR block.",
        "starter": """public class Solution {
    // 1. Calculate usable host IPs: 2^(32 - prefixLen) - 5 (AWS reserves 5 IPs)
    // Note: If prefixLen > 28, AWS does not allow subnets (must be /16 to /28), return 0.
    public static int calcUsableIps(int prefixLen) {
        // TODO: implement
        return 0;
    }

    // 2. Check if a subnet (e.g. "10.0.1.0/24") is strictly contained within VPC (e.g. "10.0.0.0/16")
    public static boolean isSubnetContained(String vpcCidr, String subnetCidr) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static int calcUsableIps(int prefixLen) {
        if (prefixLen < 16 || prefixLen > 28) return 0;
        int totalIps = 1 << (32 - prefixLen);
        return totalIps - 5;
    }

    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\\\.");
        long res = 0;
        for (String p : parts) {
            res = (res << 8) | Long.parseLong(p);
        }
        return res;
    }

    public static boolean isSubnetContained(String vpcCidr, String subnetCidr) {
        String[] vParts = vpcCidr.split("/");
        String[] sParts = subnetCidr.split("/");
        int vPrefix = Integer.parseInt(vParts[1]);
        int sPrefix = Integer.parseInt(sParts[1]);
        if (sPrefix < vPrefix) return false;

        long vMask = (0xFFFFFFFFL << (32 - vPrefix)) & 0xFFFFFFFFL;
        long vIp = ipToLong(vParts[0]);
        long sIp = ipToLong(sParts[0]);

        return (vIp & vMask) == (sIp & vMask);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: /24 subnet has 256 - 5 = 251 usable IPs in AWS
        int u24 = Solution.calcUsableIps(24);
        if (u24 == 251) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected 251, got " + u24);
        }

        // Test 2: Subnet containment inside VPC /16
        boolean inVpc = Solution.isSubnetContained("10.0.0.0/16", "10.0.5.0/24");
        if (inVpc) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected true for 10.0.5.0/24 inside 10.0.0.0/16");
        }

        // Test 3: Subnet outside VPC
        boolean outVpc = Solution.isSubnetContained("10.0.0.0/16", "192.168.1.0/24");
        if (!outVpc) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected false for 192.168.1.0/24 inside 10.0.0.0/16");
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "For usable IPs: $2^{32 - \text{prefix}} - 5$. If prefix < 16 or prefix > 28, return 0.",
            "Parse CIDR strings using split('/'), extract prefix length and base IP.",
            "Convert IPv4 strings to 32-bit unsigned longs and apply bitwise mask."
        ],
        "java_code": """// AWS VPC Subnet IP Calculator in Modern Java
public class VpcCalculator {
    public static int awsUsableIps(int prefix) {
        if (prefix < 16 || prefix > 28) return 0; // AWS VPC constraints
        return (1 << (32 - prefix)) - 5; // AWS reserves .0, .1, .2, .3, .255
    }

    public static void main(String[] args) {
        System.out.printf("/24 has %d usable hosts in AWS (256 - 5)%n", awsUsableIps(24)); // 251
        System.out.printf("/28 has %d usable hosts in AWS (16 - 5)%n", awsUsableIps(28));  // 11
    }
}""",
        "kt_code": """// AWS VPC Subnet IP Calculator in Kotlin
fun awsUsableIps(prefix: Int): Int =
    if (prefix in 16..28) (1 shl (32 - prefix)) - 5 else 0

fun main() {
    println("/24 usable: ${awsUsableIps(24)}") // 251
    println("/28 usable: ${awsUsableIps(28)}") // 11
}"""
    },
    "0053": {
        "title": "Cloud VPC Route Table Longest-Prefix Match (LPM)",
        "key": "0053-cloud-route-table",
        "desc": "Simulate an AWS VPC Route Table. Given destination routes with targets (e.g. 'local', 'igw-0123', 'nat-4567'), find the most specific route (longest prefix length) matching a destination IP. If none match, return 'NO_ROUTE'.",
        "starter": """import java.util.*;

public class Solution {
    public static class Route {
        public final String cidr;   // e.g. "10.0.0.0/16", "0.0.0.0/0", "10.0.1.0/24"
        public final String target; // e.g. "local", "igw-0a", "nat-0b"

        public Route(String cidr, String target) {
            this.cidr = cidr;
            this.target = target;
        }
    }

    public static String resolveTarget(String destinationIp, List<Route> routeTable) {
        // TODO: Find the matching route with the longest prefix length.
        // Return target or "NO_ROUTE".
        return "NO_ROUTE";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class Route {
        public final String cidr;
        public final String target;

        public Route(String cidr, String target) {
            this.cidr = cidr;
            this.target = target;
        }
    }

    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\\\.");
        long res = 0;
        for (String p : parts) res = (res << 8) | Long.parseLong(p);
        return res;
    }

    public static String resolveTarget(String destinationIp, List<Route> routeTable) {
        long dest = ipToLong(destinationIp);
        int maxPrefix = -1;
        String bestTarget = "NO_ROUTE";

        for (Route r : routeTable) {
            String[] parts = r.cidr.split("/");
            long net = ipToLong(parts[0]);
            int prefix = Integer.parseInt(parts[1]);
            long mask = prefix == 0 ? 0L : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;

            if ((dest & mask) == (net & mask)) {
                if (prefix > maxPrefix) {
                    maxPrefix = prefix;
                    bestTarget = r.target;
                }
            }
        }
        return bestTarget;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.Route> table = Arrays.asList(
            new Solution.Route("10.0.0.0/16", "local"),
            new Solution.Route("0.0.0.0/0", "nat-098abc"),
            new Solution.Route("10.0.1.0/24", "tgw-attach-01")
        );

        // Test 1: Subnet inside 10.0.1.0/24 matches most specific tgw-attach-01
        String t1 = Solution.resolveTarget("10.0.1.45", table);
        if ("tgw-attach-01".equals(t1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected tgw-attach-01, got " + t1);
        }

        // Test 2: Other internal IP matches local 10.0.0.0/16
        String t2 = Solution.resolveTarget("10.0.2.10", table);
        if ("local".equals(t2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected local, got " + t2);
        }

        // Test 3: Public Internet IP matches default 0.0.0.0/0
        String t3 = Solution.resolveTarget("93.184.216.34", table);
        if ("nat-098abc".equals(t3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected nat-098abc, got " + t3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Parse each route CIDR into a base IP and prefix length.",
            "A prefix of 0 matches all IPs with mask 0L.",
            "Compare `(destIp & mask) == (routeNet & mask)` and select the route with the highest prefix value."
        ],
        "java_code": """// Cloud Route Table Longest Prefix Match in Java
import java.util.*;

public class VpcRouteResolver {
    public record Route(String cidr, String target) {}

    public static String resolve(String destIp, List<Route> table) {
        long d = ipToLong(destIp);
        Route best = null;
        int maxLen = -1;
        for (var r : table) {
            var parts = r.cidr().split("/");
            long net = ipToLong(parts[0]);
            int len = Integer.parseInt(parts[1]);
            long mask = len == 0 ? 0L : (0xFFFFFFFFL << (32 - len)) & 0xFFFFFFFFL;
            if ((d & mask) == (net & mask) && len > maxLen) {
                maxLen = len;
                best = r;
            }
        }
        return best != null ? best.target() : "DROP";
    }

    private static long ipToLong(String ip) {
        long res = 0;
        for (var p : ip.split("\\\\.")) res = (res << 8) | Long.parseLong(p);
        return res;
    }
}""",
        "kt_code": """// Cloud Route Table Longest Prefix Match in Kotlin
data class Route(val cidr: String, val target: String)

fun resolveRoute(destIp: String, table: List<Route>): String {
    val d = ipToLong(destIp)
    return table.map { r ->
        val (ipStr, lenStr) = r.cidr.split("/")
        val len = lenStr.toInt()
        val mask = if (len == 0) 0L else (0xFFFFFFFFL shl (32 - len)) and 0xFFFFFFFFL
        val net = ipToLong(ipStr)
        Triple(r, len, (d and mask) == (net and mask))
    }.filter { it.third }
     .maxByOrNull { it.second }
     ?.first?.target ?: "DROP"
}

private fun ipToLong(ip: String) = ip.split(".").fold(0L) { acc, s -> (acc shl 8) or s.toLong() }"""
    },
    "0054": {
        "title": "Stateful Security Group & Stateless NACL Evaluator",
        "key": "0054-sg-vs-nacl",
        "desc": "Simulate the dual firewall layer in AWS: Network ACLs (stateless, evaluated in rule number order, first match wins) and Security Groups (stateful, allow-only, return traffic automatically allowed if outbound session is active). Evaluate an inbound packet.",
        "starter": """import java.util.*;

public class Solution {
    public static class NaclRule {
        public final int ruleNum;
        public final int port;      // 0 for wildcard
        public final boolean allow; // true = ALLOW, false = DENY

        public NaclRule(int ruleNum, int port, boolean allow) {
            this.ruleNum = ruleNum;
            this.port = port;
            this.allow = allow;
        }
    }

    public static class SgRule {
        public final int port; // port allowed

        public SgRule(int port) {
            this.port = port;
        }
    }

    // Returns true if inbound packet passes BOTH NACL and Security Group
    public static boolean evaluateInbound(
            int destPort,
            boolean isEstablishedReturnTraffic,
            List<NaclRule> naclRules,
            List<SgRule> sgRules) {
        // TODO: 1. Evaluate stateless NACL: lowest ruleNum wins. If no rule matches, NACL default denies (false).
        // TODO: 2. If NACL allows:
        //          - If isEstablishedReturnTraffic is true -> Security Group statefully allows!
        //          - Otherwise check if destPort matches any SgRule.
        return false;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class NaclRule {
        public final int ruleNum;
        public final int port;
        public final boolean allow;

        public NaclRule(int ruleNum, int port, boolean allow) {
            this.ruleNum = ruleNum;
            this.port = port;
            this.allow = allow;
        }
    }

    public static class SgRule {
        public final int port;

        public SgRule(int port) {
            this.port = port;
        }
    }

    public static boolean evaluateInbound(
            int destPort,
            boolean isEstablishedReturnTraffic,
            List<NaclRule> naclRules,
            List<SgRule> sgRules) {
        // 1. Evaluate NACL ordered by rule number ascending
        List<NaclRule> sorted = new ArrayList<>(naclRules);
        sorted.sort(Comparator.comparingInt(r -> r.ruleNum));

        boolean naclPassed = false;
        boolean naclMatched = false;
        for (NaclRule r : sorted) {
            if (r.port == 0 || r.port == destPort) {
                naclMatched = true;
                naclPassed = r.allow;
                break;
            }
        }
        if (!naclMatched || !naclPassed) return false;

        // 2. Evaluate Security Group
        if (isEstablishedReturnTraffic) {
            // SGs are stateful: return traffic automatically allowed regardless of inbound rules
            return true;
        }

        for (SgRule sg : sgRules) {
            if (sg.port == destPort) return true;
        }

        return false;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.NaclRule> nacls = Arrays.asList(
            new Solution.NaclRule(200, 0, true), // rule 200: allow all
            new Solution.NaclRule(100, 22, false) // rule 100: deny port 22
        );
        List<Solution.SgRule> sgs = Arrays.asList(
            new Solution.SgRule(443)
        );

        // Test 1: Port 22 is explicitly DENIED by NACL rule 100
        boolean t1 = Solution.evaluateInbound(22, false, nacls, sgs);
        if (!t1) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected port 22 to be denied by NACL");
        }

        // Test 2: Inbound HTTPS (443): NACL allows (rule 200), SG allows (port 443)
        boolean t2 = Solution.evaluateInbound(443, false, nacls, sgs);
        if (t2) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected port 443 to be allowed");
        }

        // Test 3: High ephemeral port (e.g. 52140) return traffic:
        // NACL allows (rule 200), SG statefully allows because it's established return traffic!
        boolean t3 = Solution.evaluateInbound(52140, true, nacls, sgs);
        if (t3) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected established return traffic to pass SG");
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Sort NACL rules by ruleNum ascending. The first rule matching the port (or port 0 for wildcard) decides.",
            "If no NACL rule matches, NACL default deny blocks the packet (return false).",
            "Security Groups are stateful: if isEstablishedReturnTraffic is true, SG automatically allows it!"
        ],
        "java_code": """// AWS SG vs NACL Evaluation in Java
import java.util.*;

public class CloudFirewall {
    public record NaclRule(int ruleNum, int port, boolean allow) {}

    public static boolean checkInbound(int port, boolean isReturn, List<NaclRule> nacls, Set<Integer> sgAllowedPorts) {
        // Stateless NACL: lowest number wins
        var naclDecision = nacls.stream()
            .sorted(Comparator.comparingInt(NaclRule::ruleNum))
            .filter(r -> r.port() == 0 || r.port() == port)
            .findFirst()
            .map(NaclRule::allow)
            .orElse(false);

        if (!naclDecision) return false;
        // Stateful SG: established return traffic automatically permitted
        return isReturn || sgAllowedPorts.contains(port);
    }
}""",
        "kt_code": """// AWS SG vs NACL Evaluation in Kotlin
data class NaclRule(val ruleNum: Int, val port: Int, val allow: Boolean)

fun checkInbound(port: Int, isReturn: Boolean, nacls: List<NaclRule>, sgPorts: Set<Int>): Boolean {
    val naclOk = nacls.sortedBy { it.ruleNum }
        .firstOrNull { it.port == 0 || it.port == port }
        ?.allow ?: false

    return naclOk && (isReturn || port in sgPorts)
}"""
    },
    "0055": {
        "title": "Transit Gateway (TGW) Hub-and-Spoke Route Table Router",
        "key": "0055-tgw-router",
        "desc": "Simulate an AWS Transit Gateway (TGW) route table. VPC attachments associate with a specific TGW route table. Look up the destination CIDR using longest prefix match and determine the egress attachment ('vpc-prod', 'vpc-shared', or 'BLACKHOLE').",
        "starter": """import java.util.*;

public class Solution {
    public static class TgwRoute {
        public final String cidr;
        public final String attachmentId; // e.g. "vpc-prod", "vpc-shared", "BLACKHOLE"

        public TgwRoute(String cidr, String attachmentId) {
            this.cidr = cidr;
            this.attachmentId = attachmentId;
        }
    }

    public static String forwardTgw(String destIp, List<TgwRoute> routeTable) {
        // TODO: Perform Longest Prefix Match against routeTable and return attachmentId.
        // Return "BLACKHOLE" if no match.
        return "BLACKHOLE";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class TgwRoute {
        public final String cidr;
        public final String attachmentId;

        public TgwRoute(String cidr, String attachmentId) {
            this.cidr = cidr;
            this.attachmentId = attachmentId;
        }
    }

    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\\\.");
        long res = 0;
        for (String p : parts) res = (res << 8) | Long.parseLong(p);
        return res;
    }

    public static String forwardTgw(String destIp, List<TgwRoute> routeTable) {
        long d = ipToLong(destIp);
        int maxLen = -1;
        String bestTarget = "BLACKHOLE";

        for (TgwRoute r : routeTable) {
            String[] p = r.cidr.split("/");
            long net = ipToLong(p[0]);
            int len = Integer.parseInt(p[1]);
            long mask = len == 0 ? 0L : (0xFFFFFFFFL << (32 - len)) & 0xFFFFFFFFL;

            if ((d & mask) == (net & mask) && len > maxLen) {
                maxLen = len;
                bestTarget = r.attachmentId;
            }
        }
        return bestTarget;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.TgwRoute> tgwTable = Arrays.asList(
            new Solution.TgwRoute("10.100.0.0/16", "vpc-prod"),
            new Solution.TgwRoute("10.200.0.0/16", "vpc-shared"),
            new Solution.TgwRoute("10.100.5.0/24", "vpc-prod-dmz"),
            new Solution.TgwRoute("0.0.0.0/0", "BLACKHOLE")
        );

        // Test 1: More specific /24 routed to vpc-prod-dmz
        String t1 = Solution.forwardTgw("10.100.5.22", tgwTable);
        if ("vpc-prod-dmz".equals(t1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected vpc-prod-dmz, got " + t1);
        }

        // Test 2: Standard prod IP routed to vpc-prod
        String t2 = Solution.forwardTgw("10.100.10.1", tgwTable);
        if ("vpc-prod".equals(t2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected vpc-prod, got " + t2);
        }

        // Test 3: Unmatched IP defaults to BLACKHOLE
        String t3 = Solution.forwardTgw("192.168.1.1", tgwTable);
        if ("BLACKHOLE".equals(t3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected BLACKHOLE, got " + t3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Convert IPv4 addresses to 32-bit unsigned longs.",
            "Iterate through routes, calculating subnet masks with `0xFFFFFFFFL << (32 - len)`.",
            "Track the matching route with the greatest prefix length."
        ],
        "java_code": """// AWS Transit Gateway Routing Simulator in Java
import java.util.*;

public class TgwRouter {
    public record TgwRoute(String cidr, String attachment) {}

    public static String route(String ip, List<TgwRoute> routes) {
        long addr = ipToLong(ip);
        TgwRoute best = null;
        int maxLen = -1;
        for (var r : routes) {
            var parts = r.cidr().split("/");
            long net = ipToLong(parts[0]);
            int len = Integer.parseInt(parts[1]);
            long mask = len == 0 ? 0L : (0xFFFFFFFFL << (32 - len)) & 0xFFFFFFFFL;
            if ((addr & mask) == (net & mask) && len > maxLen) {
                maxLen = len;
                best = r;
            }
        }
        return best != null ? best.attachment() : "BLACKHOLE";
    }

    private static long ipToLong(String ip) {
        long r = 0;
        for (var p : ip.split("\\\\.")) r = (r << 8) | Long.parseLong(p);
        return r;
    }
}""",
        "kt_code": """// AWS Transit Gateway Routing Simulator in Kotlin
data class TgwRoute(val cidr: String, val attachment: String)

fun routeTgw(ip: String, routes: List<TgwRoute>): String {
    val addr = ip.split(".").fold(0L) { acc, s -> (acc shl 8) or s.toLong() }
    return routes.map { r ->
        val (cidrIp, lenStr) = r.cidr.split("/")
        val len = lenStr.toInt()
        val mask = if (len == 0) 0L else (0xFFFFFFFFL shl (32 - len)) and 0xFFFFFFFFL
        val net = cidrIp.split(".").fold(0L) { acc, s -> (acc shl 8) or s.toLong() }
        Triple(r, len, (addr and mask) == (net and mask))
    }.filter { it.third }
     .maxByOrNull { it.second }
     ?.first?.attachment ?: "BLACKHOLE"
}"""
    },
    "0056": {
        "title": "PrivateLink Split-Horizon DNS Resolver",
        "key": "0056-privatelink-resolver",
        "desc": "Simulate AWS PrivateLink (Interface VPC Endpoint) DNS resolution. When 'Private DNS' is enabled for an endpoint service, queries for the service FQDN return the VPC ENI private IP; otherwise, they return the public service IP.",
        "starter": """public class Solution {
    public static String resolveServiceDns(
            String queryFqdn,
            String serviceFqdn,
            boolean privateDnsEnabled,
            String privateEniIp,
            String publicIp) {
        // TODO: If queryFqdn equals serviceFqdn:
        //       - return privateEniIp if privateDnsEnabled is true
        //       - return publicIp if privateDnsEnabled is false
        // If queryFqdn does not match, return "NXDOMAIN"
        return "NXDOMAIN";
    }
}""",
        "solution": """public class Solution {
    public static String resolveServiceDns(
            String queryFqdn,
            String serviceFqdn,
            boolean privateDnsEnabled,
            String privateEniIp,
            String publicIp) {
        if (!queryFqdn.equalsIgnoreCase(serviceFqdn)) {
            return "NXDOMAIN";
        }
        return privateDnsEnabled ? privateEniIp : publicIp;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        String fqdn = "s3.us-east-1.amazonaws.com";
        String privIp = "10.0.12.50";
        String pubIp = "52.216.142.3";

        // Test 1: Private DNS enabled returns private ENI IP
        String r1 = Solution.resolveServiceDns(fqdn, fqdn, true, privIp, pubIp);
        if (privIp.equals(r1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected " + privIp + ", got " + r1);
        }

        // Test 2: Private DNS disabled returns public IP
        String r2 = Solution.resolveServiceDns(fqdn, fqdn, false, privIp, pubIp);
        if (pubIp.equals(r2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected " + pubIp + ", got " + r2);
        }

        // Test 3: Unrelated FQDN returns NXDOMAIN
        String r3 = Solution.resolveServiceDns("unknown.internal", fqdn, true, privIp, pubIp);
        if ("NXDOMAIN".equals(r3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected NXDOMAIN, got " + r3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check case-insensitive equality between queryFqdn and serviceFqdn.",
            "If they match, return privateEniIp if privateDnsEnabled is true, else publicIp.",
            "If they do not match, return 'NXDOMAIN'."
        ],
        "java_code": """// AWS PrivateLink Split-Horizon DNS Resolver in Java
public class PrivateLinkResolver {
    public static String resolve(String query, String serviceFqdn, boolean privDns, String eniIp, String pubIp) {
        if (!query.equalsIgnoreCase(serviceFqdn)) return "NXDOMAIN";
        return privDns ? eniIp : pubIp;
    }

    public static void main(String[] args) {
        String s3 = "s3.us-east-1.amazonaws.com";
        System.out.println("Priv: " + resolve(s3, s3, true, "10.0.1.5", "52.216.1.1")); // 10.0.1.5
    }
}""",
        "kt_code": """// AWS PrivateLink Split-Horizon DNS Resolver in Kotlin
fun resolvePrivateLink(query: String, serviceFqdn: String, privDns: Boolean, eniIp: String, pubIp: String): String =
    if (query.equals(serviceFqdn, ignoreCase = true)) {
        if (privDns) eniIp else pubIp
    } else "NXDOMAIN"

fun main() {
    val s3 = "s3.us-east-1.amazonaws.com"
    println(resolvePrivateLink(s3, s3, true, "10.0.1.5", "52.216.1.1")) // 10.0.1.5
}"""
    },
    "0057": {
        "title": "DirectConnect Active/Standby BGP Failover Engine",
        "key": "0057-dx-vpn-failover",
        "desc": "Simulate hybrid cloud BGP path selection between Direct Connect (DX) and IPsec VPN. DX advertises AS path length 1. VPN advertises AS prepending (AS path length 3). If DX link status is healthy, DX wins (shortest AS path). If DX link drops, failover to VPN. If both down, return 'UNREACHABLE'.",
        "starter": """public class Solution {
    public static class HybridPath {
        public final String name; // "DIRECT_CONNECT" or "IPSEC_VPN"
        public final int asPathLength;
        public final boolean isLinkUp;

        public HybridPath(String name, int asPathLength, boolean isLinkUp) {
            this.name = name;
            this.asPathLength = asPathLength;
            this.isLinkUp = isLinkUp;
        }
    }

    public static String selectActivePath(HybridPath dx, HybridPath vpn) {
        // TODO: 1. If dx is up and vpn is up: select path with shortest asPathLength
        // TODO: 2. If only one is up, select that path
        // TODO: 3. If neither is up, return "UNREACHABLE"
        return "UNREACHABLE";
    }
}""",
        "solution": """public class Solution {
    public static class HybridPath {
        public final String name;
        public final int asPathLength;
        public final boolean isLinkUp;

        public HybridPath(String name, int asPathLength, boolean isLinkUp) {
            this.name = name;
            this.asPathLength = asPathLength;
            this.isLinkUp = isLinkUp;
        }
    }

    public static String selectActivePath(HybridPath dx, HybridPath vpn) {
        if (!dx.isLinkUp && !vpn.isLinkUp) return "UNREACHABLE";
        if (dx.isLinkUp && !vpn.isLinkUp) return dx.name;
        if (!dx.isLinkUp && vpn.isLinkUp) return vpn.name;

        // Both up: shortest AS path wins
        return dx.asPathLength <= vpn.asPathLength ? dx.name : vpn.name;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution.HybridPath dxUp = new Solution.HybridPath("DIRECT_CONNECT", 1, true);
        Solution.HybridPath dxDown = new Solution.HybridPath("DIRECT_CONNECT", 1, false);
        Solution.HybridPath vpnUp = new Solution.HybridPath("IPSEC_VPN", 3, true);
        Solution.HybridPath vpnDown = new Solution.HybridPath("IPSEC_VPN", 3, false);

        // Test 1: Both UP -> DX wins because AS path length (1 < 3)
        String p1 = Solution.selectActivePath(dxUp, vpnUp);
        if ("DIRECT_CONNECT".equals(p1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected DIRECT_CONNECT, got " + p1);
        }

        // Test 2: DX link down -> Failover to IPSEC_VPN
        String p2 = Solution.selectActivePath(dxDown, vpnUp);
        if ("IPSEC_VPN".equals(p2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected IPSEC_VPN, got " + p2);
        }

        // Test 3: Both links down -> UNREACHABLE
        String p3 = Solution.selectActivePath(dxDown, vpnDown);
        if ("UNREACHABLE".equals(p3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected UNREACHABLE, got " + p3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check link health first. If only one path is up, return its name immediately.",
            "If both paths are up, compare `asPathLength`; the smaller length wins.",
            "If neither path is up, return 'UNREACHABLE'."
        ],
        "java_code": """// Hybrid Cloud BGP Path Selection in Java
public class HybridPathEngine {
    public record Path(String name, int asPathLen, boolean up) {}

    public static String pickBestPath(Path dx, Path vpn) {
        if (!dx.up() && !vpn.up()) return "UNREACHABLE";
        if (!dx.up()) return vpn.name();
        if (!vpn.up()) return dx.name();
        return dx.asPathLen() <= vpn.asPathLen() ? dx.name() : vpn.name();
    }
}""",
        "kt_code": """// Hybrid Cloud BGP Path Selection in Kotlin
data class Path(val name: String, val asPathLen: Int, val up: Boolean)

fun pickBestPath(dx: Path, vpn: Path): String = when {
    !dx.up && !vpn.up -> "UNREACHABLE"
    !dx.up -> vpn.name
    !vpn.up -> dx.name
    dx.asPathLen <= vpn.asPathLen -> dx.name
    else -> vpn.name
}"""
    },
    "0058": {
        "title": "CDN Edge Cache-Control TTL & Freshness Engine",
        "key": "0058-cdn-cache-engine",
        "desc": "Simulate a CDN edge cache freshness evaluator. Parse Cache-Control directives ('s-maxage=N', 'max-age=N', 'no-store'). Given cached timestamp and current timestamp, determine if the cached asset is 'HIT' (fresh), 'STALE' (expired, needs revalidation), or 'BYPASS' (no-store). Note: CDN honors 's-maxage' over 'max-age'.",
        "starter": """import java.util.*;

public class Solution {
    public static String evaluateCdnCache(
            String cacheControlHeader,
            long cachedAtEpochSec,
            long nowEpochSec) {
        // TODO: 1. If cacheControl contains "no-store", return "BYPASS".
        // TODO: 2. Extract s-maxage if present; otherwise max-age.
        // TODO: 3. Calculate age = nowEpochSec - cachedAtEpochSec.
        // TODO: 4. If age <= ttl, return "HIT"; else return "STALE".
        return "STALE";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static String evaluateCdnCache(
            String cacheControlHeader,
            long cachedAtEpochSec,
            long nowEpochSec) {
        if (cacheControlHeader == null) return "STALE";
        String lower = cacheControlHeader.toLowerCase();
        if (lower.contains("no-store")) return "BYPASS";

        long ttl = -1;
        // Check for s-maxage first (shared cache CDN preference)
        for (String part : lower.split(",")) {
            String p = part.trim();
            if (p.startsWith("s-maxage=")) {
                ttl = Long.parseLong(p.substring("s-maxage=".length()));
                break;
            }
        }
        if (ttl == -1) {
            for (String part : lower.split(",")) {
                String p = part.trim();
                if (p.startsWith("max-age=")) {
                    ttl = Long.parseLong(p.substring("max-age=".length()));
                    break;
                }
            }
        }

        if (ttl <= 0) return "STALE";
        long age = nowEpochSec - cachedAtEpochSec;
        return age <= ttl ? "HIT" : "STALE";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        long cachedAt = 1700000000L;

        // Test 1: Fresh asset within s-maxage (s-maxage=3600, age=300s -> HIT)
        String r1 = Solution.evaluateCdnCache("public, max-age=60, s-maxage=3600", cachedAt, cachedAt + 300);
        if ("HIT".equals(r1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected HIT, got " + r1);
        }

        // Test 2: Expired asset (max-age=60, age=120s -> STALE)
        String r2 = Solution.evaluateCdnCache("public, max-age=60", cachedAt, cachedAt + 120);
        if ("STALE".equals(r2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected STALE, got " + r2);
        }

        // Test 3: no-store header -> BYPASS
        String r3 = Solution.evaluateCdnCache("no-store, no-cache", cachedAt, cachedAt + 10);
        if ("BYPASS".equals(r3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected BYPASS, got " + r3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check for 'no-store' first; if present, immediately return 'BYPASS'.",
            "In CDNs, 's-maxage' takes precedence over 'max-age'. Parse comma-separated tokens.",
            "Compare `nowEpochSec - cachedAtEpochSec` against the effective TTL."
        ],
        "java_code": """// CDN Edge Cache-Control Evaluator in Java
public class CdnCacheEngine {
    public static String evaluate(String cc, long cachedAt, long now) {
        if (cc == null || cc.contains("no-store")) return "BYPASS";
        long ttl = -1;
        for (var token : cc.toLowerCase().split(",")) {
            var t = token.trim();
            if (t.startsWith("s-maxage=")) ttl = Long.parseLong(t.substring(9));
            else if (ttl == -1 && t.startsWith("max-age=")) ttl = Long.parseLong(t.substring(8));
        }
        return (ttl > 0 && (now - cachedAt) <= ttl) ? "HIT" : "STALE";
    }
}""",
        "kt_code": """// CDN Edge Cache-Control Evaluator in Kotlin
fun evaluateCdn(cc: String?, cachedAt: Long, now: Long): String {
    if (cc == null || "no-store" in cc) return "BYPASS"
    val tokens = cc.lowercase().split(",").map { it.trim() }
    val sMax = tokens.firstOrNull { it.startsWith("s-maxage=") }?.substringAfter("=")?.toLongOrNull()
    val max = tokens.firstOrNull { it.startsWith("max-age=") }?.substringAfter("=")?.toLongOrNull()
    val ttl = sMax ?: max ?: return "STALE"
    return if (now - cachedAt <= ttl) "HIT" else "STALE"
}"""
    },
    "0059": {
        "title": "ALB L7 Path Router & NLB L4 5-Tuple Hash Balancer",
        "key": "0059-alb-nlb-balancer",
        "desc": "Simulate cloud load balancing architectures: 1) ALB L7 Host & Path pattern matching (e.g. host matches 'api.*' and path starts with '/v2' -> 'tg-v2'); 2) NLB L4 consistent 5-tuple hash ((srcIp + srcPort + dstIp + dstPort + proto).hashCode() % targetCount) for stateless, deterministic packet steering.",
        "starter": """import java.util.*;

public class Solution {
    public static class AlbRule {
        public final String hostPattern; // e.g. "api.example.com" or "*"
        public final String pathPrefix;  // e.g. "/v2"
        public final String targetGroup;

        public AlbRule(String hostPattern, String pathPrefix, String targetGroup) {
            this.hostPattern = hostPattern;
            this.pathPrefix = pathPrefix;
            this.targetGroup = targetGroup;
        }
    }

    // 1. ALB L7 matching: First rule whose host and path prefix match
    public static String routeAlb(String host, String path, List<AlbRule> rules) {
        // TODO: implement
        return "tg-default";
    }

    // 2. NLB L4 5-tuple hash index: Math.abs(Objects.hash(srcIp, srcPort, dstIp, dstPort, proto)) % targetCount
    public static int routeNlb(String srcIp, int srcPort, String dstIp, int dstPort, String proto, int targetCount) {
        // TODO: implement
        return 0;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class AlbRule {
        public final String hostPattern;
        public final String pathPrefix;
        public final String targetGroup;

        public AlbRule(String hostPattern, String pathPrefix, String targetGroup) {
            this.hostPattern = hostPattern;
            this.pathPrefix = pathPrefix;
            this.targetGroup = targetGroup;
        }
    }

    public static String routeAlb(String host, String path, List<AlbRule> rules) {
        for (AlbRule r : rules) {
            boolean hostMatch = r.hostPattern.equals("*") || r.hostPattern.equalsIgnoreCase(host);
            boolean pathMatch = path.startsWith(r.pathPrefix);
            if (hostMatch && pathMatch) {
                return r.targetGroup;
            }
        }
        return "tg-default";
    }

    public static int routeNlb(String srcIp, int srcPort, String dstIp, int dstPort, String proto, int targetCount) {
        int hash = Objects.hash(srcIp, srcPort, dstIp, dstPort, proto);
        return Math.floorMod(hash, targetCount);
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.AlbRule> rules = Arrays.asList(
            new Solution.AlbRule("api.example.com", "/users", "tg-users"),
            new Solution.AlbRule("api.example.com", "/orders", "tg-orders"),
            new Solution.AlbRule("*", "/", "tg-web")
        );

        // Test 1: ALB route matches specific host and path prefix
        String r1 = Solution.routeAlb("api.example.com", "/users/123", rules);
        if ("tg-users".equals(r1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected tg-users, got " + r1);
        }

        // Test 2: ALB fallback to wildcard root rule
        String r2 = Solution.routeAlb("app.example.com", "/about", rules);
        if ("tg-web".equals(r2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected tg-web, got " + r2);
        }

        // Test 3: NLB 5-tuple hash distribution is deterministic
        int b1 = Solution.routeNlb("192.168.1.10", 49152, "10.0.0.1", 443, "TCP", 4);
        int b2 = Solution.routeNlb("192.168.1.10", 49152, "10.0.0.1", 443, "TCP", 4);
        if (b1 == b2 && b1 >= 0 && b1 < 4) {
            passed++;
        } else {
            System.err.println("Test 3 failed: NLB hashing was non-deterministic: " + b1 + " vs " + b2);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "For ALB: check `(hostPattern.equals('*') || hostPattern.equalsIgnoreCase(host))` and `path.startsWith(pathPrefix)`.",
            "For NLB: use `Objects.hash(...)` and `Math.floorMod(hash, targetCount)` to ensure a non-negative index.",
            "Return the first matching ALB target group or fallback to 'tg-default'."
        ],
        "java_code": """// Cloud ALB vs NLB Routing in Java
import java.util.*;

public class LoadBalancerSimulator {
    public record AlbRule(String host, String prefix, String target) {}

    public static String routeAlb(String host, String path, List<AlbRule> rules) {
        return rules.stream()
            .filter(r -> (r.host().equals("*") || r.host().equalsIgnoreCase(host)) && path.startsWith(r.prefix()))
            .map(AlbRule::target)
            .findFirst()
            .orElse("tg-default");
    }

    public static int routeNlb(String sIp, int sPort, String dIp, int dPort, String proto, int targets) {
        return Math.floorMod(Objects.hash(sIp, sPort, dIp, dPort, proto), targets);
    }
}""",
        "kt_code": """// Cloud ALB vs NLB Routing in Kotlin
import java.util.Objects

data class AlbRule(val host: String, val prefix: String, val target: String)

fun routeAlb(host: String, path: String, rules: List<AlbRule>): String =
    rules.firstOrNull { (it.host == "*" || it.host.equals(host, true)) && path.startsWith(it.prefix) }
        ?.target ?: "tg-default"

fun routeNlb(sIp: String, sPort: Int, dIp: String, dPort: Int, proto: String, targets: Int): Int =
    Math.floorMod(Objects.hash(sIp, sPort, dIp, dPort, proto), targets)"""
    },
    "0060": {
        "title": "Kubernetes Pod veth Bridge Forwarding Engine",
        "key": "0060-k8s-veth-bridge",
        "desc": "Simulate the Kubernetes host bridge (e.g. cbr0) connecting Pod network namespaces via veth pairs. Given an incoming packet with dstMac and ingress interface, consult the bridge MAC forwarding table. If known and not the ingress interface, forward to egress interface; if unknown, FLOOD; if matching ingress interface, DROP.",
        "starter": """import java.util.*;

public class Solution {
    public static String bridgeForward(
            String ingressVeth,
            String dstMac,
            Map<String, String> macToVethTable) {
        // TODO: 1. Look up dstMac in macToVethTable
        // TODO: 2. If unknown, return "FLOOD"
        // TODO: 3. If dstMac interface equals ingressVeth, return "DROP"
        // TODO: 4. Otherwise return the target veth interface name
        return "DROP";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static String bridgeForward(
            String ingressVeth,
            String dstMac,
            Map<String, String> macToVethTable) {
        String targetVeth = macToVethTable.get(dstMac);
        if (targetVeth == null) {
            return "FLOOD";
        }
        if (targetVeth.equals(ingressVeth)) {
            return "DROP";
        }
        return targetVeth;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Map<String, String> fdb = new HashMap<>();
        fdb.put("02:42:0a:01:00:02", "veth-pod-a");
        fdb.put("02:42:0a:01:00:03", "veth-pod-b");
        fdb.put("02:42:0a:01:00:04", "veth-pod-c");

        // Test 1: Forward from pod-a to pod-b
        String f1 = Solution.bridgeForward("veth-pod-a", "02:42:0a:01:00:03", fdb);
        if ("veth-pod-b".equals(f1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected veth-pod-b, got " + f1);
        }

        // Test 2: Unknown MAC triggers FLOOD
        String f2 = Solution.bridgeForward("veth-pod-a", "02:42:ff:ff:ff:ff", fdb);
        if ("FLOOD".equals(f2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected FLOOD, got " + f2);
        }

        // Test 3: Destination on same interface gets DROPPED
        String f3 = Solution.bridgeForward("veth-pod-a", "02:42:0a:01:00:02", fdb);
        if ("DROP".equals(f3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected DROP, got " + f3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check `macToVethTable.get(dstMac)`.",
            "If null, return 'FLOOD'.",
            "If `targetVeth.equals(ingressVeth)`, return 'DROP' (bridge does not echo back onto ingress port)."
        ],
        "java_code": """// Kubernetes Linux Bridge & veth pair simulation in Java
import java.util.*;

public class K8sBridgeSimulator {
    public static String forward(String inIface, String dstMac, Map<String, String> fdb) {
        var out = fdb.get(dstMac);
        if (out == null) return "FLOOD";
        return out.equals(inIface) ? "DROP" : out;
    }
}""",
        "kt_code": """// Kubernetes Linux Bridge & veth pair simulation in Kotlin
fun forwardBridge(inIface: String, dstMac: String, fdb: Map<String, String>): String {
    val out = fdb[dstMac] ?: return "FLOOD"
    return if (out == inIface) "DROP" else out
}"""
    },
    "0061": {
        "title": "CNI IPAM Subnet IP Allocator & Reclaimer",
        "key": "0061-cni-ipam-allocator",
        "desc": "Implement a Container Network Interface (CNI) host-local IPAM allocator. Given a node's podCIDR (e.g. '10.244.1.0/24') and a set of already allocated IP strings, allocate the next available sequential IP starting at .2 (.1 is the host bridge cbr0; .255 is broadcast). Return null if the subnet is exhausted.",
        "starter": """import java.util.*;

public class Solution {
    // Allocates next free IP from podCidr (e.g. "10.244.1.0/24"), starting host index at .2 up to .254
    public static String allocatePodIp(String podCidr, Set<String> allocatedIps) {
        // TODO: parse prefix and subnet base, iterate 2 to 254, return first unallocated IP
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static String allocatePodIp(String podCidr, Set<String> allocatedIps) {
        String baseIp = podCidr.split("/")[0];
        String[] octets = baseIp.split("\\\\.");
        String prefix = octets[0] + "." + octets[1] + "." + octets[2] + ".";

        for (int host = 2; host <= 254; host++) {
            String candidate = prefix + host;
            if (!allocatedIps.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Set<String> inUse = new HashSet<>(Arrays.asList("10.244.1.2", "10.244.1.3"));

        // Test 1: Next free IP is .4
        String ip1 = Solution.allocatePodIp("10.244.1.0/24", inUse);
        if ("10.244.1.4".equals(ip1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected 10.244.1.4, got " + ip1);
        }

        // Test 2: Fill subnet and verify next allocation
        inUse.add("10.244.1.4");
        inUse.add("10.244.1.5");
        String ip2 = Solution.allocatePodIp("10.244.1.0/24", inUse);
        if ("10.244.1.6".equals(ip2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected 10.244.1.6, got " + ip2);
        }

        // Test 3: Exhaustion returns null
        Set<String> full = new HashSet<>();
        for (int i = 2; i <= 254; i++) full.add("10.244.1." + i);
        String ip3 = Solution.allocatePodIp("10.244.1.0/24", full);
        if (ip3 == null) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected null on exhaustion, got " + ip3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Extract the first three octets of the CIDR (e.g. '10.244.1.').",
            "Loop from host = 2 to 254.",
            "Return the first candidate not in allocatedIps; if loop finishes, return null."
        ],
        "java_code": """// CNI Host-Local IPAM Allocator in Java
import java.util.*;

public class CniIpam {
    public static Optional<String> allocate(String cidr, Set<String> used) {
        var base = cidr.substring(0, cidr.lastIndexOf('.') + 1);
        for (int i = 2; i <= 254; i++) {
            var ip = base + i;
            if (!used.contains(ip)) return Optional.of(ip);
        }
        return Optional.empty();
    }
}""",
        "kt_code": """// CNI Host-Local IPAM Allocator in Kotlin
fun allocatePodIp(cidr: String, used: Set<String>): String? {
    val base = cidr.substringBeforeLast(".") + "."
    return (2..254).map { "$base$it" }.firstOrNull { it !in used }
}"""
    },
    "0062": {
        "title": "kube-proxy iptables Random DNAT Balancer",
        "key": "0062-kubeproxy-dnat",
        "desc": "Simulate kube-proxy iptables DNAT service routing. In iptables mode, kube-proxy chains endpoints using statistic mode random with probability 1/N, 1/(N-1)... 1.0. Given a list of pod endpoint IPs and a pseudo-random value r (0.0 <= r < 1.0), select the target pod endpoint according to equal distribution.",
        "starter": """import java.util.*;

public class Solution {
    // Selects pod endpoint uniformly at random using value r (0.0 <= r < 1.0)
    public static String selectEndpoint(List<String> endpoints, double r) {
        // TODO: Calculate endpoint index = (int)(r * endpoints.size())
        // Return selected endpoint
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static String selectEndpoint(List<String> endpoints, double r) {
        if (endpoints == null || endpoints.isEmpty()) return null;
        int idx = (int) (r * endpoints.size());
        if (idx >= endpoints.size()) idx = endpoints.size() - 1;
        return endpoints.get(idx);
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<String> eps = Arrays.asList("10.244.1.5:8080", "10.244.2.8:8080", "10.244.3.12:8080");

        // Test 1: r = 0.1 -> index 0
        String e1 = Solution.selectEndpoint(eps, 0.1);
        if ("10.244.1.5:8080".equals(e1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected index 0, got " + e1);
        }

        // Test 2: r = 0.5 -> index 1
        String e2 = Solution.selectEndpoint(eps, 0.5);
        if ("10.244.2.8:8080".equals(e2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected index 1, got " + e2);
        }

        // Test 3: r = 0.9 -> index 2
        String e3 = Solution.selectEndpoint(eps, 0.9);
        if ("10.244.3.12:8080".equals(e3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected index 2, got " + e3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Scale r by the size of the endpoints list: `int idx = (int)(r * endpoints.size())`.",
            "Guard against edge cases where r == 1.0 by clamping to `endpoints.size() - 1`.",
            "Return `endpoints.get(idx)`."
        ],
        "java_code": """// kube-proxy iptables Random DNAT selection in Java
import java.util.*;

public class KubeProxyDnat {
    public static String route(List<String> endpoints, double r) {
        if (endpoints.isEmpty()) return null;
        int idx = Math.min((int)(r * endpoints.size()), endpoints.size() - 1);
        return endpoints.get(idx);
    }
}""",
        "kt_code": """// kube-proxy iptables Random DNAT selection in Kotlin
fun selectKubeEndpoint(endpoints: List<String>, r: Double): String? {
    if (endpoints.isEmpty()) return null
    val idx = (r * endpoints.size).toInt().coerceIn(0, endpoints.lastIndex)
    return endpoints[idx]
}"""
    },
    "0063": {
        "title": "eBPF XDP Packet Parser & Fast Hash Drop Filter",
        "key": "0063-ebpf-xdp-filter",
        "desc": "Simulate an eBPF XDP (eXpress Data Path) program in the network driver. Given raw Ethernet + IPv4 bytes, extract the source IPv4 address (offset 26..29). Check if the source IP is in a blocked IPs hash set. If blocked, return XDP_DROP (1); otherwise return XDP_PASS (2). If packet is shorter than 34 bytes (14 Ethernet + 20 IP) or not IPv4 (EtherType != 0x0800), return XDP_PASS (2).",
        "starter": """import java.util.*;

public class Solution {
    public static final int XDP_DROP = 1;
    public static final int XDP_PASS = 2;

    public static int xdpFilter(byte[] pkt, Set<String> blockedIps) {
        // TODO: 1. Check packet length >= 34
        // TODO: 2. Check EtherType at offset 12..13 is 0x0800 (IPv4)
        // TODO: 3. Extract src IP at bytes 26..29 and format as "a.b.c.d"
        // TODO: 4. If blockedIps contains src IP, return XDP_DROP; else XDP_PASS
        return XDP_PASS;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static final int XDP_DROP = 1;
    public static final int XDP_PASS = 2;

    public static int xdpFilter(byte[] pkt, Set<String> blockedIps) {
        if (pkt == null || pkt.length < 34) return XDP_PASS;
        int etherType = ((pkt[12] & 0xFF) << 8) | (pkt[13] & 0xFF);
        if (etherType != 0x0800) return XDP_PASS;

        String srcIp = String.format("%d.%d.%d.%d",
            pkt[26] & 0xFF,
            pkt[27] & 0xFF,
            pkt[28] & 0xFF,
            pkt[29] & 0xFF
        );

        return blockedIps.contains(srcIp) ? XDP_DROP : XDP_PASS;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Set<String> blocklist = new HashSet<>(Arrays.asList("198.51.100.4", "203.0.113.88"));

        // Build valid 34-byte packet from 198.51.100.4 (blocked)
        byte[] pBlocked = new byte[34];
        pBlocked[12] = 0x08; pBlocked[13] = 0x00; // EtherType 0x0800
        pBlocked[26] = (byte)198; pBlocked[27] = 51; pBlocked[28] = 100; pBlocked[29] = 4;

        // Build valid packet from 10.0.0.1 (allowed)
        byte[] pAllowed = new byte[34];
        pAllowed[12] = 0x08; pAllowed[13] = 0x00;
        pAllowed[26] = 10; pAllowed[27] = 0; pAllowed[28] = 0; pAllowed[29] = 1;

        // Test 1: Blocked IP returns XDP_DROP
        int a1 = Solution.xdpFilter(pBlocked, blocklist);
        if (a1 == Solution.XDP_DROP) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected XDP_DROP (1), got " + a1);
        }

        // Test 2: Clean IP returns XDP_PASS
        int a2 = Solution.xdpFilter(pAllowed, blocklist);
        if (a2 == Solution.XDP_PASS) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected XDP_PASS (2), got " + a2);
        }

        // Test 3: Short packet passes without crash
        int a3 = Solution.xdpFilter(new byte[10], blocklist);
        if (a3 == Solution.XDP_PASS) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected XDP_PASS on short packet, got " + a3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Offset 12 and 13 represent the 16-bit EtherType: `((pkt[12] & 0xFF) << 8) | (pkt[13] & 0xFF)`.",
            "IPv4 source address starts at byte offset 26 through 29.",
            "Format the octets with `& 0xFF` to avoid negative signed byte values."
        ],
        "java_code": """// eBPF XDP Packet Filter in Java
import java.util.*;

public class EbpfXdpSimulator {
    public static final int XDP_DROP = 1;
    public static final int XDP_PASS = 2;

    public static int runXdp(byte[] frame, Set<String> bpfBlockMap) {
        if (frame.length < 34) return XDP_PASS;
        int etherType = ((frame[12] & 0xFF) << 8) | (frame[13] & 0xFF);
        if (etherType != 0x0800) return XDP_PASS;
        var src = (frame[26] & 0xFF) + "." + (frame[27] & 0xFF) + "." +
                  (frame[28] & 0xFF) + "." + (frame[29] & 0xFF);
        return bpfBlockMap.contains(src) ? XDP_DROP : XDP_PASS;
    }
}""",
        "kt_code": """// eBPF XDP Packet Filter in Kotlin
const val XDP_DROP = 1
const val XDP_PASS = 2

fun runXdp(frame: ByteArray, bpfBlockMap: Set<String>): Int {
    if (frame.size < 34) return XDP_PASS
    val etherType = ((frame[12].toInt() and 0xFF) shl 8) or (frame[13].toInt() and 0xFF)
    if (etherType != 0x0800) return XDP_PASS
    val src = "${frame[26].toInt() and 0xFF}.${frame[27].toInt() and 0xFF}.${frame[28].toInt() and 0xFF}.${frame[29].toInt() and 0xFF}"
    return if (src in bpfBlockMap) XDP_DROP else XDP_PASS
}"""
    },
    "0064": {
        "title": "Envoy Weighted Traffic Splitter & SPIFFE Validator",
        "key": "0064-envoy-service-mesh",
        "desc": "Simulate Envoy sidecar proxy mechanics: 1) Weighted canary routing (e.g. Cluster A: 90%, Cluster B: 10%). Given integer roll (1..100), return target cluster name; 2) SPIFFE mTLS ID validation: verify that a client's SAN URI matches 'spiffe://<expectedDomain>/ns/<expectedNamespace>/sa/<serviceAccount>'.",
        "starter": """import java.util.*;

public class Solution {
    public static class WeightedCluster {
        public final String name;
        public final int weight; // e.g. 90 or 10 (sum = 100)

        public WeightedCluster(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }

    // 1. Given roll (1..100), iterate clusters accumulating weight until roll <= cumulative
    public static String selectCanaryCluster(int roll, List<WeightedCluster> clusters) {
        // TODO: implement
        return null;
    }

    // 2. Validate SPIFFE ID matches spiffe://<domain>/ns/<ns>/sa/*
    public static boolean validateSpiffeId(String sanUri, String expectedDomain, String expectedNamespace) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class WeightedCluster {
        public final String name;
        public final int weight;

        public WeightedCluster(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }

    public static String selectCanaryCluster(int roll, List<WeightedCluster> clusters) {
        int acc = 0;
        for (WeightedCluster c : clusters) {
            acc += c.weight;
            if (roll <= acc) {
                return c.name;
            }
        }
        return clusters.isEmpty() ? null : clusters.get(clusters.size() - 1).name;
    }

    public static boolean validateSpiffeId(String sanUri, String expectedDomain, String expectedNamespace) {
        if (sanUri == null || !sanUri.startsWith("spiffe://")) return false;
        String prefix = "spiffe://" + expectedDomain + "/ns/" + expectedNamespace + "/sa/";
        return sanUri.startsWith(prefix) && sanUri.length() > prefix.length();
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Solution.WeightedCluster> clusters = Arrays.asList(
            new Solution.WeightedCluster("payment-v1", 90),
            new Solution.WeightedCluster("payment-v2", 10)
        );

        // Test 1: roll 45 falls into 90% v1
        String c1 = Solution.selectCanaryCluster(45, clusters);
        if ("payment-v1".equals(c1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected payment-v1, got " + c1);
        }

        // Test 2: roll 95 falls into 10% canary v2
        String c2 = Solution.selectCanaryCluster(95, clusters);
        if ("payment-v2".equals(c2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected payment-v2, got " + c2);
        }

        // Test 3: SPIFFE validation
        boolean ok = Solution.validateSpiffeId("spiffe://prod.cluster/ns/payments/sa/checkout-svc", "prod.cluster", "payments");
        boolean badNs = Solution.validateSpiffeId("spiffe://prod.cluster/ns/analytics/sa/spy", "prod.cluster", "payments");
        if (ok && !badNs) {
            passed++;
        } else {
            System.err.println("Test 3 failed: SPIFFE validation incorrect");
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "For weighted clusters: loop through and accumulate `acc += cluster.weight`. If `roll <= acc`, return that cluster name.",
            "For SPIFFE ID: build `spiffe://` + domain + `/ns/` + ns + `/sa/`.",
            "Check `sanUri.startsWith(prefix)` and ensure there is a non-empty service account name at the end."
        ],
        "java_code": """// Envoy Weighted Cluster & SPIFFE Validator in Java
import java.util.*;

public class EnvoyProxySimulator {
    public record ClusterWeight(String name, int weight) {}

    public static String routeCanary(int roll, List<ClusterWeight> list) {
        int acc = 0;
        for (var c : list) {
            acc += c.weight();
            if (roll <= acc) return c.name();
        }
        return list.get(0).name();
    }

    public static boolean verifySpiffe(String uri, String domain, String ns) {
        var prefix = "spiffe://" + domain + "/ns/" + ns + "/sa/";
        return uri != null && uri.startsWith(prefix) && uri.length() > prefix.length();
    }
}""",
        "kt_code": """// Envoy Weighted Cluster & SPIFFE Validator in Kotlin
data class ClusterWeight(val name: String, val weight: Int)

fun routeCanary(roll: Int, list: List<ClusterWeight>): String {
    var acc = 0
    for (c in list) {
        acc += c.weight
        if (roll <= acc) return c.name
    }
    return list.first().name
}

fun verifySpiffe(uri: String?, domain: String, ns: String): Boolean {
    val prefix = "spiffe://$domain/ns/$ns/sa/"
    return uri?.startsWith(prefix) == true && uri.length > prefix.length
}"""
    },
    "0065": {
        "title": "Cloud & Modern Infra Comprehensive Packet Pipeline",
        "key": "0065-cloud-infra-pipeline",
        "desc": "End-to-end packet journey through a modern cloud architecture: ALB (terminates TLS, validates host) -> Security Group (permits port 443) -> VPC Route Table (routes to K8s Node) -> kube-proxy (DNATs to Pod IP). Given a CloudPacket, validate each checkpoint and return 'DELIVERED' or the specific rejection reason ('INVALID_HOST', 'SG_BLOCKED', or 'NO_ROUTE').",
        "starter": """import java.util.*;

public class Solution {
    public static class CloudPacket {
        public final String hostHeader;
        public final int dstPort;
        public final String dstIp;

        public CloudPacket(String hostHeader, int dstPort, String dstIp) {
            this.hostHeader = hostHeader;
            this.dstPort = dstPort;
            this.dstIp = dstIp;
        }
    }

    public static String processPipeline(CloudPacket pkt, String allowedHost, Set<Integer> sgAllowedPorts, Set<String> knownSubnetsPrefix) {
        // TODO: 1. ALB host check: if !pkt.hostHeader.equalsIgnoreCase(allowedHost), return "INVALID_HOST"
        // TODO: 2. Security Group check: if !sgAllowedPorts.contains(pkt.dstPort), return "SG_BLOCKED"
        // TODO: 3. Route check: dstIp must start with any of knownSubnetsPrefix, else return "NO_ROUTE"
        // TODO: 4. If all pass, return "DELIVERED"
        return "NO_ROUTE";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class CloudPacket {
        public final String hostHeader;
        public final int dstPort;
        public final String dstIp;

        public CloudPacket(String hostHeader, int dstPort, String dstIp) {
            this.hostHeader = hostHeader;
            this.dstPort = dstPort;
            this.dstIp = dstIp;
        }
    }

    public static String processPipeline(CloudPacket pkt, String allowedHost, Set<Integer> sgAllowedPorts, Set<String> knownSubnetsPrefix) {
        if (!pkt.hostHeader.equalsIgnoreCase(allowedHost)) {
            return "INVALID_HOST";
        }
        if (!sgAllowedPorts.contains(pkt.dstPort)) {
            return "SG_BLOCKED";
        }
        boolean routeFound = false;
        for (String prefix : knownSubnetsPrefix) {
            if (pkt.dstIp.startsWith(prefix)) {
                routeFound = true;
                break;
            }
        }
        if (!routeFound) {
            return "NO_ROUTE";
        }
        return "DELIVERED";
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Set<Integer> sgPorts = new HashSet<>(Arrays.asList(443, 80));
        Set<String> subnets = new HashSet<>(Arrays.asList("10.0.", "10.244."));

        // Test 1: Valid packet is DELIVERED
        Solution.CloudPacket p1 = new Solution.CloudPacket("api.prod.io", 443, "10.244.1.5");
        String r1 = Solution.processPipeline(p1, "api.prod.io", sgPorts, subnets);
        if ("DELIVERED".equals(r1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected DELIVERED, got " + r1);
        }

        // Test 2: Blocked port by SG
        Solution.CloudPacket p2 = new Solution.CloudPacket("api.prod.io", 22, "10.244.1.5");
        String r2 = Solution.processPipeline(p2, "api.prod.io", sgPorts, subnets);
        if ("SG_BLOCKED".equals(r2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected SG_BLOCKED, got " + r2);
        }

        // Test 3: Unknown IP subnet
        Solution.CloudPacket p3 = new Solution.CloudPacket("api.prod.io", 443, "172.16.1.1");
        String r3 = Solution.processPipeline(p3, "api.prod.io", sgPorts, subnets);
        if ("NO_ROUTE".equals(r3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected NO_ROUTE, got " + r3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check the host header against `allowedHost` using `equalsIgnoreCase`.",
            "Verify `pkt.dstPort` is in `sgAllowedPorts`.",
            "Verify `pkt.dstIp` starts with any prefix in `knownSubnetsPrefix`."
        ],
        "java_code": """// End-to-End Cloud Datapath Simulator in Java
import java.util.*;

public class CloudPipelineSimulator {
    public record Packet(String host, int port, String ip) {}

    public static String evaluate(Packet p, String host, Set<Integer> ports, Set<String> prefixes) {
        if (!p.host().equalsIgnoreCase(host)) return "INVALID_HOST";
        if (!ports.contains(p.port())) return "SG_BLOCKED";
        if (prefixes.stream().noneMatch(p.ip()::startsWith)) return "NO_ROUTE";
        return "DELIVERED";
    }
}""",
        "kt_code": """// End-to-End Cloud Datapath Simulator in Kotlin
data class Packet(val host: String, val port: Int, val ip: String)

fun evaluateCloud(p: Packet, host: String, ports: Set<Int>, prefixes: Set<String>): String = when {
    !p.host.equals(host, ignoreCase = true) -> "INVALID_HOST"
    p.port !in ports -> "SG_BLOCKED"
    prefixes.none { p.ip.startsWith(it) } -> "NO_ROUTE"
    else -> "DELIVERED"
}"""
    }
}

def inject_lesson(num):
    data = MODULES[num]
    # find file matching num-*.html
    matches = [f for f in os.listdir(LESSONS_DIR) if f.startswith(f"{num}-") and f.endswith(".html")]
    if not matches:
        print(f"Error: no file found for {num}")
        return
    filename = matches[0]
    filepath = os.path.join(LESSONS_DIR, filename)

    with open(filepath, "r", encoding="utf-8") as f:
        html = f.read()

    # Check if already injected
    if 'class="code-practice"' in html and f'data-key="{data["key"]}"' in html:
        print(f"Already injected: {filename}")
        return

    hints_json = json.dumps(data["hints"])

    # Prepare Code Tabs and Practice HTML block
    block = f"""
  <h2>Code Implementation: Java &amp; Kotlin</h2>
  <div class="code-tabs">
    <div class="tab-buttons">
      <button type="button" class="tab-btn active" data-lang="java">Java 25</button>
      <button type="button" class="tab-btn" data-lang="kotlin">Kotlin</button>
    </div>
    <div class="tab-panel active" data-lang="java">
      <div class="code-block-header">
        <span class="code-lang-label">Java 25</span>
        <button type="button" class="btn-copy" title="Copy code">Copy</button>
      </div>
      <pre><code>{data['java_code'].replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')}</code></pre>
    </div>
    <div class="tab-panel" data-lang="kotlin">
      <div class="code-block-header">
        <span class="code-lang-label">Kotlin</span>
        <button type="button" class="btn-copy" title="Copy code">Copy</button>
      </div>
      <pre><code>{data['kt_code'].replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')}</code></pre>
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
    m = re.search(target_pattern, html)
    if not m:
        target_pattern = r'(<h2>Check yourself</h2>)'
        m = re.search(target_pattern, html)
    if not m:
        target_pattern = r'(<div class="quiz">)'
        m = re.search(target_pattern, html)

    if m:
        idx = m.start()
        new_html = html[:idx] + block + "\n" + html[idx:]
    else:
        print(f"Warning: could not find target for {num}")
        return

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
