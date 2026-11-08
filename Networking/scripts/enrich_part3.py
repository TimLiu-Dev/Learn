#!/usr/bin/env python3
"""
enrich_part3.py — Injects rich per-option data-why explanations into Part 3 Developer Track lessons (0021-0035).
"""

import os
import re

LESSONS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "lessons")

PART3_DATA = {
    "0021": [
        # Q0: Root DNS server response for example.com
        [
            "Incorrect: Root DNS servers do not hold individual domain IP records; they only know where TLD servers reside.",
            "Correct! Root servers do not hold domain IP records. They return a referral delegation pointing to the .com Top-Level Domain (TLD) authoritative nameservers.",
            "Incorrect: ICMP Destination Unreachable is a Layer 3 routing error, not a DNS application response.",
            "Incorrect: MAC addresses exist only on the local Layer 2 link and are never known or returned by DNS."
        ],
        # Q1: Why 13 logical root server IP addresses
        [
            "Incorrect: Root server instances are operated by 12 distinct independent organizations using Anycast across hundreds of global physical locations.",
            "Incorrect: TCP header options have no influence on legacy DNS UDP message size constraints.",
            "Incorrect: IPv4 exhaustion occurred decades after the 13 root server architecture was standardized.",
            "Correct! Original DNS over UDP had a strict 512-byte payload limit to prevent IP fragmentation. Thirteen root server IPv4 addresses plus NS records fit inside that 512-byte datagram."
        ],
        # Q2: Server holding definitive records
        [
            "Correct! The authoritative nameserver (e.g. AWS Route 53, Cloudflare, or NSD/BIND) holds the authoritative source-of-truth DNS zone records configured by the domain owner.",
            "Incorrect: The local OS stub cache only holds temporary cached answers subject to TTL expiration.",
            "Incorrect: Root servers only direct queries to TLD registries; they do not hold zone records.",
            "Incorrect: TLD registry servers maintain delegation (NS/glue) records pointing to the authoritative servers, not the individual host records."
        ]
    ],
    "0022": [
        # Q0: CNAME record target
        [
            "Incorrect: An IPv4 address is specified by an 'A' record, not a CNAME.",
            "Correct! A CNAME (Canonical Name) is an alias that maps one domain name to another canonical domain name (e.g. 'app.example.com' -> 'lb.cloud.com').",
            "Incorrect: Mail servers are designated by MX (Mail Exchange) records.",
            "Incorrect: Public cryptographic keys are stored in DNSKEY or TXT records."
        ],
        # Q1: Migration preparation step
        [
            "Incorrect: Deleting NS records would make the entire domain globally unreachable.",
            "Incorrect: Switching records to CNAME does not bypass existing cached A records.",
            "Incorrect: Raising TTL to seven days would force resolvers to cache the old IP for a full week, making rapid cutover impossible.",
            "Correct! Lowering TTL (e.g. to 300 seconds) days in advance ensures that resolvers worldwide flush old cached IP addresses quickly, enabling near-instantaneous traffic migration."
        ],
        # Q2: IPv6 address record type
        [
            "Correct! AAAA ('quad-A') records store 128-bit IPv6 addresses, named because IPv6 is four times the size of a 32-bit IPv4 'A' record.",
            "Incorrect: An 'A' record maps an IPv4 32-bit address.",
            "Incorrect: A PTR record is used for reverse DNS (IP-to-hostname lookups).",
            "Incorrect: A CNAME record aliases one domain name to another domain name."
        ]
    ],
    "0023": [
        # Q0: HTTP/1.1 request omitting Host header
        [
            "Incorrect: Intermediate routers forward IP packets without inspecting HTTP/1.1 application headers.",
            "Incorrect: HTTP servers do not silently default missing host headers to loopback 127.0.0.1.",
            "Correct! RFC 9112 §7.1 explicitly mandates that any HTTP/1.1 request message that lacks a Host header field MUST be rejected with a 400 (Bad Request) status code.",
            "Incorrect: Browsers do not omit Host headers; if an omission occurs (e.g. via raw sockets), the server must reject it."
        ],
        # Q1: Keep-Alive performance win
        [
            "Correct! Persistent connections allow multiple sequential HTTP requests and responses to be piped over a single established TCP connection, eliminating repeated 1-RTT handshake delays.",
            "Incorrect: Keep-Alive manages TCP connection persistence; TLS negotiation is handled independently.",
            "Incorrect: DNS resolution is required to discover the server IP before any TCP connection can be opened.",
            "Incorrect: Keep-Alive does not compress image payloads; payload compression is negotiated via Accept-Encoding: gzip."
        ],
        # Q2: How client knows HTTP/1.1 header block ends
        [
            "Incorrect: Content-Length specifies the size of the request/response body, not the header block.",
            "Incorrect: In HTTP/1.1 persistent connections, the socket remains open and does not send a FIN.",
            "Incorrect: HTTP headers have no arbitrary count limit; headers end on a delimiter.",
            "Correct! The header block is terminated by a standalone empty line: a carriage return and line feed sequence ('\\r\\n\\r\\n')."
        ]
    ],
    "0024": [
        # Q0: Why POST is non-idempotent
        [
            "Incorrect: POST requests routinely return HTTP status codes such as 200 OK or 201 Created.",
            "Correct! An idempotent operation produces identical side-effects regardless of how many times it is executed. Repeated POST requests typically create multiple distinct resources or transactions.",
            "Incorrect: POST requests standardly carry request bodies (e.g. JSON payloads or form data).",
            "Incorrect: HTTPS provides transport encryption regardless of the HTTP method used."
        ],
        # Q1: Upstream microservice crash returns
        [
            "Incorrect: 504 Gateway Timeout occurs when the upstream service fails to send a response within the proxy's read timeout.",
            "Incorrect: 404 Not Found indicates that the requested URL path does not exist on the target server.",
            "Incorrect: 400 Bad Request indicates malformed request syntax from the client.",
            "Correct! A 502 Bad Gateway status indicates that the proxy or gateway server attempted to contact an upstream server but received an invalid response, connection reset (RST), or unexpected termination."
        ],
        # Q2: Cache-Control: no-cache meaning
        [
            "Correct! 'no-cache' does NOT mean 'do not cache'. It instructs the client/proxy that the response MAY be cached, but it MUST NOT be used to satisfy future requests without first revalidating with the origin server.",
            "Incorrect: 'no-store' instructs caches never to write the response to disk or memory; 'no-cache' allows caching with mandatory revalidation.",
            "Incorrect: 'no-cache' has no effect on TLS encryption.",
            "Incorrect: 'no-cache' does not delete existing browser local storage or session cookies."
        ]
    ],
    "0025": [
        # Q0: HttpOnly cookie flag purpose
        [
            "Incorrect: Enforcing HTTPS transmission is the job of the 'Secure' cookie flag.",
            "Incorrect: Cross-site request inclusion is governed by the 'SameSite' attribute.",
            "Correct! The 'HttpOnly' flag instructs the browser that the cookie must not be accessible via client-side scripts (e.g. document.cookie), mitigating session hijacking via Cross-Site Scripting (XSS).",
            "Incorrect: Cookie expiration is controlled by the 'Max-Age' and 'Expires' attributes."
        ],
        # Q1: SameSite=Lax vs SameSite=Strict
        [
            "Incorrect: Neither SameSite=Lax nor Strict permits third-party tracking scripts to read cookies.",
            "Correct! SameSite=Strict blocks the cookie on all cross-site requests. SameSite=Lax permits the cookie to be sent on top-level cross-site navigations (e.g. following an external link via GET).",
            "Incorrect: Lax applies to top-level cross-site GET navigations, while Strict withholds cookies even when clicking an external incoming link.",
            "Incorrect: SameSite applies to all origins regardless of whether they are on localhost or public domains."
        ],
        # Q2: Sending cookie without Secure flag over HTTP
        [
            "Incorrect: Non-secure cookies transmit successfully over unencrypted HTTP.",
            "Correct! Without the 'Secure' flag, the browser transmits the cookie in plaintext over unencrypted HTTP, allowing eavesdroppers on the local network (Wi-Fi, ISP) to intercept session tokens.",
            "Incorrect: The browser does not hash cookies before transmission; they are transmitted verbatim.",
            "Incorrect: Standard browsers send unencrypted cookies over HTTP unless the Secure flag or HSTS is active."
        ]
    ],
    "0026": [
        # Q0: Same-Origin Policy tuple
        [
            "Incorrect: The origin is determined by scheme, host, and port; path and query parameters are ignored.",
            "Correct! An origin is defined by the strict 3-tuple of Scheme (protocol), Hostname (domain), and Port number (RFC 6454).",
            "Incorrect: User-Agent and Accept headers do not define web security origins.",
            "Incorrect: Client IP and MAC addresses are transport/link properties not used by browser Same-Origin Policy."
        ],
        # Q1: What triggers a CORS preflight request (OPTIONS)
        [
            "Incorrect: A standard GET with Accept: text/html is a simple request and does not trigger preflight.",
            "Correct! A preflight OPTIONS request is triggered whenever a cross-origin request uses a non-safe HTTP method (PUT, DELETE, PATCH) or non-standard headers (such as Content-Type: application/json or Authorization).",
            "Incorrect: Standard application/x-www-form-urlencoded POST requests are considered simple requests.",
            "Incorrect: Preflight is mandated by browser CORS security specifications, not configurable client-side flags."
        ],
        # Q2: Wildcard Access-Control-Allow-Origin with credentials
        [
            "Incorrect: Browsers explicitly reject credentials when combined with wildcard origins.",
            "Correct! The CORS specification explicitly forbids returning 'Access-Control-Allow-Origin: *' when 'Access-Control-Allow-Credentials: true' is requested. The browser will block access to the response.",
            "Incorrect: Cookies are not automatically stripped; the browser aborts the request response handling entirely.",
            "Incorrect: The browser throws a CORS security error in the console, not a silent 200 OK acceptance."
        ]
    ],
    "0027": [
        # Q0: HTTP/2 binary framing layer
        [
            "Incorrect: HTTP/2 retains all core HTTP semantics (methods, status codes, headers, and URIs).",
            "Correct! HTTP/2 introduces a binary framing layer that breaks textual HTTP/1.1 messages into binary frames (HEADERS, DATA, SETTINGS) tagged with unique stream IDs.",
            "Incorrect: HTTP/2 does not eliminate headers; it compresses them using HPACK.",
            "Incorrect: HTTP/2 runs over standard TCP connections and does not replace TCP."
        ],
        # Q1: Multiplexing in HTTP/2
        [
            "Incorrect: HTTP/2 uses a single TCP connection, not multiple parallel TCP connections.",
            "Correct! Multiple independent request/response streams are interleaved concurrently as binary frames over a single TCP connection, eliminating the need for multiple connections.",
            "Incorrect: Multiplexing does not require UDP; HTTP/2 operates over TCP.",
            "Incorrect: Multiplexing operates at the application/framing layer, not by modifying TCP sequence numbers."
        ],
        # Q2: HPACK header compression mechanism
        [
            "Incorrect: Standard gzip compression on headers was vulnerable to the CRIME attack.",
            "Correct! HPACK maintains static and dynamic indexing tables between client and server, transmitting small integer indices instead of repeated header names and values.",
            "Incorrect: Common headers are indexed rather than omitted entirely.",
            "Incorrect: HPACK is a custom header compression algorithm (RFC 7541), not Brotli."
        ]
    ],
    "0028": [
        # Q0: Transport protocol underlying HTTP/3
        [
            "Incorrect: HTTP/3 was explicitly created to eliminate TCP-level head-of-line blocking.",
            "Correct! HTTP/3 runs over QUIC, a modern transport protocol that operates entirely on top of UDP datagrams.",
            "Incorrect: SCTP is not widely supported across consumer OSes and middleboxes.",
            "Incorrect: HTTP/3 uses standard UDP packets rather than raw IP datagrams."
        ],
        # Q1: How QUIC solves Head-of-Line blocking
        [
            "Incorrect: QUIC maintains flow and congestion control windows rather than unlimited buffers.",
            "Correct! In QUIC, each multiplexed stream has its own independent stream-level flow control and sequence space. A dropped packet on stream A only pauses stream A; stream B continues delivery.",
            "Incorrect: QUIC does not bypass packet loss; it isolates the retransmission to the specific affected stream.",
            "Incorrect: QUIC maintains reliable delivery with acknowledgments and retransmissions."
        ],
        # Q2: QUIC 0-RTT Connection Resumption risk
        [
            "Incorrect: 0-RTT uses pre-shared keys and is cryptographically encrypted.",
            "Incorrect: 0-RTT does not weaken TLS cipher suites.",
            "Correct! 0-RTT early data is vulnerable to Replay Attacks: an eavesdropping attacker can capture the 0-RTT packet and re-send it to the server, replaying non-idempotent actions.",
            "Incorrect: 0-RTT does not bypass DNS lookups."
        ]
    ],
    "0029": [
        # Q0: WebSocket handshake protocol
        [
            "Incorrect: WebSockets start over standard HTTP/1.1 or HTTP/2, not raw UDP.",
            "Correct! WebSockets begin with a standard HTTP/1.1 GET request featuring 'Upgrade: websocket' and 'Connection: Upgrade' headers; the server accepts with 101 Switching Protocols.",
            "Incorrect: TLS ClientHello establishes encryption before the HTTP WebSocket handshake begins.",
            "Incorrect: WebSockets do not begin with raw TCP RST packets."
        ],
        # Q1: Server-Sent Events (SSE) vs WebSockets
        [
            "Incorrect: WebSockets provide full-duplex communication, whereas SSE is strictly unidirectional.",
            "Correct! Server-Sent Events (SSE) is unidirectional (server-to-client only) running over standard HTTP, making it simpler, firewall-friendly, and ideal for feeds and LLM streaming.",
            "Incorrect: SSE does not support binary data directly; it requires UTF-8 text framing.",
            "Incorrect: WebSockets require more complex framing and connection management than simple HTTP SSE streams."
        ],
        # Q2: Why client-to-server WebSocket frames are masked
        [
            "Incorrect: Masking does not provide confidentiality; it uses a predictable 4-byte XOR key.",
            "Correct! Masking with a random 4-byte XOR key prevents malicious web scripts from crafting byte patterns that trick misconfigured intermediate proxies into poisoning caches.",
            "Incorrect: Masking adds 4 bytes of overhead and does not compress payloads.",
            "Incorrect: Masking is XOR-based, not an integrity checksum (which is handled by TLS/TCP)."
        ]
    ],
    "0030": [
        # Q0: Symmetric vs Asymmetric encryption
        [
            "Incorrect: Asymmetric encryption uses two mathematically paired keys (public and private).",
            "Correct! Symmetric encryption uses a single shared secret key for both encryption and decryption (e.g. AES-GCM), making it computationally fast for large data transfers.",
            "Incorrect: Asymmetric encryption is several orders of magnitude slower than symmetric encryption.",
            "Incorrect: Symmetric encryption algorithms (like AES) are thoroughly standardized and proven."
        ],
        # Q1: Cryptographic hash function property
        [
            "Incorrect: Cryptographic hash functions must be strictly one-way (pre-image resistant); they cannot be reversed.",
            "Correct! Cryptographic hashes are deterministic, produce fixed-length digests, and exhibit an avalanche effect (a 1-bit input change alters ~50% of output bits).",
            "Incorrect: Hash outputs are fixed size (e.g. 256 bits for SHA-256) regardless of input size.",
            "Incorrect: Cryptographic hash functions do not require secret keys (unless keyed as an HMAC)."
        ],
        # Q2: HMAC purpose
        [
            "Incorrect: An HMAC does not encrypt data; the payload remains readable unless encrypted separately.",
            "Correct! An HMAC (Hash-based Message Authentication Code) combines a cryptographic key with the message to verify both data integrity and message authenticity (proving the sender knows the key).",
            "Incorrect: HMACs do not compress payloads.",
            "Incorrect: HMACs protect against tampering, but do not replace public-key digital signatures for non-repudiation."
        ]
    ],
    "0031": [
        # Q0: Role of Certificate Authority (CA) in PKI
        [
            "Incorrect: CAs do not generate or hold private keys for domain owners; keys are generated locally.",
            "Correct! A Certificate Authority digitally signs a certificate binding a subject's public key to their domain identity, establishing trust rooted in pre-installed browser root stores.",
            "Incorrect: CAs do not inspect real-time application traffic or decrypt packets.",
            "Incorrect: CAs do not manage DNS resolution or host domain names."
        ],
        # Q1: Subject Alternative Name (SAN) extension
        [
            "Incorrect: The Common Name (CN) is deprecated; SAN is the mandatory modern standard.",
            "Correct! The Subject Alternative Name (SAN) extension specifies all valid hostnames, subdomains, and IP addresses for which the TLS certificate is authorized.",
            "Incorrect: SAN does not specify cryptographic symmetric cipher algorithms.",
            "Incorrect: SAN records domain names, not Certificate Authority revocation URLs."
        ],
        # Q2: Online Certificate Status Protocol (OCSP) Stapling
        [
            "Incorrect: In OCSP stapling, the client does not contact the CA directly; that is traditional OCSP.",
            "Correct! In OCSP stapling, the web server periodically queries the CA for a signed revocation status timestamp and 'staples' it directly into the TLS handshake, improving privacy and latency.",
            "Incorrect: OCSP stapling does not eliminate public key cryptography.",
            "Incorrect: Revocation lists (CRLs) are downloaded out-of-band and are not stapled to handshakes."
        ]
    ],
    "0032": [
        # Q0: TLS 1.3 handshake latency
        [
            "Incorrect: Zero RTT is only possible on connection resumption with pre-shared keys.",
            "Correct! A full TLS 1.3 handshake completes in exactly 1 RTT: ClientHello (with key share) -> ServerHello (with key share, encrypted cert, and finished) -> client sends finished and data.",
            "Incorrect: 2 RTTs was the latency cost of legacy TLS 1.2; TLS 1.3 shaved off an entire round trip.",
            "Incorrect: 3 RTTs would represent unoptimized legacy stacks with separate round-trip phases."
        ],
        # Q1: Perfect Forward Secrecy (PFS)
        [
            "Incorrect: PFS does not prevent active real-time man-in-the-middle attacks.",
            "Correct! Perfect Forward Secrecy ensures that compromise of the server's long-term private key in the future cannot decrypt previously recorded historical TLS sessions, because each session uses ephemeral keys.",
            "Incorrect: PFS does not accelerate symmetric cipher throughput.",
            "Incorrect: PFS relies on ephemeral key exchange, not certificate caching."
        ],
        # Q2: Server Name Indication (SNI) purpose
        [
            "Incorrect: SNI does not encrypt DNS queries (that is DoH or DoT).",
            "Correct! SNI sends the target domain name in plaintext inside the ClientHello, allowing a single IP address to host multiple virtual domains with different TLS certificates.",
            "Incorrect: SNI does not authenticate client identity (that is mutual TLS / mTLS).",
            "Incorrect: SNI identifies the server hostname, not the client OS version."
        ]
    ],
    "0033": [
        # Q0: Underlying transport for gRPC
        [
            "Incorrect: gRPC does not run over raw UDP (except for experimental gRPC-web variants).",
            "Correct! gRPC runs strictly over HTTP/2 (and HTTP/3), taking full advantage of binary framing, multiplexing, stream prioritization, and bidirectional streaming.",
            "Incorrect: WebSockets are an alternative full-duplex protocol, not the transport layer of gRPC.",
            "Incorrect: gRPC requires a structured application transport protocol, not raw TCP."
        ],
        # Q1: Why Protocol Buffers are smaller than JSON
        [
            "Incorrect: Protobuf serialization is CPU-driven, not offloaded to network cards.",
            "Incorrect: Protobuf uses compact binary encoding, but does not inherently run gzip on every message.",
            "Correct! Protobuf strips out string field names entirely, replacing them with small integer tags (field numbers) and compact variable-length integer (varint) encodings.",
            "Incorrect: Protobuf maintains standard IEEE 754 precision for floating point numbers."
        ],
        # Q2: gRPC streaming modes
        [
            "Incorrect: gRPC supports four distinct RPC interaction patterns.",
            "Correct! gRPC supports four communication models: Unary (1:1), Server Streaming (1:N), Client Streaming (N:1), and Bidirectional Streaming (N:N).",
            "Incorrect: gRPC does not support broadcast multicast across networks.",
            "Incorrect: Streaming in gRPC is bidirectional and supports client-initiated data pipelines."
        ]
    ],
    "0034": [
        # Q0: Why add jitter to exponential backoff
        [
            "Incorrect: Jitter does not shorten average latency; it introduces controlled randomization.",
            "Incorrect: Jitter is applied to application retry timers, not TCP sequence counters.",
            "Correct! Jitter decorrelates retry intervals across clients, preventing the 'thundering herd' problem where thousands of clients retry simultaneously in lockstep waves.",
            "Incorrect: Jitter has no effect on ephemeral port exhaustion."
        ],
        # Q1: Circuit Breaker 'Half-Open' state
        [
            "Incorrect: In Half-Open state, the breaker does not reject all traffic; it is testing recovery.",
            "Correct! The Half-Open state permits a limited trial number of requests through. If they succeed, the breaker resets to Closed (healthy); if they fail, it trips back to Open (failing).",
            "Incorrect: Half-Open does not route traffic to backup databases; it probes the primary service.",
            "Incorrect: The circuit breaker evaluates real-time error rates, not operating system CPU metrics."
        ],
        # Q2: HTTP connection pooling advantage
        [
            "Incorrect: Connection pooling does not compress JSON payloads.",
            "Incorrect: Connection pooling respects all operating system firewall rules.",
            "Incorrect: Memory buffers are allocated per active pooled socket.",
            "Correct! Connection pooling maintains pre-warmed established TCP and TLS connections, eliminating redundant multi-RTT handshakes and avoiding local ephemeral port exhaustion."
        ]
    ],
    "0035": [
        # Q0: Why RFC 1912 forbids CNAME at zone apex
        [
            "Incorrect: CNAME records can resolve any domain name regardless of IP version.",
            "Incorrect: CNAME records can technically exist anywhere in DNS except where other record types are mandatory.",
            "Correct! RFC 1912 §2.4 dictates that if a CNAME exists for a node, no other data (such as mandatory SOA and NS records) may exist for that node. Because zone apexes MUST have SOA and NS, they cannot be CNAMEs.",
            "Incorrect: DNS record types do not dictate port numbers."
        ],
        # Q1: Idempotent vs non-idempotent HTTP methods
        [
            "Incorrect: Idempotence does not require identical response byte streams (e.g. timestamps or headers may vary).",
            "Correct! An idempotent method can be called multiple times with the exact same net effect on server resource state as a single invocation (e.g. GET, PUT, DELETE).",
            "Incorrect: PUT and DELETE are idempotent yet frequently modify server state.",
            "Incorrect: PUT and DELETE are idempotent methods that use request bodies and modify resources."
        ],
        # Q2: 502 Bad Gateway vs 504 Gateway Timeout
        [
            "Correct! 502 indicates that the gateway/proxy received an invalid response (e.g. TCP RST, process crash, invalid bytes) from upstream; 504 indicates the upstream did not respond before the read timeout expired.",
            "Incorrect: Both 502 and 504 are server-side/gateway error status codes.",
            "Incorrect: Database errors do not dictate status codes unless handled by the proxy; path errors return 404.",
            "Incorrect: Authentication errors return 401 Unauthorized or 403 Forbidden."
        ],
        # Q3: Best cookie configuration for XSS and CSRF
        [
            "Incorrect: Without HttpOnly, JavaScript can read session tokens during an XSS attack.",
            "Incorrect: SameSite=None enables cross-site transmission, leaving the application vulnerable to CSRF.",
            "Incorrect: Domain=localhost is not secure on production and Lax allows top-level GET navigation.",
            "Correct! HttpOnly blocks client-side JavaScript access (preventing XSS theft), Secure enforces encrypted HTTPS transmission, and SameSite=Strict blocks all cross-site requests (preventing CSRF)."
        ],
        # Q4: Why 1% loss hurts HTTP/2 more than HTTP/1.1
        [
            "Incorrect: HPACK decompression handles loss cleanly via TCP in-order delivery.",
            "Correct! HTTP/2 multiplexes all logical streams over a single TCP connection. When one TCP packet drops, the TCP receiver queue stalls all interleaved streams (Head-of-Line blocking).",
            "Incorrect: HTTP/2 fully supports TCP window scaling.",
            "Incorrect: TLS 1.3 does not renegotiate when packet drops occur."
        ],
        # Q5: Why TLS 1.3 deprecated static RSA key exchange
        [
            "Correct! With static RSA, if an attacker records encrypted traffic today and steals the server's private key years later, they can retroactively decrypt all past sessions. Ephemeral Diffie-Hellman guarantees Perfect Forward Secrecy.",
            "Incorrect: Deprecating static RSA does not alter certificate compression.",
            "Incorrect: Hashing algorithms (SHA-256) are still used throughout TLS 1.3.",
            "Incorrect: Ephemeral Diffie-Hellman enforces strong encryption and does not permit unencrypted debugging."
        ],
        # Q6: Why Protobuf is faster and smaller than JSON
        [
            "Incorrect: Protobuf serialization is not offloaded to network hardware.",
            "Incorrect: Protobuf uses compact binary encoding, not mandatory gzip on every message.",
            "Correct! Protobuf strips out string key names, encoding them as 1-byte integer tags, and packs integers into compact variable-length varints, eliminating parsing overhead and whitespace.",
            "Incorrect: Protobuf encodes standard binary floating point numbers without altering rounding logic."
        ],
        # Q7: Catastrophic failure prevented by random jitter
        [
            "Incorrect: Jitter does not affect TCP sequence number arithmetic.",
            "Incorrect: Jitter does not prevent port starvation on long-lived connections.",
            "Incorrect: Jitter is an application retry scheduling strategy, not a memory leak fix.",
            "Correct! Without jitter, thousands of disconnected clients retry at synchronized exponential intervals, hitting recovering backend systems with massive periodic traffic spikes (thundering herd)."
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
    for num, q_data in sorted(PART3_DATA.items()):
        enrich_file(num, q_data)

if __name__ == "__main__":
    main()
