#!/usr/bin/env python3
"""
enrich_part1.py — Injects rich per-option data-why explanations into Part 1 lessons (0005-0012).
"""

import os
import re

LESSONS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "lessons")

PART1_DATA = {
    "0005": [
        # Q0: Protocol field 6
        [
            "Incorrect: ICMP is IP protocol number 1.",
            "Incorrect: UDP is IP protocol number 17.",
            "Correct! Protocol number 6 designates TCP (Transmission Control Protocol).",
            "Incorrect: IPv6 is a separate Layer 3 network protocol (EtherType 0x86DD), not an IPv4 payload protocol."
        ],
        # Q1: TTL 1
        [
            "Incorrect: A router must decrement TTL before forwarding; it cannot put a packet with TTL 0 onto the wire.",
            "Incorrect: Routers do not reset TTLs on transit packets; doing so would create infinite routing loops.",
            "Incorrect: RFC 792 mandates sending an ICMP Time Exceeded message back to the source; packets are not silently dropped.",
            "Correct! Forwarding decrements TTL to 0, so the router discards the packet and sends ICMP Type 11 Code 0 (Time Exceeded) back to the sender."
        ],
        # Q2: IPv4 header length
        [
            "Correct! An IPv4 header with no options contains five 32-bit (4-byte) rows, totaling 20 bytes (IHL = 5).",
            "Incorrect: 40 bytes is the fixed header length of an IPv6 packet, not IPv4.",
            "Incorrect: 14 bytes is the standard Ethernet header length without 802.1Q tags.",
            "Incorrect: 8 bytes is the header length of UDP and ICMP echo messages, not IPv4."
        ],
        # Q3: Dropped packet
        [
            "Incorrect: IP has no retransmission mechanism or delivery timers; retransmission is left to Layer 4 (e.g. TCP).",
            "Correct! IP provides best-effort delivery: it has no acknowledgments, timeouts, or retransmissions. Higher layers must handle loss.",
            "Incorrect: IP does not request resends; it drops the packet and moves on.",
            "Incorrect: Once dropped from a queue, the packet is destroyed and cannot be rerouted."
        ],
        # Q4: 4000 byte DF packet on 1500 MTU
        [
            "Correct! The DF (Don't Fragment) bit forbids fragmentation, so the router drops the packet and returns ICMP Type 3 Code 4 (Fragmentation Needed).",
            "Incorrect: The DF bit explicitly forbids the router from fragmenting the packet.",
            "Incorrect: Routers never truncate packets as that would corrupt the transport payload and checksum.",
            "Incorrect: Routers do not hold packets in queues waiting for MTUs to change; MTU is a static link property."
        ],
        # Q5: Header field changed besides TTL
        [
            "Incorrect: Source IP remains unchanged across ordinary hops unless translated by NAT at a network boundary.",
            "Incorrect: The Identification field is set by the original sender for fragmentation tracking and is not modified by transit routers.",
            "Correct! Because TTL was decremented, the 16-bit IPv4 header checksum must be recalculated at every hop.",
            "Incorrect: The Protocol field identifies the Layer 4 payload and must remain unmodified for the destination host."
        ]
    ],
    "0006": [
        # Q0: Usable hosts /26
        [
            "Incorrect: 64 is the total number of IP addresses in a /26 block ($2^6$), but network and broadcast addresses are reserved.",
            "Correct! A /26 leaves 6 host bits ($2^6 = 64$). Subtracting 2 for network (.0) and broadcast (.63) yields 62 usable host addresses.",
            "Incorrect: 30 usable hosts corresponds to a /27 subnet ($2^5 - 2 = 30$).",
            "Incorrect: 126 usable hosts corresponds to a /25 subnet ($2^7 - 2 = 126$)."
        ],
        # Q1: Network address of 10.4.150.77/20
        [
            "Incorrect: 10.4.150.0 is not a multiple of 16 in the third octet; a /20 mask has step size 16 (256 - 240 = 16).",
            "Correct! A /20 mask is 255.255.240.0. In the third octet, 150 AND 240 (or $150 - (150 \\pmod{16})$) gives 144, so the network address is 10.4.144.0.",
            "Incorrect: 10.4.128.0 is 10.4.128.0/20, which covers 128 to 143; 150 is above this range.",
            "Incorrect: 10.4.0.0 is a /16 boundary, far too broad for a /20."
        ],
        # Q2: Netmask 0xfffffc00 prefix length
        [
            "Incorrect: 0xfffffc00 has 16 bits in the first two bytes, plus 0xfc (6 bits) in the third byte: 16 + 6 = 22 bits, not 20.",
            "Correct! 0xffff is 16 ones. 0xfc is 11111100 (6 ones). 16 + 6 = 22 ones, which is a /22 prefix.",
            "Incorrect: A /24 mask in hex is 0xffffff00.",
            "Incorrect: A /26 mask in hex is 0xffffffc0."
        ],
        # Q3: Pair in the same /25
        [
            "Correct! A /25 divides the octet into 0..127 and 128..255. Both .10 and .100 fall strictly in the lower 0..127 half.",
            "Incorrect: .120 is in the lower half (0..127) while .130 is in the upper half (128..255), so they are in separate subnets.",
            "Incorrect: .1 is in 0..127 while .200 is in 128..255, spanning two different subnets.",
            "Incorrect: .50 is in the lower half (0..127) while .250 is in the upper half (128..255)."
        ],
        # Q4: Host sends to 10.1.3.4 from 10.1.2.3/24
        [
            "Incorrect: 10.1.3.4 is on a different /24 subnet (10.1.3.0/24 vs 10.1.2.0/24), so direct broadcast ARP on the local link will not reach it.",
            "Correct! Because 10.1.3.4 is on a different subnet, the host consults its routing table and forwards the packet to its default gateway.",
            "Incorrect: The host does not drop packets destined for other subnets; that is the entire purpose of routed networking.",
            "Incorrect: Subnet masks do not expand automatically; subnets are fixed administrative boundaries."
        ],
        # Q5: Why only 254 usable in /24
        [
            "Incorrect: Gateway routers can use any valid host address (typically .1 or .254); their address is not reserved by RFC specifications.",
            "Correct! The lowest address (all host bits 0) represents the network itself, and the highest address (all host bits 1) is the directed broadcast address.",
            "Incorrect: Loopback addresses live in the 127.0.0.0/8 block, not within individual /24 subnets.",
            "Incorrect: Private networks (RFC 1918) use standard subnetting rules with standard 2-address reservation per subnet."
        ]
    ],
    "0007": [
        # Q0: ARP request destination MAC
        [
            "Incorrect: 00:00:00:00:00:00 is an invalid/null MAC address, not an Ethernet broadcast.",
            "Correct! ARP requests are broadcast to ff:ff:ff:ff:ff:ff so every host on the Layer 2 broadcast domain receives and inspects the query.",
            "Incorrect: The sender does not yet know the target's MAC address; that is the exact problem ARP is solving.",
            "Incorrect: The default gateway's MAC is only used when sending packets off-subnet, not as the L2 frame destination for local ARP requests."
        ],
        # Q1: First packet to server on same subnet
        [
            "Incorrect: The host cannot send the IP packet yet because Ethernet requires a destination MAC address for framing.",
            "Correct! The host must first send an ARP request to resolve the server's IP to its Layer 2 MAC address before it can transmit the IP packet.",
            "Incorrect: The default gateway is only used when the destination IP lies outside the local subnet.",
            "Incorrect: DNS resolves hostnames to IP addresses; here the host already has the destination IP (10.1.2.99)."
        ],
        # Q2: Why ARP reply can be unicast
        [
            "Incorrect: Switches learn MAC locations by observing ingress frames, but the unicast reply works because the replier knows the requester's MAC.",
            "Correct! The ARP request packet explicitly included the sender's MAC and IP addresses in its payload, so the replier addresses the reply directly to the requester.",
            "Incorrect: ARP replies are not sent to default gateways unless the gateway itself was the requester.",
            "Incorrect: ARP replies are strictly unicast, not broadcast, to minimize unnecessary network chatter."
        ],
        # Q3: How host checks ARP reply is genuine
        [
            "Incorrect: Standard ARP packets contain no cryptographic signatures, HMACs, or authentication headers.",
            "Incorrect: DHCP servers manage IP leasing, not real-time verification of ARP responses.",
            "Correct! Standard ARP has zero authentication: any host can claim any IP address, which makes ARP vulnerable to ARP spoofing/poisoning.",
            "Incorrect: Routers do not inspect or validate intra-subnet ARP replies between local link hosts."
        ],
        # Q4: EtherType for ARP
        [
            "Incorrect: 0x0800 designates IPv4.",
            "Correct! EtherType 0x0806 identifies an Address Resolution Protocol (ARP) packet.",
            "Incorrect: 0x86DD designates IPv6.",
            "Incorrect: 0x8100 designates an 802.1Q VLAN tagged frame."
        ],
        # Q5: Ping unused address on local subnet
        [
            "Incorrect: ICMP Destination Unreachable is generated by routers, not by local link switches or absent hosts.",
            "Correct! The host repeatedly broadcasts ARP requests ('Who has 10.1.2.X?'). Since no device responds, the ARP requests time out with no replies.",
            "Incorrect: An unused IP address has no active NIC to send an ICMP Echo Reply.",
            "Incorrect: Since ARP never succeeds, the host never transmits an ICMP Echo Request onto the wire."
        ]
    ],
    "0008": [
        # Q0: First DHCP message dest IP
        [
            "Incorrect: 127.0.0.1 is the local loopback interface.",
            "Incorrect: 10.0.0.1 is a specific unicast IP; the client does not yet know the network subnet or router address.",
            "Correct! The client has no IP address yet (source 0.0.0.0) and broadcasts to 255.255.255.255 so any local DHCP server or relay agent receives it.",
            "Incorrect: 224.0.0.1 is the all-hosts multicast address, not the destination for initial DHCP discovery."
        ],
        # Q1: DHCP transport and ports
        [
            "Incorrect: DHCP uses UDP, not TCP, because TCP requires a 3-way handshake that cannot operate without assigned IP addresses.",
            "Correct! DHCP runs over UDP: clients send to server port 67, and servers respond to client port 68.",
            "Incorrect: Ports 53/5353 are used by DNS and mDNS.",
            "Incorrect: DHCP does not use raw IP packets; it uses standard UDP datagrams."
        ],
        # Q2: DHCP DORA order
        [
            "Correct! The standard 4-step sequence is Discover (client broadcast), Offer (server response), Request (client selection), and Acknowledge (server confirmation) — DORA.",
            "Incorrect: The server cannot Offer before the client Discovers, and Request must precede Ack.",
            "Incorrect: Discover must come first; Request cannot occur before an Offer is received.",
            "Incorrect: Acknowledgement is the final step confirming the lease, not the second step."
        ],
        # Q3: 169.254.x.x address meaning
        [
            "Incorrect: Public Wi-Fi captive portals assign private RFC 1918 addresses (e.g. 10.x.x.x or 192.168.x.x) and redirect HTTP traffic.",
            "Correct! 169.254.0.0/16 is the IPv4 Link-Local (APIPA) range, assigned by the OS when DHCP discovery times out with no server response.",
            "Incorrect: 169.254.x.x indicates DHCP failure, not static configuration.",
            "Incorrect: APIPA addresses cannot be routed across the Internet; they only allow communication between devices on the same physical link."
        ],
        # Q4: Lease 8 hours, when first renew
        [
            "Incorrect: Waiting until 8 hours (lease expiration) would risk immediate network disruption if the renewal packet is delayed.",
            "Correct! By default (RFC 2131), the renewal timer T1 defaults to 50% of the lease duration (4 hours for an 8-hour lease).",
            "Incorrect: T2 (rebinding timer) defaults to 87.5% (7 hours), used only if T1 unicast renewal fails.",
            "Incorrect: 1 hour is 12.5% of the lease; renewal does not begin that early unless specified by custom DHCP options."
        ],
        # Q5: Rejoin home Wi-Fi: only Request and Ack
        [
            "Incorrect: DHCP servers do not push leases to clients without an initial client message.",
            "Correct! When reconnecting to a known network, the client sends a directed DHCP Request asking to reuse its previously assigned IP; the server replies with Ack.",
            "Incorrect: Home routers do not cache client MACs indefinitely; the client actively initiates the exchange with DHCP Request.",
            "Incorrect: Discover is only omitted when the client attempts to renew/rebind an existing lease; it is not bypassed by ARP."
        ]
    ],
    "0009": [
        # Q0: LPM table lookup
        [
            "Incorrect: Longest Prefix Match selects the route with the most specific (longest) subnet mask matching the destination IP.",
            "Correct! The router compares all matching routes and forwards through the interface associated with the longest prefix length (most specific mask).",
            "Incorrect: Routing tables are not evaluated in first-come order; prefix length determines precedence.",
            "Incorrect: Metric/cost is only used to break ties between routes with identical prefix lengths."
        ],
        # Q1: traceroute * * * at hop 6 then normal hops 7-12
        [
            "Incorrect: If hop 6 dropped packets entirely, hops 7 to 12 could not have received packets or replied.",
            "Correct! The router at hop 6 forwarded the transit data packets normally, but rate-limited or disabled generating ICMP Time Exceeded control messages for its own IP.",
            "Incorrect: Traceroute probes cannot jump over intermediate physical routers without an overlay tunnel.",
            "Incorrect: Hops 7-12 prove the path was functional; only ICMP diagnostic generation at hop 6 was silenced."
        ],
        # Q2: Forwarding vs Routing
        [
            "Incorrect: Computing shortest paths using Dijkstra or Bellman-Ford is the control plane routing function.",
            "Correct! Forwarding is the data-plane action: taking a packet from an input interface, looking up its destination IP in the FIB, and pushing it to the output interface.",
            "Incorrect: Exchanging BGP or OSPF advertisements is the distributed routing control plane.",
            "Incorrect: Building the routing information base (RIB) is routing, not packet forwarding."
        ],
        # Q3: How traceroute learns third router address
        [
            "Incorrect: Routers do not append their IP addresses to traversing packets (unless IP Record Route option is used, which traceroute does not use).",
            "Correct! Traceroute sends a probe with TTL=3. The 3rd router decrements TTL to 0, drops the probe, and sends ICMP Time Exceeded using its own interface IP as the source.",
            "Incorrect: DNS reverse lookups translate IPs to names after the IP is discovered; they do not reveal intermediate router hops.",
            "Incorrect: Routers do not run specialized traceroute daemons or query protocols for path tracing."
        ],
        # Q4: Table with 0.0.0.0/0 and 10.8.0.0/16
        [
            "Incorrect: 0.0.0.0/0 has prefix length 0; any more specific route (like /16) takes precedence for matching destinations.",
            "Correct! Packets for 10.8.x.x match 10.8.0.0/16 (prefix 16 > 0) and route through the VPN; all other traffic matches the default route (0.0.0.0/0) via the home gateway.",
            "Incorrect: Routing does not round-robin across routes of different prefix lengths.",
            "Incorrect: The home gateway never sees 10.8.x.x traffic because the /16 route directs it into the VPN tunnel interface."
        ],
        # Q5: Local subnet ARP target
        [
            "Incorrect: ARP is only sent for the default gateway when the destination IP is on an outside remote network.",
            "Correct! For hosts on the local subnet, the sender ARPs directly for the destination host's IP address.",
            "Incorrect: Broadcast address (255.255.255.255) is not an ARP target; ARP resolves specific unicast IPs.",
            "Incorrect: The host does not ARP for its own IP unless performing Gratuitous ARP for duplicate IP detection."
        ]
    ],
    "0010": [
        # Q0: What does NAT rewrite on outbound packet
        [
            "Incorrect: The destination IP remains the external server's IP so the packet reaches the target.",
            "Correct! In SNAT/PAT, the router replaces the private source IP (and source port) with its own public IP and an allocated public port.",
            "Incorrect: NAT does not modify destination MAC; standard Layer 2 hop-by-hop framing handles MAC rewriting.",
            "Incorrect: The TCP sequence number is not altered by standard NAPT/PAT routers."
        ],
        # Q1: How PAT multiplexes multiple hosts
        [
            "Incorrect: Ethernet VLAN tags are link-local and stripped at the Layer 3 router boundary.",
            "Incorrect: Standard NAT uses a single public IP, not a distinct public IP per internal client.",
            "Correct! PAT (Port Address Translation) tracks connection 5-tuples and rewrites the source Layer 4 port to uniquely identify each internal flow.",
            "Incorrect: IP Identification fields are not used for flow demultiplexing by NAT gateways."
        ],
        # Q2: Inbound connection to private host behind NAT
        [
            "Incorrect: NAT routers drop unsolicited inbound packets because no matching entry exists in the translation state table.",
            "Correct! By default, inbound packets without an existing state table entry are dropped; port forwarding or UPnP/NAT-PMP is required to map inbound ports.",
            "Incorrect: NAT routers do not broadcast unsolicited inbound packets to all internal machines.",
            "Incorrect: NAT routers do not respond with ICMP Redirect for inbound external connection attempts."
        ],
        # Q3: NAT traversal techniques (STUN/TURN)
        [
            "Incorrect: STUN does not bypass NAT; it discovers the external public IP and port mapped by the NAT.",
            "Correct! STUN discovers public reflexive IP:port bindings; if symmetric NAT prevents direct peer-to-peer connection, TURN relays the media traffic.",
            "Incorrect: TURN is a relay protocol, not a firewall rule injector.",
            "Incorrect: STUN/TURN operate at application Layer 7 and do not modify router firmware."
        ],
        # Q4: Effect of NAT on end-to-end principle
        [
            "Incorrect: NAT does not improve end-to-end transparency; it breaks bidirectional symmetry.",
            "Correct! NAT breaks the original end-to-end principle of the Internet because hosts behind NAT cannot directly receive incoming connections without intermediate relays or state punching.",
            "Incorrect: NAT has no effect on cryptographic encryption strength, though it interferes with protocols embedding IP addresses in payloads (like FTP/SIP).",
            "Incorrect: NAT does not eliminate the need for Layer 4 port numbers; it relies heavily on them."
        ],
        # Q5: Full cone vs Symmetric NAT
        [
            "Incorrect: Full cone NAT maps an internal port to an external port that any external host can send to.",
            "Correct! Symmetric NAT assigns a different external port mapping for every distinct destination IP:port, making peer-to-peer hole punching fail without TURN relays.",
            "Incorrect: Symmetric NAT does not use symmetric key encryption.",
            "Incorrect: Both full cone and symmetric NAT perform address and port translation."
        ]
    ],
    "0011": [
        # Q0: IPv6 address bit length
        [
            "Incorrect: 32 bits is the length of an IPv4 address.",
            "Incorrect: 64 bits is the length of a standard IPv6 interface identifier (host portion).",
            "Correct! An IPv6 address is 128 bits (16 octets) long, represented as eight groups of four hexadecimal digits.",
            "Incorrect: 256 bits is the key length of AES-256 or SHA-256, not an IP address."
        ],
        # Q1: What replaced ARP in IPv6
        [
            "Incorrect: DHCPv6 assigns addresses and prefixes, but does not perform Layer 2 MAC resolution.",
            "Correct! Neighbor Discovery Protocol (NDP), which uses ICMPv6 Neighbor Solicitation (NS) and Neighbor Advertisement (NA), replaces ARP in IPv6.",
            "Incorrect: IGMP is used for IPv4 multicast group management, replaced by MLD in IPv6.",
            "Incorrect: ARPv6 does not exist; IPv6 entirely eliminated broadcast and ARP in favor of multicast NDP."
        ],
        # Q2: IPv6 broadcast addresses
        [
            "Incorrect: IPv6 has no broadcast addresses; broadcast was completely removed from the protocol.",
            "Correct! IPv6 eliminated broadcast; all broadcast functions are performed using targeted multicast groups (such as all-nodes multicast ff02::1).",
            "Incorrect: ff02::1 is all-nodes multicast, not a broadcast address.",
            "Incorrect: 255.255.255.255 is an IPv4 broadcast address."
        ],
        # Q3: IPv6 loopback address
        [
            "Incorrect: 127.0.0.1 is the standard IPv4 loopback address.",
            "Correct! The IPv6 loopback address is ::1 (127 zeros followed by a single one).",
            "Incorrect: :: (all zeros) represents the unspecified address (INADDR_ANY in IPv4).",
            "Incorrect: fe80::1 is a link-local unicast address, commonly used as a default gateway."
        ],
        # Q4: IPv6 link-local prefix
        [
            "Incorrect: fc00::/7 is Unique Local Addresses (ULA), the IPv6 equivalent of RFC 1918 private space.",
            "Correct! fe80::/10 defines link-local addresses, required on every active IPv6 interface and non-routable beyond the local link.",
            "Incorrect: 2000::/3 defines the global unicast address (GUA) space routable on the public Internet.",
            "Incorrect: ff00::/8 defines IPv6 multicast addresses."
        ],
        # Q5: IPv6 header checksum
        [
            "Incorrect: IPv6 headers do not contain a checksum.",
            "Correct! IPv6 removed the header checksum completely to speed up forwarding; error checking is delegated to Layer 2 CRC and Layer 4 checksums.",
            "Incorrect: The checksum was not moved to extension headers; it was eliminated from L3 entirely.",
            "Incorrect: Routers do not verify a Layer 3 checksum on transit IPv6 packets because none exists."
        ]
    ],
    "0012": [
        # Q0: Teammate Mac 169.254.81.3
        [
            "Incorrect: 169.254.x.x means the host has no gateway or network IP; changing DNS settings cannot resolve link-layer DHCP failure.",
            "Incorrect: Traceroute requires IP routing; with an APIPA address and no default gateway, packets cannot leave the machine.",
            "Correct! 169.254.x.x indicates DHCP failure. Capturing on en0 filtered on 'bootp || dhcp' reveals whether Discover broadcasts leave the Mac and if any DHCP server replies.",
            "Incorrect: The host only ARPs for machines on its local subnet; remote web servers never appear in the local ARP cache."
        ],
        # Q1: traceroute * * * at hop 9 but curl works
        [
            "Correct! curl works, proving the path and server are up. Stars at the destination usually mean a firewall drops UDP traceroute probes; probing with ICMP Echo (-I) often gets through.",
            "Incorrect: The probes are already reaching hop 9 near the server's network; increasing max hops does not bypass firewall drops.",
            "Incorrect: curl succeeded, so there is no general packet loss; only traceroute's UDP probe packets are being filtered.",
            "Incorrect: curl succeeds, proving the server and application are completely healthy."
        ],
        # Q2: Guest Wi-Fi 192.168.50.23/24 to printer 192.168.1.40
        [
            "Incorrect: 192.168.1.40 is in a different /24; the Mac will never ARP for it directly.",
            "Incorrect: Multicast ping6 ff02::1 is IPv6 all-nodes multicast, irrelevant for diagnosing an IPv4 destination.",
            "Incorrect: The netmask is working as configured; 192.168.1.0/24 is topologically separate from 192.168.50.0/24.",
            "Correct! 'route -n get 192.168.1.40' displays the kernel routing decision and which gateway interface the Mac attempts to use."
        ],
        # Q3: 10.0.6.37/22 ping 10.0.5.200 Host is down
        [
            "Incorrect: 10.0.5.200 is inside 10.0.4.0/22 (10.0.4.0–10.0.7.255); the Mac ARPs directly and never involves the gateway.",
            "Correct! 'Host is down' on macOS means the OS sent repeated ARP requests on the local link without receiving any ARP reply.",
            "Incorrect: If a host's firewall drops ICMP Echo, it still responds to ARP requests, resulting in 'Request timeout', not 'Host is down'.",
            "Incorrect: 10.0.5.200 is strictly within the /22 subnet (10.0.4.0 to 10.0.7.255)."
        ],
        # Q4: Forwarded port 25565, internet-side 100.72.14.9
        [
            "Incorrect: Port 25565 is an unprivileged user port (> 1024) commonly used by Minecraft.",
            "Incorrect: The router already has a default route to the ISP.",
            "Correct! 100.72.14.9 is inside 100.64.0.0/10 (RFC 6598 Carrier-Grade NAT). The ISP's CGNAT blocks unsolicited inbound connections to your home router.",
            "Incorrect: CGNAT upstream causes this blockage, regardless of whether the home router is symmetric NAT."
        ],
        # Q5: Hotel network: ping -D -s 1472 fails, ping -D -s 1400 works
        [
            "Correct! 1,472 data bytes + 8 ICMP + 20 IP = 1,500 bytes. With DF set, an intermediate router with MTU < 1500 must drop the packet, proving path MTU is between 1428 and 1499.",
            "Incorrect: If the hotel Wi-Fi was fragmenting packets, the large ping would have succeeded instead of failing.",
            "Incorrect: 1.1.1.1 handles standard 1500-byte packets; the drop is caused by an intermediate tunnel MTU on the local network path.",
            "Incorrect: ICMP has no inherent 1,400-byte cap."
        ],
        # Q6: Amsterdam to Tokyo (9300 km), ping 6 ms
        [
            "Incorrect: Speed of light in fiber limits one-way propagation to ~5 µs/km (200 km/ms). 9,300 km round-trip requires at least 93 ms.",
            "Incorrect: Queuing delay can only add time; it cannot violate the physical speed of light in optical glass.",
            "Incorrect: Hardware NIC optimizations cannot accelerate photons travelling through transoceanic cables.",
            "Correct! Physics mandates ~93 ms floor for a 9,300 km round-trip. A 6 ms ping proves an Anycast CDN edge node within ~600 km answered the query locally."
        ],
        # Q7: ifconfig en0 inet6 shows fe80:: but no global address
        [
            "Correct! SLAAC global address autoconfiguration requires a Router Advertisement (ICMPv6 type 134) containing prefix information.",
            "Incorrect: ICMPv6 type 135 is Neighbor Solicitation (NDP's equivalent of ARP request), not a router advertisement.",
            "Incorrect: ARP is strictly an IPv4 protocol; IPv6 uses ICMPv6 NDP.",
            "Incorrect: ICMP type 11 is IPv4 Time Exceeded."
        ],
        # Q8: Table with default, 10.0.0.0/8 VPN, 10.0.4.0/22 Wi-Fi. Packet for 10.0.7.200?
        [
            "Incorrect: The VPN /8 route has a shorter prefix length (8 bits) than the directly connected /22 route (22 bits).",
            "Correct! 10.0.7.200 matches default /0, VPN /8, and Wi-Fi /22. Longest Prefix Match (/22) wins, which is directly connected on Wi-Fi, so the host ARPs directly for 10.0.7.200.",
            "Incorrect: The gateway at 10.0.4.1 is only used when the default route /0 wins; here the more specific /22 wins.",
            "Incorrect: The packet is not dropped because it matches all three valid routing entries."
        ],
        # Q9: 2-hour DHCP lease, when broadcast Requests
        [
            "Incorrect: At T1 = 50% (60 minutes), the client unicasts renewal requests directly to its original server.",
            "Incorrect: 90 minutes is 75% of the lease; the standard rebinding timer is 87.5%.",
            "Incorrect: At 120 minutes, the lease expires completely and the client reverts to DHCP Discover.",
            "Correct! According to RFC 2131, at T2 = 87.5% (105 minutes for a 2-hour lease), the client transitions to REBINDING and broadcasts Requests to any available server."
        ],
        # Q10: RFC 5952 canonical form of 2001:0db8:0000:0000:00a0:0000:0000:0001
        [
            "Incorrect: '::' can only appear once in an IPv6 address; having two '::' violates RFC 5952.",
            "Incorrect: Leading zeros in 00a0 must be trimmed to a0.",
            "Correct! Under RFC 5952, leading zeros are omitted (00a0 → a0), and the first run of consecutive zero groups is compressed to '::' (2001:db8::a0:0:0:1).",
            "Incorrect: Leading zero in '0db8' must be compressed to 'db8'."
        ],
        # Q11: Outgoing web request, headers read/rewritten by home NAT
        [
            "Correct! A home NAT router builds a new Link frame (MAC), rewrites Network IP and decrements TTL, and rewrites Transport (TCP/UDP) port and recalculates checksums.",
            "Incorrect: Standard NAT must rewrite transport layer ports (PAT/NAPT) to demultiplex returning flows.",
            "Incorrect: The Layer 2 Ethernet link header is also reconstructed at every hop.",
            "Incorrect: Standard NAT routers do not inspect or rewrite Application layer (HTTP) payloads."
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

    # Find each <div class="q" data-answer="..."> ... </div>
    # and replace its <ol class="options">...</ol>
    q_matches = list(re.finditer(r'<div class="q" data-answer="(\d+)">([\s\S]*?<div class="q-explain">[\s\S]*?</div>\s*</div>)', html))
    if len(q_matches) != len(questions_data):
        print(f"Warning: {filename} has {len(q_matches)} questions, but {len(questions_data)} data entries provided.")

    # Apply replacements in reverse order of appearance to keep indices stable
    new_html = html
    for qi in reversed(range(min(len(q_matches), len(questions_data)))):
        qm = q_matches[qi]
        q_text = qm.group(0)
        options_data = questions_data[qi]

        # Find <ol class="options">([\s\S]*?)</ol> inside q_text
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
    for num, q_data in sorted(PART1_DATA.items()):
        enrich_file(num, q_data)

if __name__ == "__main__":
    main()
