#!/usr/bin/env python3
"""
inject_part6.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 6 Capstone & Outage lessons (0066 to 0072).
"""

import os
import re
import json

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0066": {
        "title": "The URL Question: End-to-End Request State Machine",
        "key": "0066-url-state-machine",
        "desc": "Simulate the browser URL request lifecycle state machine: IDLE -> DNS_RESOLVED -> TCP_CONNECTED -> TLS_ESTABLISHED -> REQUEST_SENT -> RESPONSE_RECEIVED. Enforce strict ordering: events must arrive in sequence. Return the next state name, or 'ERROR: ILLEGAL_TRANSITION' if an event is applied out of order.",
        "starter": """public class Solution {
    // Current states: "IDLE", "DNS_RESOLVED", "TCP_CONNECTED", "TLS_ESTABLISHED", "REQUEST_SENT", "RESPONSE_RECEIVED"
    // Events: "RESOLVE_DNS", "CONNECT_TCP", "HANDSHAKE_TLS", "SEND_REQUEST", "RECEIVE_RESPONSE"
    public static String transition(String currentState, String event) {
        // TODO: Validate transition and return new state, or "ERROR: ILLEGAL_TRANSITION"
        return "ERROR: ILLEGAL_TRANSITION";
    }
}""",
        "solution": """public class Solution {
    public static String transition(String currentState, String event) {
        if ("IDLE".equals(currentState) && "RESOLVE_DNS".equals(event)) {
            return "DNS_RESOLVED";
        }
        if ("DNS_RESOLVED".equals(currentState) && "CONNECT_TCP".equals(event)) {
            return "TCP_CONNECTED";
        }
        if ("TCP_CONNECTED".equals(currentState) && "HANDSHAKE_TLS".equals(event)) {
            return "TLS_ESTABLISHED";
        }
        if ("TLS_ESTABLISHED".equals(currentState) && "SEND_REQUEST".equals(event)) {
            return "REQUEST_SENT";
        }
        if ("REQUEST_SENT".equals(currentState) && "RECEIVE_RESPONSE".equals(event)) {
            return "RESPONSE_RECEIVED";
        }
        return "ERROR: ILLEGAL_TRANSITION";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Legal start: IDLE + RESOLVE_DNS -> DNS_RESOLVED
        String s1 = Solution.transition("IDLE", "RESOLVE_DNS");
        if ("DNS_RESOLVED".equals(s1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected DNS_RESOLVED, got " + s1);
        }

        // Test 2: Legal progression: TCP_CONNECTED + HANDSHAKE_TLS -> TLS_ESTABLISHED
        String s2 = Solution.transition("TCP_CONNECTED", "HANDSHAKE_TLS");
        if ("TLS_ESTABLISHED".equals(s2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected TLS_ESTABLISHED, got " + s2);
        }

        // Test 3: Illegal skip: IDLE + SEND_REQUEST -> ERROR: ILLEGAL_TRANSITION
        String s3 = Solution.transition("IDLE", "SEND_REQUEST");
        if ("ERROR: ILLEGAL_TRANSITION".equals(s3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected ERROR: ILLEGAL_TRANSITION, got " + s3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check current state and incoming event against the valid ordered pairs.",
            "Valid pairs: IDLE+RESOLVE_DNS, DNS_RESOLVED+CONNECT_TCP, TCP_CONNECTED+HANDSHAKE_TLS, TLS_ESTABLISHED+SEND_REQUEST, REQUEST_SENT+RECEIVE_RESPONSE.",
            "Return 'ERROR: ILLEGAL_TRANSITION' for any other combination."
        ],
        "java_code": """// URL Request Lifecycle State Machine in Java
public class UrlStateMachine {
    public enum State { IDLE, DNS_RESOLVED, TCP_CONNECTED, TLS_ESTABLISHED, REQUEST_SENT, RESPONSE_RECEIVED, ERROR }
    public enum Event { RESOLVE_DNS, CONNECT_TCP, HANDSHAKE_TLS, SEND_REQUEST, RECEIVE_RESPONSE }

    public static State step(State current, Event event) {
        return switch (current) {
            case IDLE -> event == Event.RESOLVE_DNS ? State.DNS_RESOLVED : State.ERROR;
            case DNS_RESOLVED -> event == Event.CONNECT_TCP ? State.TCP_CONNECTED : State.ERROR;
            case TCP_CONNECTED -> event == Event.HANDSHAKE_TLS ? State.TLS_ESTABLISHED : State.ERROR;
            case TLS_ESTABLISHED -> event == Event.SEND_REQUEST ? State.REQUEST_SENT : State.ERROR;
            case REQUEST_SENT -> event == Event.RECEIVE_RESPONSE ? State.RESPONSE_RECEIVED : State.ERROR;
            default -> State.ERROR;
        };
    }
}""",
        "kt_code": """// URL Request Lifecycle State Machine in Kotlin
enum class UrlState { IDLE, DNS_RESOLVED, TCP_CONNECTED, TLS_ESTABLISHED, REQUEST_SENT, RESPONSE_RECEIVED, ERROR }
enum class UrlEvent { RESOLVE_DNS, CONNECT_TCP, HANDSHAKE_TLS, SEND_REQUEST, RECEIVE_RESPONSE }

fun stepUrl(state: UrlState, event: UrlEvent): UrlState = when (state) {
    UrlState.IDLE -> if (event == UrlEvent.RESOLVE_DNS) UrlState.DNS_RESOLVED else UrlState.ERROR
    UrlState.DNS_RESOLVED -> if (event == UrlEvent.CONNECT_TCP) UrlState.TCP_CONNECTED else UrlState.ERROR
    UrlState.TCP_CONNECTED -> if (event == UrlEvent.HANDSHAKE_TLS) UrlState.TLS_ESTABLISHED else UrlState.ERROR
    UrlState.TLS_ESTABLISHED -> if (event == UrlEvent.SEND_REQUEST) UrlState.REQUEST_SENT else UrlState.ERROR
    UrlState.REQUEST_SENT -> if (event == UrlEvent.RECEIVE_RESPONSE) UrlState.RESPONSE_RECEIVED else UrlState.ERROR
    else -> UrlState.ERROR
}"""
    },
    "0067": {
        "title": "Network Diagnostic Triage Root-Cause Engine",
        "key": "0067-diagnostic-triage",
        "desc": "Implement a layered network diagnostic classifier: evaluates probe results (DNS, ICMP ping, TCP port probe, TLS handshake, HTTP status code) and pinpoint the root-cause failure layer.",
        "starter": """public class Solution {
    public static String diagnose(
            boolean dnsSuccess,
            boolean pingSuccess,
            boolean tcpPortOpen,
            boolean tlsHandshakeOk,
            int httpStatusCode) {
        // TODO:
        // 1. If !dnsSuccess -> "L3_DNS_FAILURE"
        // 2. If !pingSuccess && !tcpPortOpen -> "L3_HOST_UNREACHABLE"
        // 3. If pingSuccess && !tcpPortOpen -> "L4_PORT_CLOSED_OR_FIREWALL"
        // 4. If tcpPortOpen && !tlsHandshakeOk -> "L6_TLS_CERT_OR_CIPHER_FAILURE"
        // 5. If httpStatusCode == 504 -> "L7_UPSTREAM_TIMEOUT"
        // 6. If httpStatusCode == 502 -> "L7_BAD_GATEWAY"
        // 7. If httpStatusCode == 200 -> "HEALTHY"
        return "UNKNOWN";
    }
}""",
        "solution": """public class Solution {
    public static String diagnose(
            boolean dnsSuccess,
            boolean pingSuccess,
            boolean tcpPortOpen,
            boolean tlsHandshakeOk,
            int httpStatusCode) {
        if (!dnsSuccess) return "L3_DNS_FAILURE";
        if (!pingSuccess && !tcpPortOpen) return "L3_HOST_UNREACHABLE";
        if (pingSuccess && !tcpPortOpen) return "L4_PORT_CLOSED_OR_FIREWALL";
        if (!tlsHandshakeOk) return "L6_TLS_CERT_OR_CIPHER_FAILURE";
        if (httpStatusCode == 504) return "L7_UPSTREAM_TIMEOUT";
        if (httpStatusCode == 502) return "L7_BAD_GATEWAY";
        if (httpStatusCode == 200) return "HEALTHY";
        return "UNKNOWN";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Host responds to ping but port 443 closed -> L4_PORT_CLOSED_OR_FIREWALL
        String d1 = Solution.diagnose(true, true, false, false, 0);
        if ("L4_PORT_CLOSED_OR_FIREWALL".equals(d1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected L4_PORT_CLOSED_OR_FIREWALL, got " + d1);
        }

        // Test 2: TCP open but TLS handshake certificate expired -> L6_TLS_CERT_OR_CIPHER_FAILURE
        String d2 = Solution.diagnose(true, true, true, false, 0);
        if ("L6_TLS_CERT_OR_CIPHER_FAILURE".equals(d2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected L6_TLS_CERT_OR_CIPHER_FAILURE, got " + d2);
        }

        // Test 3: Upstream reverse proxy timeout -> L7_UPSTREAM_TIMEOUT
        String d3 = Solution.diagnose(true, true, true, true, 504);
        if ("L7_UPSTREAM_TIMEOUT".equals(d3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected L7_UPSTREAM_TIMEOUT, got " + d3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Evaluate in order from lowest to highest network layer: DNS -> L3 Connectivity -> L4 Transport -> L6 TLS -> L7 HTTP.",
            "If ping succeeds but TCP connection fails, the host is reachable but the target port is closed or blocked by a stateful firewall.",
            "Map 504 to L7_UPSTREAM_TIMEOUT and 502 to L7_BAD_GATEWAY."
        ],
        "java_code": """// Network Diagnostic Triage Classifier in Java
public class NetworkTriage {
    public static String classify(boolean dns, boolean ping, boolean tcp, boolean tls, int http) {
        if (!dns) return "L3_DNS_FAILURE";
        if (!ping && !tcp) return "L3_HOST_UNREACHABLE";
        if (!tcp) return "L4_PORT_CLOSED_OR_FIREWALL";
        if (!tls) return "L6_TLS_CERT_OR_CIPHER_FAILURE";
        return switch (http) {
            case 200 -> "HEALTHY";
            case 502 -> "L7_BAD_GATEWAY";
            case 504 -> "L7_UPSTREAM_TIMEOUT";
            default -> "UNKNOWN";
        };
    }
}""",
        "kt_code": """// Network Diagnostic Triage Classifier in Kotlin
fun classifyTriage(dns: Boolean, ping: Boolean, tcp: Boolean, tls: Boolean, http: Int): String = when {
    !dns -> "L3_DNS_FAILURE"
    !ping && !tcp -> "L3_HOST_UNREACHABLE"
    !tcp -> "L4_PORT_CLOSED_OR_FIREWALL"
    !tls -> "L6_TLS_CERT_OR_CIPHER_FAILURE"
    http == 200 -> "HEALTHY"
    http == 502 -> "L7_BAD_GATEWAY"
    http == 504 -> "L7_UPSTREAM_TIMEOUT"
    else -> "UNKNOWN"
}"""
    },
    "0068": {
        "title": "Raw Socket HTTP/1.1 Request Parser",
        "key": "0068-raw-http-parser",
        "desc": "Capstone Part 1: Implement a raw HTTP/1.1 request parser. Given a raw request string with '\\r\\n' line endings, extract the HTTP method (e.g. GET), target path (e.g. /index.html), HTTP version (e.g. HTTP/1.1), and headers into a case-insensitive Map<String, String>. If the request line has fewer than 3 tokens, return null.",
        "starter": """import java.util.*;

public class Solution {
    public static class HttpRequest {
        public final String method;
        public final String path;
        public final String version;
        public final Map<String, String> headers;

        public HttpRequest(String method, String path, String version, Map<String, String> headers) {
            this.method = method;
            this.path = path;
            this.version = version;
            this.headers = headers;
        }
    }

    public static HttpRequest parseRequest(String raw) {
        // TODO: Split by "\\r?\\n", parse request line (method path version),
        // then parse header lines "Name: Value" into headers map (lowercase keys).
        // Return null if request line is invalid.
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class HttpRequest {
        public final String method;
        public final String path;
        public final String version;
        public final Map<String, String> headers;

        public HttpRequest(String method, String path, String version, Map<String, String> headers) {
            this.method = method;
            this.path = path;
            this.version = version;
            this.headers = headers;
        }
    }

    public static HttpRequest parseRequest(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String[] lines = raw.split("\\r?\\n");
        if (lines.length == 0) return null;

        String[] reqLine = lines[0].trim().split("\\\\s+");
        if (reqLine.length < 3) return null;

        String method = reqLine[0];
        String path = reqLine[1];
        String version = reqLine[2];

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) break; // Header end
            int colon = line.indexOf(':');
            if (colon > 0) {
                String key = line.substring(0, colon).trim().toLowerCase();
                String val = line.substring(colon + 1).trim();
                headers.put(key, val);
            }
        }

        return new HttpRequest(method, path, version, headers);
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        String valid = "GET /api/v1/status HTTP/1.1\\r\\nHost: api.example.com\\r\\nUser-Agent: curl/8.0\\r\\n\\r\\n";

        // Test 1: Method and path extraction
        Solution.HttpRequest r1 = Solution.parseRequest(valid);
        if (r1 != null && "GET".equals(r1.method) && "/api/v1/status".equals(r1.path) && "HTTP/1.1".equals(r1.version)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: invalid method/path/version");
        }

        // Test 2: Header parsing (case-insensitive key)
        if (r1 != null && "api.example.com".equals(r1.headers.get("host"))) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected host header 'api.example.com', got " + (r1 != null ? r1.headers.get("host") : null));
        }

        // Test 3: Malformed request line returns null
        Solution.HttpRequest r3 = Solution.parseRequest("BADREQUEST\\r\\nHost: foo\\r\\n\\r\\n");
        if (r3 == null) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected null on malformed request line");
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Split raw input by regex `\\r?\\n`.",
            "Line 0 contains `method`, `path`, and `version` separated by whitespace.",
            "Parse remaining lines until an empty line; split each header line at the first colon (`indexOf(':')`)."
        ],
        "java_code": """// Raw Socket HTTP/1.1 Request Parser in Java
import java.util.*;

public class RawHttpParser {
    public record Request(String method, String path, String version, Map<String, String> headers) {}

    public static Request parse(String raw) {
        var lines = raw.split("\\r?\\n");
        if (lines.length == 0) return null;
        var req = lines[0].split("\\\\s+");
        if (req.length < 3) return null;
        var headers = new HashMap<String, String>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isBlank()) break;
            int idx = lines[i].indexOf(':');
            if (idx > 0) headers.put(lines[i].substring(0, idx).trim().toLowerCase(), lines[i].substring(idx + 1).trim());
        }
        return new Request(req[0], req[1], req[2], headers);
    }
}""",
        "kt_code": """// Raw Socket HTTP/1.1 Request Parser in Kotlin
data class HttpRequest(val method: String, val path: String, val version: String, val headers: Map<String, String>)

fun parseHttp(raw: String): HttpRequest? {
    val lines = raw.lines()
    val req = lines.firstOrNull()?.split("\\\\s+".toRegex()) ?: return null
    if (req.size < 3) return null
    val headers = mutableMapOf<String, String>()
    for (line in lines.drop(1)) {
        if (line.isBlank()) break
        val idx = line.indexOf(':')
        if (idx > 0) headers[line.substring(0, idx).trim().lowercase()] = line.substring(idx + 1).trim()
    }
    return HttpRequest(req[0], req[1], req[2], headers)
}"""
    },
    "0069": {
        "title": "HTTP/1.1 Response Wire Serializer",
        "key": "0069-raw-http-serializer",
        "desc": "Capstone Part 2: Build a compliant HTTP/1.1 response wire byte serializer. Given statusCode (200, 404, 500), contentType, and body string, format: status line ('HTTP/1.1 200 OK\\r\\n', 'HTTP/1.1 404 Not Found\\r\\n', 'HTTP/1.1 500 Internal Server Error\\r\\n'), 'Content-Type: <type>\\r\\n', 'Content-Length: <len>\\r\\n', 'Connection: close\\r\\n\\r\\n', followed by the body.",
        "starter": """public class Solution {
    public static String buildResponse(int statusCode, String contentType, String body) {
        // TODO: Map statusCode to phrase:
        // 200 -> "OK", 404 -> "Not Found", 500 -> "Internal Server Error"
        // Return fully serialized HTTP/1.1 response string with \\r\\n line endings.
        return null;
    }
}""",
        "solution": """public class Solution {
    public static String buildResponse(int statusCode, String contentType, String body) {
        String phrase = switch (statusCode) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };

        int len = body != null ? body.getBytes(java.nio.charset.StandardCharsets.UTF_8).length : 0;
        String b = body != null ? body : "";

        return "HTTP/1.1 " + statusCode + " " + phrase + "\\r\\n" +
               "Content-Type: " + contentType + "\\r\\n" +
               "Content-Length: " + len + "\\r\\n" +
               "Connection: close\\r\\n\\r\\n" +
               b;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: 200 OK text response
        String r1 = Solution.buildResponse(200, "text/plain", "Hello World");
        if (r1 != null && r1.startsWith("HTTP/1.1 200 OK\\r\\n") && r1.contains("Content-Length: 11\\r\\n") && r1.endsWith("Hello World")) {
            passed++;
        } else {
            System.err.println("Test 1 failed: got\\n" + r1);
        }

        // Test 2: 404 Not Found
        String r2 = Solution.buildResponse(404, "application/json", "{\"error\":\"not found\"}");
        if (r2 != null && r2.startsWith("HTTP/1.1 404 Not Found\\r\\n") && r2.contains("Content-Type: application/json\\r\\n")) {
            passed++;
        } else {
            System.err.println("Test 2 failed: got\\n" + r2);
        }

        // Test 3: 500 Internal Server Error
        String r3 = Solution.buildResponse(500, "text/html", "<h1>Crash</h1>");
        if (r3 != null && r3.startsWith("HTTP/1.1 500 Internal Server Error\\r\\n")) {
            passed++;
        } else {
            System.err.println("Test 3 failed: got\\n" + r3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Use a switch expression to map statusCode to standard phrases (200 -> 'OK', 404 -> 'Not Found', 500 -> 'Internal Server Error').",
            "Calculate byte length using `body.getBytes(StandardCharsets.UTF_8).length`.",
            "Ensure lines terminate strictly with `\\r\\n`, and an extra `\\r\\n` separates headers from the body."
        ],
        "java_code": """// HTTP/1.1 Wire Serializer in Java
import java.nio.charset.StandardCharsets;

public class HttpResponseBuilder {
    public static String build(int code, String type, String body) {
        String phrase = switch (code) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return "HTTP/1.1 " + code + " " + phrase + "\\r\\n" +
               "Content-Type: " + type + "\\r\\n" +
               "Content-Length: " + bytes.length + "\\r\\n" +
               "Connection: close\\r\\n\\r\\n" +
               body;
    }
}""",
        "kt_code": """// HTTP/1.1 Wire Serializer in Kotlin
fun buildResponse(code: Int, type: String, body: String): String {
    val phrase = when (code) {
        200 -> "OK"
        404 -> "Not Found"
        500 -> "Internal Server Error"
        else -> "Unknown"
    }
    val bytes = body.toByteArray(Charsets.UTF_8)
    return "HTTP/1.1 $code $phrase\\r\\n" +
           "Content-Type: $type\\r\\n" +
           "Content-Length: ${bytes.size}\\r\\n" +
           "Connection: close\\r\\n\\r\\n" +
           body
}"""
    },
    "0070": {
        "title": "BGP Peering Teardown & DNS Cascade Simulator",
        "key": "0070-bgp-dns-cascade",
        "desc": "Simulate the October 2021 Meta global collapse: A configuration push withdraws BGP routes for the authoritative DNS server prefix. Without BGP routes, global DNS resolvers cannot reach the nameservers, returning SERVFAIL to client apps worldwide. Given bgpPrefixActive and authoritativeDnsUp, determine system state: 'OPERATIONAL', 'DNS_SERVFAIL_GLOBAL', or 'COMPLETE_BLACKOUT'.",
        "starter": """public class Solution {
    public static String evaluateOutage(boolean bgpPrefixActive, boolean authoritativeDnsUp) {
        // TODO:
        // 1. If bgpPrefixActive && authoritativeDnsUp -> "OPERATIONAL"
        // 2. If !bgpPrefixActive (BGP routes withdrawn) -> "DNS_SERVFAIL_GLOBAL"
        // 3. If !authoritativeDnsUp -> "COMPLETE_BLACKOUT"
        return "COMPLETE_BLACKOUT";
    }
}""",
        "solution": """public class Solution {
    public static String evaluateOutage(boolean bgpPrefixActive, boolean authoritativeDnsUp) {
        if (bgpPrefixActive && authoritativeDnsUp) {
            return "OPERATIONAL";
        }
        if (!bgpPrefixActive) {
            return "DNS_SERVFAIL_GLOBAL";
        }
        return "COMPLETE_BLACKOUT";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Normal operations
        String s1 = Solution.evaluateOutage(true, true);
        if ("OPERATIONAL".equals(s1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected OPERATIONAL, got " + s1);
        }

        // Test 2: BGP withdrawn triggers global DNS SERVFAIL
        String s2 = Solution.evaluateOutage(false, true);
        if ("DNS_SERVFAIL_GLOBAL".equals(s2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected DNS_SERVFAIL_GLOBAL, got " + s2);
        }

        // Test 3: Total blackout when DNS daemon crashes
        String s3 = Solution.evaluateOutage(true, false);
        if ("COMPLETE_BLACKOUT".equals(s3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected COMPLETE_BLACKOUT, got " + s3);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check both flags: if both are true, return 'OPERATIONAL'.",
            "If `!bgpPrefixActive`, BGP route withdrawal prevents Internet resolvers from reaching the DNS IPs -> 'DNS_SERVFAIL_GLOBAL'.",
            "Otherwise return 'COMPLETE_BLACKOUT'."
        ],
        "java_code": """// Meta 2021 BGP / DNS Cascade Outage Simulator in Java
public class OutageSimulator {
    public static String evaluate(boolean bgpActive, boolean dnsHealthy) {
        if (bgpActive && dnsHealthy) return "OPERATIONAL";
        if (!bgpActive) return "DNS_SERVFAIL_GLOBAL";
        return "COMPLETE_BLACKOUT";
    }
}""",
        "kt_code": """// Meta 2021 BGP / DNS Cascade Outage Simulator in Kotlin
fun evaluateMetaOutage(bgpActive: Boolean, dnsHealthy: Boolean): String = when {
    bgpActive && dnsHealthy -> "OPERATIONAL"
    !bgpActive -> "DNS_SERVFAIL_GLOBAL"
    else -> "COMPLETE_BLACKOUT"
}"""
    },
    "0071": {
        "title": "PMTUD Black Hole Detector & MSS Clamp Calculator",
        "key": "0071-pmtud-mss-clamp",
        "desc": "Detect Path MTU Discovery (PMTUD) black holes and calculate TCP MSS clamping: 1) isBlackHole(packetSize, pathMtu, dfBit, icmpBlocked): drops into black hole if packetSize > pathMtu AND dfBit == true AND icmpBlocked == true; 2) calculateClampedMss(tunnelMtu): returns tunnelMtu - 40 (20-byte IP header + 20-byte TCP header).",
        "starter": """public class Solution {
    // 1. Returns true if packet silently vanishes without ICMP error
    public static boolean isBlackHole(int packetSize, int pathMtu, boolean dfBit, boolean icmpBlocked) {
        // TODO: implement
        return false;
    }

    // 2. Returns clamped TCP MSS (pathMtu - 40 bytes)
    public static int calculateClampedMss(int pathMtu) {
        // TODO: implement
        return 0;
    }
}""",
        "solution": """public class Solution {
    public static boolean isBlackHole(int packetSize, int pathMtu, boolean dfBit, boolean icmpBlocked) {
        return (packetSize > pathMtu) && dfBit && icmpBlocked;
    }

    public static int calculateClampedMss(int pathMtu) {
        return pathMtu - 40;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: 1500-byte packet over 1420 VPN tunnel with DF=true and ICMP blocked -> BLACK HOLE
        boolean bh = Solution.isBlackHole(1500, 1420, true, true);
        if (bh) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected black hole for 1500 byte packet");
        }

        // Test 2: ICMP allowed enables normal PMTUD (no silent black hole)
        boolean noBh = Solution.isBlackHole(1500, 1420, true, false);
        if (!noBh) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected false when ICMP is unblocked");
        }

        // Test 3: MSS Clamping calculation for 1420 MTU tunnel (1420 - 40 = 1380)
        int mss = Solution.calculateClampedMss(1420);
        if (mss == 1380) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected clamped MSS 1380, got " + mss);
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "A black hole occurs when packet size exceeds path MTU, DF bit forbids fragmentation, and firewalls block ICMP Destination Unreachable Type 3 Code 4 messages.",
            "TCP MSS clamp = `pathMtu - 40` (subtracting 20-byte IPv4 and 20-byte TCP headers).",
            "WireGuard/Geneve overlays typically clamp MSS to 1380 or 1410."
        ],
        "java_code": """// PMTUD Black Hole & MSS Clamp in Java
public class PmtudEngine {
    public static boolean detectBlackHole(int size, int mtu, boolean df, boolean icmpBlocked) {
        return size > mtu && df && icmpBlocked;
    }

    public static int clampMss(int mtu) {
        return mtu - 40; // 20 bytes IP + 20 bytes TCP
    }
}""",
        "kt_code": """// PMTUD Black Hole & MSS Clamp in Kotlin
fun detectBlackHole(size: Int, mtu: Int, df: Boolean, icmpBlocked: Boolean): Boolean =
    size > mtu && df && icmpBlocked

fun clampMss(mtu: Int): Int = mtu - 40"""
    },
    "0072": {
        "title": "Final Master Challenge: Multi-Protocol Frame Decoder",
        "key": "0072-master-packet-decoder",
        "desc": "The ultimate comprehensive network frame decoder. Inspect raw packet bytes across all layers: 1) Verify Ethernet EtherType at offset 12..13 is 0x0800 (IPv4); 2) Verify IPv4 Protocol at offset 23 is 6 (TCP); 3) Extract TCP destination port at offset 36..37; 4) Extract ASCII payload starting at byte offset 54. Return 'TCP_PORT:<port>:PAYLOAD:<payload>' or 'INVALID_FRAME'.",
        "starter": """public class Solution {
    public static String decodeMasterFrame(byte[] frame) {
        // TODO:
        // 1. Frame length must be at least 54 bytes
        // 2. EtherType at bytes 12..13 must be 0x0800
        // 3. IP protocol at byte 23 must be 6 (TCP)
        // 4. TCP dst port is 16-bit int at bytes 36..37
        // 5. Payload is new String(frame, 54, frame.length - 54, StandardCharsets.UTF_8)
        // Return "TCP_PORT:" + dstPort + ":PAYLOAD:" + payload, or "INVALID_FRAME"
        return "INVALID_FRAME";
    }
}""",
        "solution": """import java.nio.charset.StandardCharsets;

public class Solution {
    public static String decodeMasterFrame(byte[] frame) {
        if (frame == null || frame.length < 54) return "INVALID_FRAME";

        int etherType = ((frame[12] & 0xFF) << 8) | (frame[13] & 0xFF);
        if (etherType != 0x0800) return "INVALID_FRAME";

        int protocol = frame[23] & 0xFF;
        if (protocol != 6) return "INVALID_FRAME";

        int dstPort = ((frame[36] & 0xFF) << 8) | (frame[37] & 0xFF);
        String payload = new String(frame, 54, frame.length - 54, StandardCharsets.UTF_8);

        return "TCP_PORT:" + dstPort + ":PAYLOAD:" + payload;
    }
}""",
        "harness": """// __STUDENT_CODE__

import java.nio.charset.StandardCharsets;

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Build valid 60-byte frame
        byte[] valid = new byte[60];
        valid[12] = 0x08; valid[13] = 0x00; // EtherType 0x0800
        valid[23] = 6;                       // TCP protocol
        valid[36] = 0x01; valid[37] = (byte)0xBB; // Port 443 (0x01BB)
        byte[] payload = "PING".getBytes(StandardCharsets.UTF_8);
        System.arraycopy(payload, 0, valid, 54, payload.length);

        // Test 1: Valid frame decodes port 443 and PING payload
        String d1 = Solution.decodeMasterFrame(valid);
        if ("TCP_PORT:443:PAYLOAD:PING".equals(d1)) {
            passed++;
        } else {
            System.err.println("Test 1 failed: expected TCP_PORT:443:PAYLOAD:PING, got " + d1);
        }

        // Test 2: UDP frame (protocol = 17) is rejected
        byte[] udp = valid.clone();
        udp[23] = 17;
        String d2 = Solution.decodeMasterFrame(udp);
        if ("INVALID_FRAME".equals(d2)) {
            passed++;
        } else {
            System.err.println("Test 2 failed: expected INVALID_FRAME for UDP");
        }

        // Test 3: Truncated frame is rejected
        String d3 = Solution.decodeMasterFrame(new byte[30]);
        if ("INVALID_FRAME".equals(d3)) {
            passed++;
        } else {
            System.err.println("Test 3 failed: expected INVALID_FRAME for short frame");
        }

        System.out.printf("Passed %d of %d tests.%n", passed, total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check `frame.length >= 54`.",
            "EtherType is 16-bit big-endian at indices 12 and 13: `((frame[12] & 0xFF) << 8) | (frame[13] & 0xFF)`.",
            "IP protocol is at byte 23; TCP dst port is 16-bit at bytes 36 and 37.",
            "Convert payload starting at index 54 to a UTF-8 String."
        ],
        "java_code": """// Final Master Multi-Protocol Frame Decoder in Java
import java.nio.charset.StandardCharsets;

public class MasterFrameDecoder {
    public static String decode(byte[] frame) {
        if (frame.length < 54) return "INVALID_FRAME";
        int ether = ((frame[12] & 0xFF) << 8) | (frame[13] & 0xFF);
        if (ether != 0x0800 || (frame[23] & 0xFF) != 6) return "INVALID_FRAME";
        int port = ((frame[36] & 0xFF) << 8) | (frame[37] & 0xFF);
        var payload = new String(frame, 54, frame.length - 54, StandardCharsets.UTF_8);
        return "TCP_PORT:" + port + ":PAYLOAD:" + payload;
    }
}""",
        "kt_code": """// Final Master Multi-Protocol Frame Decoder in Kotlin
import java.nio.charset.StandardCharsets

fun decodeMasterFrame(frame: ByteArray): String {
    if (frame.size < 54) return "INVALID_FRAME"
    val ether = ((frame[12].toInt() and 0xFF) shl 8) or (frame[13].toInt() and 0xFF)
    if (ether != 0x0800 || (frame[23].toInt() and 0xFF) != 6) return "INVALID_FRAME"
    val port = ((frame[36].toInt() and 0xFF) shl 8) or (frame[37].toInt() and 0xFF)
    val payload = String(frame, 54, frame.size - 54, StandardCharsets.UTF_8)
    return "TCP_PORT:$port:PAYLOAD:$payload"
}"""
    }
}

def inject_lesson(num):
    data = MODULES[num]
    matches = [f for f in os.listdir(LESSONS_DIR) if f.startswith(f"{num}-") and f.endswith(".html")]
    if not matches:
        print(f"Error: no file found for {num}")
        return
    filename = matches[0]
    filepath = os.path.join(LESSONS_DIR, filename)

    with open(filepath, "r", encoding="utf-8") as f:
        html = f.read()

    if 'class="code-practice"' in html and f'data-key="{data["key"]}"' in html:
        print(f"Already injected: {filename}")
        return

    hints_json = json.dumps(data["hints"])

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

    target_pattern = r'(<section>\s*<h2>(?:Check yourself|Interactive Knowledge Check|Free Recall Practice|The 10-Question Comprehensive Master Exam)</h2>)'
    m = re.search(target_pattern, html)
    if not m:
        target_pattern = r'(<h2>(?:Check yourself|Interactive Knowledge Check|Free Recall Practice|The 10-Question Comprehensive Master Exam)</h2>)'
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
