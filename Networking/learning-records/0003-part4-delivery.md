# Delivery of Part 4 (Professional Network Engineer Track)

## Scope Completed
- **Part 4: Track B — Professional Network Engineers (Sessions 36–50)**
  - 36: Physical Media & Optics (Cat6a, SMF 9µm vs MMF 50µm, modal dispersion, optical loss budget, 802.3ad LACP flow pinning)
  - 37: Enterprise Switching & CAM Tables (ASIC forwarding, CAM single-cycle lookup, learn/forward/flood rules, aging timers, CAM flooding attacks & port security)
  - 38: VLANs & 802.1Q Trunking (Access vs Trunk ports, 4-byte 802.1Q header: TPID 0x8100, PCP, DEI, 12-bit VID 4094 limit, Native VLAN, double-tagging hopping exploit)
  - 39: Inter-VLAN Routing (Router-on-a-Stick subinterfaces, trunk hairpin bottleneck, Layer 3 Multilayer Switches, SVIs, TCAM ternary matching)
  - 40: Spanning Tree Protocol (L2 loops vs IP TTL, broadcast storms, MAC flapping, Bridge ID, Root Bridge election, Root/Designated/Alternate ports, 802.1D timers vs RSTP 802.1w, PortFast & BPDU Guard)
  - 41: Enterprise IGP: OSPF (Distance-Vector vs Link-State, Dijkstra SPF, Area 0 backbone, 7-stage adjacency, DR/BDR election on multi-access, cost reference bandwidth tuning)
  - 42: BGP Fundamentals (Autonomous Systems, 2-byte/4-byte ASNs, Path-Vector model, TCP port 179, eBGP TTL=1 vs iBGP split-horizon rule & Route Reflectors)
  - 43: BGP Attributes & Path Selection (Transit vs Settlement-Free Peering at IXPs, Gao-Rexford valley-free rules, 13-step best-path selection, Local Preference, AS-PATH prepending, MED, Hot-Potato routing)
  - 44: Internet Routing Security & RPKI (The trust flaw of BGP, Longest Prefix Match sub-prefix hijacking, 2008 Pakistan YouTube incident, ROA cryptographically signed origins, RPKI ROV Valid/Invalid/NotFound)
  - 45: Perimeter Firewalls & Stateful Inspection (Stateless ACLs and the ephemeral port vulnerability, stateful connection tracking / conntrack, TCP state validation, tri-homed DMZ design)
  - 46: Enterprise VPNs: IPsec & WireGuard (Site-to-site vs remote access, AH vs ESP, Transport vs Tunnel modes, IKEv2 Phase 1/2 SAs, NAT-Traversal UDP 4500, MTU black holes & MSS clamping, WireGuard noise framework)
  - 47: Overlay Encapsulation: GRE & VXLAN (Limitations of 4,096 VLANs in multi-tenant clouds, Leaf-Spine Clos routed underlay with ECMP, 24-bit VNI 16M scale, UDP port 4789, VTEPs, 50-byte overhead & Jumbo Frames)
  - 48: Quality of Service: DSCP & Queuing (Bandwidth/Delay/Jitter/Loss, DiffServ architecture, 6-bit DSCP in TOS, Expedited Forwarding EF 46 for VoIP, PQ vs CBWFQ vs Low Latency Queuing LLQ, WRED, Policing vs Shaping)
  - 49: Network Observability & Telemetry (Syslog RFC 5424 severity 0-7, SNMPv3 USM authPriv vs v1/v2c cleartext community strings, MIB/OIDs, NetFlow v9/IPFIX flow records, gNMI push streaming telemetry over gRPC)
  - 50: Mixed Review: Network Engineering Mastery (Staff/Principal enterprise architecture gauntlet, root bridge hijack triage, accidental transit leak diagnosis, IPsec MTU black hole recovery)

## Quality & Pedagogical Verification
- Adheres to the benchmark standard established in Session 1:
  - Tufte-inspired clean typography, custom responsive inline SVGs for all architectural concepts.
  - Active recall prompts with revealable model answer keypoints.
  - Interactive fill-in drills across all lessons.
  - Equal-length multiple choice quizzes with zero clue giveaways.
  - Hands-on terminal CLI instructions (`show mac address-table`, `show ip bgp`, `pfctl -ss`, `tshark`, `netstat`).
  - Unbroken two-way navigation chain from Session 1 to Session 50 and back to `index.html`.
