#!/usr/bin/env python3
"""
enrich_part5.py — Injects rich per-option data-why explanations into Part 5 Cloud & Modern Infra lessons (0051-0065).
"""

import os
import re

LESSONS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "lessons")

PART5_DATA = {
    "0051": [
        # Q0: SDN primary architectural change
        [
            "Incorrect: BGP tables are massive (~950,000 routes) and maintained by core edge routers, not individual host NICs.",
            "Correct! SDN decouples the control plane (routing logic, state, and policy decisions) from the distributed hardware data plane forwarding engines, centralizing intelligence in software.",
            "Incorrect: SDN does not replace dynamic address resolution with static text files.",
            "Incorrect: SDN forwards standard IP and Ethernet packets, continuing to rely on IP addresses and port numbers on the wire."
        ],
        # Q1: Northbound API role
        [
            "Correct! Northbound APIs expose high-level network abstractions to business applications, cloud orchestration platforms (like Kubernetes or OpenStack), and automation scripts.",
            "Incorrect: Programming switch TCAM forwarding entries is handled by the Southbound API (e.g. OpenFlow, P4, OVSDB).",
            "Incorrect: BGP peering uses standard TCP port 179 routing exchanges, not an SDN controller Northbound API.",
            "Incorrect: Clock synchronization across infrastructure is governed by NTP or PTP, not SDN Northbound APIs."
        ],
        # Q2: Modern SDN flow table entry elements
        [
            "Incorrect: DNS records and certificates belong to application/security services, not flow table forwarding rules.",
            "Incorrect: OSPF areas and Spanning Tree costs are legacy distributed control plane metrics.",
            "Correct! An SDN OpenFlow-style flow entry consists of match fields (packet headers, ingress port), actions (forward, drop, rewrite headers), and statistics/counters (packet and byte counters).",
            "Incorrect: AS paths and MED attributes are specific to BGP, not flow table pipeline entries."
        ]
    ],
    "0052": [
        # Q0: AWS /28 usable IPs
        [
            "Incorrect: 16 is the total raw count of addresses in a /28 ($2^4 = 16$), but AWS reserves 5 addresses per subnet.",
            "Incorrect: 14 is the standard RFC usable count ($16 - 2 = 14$), but AWS reserves 5 addresses, not 2.",
            "Correct! A /28 has 16 total IP addresses ($2^{32-28} = 16$). AWS reserves 5 IPs (.0 network, .1 VPC router, .2 Amazon DNS, .3 future reserved, .255 broadcast), leaving exactly 16 - 5 = 11 usable host IPs.",
            "Incorrect: AWS fully supports /28 subnets (AWS VPC subnets range from /16 to /28)."
        ],
        # Q1: What makes a subnet isolated
        [
            "Correct! An isolated subnet (such as a secure database tier) has no route table entry pointing to either an Internet Gateway (IGW) or a NAT Gateway, preventing any outbound or inbound Internet traffic.",
            "Incorrect: Subnets permit full intra-VPC communication with other subnets unless blocked by Security Groups or NACLs.",
            "Incorrect: CGNAT addresses (100.64.0.0/10) are used by carriers and VPC endpoints, not mandatory for isolated subnets.",
            "Incorrect: IP packets are not converted into DNS queries."
        ],
        # Q2: AWS subnet geographic boundary
        [
            "Incorrect: In AWS, subnets cannot span multiple Availability Zones; multi-AZ resilience requires distinct subnets per AZ.",
            "Correct! In AWS, every subnet is strictly bound to a single physical Availability Zone (AZ) and cannot cross AZ boundaries.",
            "Incorrect: An Internet Gateway is an elastic regional VPC-level gateway; it does not require dedicated per-AZ deployment.",
            "Incorrect: Cloud VPC subnets almost exclusively use private RFC 1918 IPv4 address ranges."
        ]
    ],
    "0053": [
        # Q0: Why IGW does not suffer bottlenecks
        [
            "Incorrect: The IGW is not a single EC2 virtual machine appliance.",
            "Correct! The AWS Internet Gateway is a distributed, software-defined SDN translation service built directly into the cloud hypervisors and VPC network fabric, automatically scaling to line rate without single-point bottlenecks.",
            "Incorrect: An IGW performs 1:1 stateless NAT between public and private IPs at Layer 3; it does not terminate TLS 1.3 certificates.",
            "Incorrect: The IGW performs 1:1 IP address mapping; it does not multiplex source ports (which is what NAT Gateway does)."
        ],
        # Q1: Managed NAT Gateway primary benefit
        [
            "Incorrect: NAT Gateways are network appliances and have no effect on instance CPU allocations.",
            "Incorrect: NAT Gateways block all unsolicited inbound connections, preventing external Internet users from initiating SSH sessions.",
            "Correct! A Managed NAT Gateway performs Port Address Translation (PAT), allowing instances in private subnets to initiate outbound requests (e.g. for OS updates) while strictly blocking external hosts from initiating connections inward.",
            "Incorrect: NAT Gateways do not compress application JSON payloads."
        ],
        # Q2: Public vs Private subnet in AWS
        [
            "Incorrect: Public subnets do not require every instance to have a public IP; instances can have private-only IPs.",
            "Incorrect: Multi-factor authentication is an identity/IAM control, not a subnet routing classification.",
            "Correct! In AWS networking, a subnet is defined as 'public' solely if its associated route table contains a default route (0.0.0.0/0) pointing to an Internet Gateway (igw-xxxx).",
            "Incorrect: Both public and private subnets reside inside the same parent VPC CIDR block."
        ]
    ],
    "0054": [
        # Q0: Security Group vs NACL statefulness
        [
            "Incorrect: Network ACLs are stateless, not stateful.",
            "Correct! Security Groups are stateful: if a request is permitted outbound, the return traffic is automatically tracked and allowed regardless of inbound rules. Network ACLs are stateless: both inbound and outbound traffic must be explicitly permitted.",
            "Incorrect: Security Groups operate at the Elastic Network Interface (ENI) level, whereas NACLs operate at the subnet boundary.",
            "Incorrect: Security Groups support permit-only rules, whereas NACLs support both allow and deny rules."
        ],
        # Q1: Order of evaluation in AWS NACL
        [
            "Incorrect: NACLs are evaluated by rule number, not protocol priority.",
            "Correct! NACL rules are evaluated sequentially in ascending order by rule number (e.g. rule 100 before rule 200). The first matching rule immediately determines whether the packet is ALLOWED or DENIED.",
            "Incorrect: Lowest rule numbers take precedence over higher rule numbers.",
            "Incorrect: NACL evaluation stops immediately upon the first matching rule."
        ],
        # Q2: Missing ephemeral ports in stateless NACL
        [
            "Incorrect: Security Groups are stateful and automatically permit established return traffic.",
            "Correct! Because NACLs are stateless, when an instance sends an outbound request to port 443, the server's reply arrives on an ephemeral port (1024-65535). If the inbound NACL lacks an allow rule for ephemeral ports, the return packets are dropped.",
            "Incorrect: AWS route tables do not filter traffic based on TCP port numbers.",
            "Incorrect: TCP delayed ACKs do not cause permanent packet drops."
        ]
    ],
    "0055": [
        # Q0: VPC Peering non-transitive limitation
        [
            "Incorrect: VPC Peering connections can span different AWS accounts and regions.",
            "Correct! VPC Peering is non-transitive: if VPC A is peered with VPC B, and VPC B is peered with VPC C, traffic from VPC A cannot transit through VPC B to reach VPC C.",
            "Incorrect: VPC Peering supports high-bandwidth line-rate communication without arbitrary 1 Gbps caps.",
            "Incorrect: VPC Peering operates strictly over private AWS network links, not public Internet tunnels."
        ],
        # Q1: Formula for full mesh peering
        [
            "Incorrect: $N$ connections only provides a ring or hub-and-spoke topology, not a full mesh.",
            "Correct! A full mesh interconnecting $N$ VPCs requires $N(N - 1) / 2$ individual peering connections, leading to severe configuration sprawl at scale (e.g. 50 VPCs require 1,225 peering links).",
            "Incorrect: $N^2$ includes self-connections and directional duplicates.",
            "Incorrect: $2N - 1$ is an arbitrary linear expression."
        ],
        # Q2: AWS Transit Gateway (TGW) architecture
        [
            "Incorrect: TGW is a regional managed cloud router, not an on-premises physical appliance.",
            "Correct! AWS Transit Gateway acts as a regional hub-and-spoke cloud router. VPCs and VPNs attach to the TGW, and route tables inside the TGW enable fully transitive, scalable routing across thousands of VPCs.",
            "Incorrect: TGW manages Layer 3 IP routing; it does not replace DNS with static hosts files.",
            "Incorrect: TGW attachments scale dynamically and do not require full-mesh peering."
        ]
    ],
    "0056": [
        # Q0: Gateway Endpoints vs Interface Endpoints
        [
            "Incorrect: Gateway endpoints deploy via route tables at zero cost, whereas Interface endpoints deploy ENIs that incur hourly charges.",
            "Correct! Gateway Endpoints are free route table targets available exclusively for S3 and DynamoDB; Interface Endpoints (AWS PrivateLink) deploy an ENI with a private IP in your subnet, charge hourly, and support hundreds of AWS and partner SaaS services.",
            "Incorrect: Interface Endpoints are private ENIs; they do not require public IP addresses.",
            "Incorrect: Gateway Endpoints operate within an AWS region; they do not cross on-premises Direct Connect without proxying."
        ],
        # Q1: PrivateLink private DNS resolution
        [
            "Incorrect: PrivateLink does not overwrite the instance /etc/hosts file.",
            "Correct! When Private DNS is enabled, Route 53 creates a private hosted zone that overrides the public service FQDN (e.g. s3.us-east-1.amazonaws.com) to resolve directly to the private IP of the interface VPC endpoint ENI.",
            "Incorrect: PrivateLink does not bypass DNS lookups.",
            "Incorrect: The operating system stub resolver does not require custom BGP routing daemons."
        ],
        # Q2: Connecting overlapping CIDRs via PrivateLink
        [
            "Incorrect: Layer 3 IP routing cannot forward packets between overlapping IP spaces without collisions.",
            "Correct! PrivateLink operates as a Layer 4 proxy: the consumer connects to a local private ENI IP in their own CIDR, and AWS proxy fabric routes the payload to the provider target group, completely decoupling internal IP addressing.",
            "Incorrect: BGP cannot resolve routing between identical IP subnets without translation.",
            "Incorrect: PrivateLink does not route over public Internet IPsec tunnels."
        ]
    ],
    "0057": [
        # Q0: AWS Direct Connect vs IPsec VPN
        [
            "Incorrect: Direct Connect operates over dedicated physical circuits, not the public Internet.",
            "Correct! AWS Direct Connect provides dedicated private physical fiber cross-connects (1G to 100G) bypassing the public Internet for predictable latency; IPsec VPN establishes encrypted tunnels over the public Internet.",
            "Incorrect: Direct Connect delivers lower, consistent latency compared to variable Internet VPN paths.",
            "Incorrect: Direct Connect is high performance; it does not cap bandwidth at 50 Mbps."
        ],
        # Q1: BGP active/standby failover between DX and VPN
        [
            "Incorrect: Direct Connect should be primary, not standby.",
            "Correct! Advertise corporate routes over Direct Connect with standard AS-path length, and advertise the same routes over the IPsec VPN with AS-path prepending (making it longer). AWS will prefer DX; if DX fails, traffic immediately fails over to VPN.",
            "Incorrect: BGP does not switch routes based on ping timeouts unless BFD or hold-down timers trigger.",
            "Incorrect: Static default routes lack automated link-failure detection across remote peers."
        ],
        # Q2: Bidirectional Forwarding Detection (BFD) purpose
        [
            "Incorrect: BFD does not encrypt payloads; it monitors link liveness.",
            "Correct! Standard BGP hold timers take 90 seconds (3 keepalives) to declare a dead neighbor. BFD sends sub-second heartbeats between router interfaces, detecting physical or link failures in under 300 ms for near-instantaneous BGP failover.",
            "Incorrect: BFD does not compress BGP routing table advertisements.",
            "Incorrect: BFD operates between adjacent Layer 3 router hops, not over HTTP/2."
        ]
    ],
    "0058": [
        # Q0: BGP Anycast routing mechanism
        [
            "Incorrect: DNS Anycast does not return all IP addresses in a round-robin list.",
            "Correct! In BGP Anycast, multiple geographically distributed data centers advertise the exact same IP address prefix via BGP. The Internet's routing fabric automatically directs each client's packets to the topologically closest PoP.",
            "Incorrect: Anycast does not clone packets across every data center; each packet travels to exactly one location.",
            "Incorrect: Anycast is standard Layer 3 IP routing supported by all Internet routers."
        ],
        # Q1: Why TCP over Anycast is vulnerable to route flapping
        [
            "Incorrect: Anycast supports standard TCP handshakes without packet corruption.",
            "Correct! If BGP routing shifts mid-connection (route flap), subsequent TCP packets for that flow are routed to a different physical PoP. Because the new PoP has no TCP connection state for that 5-tuple, it sends a TCP RST, aborting the connection.",
            "Incorrect: Anycast has no effect on TLS certificate verification.",
            "Incorrect: Anycast works over standard fiber optic links."
        ],
        # Q2: Cache-Control s-maxage vs max-age
        [
            "Incorrect: max-age applies to all caches unless overridden by s-maxage.",
            "Correct! 's-maxage' (shared max-age) applies specifically to public/shared caches such as CDNs and reverse proxies, overriding the standard 'max-age' which applies to client web browsers.",
            "Incorrect: s-maxage specifies a TTL in seconds; it does not mandate synchronous revalidation.",
            "Incorrect: s-maxage has no relationship to streaming video chunks."
        ]
    ],
    "0059": [
        # Q0: ALB (L7) vs NLB (L4) layer of operation
        [
            "Incorrect: NLB operates at Layer 4, while ALB operates at Layer 7.",
            "Correct! Application Load Balancer (ALB) operates at Layer 7 (HTTP/HTTPS), inspecting headers and paths; Network Load Balancer (NLB) operates at Layer 4 (TCP/UDP), forwarding packets based on 5-tuples at ultra-high throughput.",
            "Incorrect: NLB supports TLS termination, but operates at Layer 4; ALB operates at Layer 7.",
            "Incorrect: ALB does not forward raw Ethernet frames."
        ],
        # Q1: Preserving client source IP in ALB
        [
            "Incorrect: An ALB terminates the client TCP connection and opens a new connection to backends, rewriting the IP packet source.",
            "Correct! Because ALB is a reverse proxy that terminates client connections, it appends the original client IP to the 'X-Forwarded-For' HTTP request header before forwarding to target groups.",
            "Incorrect: ALB does not use IPsec tunneling to backends.",
            "Incorrect: Client IPs are not stored in DNS PTR records."
        ],
        # Q2: When to choose NLB over ALB
        [
            "Incorrect: Path-based URL routing requires Layer 7 header inspection, which requires an ALB.",
            "Correct! Choose an NLB when you require extreme line-rate performance (millions of RPS), sub-millisecond latency, static Elastic IP addresses, or support for non-HTTP protocols (raw TCP, UDP, gaming, financial feeds).",
            "Incorrect: Cookie-based sticky sessions for web apps are natively handled by ALB.",
            "Incorrect: WAF deep request payload inspection is integrated with ALB, not bare NLB."
        ]
    ],
    "0060": [
        # Q0: Fundamental Kubernetes Network Model rule
        [
            "Incorrect: The Kubernetes model mandates that Pods communicate without NAT.",
            "Correct! The K8s network model requires that every Pod gets its own unique IP address, and all Pods can communicate with all other Pods across nodes without Network Address Translation (NAT).",
            "Incorrect: Containers within the same Pod share an IP; each Pod has a distinct IP.",
            "Incorrect: Pods communicate using standard IP routing rather than forced proxying."
        ],
        # Q1: Linux veth pair role in Pod networking
        [
            "Incorrect: A veth pair is a software virtual device, not a physical hardware transceiver.",
            "Correct! A veth (virtual ethernet) pair acts like a virtual patch cable: one end is placed inside the Pod's private network namespace (as eth0), and the other end is attached to the host network bridge (e.g. cbr0).",
            "Incorrect: veth pairs forward Ethernet frames locally; they do not encrypt packets with AES.",
            "Incorrect: veth pairs operate in kernel software and do not configure switch trunk ports."
        ],
        # Q2: Multi-container communication in same Pod
        [
            "Incorrect: Containers in the same Pod do not communicate through external load balancers.",
            "Correct! Because all containers within a Pod share the exact same Linux network namespace, they share the same IP address and network interfaces, communicating directly over localhost (127.0.0.1).",
            "Incorrect: Broadcast frames across bridges are not used for intra-Pod IPC.",
            "Incorrect: Intra-Pod communication does not traverse IPsec VPN tunnels."
        ]
    ],
    "0061": [
        # Q0: Overlay CNI vs Native VPC CNI
        [
            "Incorrect: Native VPC CNIs (like AWS VPC CNI) provide higher performance with zero encapsulation overhead.",
            "Correct! Overlay CNIs (like Flannel or Calico VXLAN) encapsulate Pod packets inside UDP packets, adding packet overhead and hiding Pod IPs from the physical network; Native VPC CNIs assign real VPC secondary IPs directly to Pods.",
            "Incorrect: Overlay CNIs add encapsulation headers (e.g. 50 bytes for VXLAN), reducing effective MTU.",
            "Incorrect: Native VPC CNIs do not require physical fiber rewiring."
        ],
        # Q1: VXLAN encapsulation MTU impact
        [
            "Incorrect: Adding headers decreases available MTU, rather than expanding it.",
            "Correct! VXLAN adds 50 bytes of overhead (14 Ethernet + 20 IP + 8 UDP + 8 VXLAN). If the physical underlay MTU is 1500 bytes, the Pod interface MTU must be reduced to 1450 bytes to avoid packet fragmentation.",
            "Incorrect: 802.1Q tagging adds 4 bytes, not VXLAN encapsulation.",
            "Incorrect: MTU configuration is critical in overlays to prevent silent PMTUD black hole drops."
        ],
        # Q2: Secondary IP exhaustion in AWS VPC CNI
        [
            "Incorrect: Kubernetes clusters can scale to thousands of nodes.",
            "Correct! The AWS VPC CNI allocates real private secondary IP addresses from the host node's VPC subnet. If subnets are sized too small (e.g. a /24), deploying hundreds of Pods rapidly exhausts the available subnet IP addresses.",
            "Incorrect: AWS VPC CNI uses native VPC routing, completely bypassing VXLAN encapsulation.",
            "Incorrect: AWS VPC CNI is an official open-source Kubernetes CNI plugin maintained by AWS."
        ]
    ],
    "0062": [
        # Q0: Nature of a Kubernetes ClusterIP
        [
            "Incorrect: A ClusterIP is not bound to any physical or virtual network interface.",
            "Correct! A ClusterIP is a virtual 'ghost' IP address that does not exist on any physical interface or Pod. It exists purely as routing and translation rules inside the Linux kernel (iptables, IPVS, or eBPF).",
            "Incorrect: ClusterIPs are internal virtual IPs and are not routable on the public Internet.",
            "Incorrect: The host bridge (cbr0) carries Pod network traffic, not ClusterIP bindings."
        ],
        # Q1: How kube-proxy iptables mode load-balances
        [
            "Incorrect: iptables is a packet filter and NAT utility; it does not manage BGP peering sessions.",
            "Correct! In iptables mode, kube-proxy creates chains using the 'statistic mode random' module with probabilities $1/N, 1/(N-1), \\dots, 1.0$ to randomly DNAT destination packets to pod endpoints.",
            "Incorrect: Round-robin state is maintained in user space or IPVS; iptables uses random probability chains.",
            "Incorrect: DNS lookups resolve service names, but the ClusterIP-to-Pod translation is handled by iptables in the datapath."
        ],
        # Q2: IPVS mode performance advantage over iptables
        [
            "Incorrect: IPVS and iptables are both software kernel mechanisms.",
            "Correct! iptables evaluates rules sequentially ($O(N)$ complexity), causing high CPU consumption and packet latency as services scale to thousands. IPVS uses kernel hash tables ($O(1)$ complexity), providing constant-time lookups.",
            "Incorrect: IPVS operates within the Linux network stack and does not bypass the kernel.",
            "Incorrect: IPVS is a transport load balancer; it does not perform TLS certificate caching."
        ]
    ],
    "0063": [
        # Q0: What eBPF allows in the Linux kernel
        [
            "Incorrect: eBPF programs are sandboxed and verified; they do not crash or corrupt kernel memory.",
            "Correct! eBPF (extended Berkeley Packet Filter) allows developers to run sandboxed, verified bytecode programs directly inside the Linux kernel at runtime in response to events (like packet arrival), without modifying kernel code or loading kernel modules.",
            "Incorrect: eBPF is a software kernel virtual machine, not hardware ASIC microcode.",
            "Incorrect: eBPF operates in kernel space; it does not replace the POSIX C standard library."
        ],
        # Q1: XDP (eXpress Data Path) hook execution point
        [
            "Incorrect: XDP executes long before packets reach user-space socket buffers.",
            "Correct! XDP hooks execute at the lowest possible level in the network subsystem: directly inside the network device driver before the kernel allocates the costly `sk_buff` data structure, enabling line-rate DDoS filtering.",
            "Incorrect: Netfilter iptables chains execute much higher in the network stack after sk_buff allocation.",
            "Incorrect: XDP is an ingress packet hook, not an outbound queue scheduler."
        ],
        # Q2: How Cilium bypasses iptables overhead
        [
            "Incorrect: Cilium routes packets natively without wrapping all traffic in WireGuard.",
            "Correct! Cilium attaches eBPF programs directly to the socket layer (`sockops`) and network device hooks (`tc`/XDP), doing $O(1)$ BPF map lookups to forward packets directly between sockets, completely bypassing the netfilter iptables stack.",
            "Incorrect: Cilium operates on standard Linux kernels without proprietary vendor drivers.",
            "Incorrect: Cilium manages pod networking while preserving standard Linux process separation."
        ]
    ],
    "0064": [
        # Q0: Primary security benefit of Service Mesh mTLS
        [
            "Incorrect: mTLS operates over TCP/IP and does not eliminate IP routing tables.",
            "Correct! Mutual TLS (mTLS) ensures that all pod-to-pod communications are cryptographically encrypted in transit and mutually authenticated using cryptographic SPIFFE certificates, enforcing Zero Trust identity.",
            "Incorrect: mTLS encrypts traffic at Layer 4/7; it has no effect on optical dispersion.",
            "Incorrect: mTLS secures application payloads, but standard IP headers remain unencrypted for routing."
        ],
        # Q1: Service Mesh "Sidecar Tax" definition
        [
            "Incorrect: Cloud providers do not charge software license fees for open-source Istio or Envoy.",
            "Incorrect: Service mesh operates at the application/proxy layer and does not increase BGP route updates.",
            "Correct! The sidecar tax refers to the cumulative CPU/memory resource consumption and latency overhead (adding 1–3 ms and two extra proxy hops per RPC) introduced by injecting an Envoy proxy container into every application Pod.",
            "Incorrect: Pods continue to share a single IP address with their sidecar proxy container."
        ],
        # Q2: Container-to-Envoy communication in same Pod
        [
            "Incorrect: Containers inside a virtual machine do not connect across physical optical cables.",
            "Correct! Because all containers within the same Pod share the exact same Linux network namespace, the application container communicates with its local Envoy sidecar proxy directly over the loopback interface (`localhost` / 127.0.0.1).",
            "Incorrect: Intra-pod communication does not use BGP.",
            "Incorrect: Communication between containers in the same Pod is purely local and never leaves the host node."
        ]
    ],
    "0065": [
        # Q0: Stateless NACL blocking software downloads
        [
            "Incorrect: Security Groups are stateful and automatically permit inbound return packets.",
            "Correct! Network ACLs are completely stateless. An instance downloading updates sends outbound requests to port 443, but the server's return packets arrive on ephemeral client ports (1024-65535). If the inbound NACL lacks an ephemeral allow rule, packets are dropped.",
            "Incorrect: EC2 instances do not establish BGP sessions with default gateways.",
            "Incorrect: NAT Gateways support the full range of dynamic TCP ports."
        ],
        # Q1: Why S3 downloads via NAT Gateway cause bill shock
        [
            "Incorrect: S3 Gateway Endpoints are completely free and do not incur hourly or data charges.",
            "Incorrect: S3 does not throttle requests based on private IP origin.",
            "Correct! AWS Managed NAT Gateways charge approximately \\$0.045 per Gigabyte of data processed. Pulling terabytes of S3 data through a NAT Gateway incurs massive data processing fees; configuring a free S3 Gateway Endpoint routes traffic directly at zero cost.",
            "Incorrect: Cloud VPC route tables do not charge lookup fees."
        ],
        # Q2: AWS PrivateLink with overlapping CIDRs
        [
            "Correct! PrivateLink functions as a Layer 4 proxy: the consumer connects to a local private ENI IP inside their own subnet, and AWS network fabric proxies requests to the provider target group, completely decoupling internal IP address schemes.",
            "Incorrect: VPC Peering fails when CIDRs overlap due to routing collisions.",
            "Incorrect: BGP cannot route between identical IP blocks without translation.",
            "Incorrect: PrivateLink routes traffic privately across the AWS backbone, not over the public Internet."
        ],
        # Q3: NLB (L4) vs ALB (L7) distinguishing difference
        [
            "Incorrect: ALBs terminate connections and rely on X-Forwarded-For to preserve client IPs.",
            "Incorrect: ALBs operate at Layer 7 and do not route arbitrary raw UDP packets.",
            "Incorrect: NLBs operate at Layer 4 and do not inspect HTTP/2 paths.",
            "Correct! A Network Load Balancer (NLB) operates at Layer 4, providing static Elastic IP addresses, ultra-low sub-millisecond latency, and line-rate performance capable of handling millions of requests per second."
        ],
        # Q4: Intra-Pod container communication
        [
            "Incorrect: Intra-pod traffic does not traverse external cloud load balancers.",
            "Correct! Containers running within the exact same Pod share the same Linux network namespace, sharing IP and port spaces and communicating directly over localhost (127.0.0.1).",
            "Incorrect: Ethernet broadcast frames across bridges are not used for local container IPC.",
            "Incorrect: Containers within a Pod do not require encrypted IPsec tunnels."
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
    for num, q_data in sorted(PART5_DATA.items()):
        enrich_file(num, q_data)

if __name__ == "__main__":
    main()
