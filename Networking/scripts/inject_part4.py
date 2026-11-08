#!/usr/bin/env python3
"""
inject_part4.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 4 Network Engineering lessons (0036 to 0050).
"""

import os
import re

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0036": {
        "title": "Optical Power Budget & Link Margin Calculator",
        "key": "0036-optical-budget",
        "desc": "Calculate optical fiber link budget: Power Budget = Tx Power (dBm) - Rx Sensitivity (dBm); Link Margin = Budget - (attenuation * distance + connector losses).",
        "starter": """public class Solution {
    // 1. Calculate Optical Power Budget in dB: txDbm - rxSensDbm (e.g. -3 dBm - (-20 dBm) = 17 dB)
    public static double calcPowerBudget(double txDbm, double rxSensDbm) {
        // TODO: implement
        return 0.0;
    }

    // 2. Calculate remaining Link Margin in dB:
    // margin = budget - (distanceKm * lossPerKm + connectorLossDb)
    public static double calcLinkMargin(double budgetDb, double distanceKm, double lossPerKm, double connectorLossDb) {
        // TODO: implement
        return 0.0;
    }
}""",
        "solution": """public class Solution {
    public static double calcPowerBudget(double txDbm, double rxSensDbm) {
        return txDbm - rxSensDbm;
    }

    public static double calcLinkMargin(double budgetDb, double distanceKm, double lossPerKm, double connectorLossDb) {
        double totalLoss = (distanceKm * lossPerKm) + connectorLossDb;
        return budgetDb - totalLoss;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: SFP+ 10GBASE-LR (-3 dBm Tx, -14.4 dBm Rx sensitivity) -> 11.4 dB budget
        double b1 = Solution.calcPowerBudget(-3.0, -14.4);
        if (Math.abs(b1 - 11.4) < 0.01) {
            System.out.println("PASS: Test 1: 10GBASE-LR Power Budget = 11.4 dB");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 11.4 dB, got " + b1);
        }

        // Test 2: 10 km Single-mode fiber (0.4 dB/km loss, 1.5 dB connector loss)
        double m1 = Solution.calcLinkMargin(11.4, 10.0, 0.4, 1.5);
        // Total loss = 4.0 + 1.5 = 5.5 dB. Margin = 11.4 - 5.5 = 5.9 dB
        if (Math.abs(m1 - 5.9) < 0.01) {
            System.out.println("PASS: Test 2: Link margin over 10km = 5.9 dB (healthy link)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 5.9 dB, got " + m1);
        }

        // Test 3: Link over budget (negative margin = dark fiber link down)
        double m2 = Solution.calcLinkMargin(11.4, 30.0, 0.4, 2.0); // Loss = 12 + 2 = 14 dB
        if (m2 < 0) {
            System.out.println("PASS: Test 3: Attenuation deficit correctly recognized (negative margin)");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Excessive distance should yield negative margin");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Power budget in decibels is simply transmitter output power minus receiver sensitivity: txDbm - rxSensDbm.",
            "Fiber loss scales with distance: distanceKm * lossPerKm.",
            "Remaining margin = budget - totalLoss. A safe production optical link maintains at least +3 dB margin."
        ],
        "example_java": """public class OpticsExample {
    public static void main(String[] args) {
        System.out.println("Single-mode fiber (1310nm/1550nm) supports tens of kilometers; multi-mode (850nm) is limited to datacenter spans (<300m).");
    }
}""",
        "example_kt": """object OpticsExample {
    fun main() {
        println("Single-mode fiber (1310nm/1550nm) supports tens of kilometers; multi-mode (850nm) is limited to datacenter spans (<300m).")
    }
}"""
    },
    "0037": {
        "title": "Switch CAM Table MAC Learning & Flooding Engine",
        "key": "0037-cam-table",
        "desc": "Implement an Ethernet Layer 2 Switch CAM table: learn source MAC to ingress port, forward unicast frames to learned ports, or flood out all other ports on miss.",
        "starter": """import java.util.*;

public class Solution {
    // 1. Record source MAC address to ingress port mapping
    public void learn(String mac, int ingressPort) {
        // TODO: implement
    }

    // 2. Lookup destination MAC:
    // If destination is in CAM table, return List of 1 port: [egressPort]
    // If unknown (miss) or Broadcast (FF:FF:FF:FF:FF:FF), return list of all ports EXCEPT ingressPort
    public List<Integer> forward(String dstMac, int ingressPort, List<Integer> allPorts) {
        // TODO: implement
        return Collections.emptyList();
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private final Map<String, Integer> camTable = new HashMap<>();

    public void learn(String mac, int ingressPort) {
        if (mac != null) {
            camTable.put(mac.trim().toUpperCase(), ingressPort);
        }
    }

    public List<Integer> forward(String dstMac, int ingressPort, List<Integer> allPorts) {
        String cleanDst = dstMac != null ? dstMac.trim().toUpperCase() : "";
        if (!"FF:FF:FF:FF:FF:FF".equals(cleanDst) && camTable.containsKey(cleanDst)) {
            int targetPort = camTable.get(cleanDst);
            if (targetPort != ingressPort) {
                return List.of(targetPort);
            }
            return Collections.emptyList(); // Drop if target is same as ingress
        }
        // Unknown Unicast Flooding or Broadcast: flood all ports except ingress
        List<Integer> floodPorts = new ArrayList<>();
        for (int p : allPorts) {
            if (p != ingressPort) floodPorts.add(p);
        }
        return floodPorts;
    }
}""",
        "harness": """// __STUDENT_CODE__
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution sw = new Solution();
        List<Integer> switchPorts = List.of(1, 2, 3, 4);

        // Host A is on port 1
        sw.learn("00:11:22:33:44:AA", 1);
        // Host B is on port 2
        sw.learn("00:11:22:33:44:BB", 2);

        // Test 1: Known unicast forwarding
        List<Integer> f1 = sw.forward("00:11:22:33:44:BB", 1, switchPorts);
        if (f1.size() == 1 && f1.get(0) == 2) {
            System.out.println("PASS: Test 1: Frame forwarded directly to learned port 2");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Direct unicast forwarding failed");
        }

        // Test 2: Unknown unicast destination flooded out ports 2, 3, 4
        List<Integer> f2 = sw.forward("00:11:22:33:44:CC", 1, switchPorts);
        if (f2.size() == 3 && f2.contains(2) && f2.contains(3) && f2.contains(4) && !f2.contains(1)) {
            System.out.println("PASS: Test 2: Unknown unicast destination flooded to other 3 ports");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Flooding logic failed");
        }

        // Test 3: Broadcast frame flooded to all except ingress
        List<Integer> f3 = sw.forward("FF:FF:FF:FF:FF:FF", 2, switchPorts);
        if (f3.size() == 3 && f3.contains(1) && f3.contains(3) && f3.contains(4) && !f3.contains(2)) {
            System.out.println("PASS: Test 3: Broadcast frame flooded to ports 1, 3, 4");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Broadcast flooding failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Maintain a Map<String, Integer> mapping uppercase MAC to port number.",
            "If dstMac is in CAM table, forward strictly to that port.",
            "If dstMac is missing (unknown unicast) or is FF:FF:FF:FF:FF:FF (broadcast), flood to all ports except ingressPort."
        ],
        "example_java": """public class CamTableExample {
    public static void main(String[] args) {
        System.out.println("Switches learn dynamically from frame source MACs; they forward or flood based on destination MACs.");
    }
}""",
        "example_kt": """object CamTableExample {
    fun main() {
        println("Switches learn dynamically from frame source MACs; they forward or flood based on destination MACs.")
    }
}"""
    },
    "0038": {
        "title": "802.1Q VLAN Tagging & TCI Encoder",
        "key": "0038-vlan-tagging",
        "desc": "Implement IEEE 802.1Q trunk tagging: assemble 4-byte 802.1Q tag (TPID 0x8100 + 16-bit TCI containing 3-bit PCP, 1-bit DEI, 12-bit VID) and extract VID.",
        "starter": """public class Solution {
    // 1. Pack 802.1Q Tag: 2 bytes TPID (0x8100) + 2 bytes TCI ((pcp << 13) | vid)
    public static byte[] buildVlanTag(int pcp, int vid) {
        // TODO: implement
        return new byte[4];
    }

    // 2. Extract 12-bit VLAN ID (VID) from 4-byte 802.1Q tag
    public static int extractVid(byte[] tag4Bytes) {
        // TODO: implement
        return 0;
    }
}""",
        "solution": """public class Solution {
    public static byte[] buildVlanTag(int pcp, int vid) {
        byte[] b = new byte[4];
        b[0] = (byte) 0x81;
        b[1] = 0x00; // TPID = 0x8100
        int tci = ((pcp & 0x07) << 13) | (vid & 0x0FFF);
        b[2] = (byte) ((tci >> 8) & 0xFF);
        b[3] = (byte) (tci & 0xFF);
        return b;
    }

    public static int extractVid(byte[] b) {
        if (b == null || b.length < 4) return 0;
        int tci = ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);
        return tci & 0x0FFF; // 12 bits for VID
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Build tag for VLAN 100 with PCP 5
        byte[] tag = Solution.buildVlanTag(5, 100);
        if ((tag[0] & 0xFF) == 0x81 && tag[1] == 0x00) {
            System.out.println("PASS: Test 1: TPID 0x8100 verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: TPID mismatch");
        }

        // Test 2: Extract VID 100
        if (Solution.extractVid(tag) == 100) {
            System.out.println("PASS: Test 2: Extracted 12-bit VID = 100");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: VID extraction failed");
        }

        // Test 3: Maximum 12-bit VLAN boundary = 4094
        byte[] maxTag = Solution.buildVlanTag(0, 4094);
        if (Solution.extractVid(maxTag) == 4094) {
            System.out.println("PASS: Test 3: Maximum VLAN ID 4094 supported");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Max VLAN check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "802.1Q tags are 4 bytes: 2 bytes EtherType (0x8100) followed by 2 bytes Tag Control Information (TCI).",
            "TCI format: 3 bits Priority Code Point (PCP), 1 bit Drop Eligible Indicator (DEI), 12 bits VLAN Identifier (VID).",
            "12 bits allows VLAN IDs from 1 to 4094 (VLAN 0 and 4095 are reserved)."
        ],
        "example_java": """public class VlanExample {
    public static void main(String[] args) {
        System.out.println("802.1Q inserts a 4-byte tag into Ethernet headers across switch trunk links to isolate broadcast domains.");
    }
}""",
        "example_kt": """object VlanExample {
    fun main() {
        println("802.1Q inserts a 4-byte tag into Ethernet headers across switch trunk links to isolate broadcast domains.")
    }
}"""
    },
    "0039": {
        "title": "Inter-VLAN SVI Sub-Interface Routing",
        "key": "0039-inter-vlan-svi",
        "desc": "Simulate Layer 3 Switched Virtual Interface (SVI) routing between tagged VLANs: strip incoming VLAN tag, decrement TTL, and apply outgoing VLAN tag.",
        "starter": """public class Solution {
    public static class RoutedFrame {
        public int outVlan;
        public int newTtl;
        public String nextHopMac;
    }

    public static RoutedFrame routeVlan(int inVlan, int ttl, int targetVlan, String nextHopMac) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class RoutedFrame {
        public int outVlan;
        public int newTtl;
        public String nextHopMac;
    }

    public static RoutedFrame routeVlan(int inVlan, int ttl, int targetVlan, String nextHopMac) {
        if (ttl <= 1) return null; // Time Exceeded
        RoutedFrame rf = new RoutedFrame();
        rf.outVlan = targetVlan;
        rf.newTtl = ttl - 1;
        rf.nextHopMac = nextHopMac;
        return rf;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Inter-VLAN routing from VLAN 10 to VLAN 20
        Solution.RoutedFrame rf1 = Solution.routeVlan(10, 64, 20, "00:50:56:00:00:20");
        if (rf1 != null && rf1.outVlan == 20 && rf1.newTtl == 63 && "00:50:56:00:00:20".equals(rf1.nextHopMac)) {
            System.out.println("PASS: Test 1: SVI routed packet across VLAN 10 -> VLAN 20 with decremented TTL");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: SVI routing failed");
        }

        // Test 2: TTL expired packet dropped
        Solution.RoutedFrame rf2 = Solution.routeVlan(10, 1, 20, "00:50:56:00:00:20");
        if (rf2 == null) {
            System.out.println("PASS: Test 2: Packet with TTL=1 dropped at L3 SVI");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Expired TTL should be dropped");
        }

        // Test 3: Same VLAN does not route through SVI (bridged at L2)
        Solution.RoutedFrame rf3 = Solution.routeVlan(10, 64, 10, "00:50:56:00:00:10");
        if (rf3 != null && rf3.outVlan == 10) {
            System.out.println("PASS: Test 3: Intra-VLAN SVI bridge preserved");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Intra-VLAN check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "When traffic crosses between VLANs, it must pass through a Layer 3 gateway (Router-on-a-Stick or L3 Switch SVI).",
            "At Layer 3, TTL is decremented by 1.",
            "The frame is re-encapsulated with the destination VLAN's 802.1Q tag and next hop MAC."
        ],
        "example_java": """public class SviExample {
    public static void main(String[] args) {
        System.out.println("Switched Virtual Interfaces (SVIs) provide wire-speed Layer 3 inter-VLAN routing inside core enterprise switches.");
    }
}""",
        "example_kt": """object SviExample {
    fun main() {
        println("Switched Virtual Interfaces (SVIs) provide wire-speed Layer 3 inter-VLAN routing inside core enterprise switches.")
    }
}"""
    },
    "0040": {
        "title": "Spanning Tree Protocol (STP) Bridge ID Election",
        "key": "0040-stp-election",
        "desc": "Implement IEEE 802.1D Spanning Tree Root Bridge election: compare 8-byte Bridge IDs (2B Priority + 6B MAC address); the lowest Bridge ID becomes the Root Bridge.",
        "starter": """public class Solution {
    // Return true if Bridge A beats Bridge B to become Root Bridge (lowest priority wins; on tie, lowest MAC wins)
    public static boolean bridgeAWins(int prioA, String macA, int prioB, String macB) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static boolean bridgeAWins(int prioA, String macA, int prioB, String macB) {
        if (prioA != prioB) {
            return prioA < prioB;
        }
        return macA.trim().compareToIgnoreCase(macB.trim()) < 0;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Lower priority wins (4096 beats default 32768)
        if (Solution.bridgeAWins(4096, "00:11:22:33:44:55", 32768, "00:00:00:00:00:01")) {
            System.out.println("PASS: Test 1: Lower STP priority (4096 < 32768) elected as Root Bridge");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Lower priority should win");
        }

        // Test 2: Equal priority tie-breaker resolved by lowest MAC address
        if (Solution.bridgeAWins(32768, "00:00:00:00:00:01", 32768, "00:00:00:00:00:02")) {
            System.out.println("PASS: Test 2: Priority tie broken by lower MAC address");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Lower MAC should win on priority tie");
        }

        // Test 3: Higher priority loses
        if (!Solution.bridgeAWins(61440, "00:00:00:00:00:01", 32768, "00:11:22:33:44:55")) {
            System.out.println("PASS: Test 3: Higher priority bridge correctly loses root election");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Higher priority should lose");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "In Spanning Tree Protocol (STP), the Root Bridge is the switch with the lowest Bridge ID.",
            "Bridge ID = 2-byte Priority + 6-byte MAC address.",
            "Compare priority first (lower is better). If equal, compare MAC addresses lexicographically."
        ],
        "example_java": """public class StpExample {
    public static void main(String[] args) {
        System.out.println("STP blocks redundant loops; network engineers manually configure primary core switches with priority 4096.");
    }
}""",
        "example_kt": """object StpExample {
    fun main() {
        println("STP blocks redundant loops; network engineers manually configure primary core switches with priority 4096.")
    }
}"""
    },
    "0041": {
        "title": "OSPF Dijkstra Shortest Path First (SPF) Engine",
        "key": "0041-ospf-dijkstra",
        "desc": "Implement Dijkstra's SPF algorithm to calculate OSPF cumulative link-state routing costs across network nodes.",
        "starter": """import java.util.*;

public class Solution {
    // Calculate lowest total link cost from startNode to destNode in topology graph
    public static int calcLowestCost(Map<String, Map<String, Integer>> graph, String start, String dest) {
        // TODO: implement
        return -1;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static int calcLowestCost(Map<String, Map<String, Integer>> graph, String start, String dest) {
        Map<String, Integer> dist = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (String node : graph.keySet()) dist.put(node, Integer.MAX_VALUE);
        dist.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            int d = dist.get(u);
            if (u.equals(dest)) return d;

            Map<String, Integer> neighbors = graph.get(u);
            if (neighbors == null) continue;

            for (Map.Entry<String, Integer> edge : neighbors.entrySet()) {
                String v = edge.getKey();
                int weight = edge.getValue();
                if (d + weight < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, d + weight);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        return dist.getOrDefault(dest, -1);
    }
}""",
        "harness": """// __STUDENT_CODE__
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // OSPF Topology:
        // R1 --(cost 10)--> R2 --(cost 10)--> R4  (Total: 20)
        // R1 --(cost 100)--> R3 --(cost 1)--> R4 (Total: 101)
        Map<String, Map<String, Integer>> topo = new HashMap<>();
        topo.put("R1", Map.of("R2", 10, "R3", 100));
        topo.put("R2", Map.of("R1", 10, "R4", 10));
        topo.put("R3", Map.of("R1", 100, "R4", 1));
        topo.put("R4", Map.of("R2", 10, "R3", 1));

        // Test 1: Lowest cost path R1 -> R4 is via R2 (cost 20)
        int c1 = Solution.calcLowestCost(topo, "R1", "R4");
        if (c1 == 20) {
            System.out.println("PASS: Test 1: Dijkstra SPF selected lowest cost path (cost 20)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 20, got " + c1);
        }

        // Test 2: Direct neighbor cost R1 -> R2
        int c2 = Solution.calcLowestCost(topo, "R1", "R2");
        if (c2 == 10) {
            System.out.println("PASS: Test 2: Direct neighbor SPF cost = 10");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 10, got " + c2);
        }

        // Test 3: Unreachable node returns -1
        topo.put("R5_ISOLATED", new HashMap<>());
        int c3 = Solution.calcLowestCost(topo, "R1", "R5_ISOLATED");
        if (c3 == -1 || c3 == Integer.MAX_VALUE) {
            System.out.println("PASS: Test 3: Unreachable destination returns -1/infinity");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Unreachable check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "OSPF routers maintain identical Link-State Databases (LSDB) and independently run Dijkstra's SPF algorithm.",
            "OSPF cost = Reference Bandwidth (100 Mbps default) / Link Bandwidth.",
            "Maintain a PriorityQueue to greedily extract the unvisited node with minimal cumulative cost."
        ],
        "example_java": """public class OspfExample {
    public static void main(String[] args) {
        System.out.println("OSPF is an Interior Gateway Protocol (IGP) using Dijkstra SPF within an Autonomous System.");
    }
}""",
        "example_kt": """object OspfExample {
    fun main() {
        println("OSPF is an Interior Gateway Protocol (IGP) using Dijkstra SPF within an Autonomous System.")
    }
}"""
    },
    "0042": {
        "title": "BGP AS-PATH Loop Prevention Engine",
        "key": "0042-bgp-loop-prevention",
        "desc": "Implement BGP Path-Vector loop prevention: detect if a router's own Autonomous System Number (ASN) appears in the incoming AS-PATH attribute, and prepend local ASN on eBGP advertisement.",
        "starter": """import java.util.*;

public class Solution {
    // 1. Return true if localAsn is in asPath (routing loop detected -> MUST DROP route)
    public static boolean hasRoutingLoop(List<Integer> asPath, int localAsn) {
        // TODO: implement
        return false;
    }

    // 2. Prepend localAsn to outgoing AS-PATH attribute for eBGP export
    public static List<Integer> prependAsn(List<Integer> asPath, int localAsn) {
        // TODO: implement
        return Collections.emptyList();
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static boolean hasRoutingLoop(List<Integer> asPath, int localAsn) {
        if (asPath == null) return false;
        return asPath.contains(localAsn);
    }

    public static List<Integer> prependAsn(List<Integer> asPath, int localAsn) {
        List<Integer> out = new ArrayList<>();
        out.add(localAsn);
        if (asPath != null) out.addAll(asPath);
        return out;
    }
}""",
        "harness": """// __STUDENT_CODE__
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        List<Integer> path = List.of(15169, 13335, 701); // Google -> Cloudflare -> Verizon

        // Test 1: Loop detected when local ASN (13335) is already present
        if (Solution.hasRoutingLoop(path, 13335)) {
            System.out.println("PASS: Test 1: BGP routing loop detected and dropped (ASN 13335 present)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Loop detection failed");
        }

        // Test 2: Clean route has no loop
        if (!Solution.hasRoutingLoop(path, 20940)) {
            System.out.println("PASS: Test 2: Clean route accepted (ASN 20940 absent)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Clean route rejected incorrectly");
        }

        // Test 3: Prepend local ASN on eBGP advertisement
        List<Integer> prepended = Solution.prependAsn(path, 20940);
        if (prepended.size() == 4 && prepended.get(0) == 20940) {
            System.out.println("PASS: Test 3: Local ASN prepended to AS-PATH attribute");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: AS-PATH prepending failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "BGP is a Path-Vector protocol running over TCP port 179.",
            "Loop prevention rule (RFC 4271): if a BGP router sees its own ASN in the AS-PATH attribute of an incoming UPDATE, it MUST silently discard the route.",
            "When advertising to an external peer (eBGP), a router prepends its own ASN to the head of the AS-PATH list."
        ],
        "example_java": """public class BgpExample {
    public static void main(String[] args) {
        System.out.println("BGP glues the global Internet together across 75,000+ Autonomous Systems using TCP port 179.");
    }
}""",
        "example_kt": """object BgpExample {
    fun main() {
        println("BGP glues the global Internet together across 75,000+ Autonomous Systems using TCP port 179.")
    }
}"""
    },
    "0043": {
        "title": "BGP Best Path Decision Algorithm",
        "key": "0043-bgp-best-path",
        "desc": "Implement the canonical BGP path selection order: 1. Highest Weight (Cisco-specific) -> 2. Highest Local Preference -> 3. Shortest AS-PATH -> 4. Lowest MED.",
        "starter": """public class Solution {
    public static class BgpRoute {
        public String id;
        public int weight;
        public int localPref;
        public int asPathLength;
        public int med;
    }

    // Return the best route ID according to standard BGP tie-breaking rules
    public static String selectBestPath(BgpRoute a, BgpRoute b) {
        // TODO: implement
        return "";
    }
}""",
        "solution": """public class Solution {
    public static class BgpRoute {
        public String id;
        public int weight;
        public int localPref;
        public int asPathLength;
        public int med;
    }

    public static String selectBestPath(BgpRoute a, BgpRoute b) {
        if (a.weight != b.weight) return a.weight > b.weight ? a.id : b.id;
        if (a.localPref != b.localPref) return a.localPref > b.localPref ? a.id : b.id;
        if (a.asPathLength != b.asPathLength) return a.asPathLength < b.asPathLength ? a.id : b.id;
        if (a.med != b.med) return a.med < b.med ? a.id : b.id;
        return a.id;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution.BgpRoute r1 = new Solution.BgpRoute();
        r1.id = "ISP-A"; r1.weight = 0; r1.localPref = 200; r1.asPathLength = 4; r1.med = 50;

        Solution.BgpRoute r2 = new Solution.BgpRoute();
        r2.id = "ISP-B"; r2.weight = 0; r2.localPref = 100; r2.asPathLength = 2; r2.med = 10;

        // Test 1: Highest Local Preference (200 > 100) beats shorter AS-PATH (4 vs 2)
        String best1 = Solution.selectBestPath(r1, r2);
        if ("ISP-A".equals(best1)) {
            System.out.println("PASS: Test 1: Higher Local Preference (200 > 100) beat shorter AS-PATH");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Local preference tie-breaker failed");
        }

        // Test 2: Equal Local Preference broken by shorter AS-PATH
        r1.localPref = 100;
        String best2 = Solution.selectBestPath(r1, r2);
        if ("ISP-B".equals(best2)) {
            System.out.println("PASS: Test 2: Shorter AS-PATH (2 < 4) won on equal Local Preference");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: AS-PATH tie-breaker failed");
        }

        // Test 3: Weight overrides everything
        r2.weight = 1000;
        String best3 = Solution.selectBestPath(r1, r2);
        if ("ISP-B".equals(best3)) {
            System.out.println("PASS: Test 3: Weight (1000 > 0) overrides all subsequent attributes");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Weight check failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "BGP tie-breaking sequence: Weight (highest) -> Local Preference (highest) -> AS-PATH (shortest) -> Origin (IGP < EGP < Incomplete) -> MED (lowest) -> eBGP over iBGP.",
            "Local Preference controls outbound egress traffic across an Autonomous System.",
            "Multi-Exit Discriminator (MED) suggests inbound path preference to external peers."
        ],
        "example_java": """public class BgpDecisionExample {
    public static void main(String[] args) {
        System.out.println("Local Preference is strictly internal to an AS; it is stripped when advertising to external peers.");
    }
}""",
        "example_kt": """object BgpDecisionExample {
    fun main() {
        println("Local Preference is strictly internal to an AS; it is stripped when advertising to external peers.")
    }
}"""
    },
    "0044": {
        "title": "RPKI Route Origin Authorization (ROA) Validator",
        "key": "0044-rpki-validator",
        "desc": "Implement RPKI BGP route security validation: classify routes as VALID, INVALID (hijack / length violation), or NOT_FOUND (unknown).",
        "starter": """import java.util.*;

public class Solution {
    public static class Roa {
        public String prefix;
        public int maxLength;
        public int asn;
    }

    // Return "VALID", "INVALID", or "NOT_FOUND"
    public static String validate(String routePrefix, int routePrefixLen, int originAsn, List<Roa> roas) {
        // TODO: implement
        return "NOT_FOUND";
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class Roa {
        public String prefix;
        public int maxLength;
        public int asn;
    }

    public static String validate(String routePrefix, int routePrefixLen, int originAsn, List<Roa> roas) {
        if (roas == null || roas.isEmpty()) return "NOT_FOUND";
        boolean matchedPrefix = false;

        for (Roa roa : roas) {
            if (roa.prefix.equals(routePrefix)) {
                matchedPrefix = true;
                if (roa.asn == originAsn && routePrefixLen <= roa.maxLength) {
                    return "VALID";
                }
            }
        }
        return matchedPrefix ? "INVALID" : "NOT_FOUND";
    }
}""",
        "harness": """// __STUDENT_CODE__
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution.Roa roa = new Solution.Roa();
        roa.prefix = "1.1.1.0"; roa.maxLength = 24; roa.asn = 13335; // Cloudflare ROA
        List<Solution.Roa> roas = List.of(roa);

        // Test 1: Valid advertisement
        String v1 = Solution.validate("1.1.1.0", 24, 13335, roas);
        if ("VALID".equals(v1)) {
            System.out.println("PASS: Test 1: Legitimate Cloudflare route validated by RPKI ROA");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected VALID, got " + v1);
        }

        // Test 2: BGP Route Hijack (unauthorized ASN 99999 advertising Cloudflare prefix)
        String v2 = Solution.validate("1.1.1.0", 24, 99999, roas);
        if ("INVALID".equals(v2)) {
            System.out.println("PASS: Test 2: BGP route hijack attempt caught and marked INVALID");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected INVALID, got " + v2);
        }

        // Test 3: Unregistered prefix -> NOT_FOUND
        String v3 = Solution.validate("8.8.8.0", 24, 15169, roas);
        if ("NOT_FOUND".equals(v3)) {
            System.out.println("PASS: Test 3: Unregistered prefix classified as NOT_FOUND (unknown)");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected NOT_FOUND, got " + v3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "RPKI uses cryptographic X.509 certificates to anchor Route Origin Authorizations (ROAs).",
            "A route is VALID if there is a matching ROA with identical ASN and prefix length <= maxLength.",
            "If a ROA covers the prefix but the advertising ASN differs or length exceeds maxLength, the route is INVALID (dropping hijacks!).",
            "If no ROA covers the prefix, it is NOT_FOUND (unknown)."
        ],
        "example_java": """public class RpkiExample {
    public static void main(String[] args) {
        System.out.println("RPKI Route Origin Validation (ROV) eliminates accidental BGP route leaks and malicious prefix hijacks.");
    }
}""",
        "example_kt": """object RpkiExample {
    fun main() {
        println("RPKI Route Origin Validation (ROV) eliminates accidental BGP route leaks and malicious prefix hijacks.")
    }
}"""
    },
    "0045": {
        "title": "Stateful Firewall Connection Tracking (conntrack)",
        "key": "0045-conntrack-firewall",
        "desc": "Implement stateful firewall packet filtering: track connection states (NEW, ESTABLISHED) and enforce stateful return traffic permit rules.",
        "starter": """import java.util.*;

public class Solution {
    // Return true if packet is permitted through firewall; false if dropped
    // Rule: Outbound SYN creates NEW connection -> ALLOW
    // Inbound reply matching existing connection -> ESTABLISHED -> ALLOW
    // Unsolicited inbound SYN -> DROP
    public boolean processPacket(boolean isOutbound, String srcIp, int srcPort, String dstIp, int dstPort, boolean isSyn) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private final Set<String> establishedConns = new HashSet<>();

    public boolean processPacket(boolean isOutbound, String srcIp, int srcPort, String dstIp, int dstPort, boolean isSyn) {
        String forwardKey = srcIp + ":" + srcPort + "->" + dstIp + ":" + dstPort;
        String reverseKey = dstIp + ":" + dstPort + "->" + srcIp + ":" + srcPort;

        if (isOutbound) {
            establishedConns.add(forwardKey);
            establishedConns.add(reverseKey);
            return true; // Outbound permitted
        } else {
            // Inbound: permit only if reverse session exists in state table
            if (establishedConns.contains(forwardKey)) {
                return true; // ESTABLISHED reply
            }
            return false; // Unsolicited inbound probe blocked!
        }
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution fw = new Solution();

        // Test 1: Outbound HTTP request permitted
        boolean ok1 = fw.processPacket(true, "192.168.1.50", 50000, "1.1.1.1", 443, true);
        if (ok1) {
            System.out.println("PASS: Test 1: Outbound connection permitted and session recorded");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Outbound packet should be allowed");
        }

        // Test 2: Inbound reply matching outbound session permitted
        boolean ok2 = fw.processPacket(false, "1.1.1.1", 443, "192.168.1.50", 50000, false);
        if (ok2) {
            System.out.println("PASS: Test 2: Stateful inbound return traffic automatically permitted");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Inbound return traffic was dropped");
        }

        // Test 3: Unsolicited inbound SYN blocked
        boolean ok3 = fw.processPacket(false, "198.51.100.99", 12345, "192.168.1.50", 22, true);
        if (!ok3) {
            System.out.println("PASS: Test 3: Unsolicited inbound probe blocked by stateful firewall");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Unsolicited inbound probe should be blocked");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Stateful firewalls maintain a connection tracking (conntrack) table.",
            "When an outbound packet creates a session, store both forward and reverse keys.",
            "Inbound packets are permitted only if a corresponding session already exists in the state table."
        ],
        "example_java": """public class ConntrackExample {
    public static void main(String[] args) {
        System.out.println("Stateful firewalls inspect TCP flags and sequence numbers, eliminating the need for open inbound ephemeral ACL rules.");
    }
}""",
        "example_kt": """object ConntrackExample {
    fun main() {
        println("Stateful firewalls inspect TCP flags and sequence numbers, eliminating the need for open inbound ephemeral ACL rules.")
    }
}"""
    },
    "0046": {
        "title": "IPsec Anti-Replay Sliding Window",
        "key": "0046-ipsec-anti-replay",
        "desc": "Implement RFC 4303 IPsec ESP sequence number anti-replay verification: reject old or duplicate sequence numbers within a 64-packet sliding window.",
        "starter": """public class Solution {
    // Process incoming ESP packet sequence number:
    // Return true if packet sequence is valid (new or within window and not duplicate);
    // Return false if packet is duplicate or too old (replay attack!)
    public boolean checkAndAdvance(long seq) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    private long maxSeq = 0;
    private long bitmap = 0; // 64-bit sliding window

    public boolean checkAndAdvance(long seq) {
        if (seq <= 0) return false;
        if (seq > maxSeq) {
            long diff = seq - maxSeq;
            if (diff < 64) {
                bitmap <<= diff;
            } else {
                bitmap = 0;
            }
            bitmap |= 1L; // Mark current packet received
            maxSeq = seq;
            return true;
        }
        long diff = maxSeq - seq;
        if (diff >= 64) return false; // Too old (outside window)
        long mask = 1L << diff;
        if ((bitmap & mask) != 0) return false; // Duplicate packet (replay attack!)
        bitmap |= mask;
        return true;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution ipsec = new Solution();

        // Test 1: In-order packets
        boolean p1 = ipsec.checkAndAdvance(1L);
        boolean p2 = ipsec.checkAndAdvance(2L);
        if (p1 && p2) {
            System.out.println("PASS: Test 1: In-order ESP sequence numbers accepted");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: In-order packets rejected");
        }

        // Test 2: Duplicate sequence number (Replay attack!)
        boolean dup = ipsec.checkAndAdvance(1L);
        if (!dup) {
            System.out.println("PASS: Test 2: Duplicate sequence number 1 caught and dropped (Anti-Replay)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Duplicate packet was accepted");
        }

        // Test 3: Advance window and reject packets falling off left edge
        ipsec.checkAndAdvance(100L); // maxSeq is now 100 (window covers 37..100)
        boolean tooOld = ipsec.checkAndAdvance(20L); // Sequence 20 is too old (diff = 80 >= 64)
        if (!tooOld) {
            System.out.println("PASS: Test 3: Sequence number outside 64-packet window dropped");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Stale packet was accepted");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "IPsec ESP uses a 64-packet sliding window bitmap to prevent replay attacks over connectionless IP.",
            "If seq > maxSeq, shift bitmap left by (seq - maxSeq) and mark bit 0.",
            "If seq <= maxSeq, check if (maxSeq - seq) < 64 and test whether that bit was already received."
        ],
        "example_java": """public class IpsecExample {
    public static void main(String[] args) {
        System.out.println("IPsec ESP guarantees confidentiality, integrity, and anti-replay protection across site-to-site VPN tunnels.");
    }
}""",
        "example_kt": """object IpsecExample {
    fun main() {
        println("IPsec ESP guarantees confidentiality, integrity, and anti-replay protection across site-to-site VPN tunnels.")
    }
}"""
    },
    "0047": {
        "title": "VXLAN Header Generator & 24-Bit VNI Encoder",
        "key": "0047-vxlan-encapsulation",
        "desc": "Implement RFC 7348 VXLAN overlay encapsulation: pack 8-byte VXLAN header (Flags with I-bit set, 24-bit Virtual Network Identifier VNI) into UDP port 4789.",
        "starter": """public class Solution {
    // 1. Pack 8-byte VXLAN header: byte 0 has I-bit (0x08), bytes 4..6 contain 24-bit VNI
    public static byte[] buildVxlanHeader(int vni) {
        // TODO: implement
        return new byte[8];
    }

    // 2. Extract 24-bit VNI from 8-byte VXLAN header
    public static int extractVni(byte[] vxlanHeader) {
        // TODO: implement
        return 0;
    }
}""",
        "solution": """public class Solution {
    public static byte[] buildVxlanHeader(int vni) {
        byte[] b = new byte[8];
        b[0] = 0x08; // I-bit (VNI valid)
        b[4] = (byte) ((vni >> 16) & 0xFF);
        b[5] = (byte) ((vni >> 8) & 0xFF);
        b[6] = (byte) (vni & 0xFF);
        return b;
    }

    public static int extractVni(byte[] b) {
        if (b == null || b.length < 8) return 0;
        return ((b[4] & 0xFF) << 16) | ((b[5] & 0xFF) << 8) | (b[6] & 0xFF);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: I-bit set in byte 0 (0x08)
        byte[] hdr = Solution.buildVxlanHeader(5001);
        if (hdr[0] == 0x08) {
            System.out.println("PASS: Test 1: VXLAN I-bit (0x08) asserted");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: I-bit mismatch");
        }

        // Test 2: Extract 24-bit VNI = 5001
        if (Solution.extractVni(hdr) == 5001) {
            System.out.println("PASS: Test 2: 24-bit VNI 5001 extracted");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: VNI extraction failed");
        }

        // Test 3: Large 24-bit VNI boundary = 16,777,215 (2^24 - 1)
        byte[] maxHdr = Solution.buildVxlanHeader(16777215);
        if (Solution.extractVni(maxHdr) == 16777215) {
            System.out.println("PASS: Test 3: Maximum 24-bit VNI (16,777,215) verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Max VNI failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "VXLAN encapsulates Layer 2 Ethernet frames in Layer 4 UDP datagrams on destination port 4789.",
            "The 8-byte VXLAN header sets bit 3 of byte 0 (0x08) to indicate valid VNI.",
            "VNI is 24 bits (bytes 4, 5, 6), expanding network segmentation from 4,096 VLANs to over 16 million overlay virtual networks!"
        ],
        "example_java": """public class VxlanExample {
    public static void main(String[] args) {
        System.out.println("VXLAN overcomes the 4,096 VLAN limit by expanding network virtualization to 16.7 million VNIs.");
    }
}""",
        "example_kt": """object VxlanExample {
    fun main() {
        println("VXLAN overcomes the 4,096 VLAN limit by expanding network virtualization to 16.7 million VNIs.")
    }
}"""
    },
    "0048": {
        "title": "Token Bucket Traffic Policer & Shaper",
        "key": "0048-token-bucket",
        "desc": "Implement a QoS Token Bucket rate limiter: refill tokens at rate r bytes/sec up to capacity C; permit packet and deduct tokens if available.",
        "starter": """public class Solution {
    private final long capacity;
    private final long refillRatePerSec;
    private double currentTokens;
    private long lastTimeMillis;

    public Solution(long capacity, long refillRatePerSec, long startTimeMillis) {
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
        this.currentTokens = capacity;
        this.lastTimeMillis = startTimeMillis;
    }

    // Attempt to consume packetBytes at currentTimeMillis
    // Return true if permitted; false if rate-limited / dropped
    public boolean tryConsume(int packetBytes, long currentTimeMillis) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    private final long capacity;
    private final long refillRatePerSec;
    private double currentTokens;
    private long lastTimeMillis;

    public Solution(long capacity, long refillRatePerSec, long startTimeMillis) {
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
        this.currentTokens = capacity;
        this.lastTimeMillis = startTimeMillis;
    }

    public boolean tryConsume(int packetBytes, long currentTimeMillis) {
        long elapsed = Math.max(0, currentTimeMillis - lastTimeMillis);
        lastTimeMillis = currentTimeMillis;
        currentTokens = Math.min(capacity, currentTokens + (elapsed * (double) refillRatePerSec / 1000.0));

        if (currentTokens >= packetBytes) {
            currentTokens -= packetBytes;
            return true;
        }
        return false;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        long t0 = 100000L;
        // Capacity = 3000 bytes, refill rate = 1000 bytes/sec
        Solution tb = new Solution(3000, 1000, t0);

        // Test 1: Immediate burst consumption up to capacity
        boolean p1 = tb.tryConsume(1500, t0);
        boolean p2 = tb.tryConsume(1500, t0);
        boolean p3 = tb.tryConsume(500, t0); // Exceeds 3000 byte capacity!
        if (p1 && p2 && !p3) {
            System.out.println("PASS: Test 1: Token bucket permitted burst up to capacity and blocked excess");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Burst consumption failed");
        }

        // Test 2: Token refill over time (after 1000 ms, 1000 bytes refilled)
        boolean p4 = tb.tryConsume(1000, t0 + 1000);
        if (p4) {
            System.out.println("PASS: Test 2: Refilled tokens permitted subsequent packet");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Refill logic failed");
        }

        // Test 3: Cap at maximum capacity
        boolean p5 = tb.tryConsume(100, t0 + 100000); // long time passes
        if (p5) {
            System.out.println("PASS: Test 3: Maximum capacity cap respected");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Capacity cap failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Calculate tokens added: elapsedMillis * (refillRatePerSec / 1000.0).",
            "Cap tokens at bucket capacity: currentTokens = Math.min(capacity, currentTokens + addedTokens).",
            "If currentTokens >= packetBytes, subtract packetBytes and return true."
        ],
        "example_java": """public class QoSExample {
    public static void main(String[] args) {
        System.out.println("Token bucket allows controlled bursts while strictly enforcing long-term average bandwidth rates.");
    }
}""",
        "example_kt": """object QoSExample {
    fun main() {
        println("Token bucket allows controlled bursts while strictly enforcing long-term average bandwidth rates.")
    }
}"""
    },
    "0049": {
        "title": "NetFlow v5 Flow Record Binary Parser",
        "key": "0049-netflow-parser",
        "desc": "Parse Cisco NetFlow v5 48-byte flow record binary stream: extract Source IP, Dest IP, Packet Count, Byte Count, and L4 Ports.",
        "starter": """public class Solution {
    public static class FlowRecord {
        public String srcIp;
        public String dstIp;
        public long packets;
        public long bytes;
        public int srcPort;
        public int dstPort;
        public int protocol;
    }

    public static FlowRecord parse(byte[] record48Bytes) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class FlowRecord {
        public String srcIp;
        public String dstIp;
        public long packets;
        public long bytes;
        public int srcPort;
        public int dstPort;
        public int protocol;
    }

    public static FlowRecord parse(byte[] b) {
        if (b == null || b.length < 48) return null;
        FlowRecord f = new FlowRecord();
        f.srcIp = String.format("%d.%d.%d.%d", b[0] & 0xFF, b[1] & 0xFF, b[2] & 0xFF, b[3] & 0xFF);
        f.dstIp = String.format("%d.%d.%d.%d", b[4] & 0xFF, b[5] & 0xFF, b[6] & 0xFF, b[7] & 0xFF);
        f.packets = ((long)(b[16] & 0xFF) << 24) | ((long)(b[17] & 0xFF) << 16) | ((long)(b[18] & 0xFF) << 8) | (b[19] & 0xFF);
        f.bytes = ((long)(b[20] & 0xFF) << 24) | ((long)(b[21] & 0xFF) << 16) | ((long)(b[22] & 0xFF) << 8) | (b[23] & 0xFF);
        f.srcPort = ((b[32] & 0xFF) << 8) | (b[33] & 0xFF);
        f.dstPort = ((b[34] & 0xFF) << 8) | (b[35] & 0xFF);
        f.protocol = b[38] & 0xFF;
        return f;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Build 48-byte NetFlow v5 record
        byte[] rec = new byte[48];
        // Src IP: 10.0.0.1
        rec[0] = 10; rec[1] = 0; rec[2] = 0; rec[3] = 1;
        // Dst IP: 198.51.100.20
        rec[4] = (byte)198; rec[5] = 51; rec[6] = 100; rec[7] = 20;
        // Packets = 50
        rec[19] = 50;
        // Bytes = 75,000 (0x000124F8)
        rec[21] = 0x01; rec[22] = 0x24; rec[23] = (byte)0xF8;
        // Src Port = 54321 (0xD431)
        rec[32] = (byte)0xD4; rec[33] = 0x31;
        // Dst Port = 443 (0x01BB)
        rec[34] = 0x01; rec[35] = (byte)0xBB;
        // Protocol = 6 (TCP)
        rec[38] = 6;

        Solution.FlowRecord f = Solution.parse(rec);

        // Test 1: IP parsing
        if (f != null && "10.0.0.1".equals(f.srcIp) && "198.51.100.20".equals(f.dstIp)) {
            System.out.println("PASS: Test 1: Source and destination IPs decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: IP decoding failed");
        }

        // Test 2: Packets and Bytes
        if (f != null && f.packets == 50 && f.bytes == 75000) {
            System.out.println("PASS: Test 2: Flow packet count (50) and byte volume (75,000) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Volume metrics mismatch");
        }

        // Test 3: Ports and Protocol
        if (f != null && f.srcPort == 54321 && f.dstPort == 443 && f.protocol == 6) {
            System.out.println("PASS: Test 3: Ports (54321 -> 443) and TCP protocol (6) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: L4 metrics mismatch");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "NetFlow v5 records are fixed 48 bytes long in big-endian network byte order.",
            "Bytes 0..3 = Src IP; Bytes 4..7 = Dst IP.",
            "Bytes 16..19 = 32-bit Packet count; Bytes 20..23 = 32-bit Total bytes.",
            "Bytes 32..33 = Src Port; Bytes 34..35 = Dst Port; Byte 38 = Protocol (6=TCP, 17=UDP)."
        ],
        "example_java": """public class TelemetryExample {
    public static void main(String[] args) {
        System.out.println("NetFlow exports conversation metadata (5-tuples + volume counters) without copying packet payload bodies.");
    }
}""",
        "example_kt": """object TelemetryExample {
    fun main() {
        println("NetFlow exports conversation metadata (5-tuples + volume counters) without copying packet payload bodies.")
    }
}"""
    },
    "0050": {
        "title": "Enterprise Network Topology Validator",
        "key": "0050-topology-audit",
        "desc": "Audit enterprise network topology: verify VLAN trunk allowed lists, STP root bridge alignment with default gateway HSRP/VRRP active router.",
        "starter": """public class Solution {
    // Return true if Root Bridge switch MAC matches HSRP Active Gateway switch MAC
    // (Prevents suboptimal traffic dog-legging across STP blocked links!)
    public static boolean isStpGatewayAligned(String stpRootMac, String hsrpActiveMac) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static boolean isStpGatewayAligned(String stpRootMac, String hsrpActiveMac) {
        if (stpRootMac == null || hsrpActiveMac == null) return false;
        return stpRootMac.trim().equalsIgnoreCase(hsrpActiveMac.trim());
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Aligned Core Switch 1
        boolean ok1 = Solution.isStpGatewayAligned("00:00:0C:07:AC:01", "00:00:0C:07:AC:01");
        if (ok1) {
            System.out.println("PASS: Test 1: STP Root and HSRP Gateway aligned on Core Switch 1");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Aligned check failed");
        }

        // Test 2: Misaligned topology (Dog-leg routing traffic penalty)
        boolean ok2 = Solution.isStpGatewayAligned("00:00:0C:07:AC:01", "00:00:0C:07:AC:02");
        if (!ok2) {
            System.out.println("PASS: Test 2: Misaligned STP root and default gateway flagged");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Misalignment should be flagged");
        }

        // Test 3: Null safety
        if (!Solution.isStpGatewayAligned(null, "00:00:0C:07:AC:01")) {
            System.out.println("PASS: Test 3: Null safety verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Null safety failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "In high-availability enterprise design, the STP Root Bridge switch MUST also be the HSRP/VRRP Active default gateway for that VLAN.",
            "If they are on different switches, traffic must cross the inter-switch trunk link unnecessarily before reaching the default gateway (suboptimal dog-leg routing).",
            "Compare MAC strings case-insensitively with .equalsIgnoreCase()."
        ],
        "example_java": """public class EnterpriseAuditExample {
    public static void main(String[] args) {
        System.out.println("Always align STP Root Bridge and HSRP/VRRP Active Gateway on the same physical switch.");
    }
}""",
        "example_kt": """object EnterpriseAuditExample {
    fun main() {
        println("Always align STP Root Bridge and HSRP/VRRP Active Gateway on the same physical switch.")
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
