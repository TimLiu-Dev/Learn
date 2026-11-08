#!/usr/bin/env python3
"""
inject_part3.py — Injects code examples, runnable code practice, and per-option
explanatory code quizzes into Part 3 Developer Track lessons (0021 to 0035).
"""

import os
import re

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

MODULES = {
    "0021": {
        "title": "DNS Wire Header Parser",
        "key": "0021-dns-wire-header",
        "desc": "Parse RFC 1035 12-byte DNS wire message header: Transaction ID, QR bit (0=Query, 1=Response), Opcode, and Question/Answer counts.",
        "starter": """public class Solution {
    // 1. Return true if QR bit (bit 15 of flags) is 1 (Response); false if 0 (Query)
    public static boolean isResponse(byte[] dnsPkt) {
        // TODO: implement
        return false;
    }

    // 2. Extract 16-bit Question Count (QDCOUNT) at byte offset 4..5
    public static int getQuestionCount(byte[] dnsPkt) {
        // TODO: implement
        return 0;
    }
}""",
        "solution": """public class Solution {
    public static boolean isResponse(byte[] dnsPkt) {
        if (dnsPkt == null || dnsPkt.length < 12) return false;
        int flags = ((dnsPkt[2] & 0xFF) << 8) | (dnsPkt[3] & 0xFF);
        return (flags & 0x8000) != 0;
    }

    public static int getQuestionCount(byte[] dnsPkt) {
        if (dnsPkt == null || dnsPkt.length < 12) return 0;
        return ((dnsPkt[4] & 0xFF) << 8) | (dnsPkt[5] & 0xFF);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Sample DNS Query packet header (12 bytes)
        // ID=0x1234, Flags=0x0100 (Standard query, RD=1), QDCOUNT=1, ANCOUNT=0, NSCOUNT=0, ARCOUNT=0
        byte[] query = {0x12, 0x34, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        // Sample DNS Response packet header (12 bytes)
        // ID=0x1234, Flags=0x8180 (Response, RD=1, RA=1), QDCOUNT=1, ANCOUNT=1
        byte[] response = {0x12, 0x34, (byte)0x81, (byte)0x80, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00};

        // Test 1: Query detection (QR=0)
        if (!Solution.isResponse(query)) {
            System.out.println("PASS: Test 1: Query identified (QR=0)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Query misidentified as response");
        }

        // Test 2: Response detection (QR=1)
        if (Solution.isResponse(response)) {
            System.out.println("PASS: Test 2: Response identified (QR=1)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Response misidentified");
        }

        // Test 3: Question count extraction
        if (Solution.getQuestionCount(query) == 1 && Solution.getQuestionCount(response) == 1) {
            System.out.println("PASS: Test 3: Extracted QDCOUNT=1");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: QDCOUNT extraction failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "DNS headers are exactly 12 bytes long: ID (2B), Flags (2B), QDCOUNT (2B), ANCOUNT (2B), NSCOUNT (2B), ARCOUNT (2B).",
            "The QR bit is the most significant bit of the flags: (dnsPkt[2] & 0x80) != 0.",
            "QDCOUNT occupies bytes 4 and 5 in big-endian network byte order."
        ],
        "example_java": """public class DnsWireExample {
    public static void main(String[] args) {
        System.out.println("DNS uses UDP port 53; payloads start with a fixed 12-byte binary header.");
    }
}""",
        "example_kt": """object DnsWireExample {
    fun main() {
        println("DNS uses UDP port 53; payloads start with a fixed 12-byte binary header.")
    }
}"""
    },
    "0022": {
        "title": "DNS Caching Engine with TTL Expiry",
        "key": "0022-dns-ttl-cache",
        "desc": "Implement in-memory DNS caching respecting record TTL (Time to Live) and negative response caching.",
        "starter": """import java.util.*;

public class Solution {
    // 1. Insert record with TTL in seconds
    public void put(String host, String ip, long ttlSeconds, long currentTimeSeconds) {
        // TODO: implement
    }

    // 2. Resolve host: return cached IP if valid; return null if expired or not found
    public String resolve(String host, long currentTimeSeconds) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    private static class Entry {
        String ip;
        long expiresAt;
    }
    private final Map<String, Entry> cache = new HashMap<>();

    public void put(String host, String ip, long ttlSeconds, long currentTimeSeconds) {
        Entry e = new Entry();
        e.ip = ip;
        e.expiresAt = currentTimeSeconds + ttlSeconds;
        cache.put(host.toLowerCase().trim(), e);
    }

    public String resolve(String host, long currentTimeSeconds) {
        Entry e = cache.get(host.toLowerCase().trim());
        if (e == null) return null;
        if (currentTimeSeconds >= e.expiresAt) {
            cache.remove(host.toLowerCase().trim());
            return null;
        }
        return e.ip;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Solution dns = new Solution();
        long now = 1700000000L;

        // Record with 60 second TTL
        dns.put("api.example.com", "93.184.216.34", 60, now);

        // Test 1: Immediate hit
        if ("93.184.216.34".equals(dns.resolve("api.example.com", now + 10))) {
            System.out.println("PASS: Test 1: Cache hit before TTL expires");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Cache hit failed");
        }

        // Test 2: Expired query returns null
        if (dns.resolve("api.example.com", now + 61) == null) {
            System.out.println("PASS: Test 2: Cache miss after TTL expiration");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Expired record was not evicted");
        }

        // Test 3: Unknown host returns null
        if (dns.resolve("nonexistent.org", now) == null) {
            System.out.println("PASS: Test 3: Unknown domain returns null");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Unknown host should return null");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Store expiresAt = currentTimeSeconds + ttlSeconds.",
            "Normalize hostnames using .toLowerCase().trim().",
            "In resolve(), if currentTimeSeconds >= entry.expiresAt, remove from cache and return null."
        ],
        "example_java": """public class DnsCacheExample {
    public static void main(String[] args) {
        System.out.println("Java default DNS cache locks negative responses for 10s and successful ones according to TTL or networkaddress.cache.ttl.");
    }
}""",
        "example_kt": """object DnsCacheExample {
    fun main() {
        println("Java default DNS cache locks negative responses for 10s and successful ones according to TTL or networkaddress.cache.ttl.")
    }
}"""
    },
    "0023": {
        "title": "HTTP/1.1 Request Line & Header Parser",
        "key": "0023-http1-parser",
        "desc": "Parse HTTP/1.1 request wire format: extract Method, Request-URI, HTTP Version, and header key-value pairs.",
        "starter": """import java.util.*;

public class Solution {
    public static class HttpRequest {
        public String method;
        public String uri;
        public String version;
        public Map<String, String> headers = new HashMap<>();
    }

    // Parse raw HTTP/1.1 request bytes (delimited by \\r\\n)
    public static HttpRequest parse(String rawRequest) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static class HttpRequest {
        public String method;
        public String uri;
        public String version;
        public Map<String, String> headers = new HashMap<>();
    }

    public static HttpRequest parse(String rawRequest) {
        if (rawRequest == null || rawRequest.isEmpty()) return null;
        String[] lines = rawRequest.split("\\r?\\n");
        if (lines.length == 0) return null;

        String[] reqLine = lines[0].split("\\s+");
        if (reqLine.length < 3) return null;

        HttpRequest req = new HttpRequest();
        req.method = reqLine[0];
        req.uri = reqLine[1];
        req.version = reqLine[2];

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) break; // End of headers
            int colon = line.indexOf(':');
            if (colon != -1) {
                String k = line.substring(0, colon).trim().toLowerCase();
                String v = line.substring(colon + 1).trim();
                req.headers.put(k, v);
            }
        }
        return req;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        String raw = "GET /api/v1/users?limit=10 HTTP/1.1\\r\\n" +
                     "Host: api.example.com\\r\\n" +
                     "User-Agent: curl/8.4.0\\r\\n" +
                     "Accept: application/json\\r\\n\\r\\n";

        Solution.HttpRequest req = Solution.parse(raw);

        // Test 1: Request line
        if (req != null && "GET".equals(req.method) && "/api/v1/users?limit=10".equals(req.uri) && "HTTP/1.1".equals(req.version)) {
            System.out.println("PASS: Test 1: Request line parsed (Method, URI, Version)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Request line parsing failed");
        }

        // Test 2: Host header
        if (req != null && "api.example.com".equals(req.headers.get("host"))) {
            System.out.println("PASS: Test 2: Host header extracted");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Host header mismatch");
        }

        // Test 3: Case-insensitive header keys
        if (req != null && "application/json".equals(req.headers.get("accept"))) {
            System.out.println("PASS: Test 3: Header keys normalized case-insensitively");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Accept header lookup failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Split rawRequest by CRLF line endings: split(\"\\\\r?\\\\n\").",
            "First line is the Request Line: Method, URI, Version separated by spaces.",
            "Header names in HTTP are case-insensitive: store keys in lowercase."
        ],
        "example_java": """public class HttpParserExample {
    public static void main(String[] args) {
        System.out.println("HTTP/1.1 request headers are terminated by a blank CRLF line (\\\\r\\\\n\\\\r\\\\n).");
    }
}""",
        "example_kt": """object HttpParserExample {
    fun main() {
        println("HTTP/1.1 request headers are terminated by a blank CRLF line (\\r\\n\\r\\n).")
    }
}"""
    },
    "0024": {
        "title": "HTTP Conditional Caching & ETag Validator",
        "key": "0024-http-caching",
        "desc": "Implement HTTP conditional request evaluation: compare client If-None-Match with server ETag to generate 304 Not Modified vs 200 OK.",
        "starter": """public class Solution {
    // Return 304 if ifNoneMatch matches serverEtag (ignoring weak 'W/' validator);
    // Otherwise return 200
    public static int evaluateCache(String ifNoneMatch, String serverEtag) {
        // TODO: implement
        return 200;
    }
}""",
        "solution": """public class Solution {
    public static int evaluateCache(String ifNoneMatch, String serverEtag) {
        if (ifNoneMatch == null || serverEtag == null) return 200;
        String clientTag = cleanEtag(ifNoneMatch);
        String srvTag = cleanEtag(serverEtag);
        return clientTag.equals(srvTag) ? 304 : 200;
    }

    private static String cleanEtag(String tag) {
        String s = tag.trim();
        if (s.startsWith("W/")) s = s.substring(2);
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Exact match -> 304 Not Modified
        int c1 = Solution.evaluateCache("\"v1.2.3\"", "\"v1.2.3\"");
        if (c1 == 304) {
            System.out.println("PASS: Test 1: Matching ETag returns 304 Not Modified");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 304, got " + c1);
        }

        // Test 2: Weak validator comparison (W/"hash") -> 304
        int c2 = Solution.evaluateCache("W/\"abc1234\"", "\"abc1234\"");
        if (c2 == 304) {
            System.out.println("PASS: Test 2: Weak validator W/ correctly stripped and matched");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 304, got " + c2);
        }

        // Test 3: Stale cache -> 200 OK
        int c3 = Solution.evaluateCache("\"old-v1\"", "\"new-v2\"");
        if (c3 == 200) {
            System.out.println("PASS: Test 3: Changed ETag triggers 200 OK full response");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected 200, got " + c3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "ETag values often come wrapped in quotes (e.g. \"hash\") or with weak validation prefix (W/\"hash\").",
            "Strip \"W/\" and surrounding quotes before comparison.",
            "If clean client tag equals clean server tag, return 304; else return 200."
        ],
        "example_java": """public class ETagExample {
    public static void main(String[] args) {
        System.out.println("304 Not Modified sends zero body bytes, saving mobile bandwidth and battery.");
    }
}""",
        "example_kt": """object ETagExample {
    fun main() {
        println("304 Not Modified sends zero body bytes, saving mobile bandwidth and battery.")
    }
}"""
    },
    "0025": {
        "title": "Set-Cookie Security Attribute Auditor",
        "key": "0025-cookie-security",
        "desc": "Audit Set-Cookie headers: enforce HttpOnly, Secure, and SameSite (Lax/Strict) flags to prevent XSS session hijacking and CSRF.",
        "starter": """public class Solution {
    public static class CookieAudit {
        public boolean isSecure;
        public boolean isHttpOnly;
        public String sameSite; // "Strict", "Lax", "None", or "Missing"
    }

    public static CookieAudit audit(String setCookieHeader) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class CookieAudit {
        public boolean isSecure;
        public boolean isHttpOnly;
        public String sameSite = "Missing";
    }

    public static CookieAudit audit(String setCookieHeader) {
        CookieAudit a = new CookieAudit();
        if (setCookieHeader == null) return a;

        String[] parts = setCookieHeader.split(";");
        for (String part : parts) {
            String p = part.trim();
            if ("secure".equalsIgnoreCase(p)) a.isSecure = true;
            else if ("httponly".equalsIgnoreCase(p)) a.isHttpOnly = true;
            else if (p.toLowerCase().startsWith("samesite=")) {
                a.sameSite = p.substring(9).trim();
            }
        }
        return a;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Full secure session cookie
        String c1 = "sessionId=secret123; Path=/; Secure; HttpOnly; SameSite=Strict";
        Solution.CookieAudit a1 = Solution.audit(c1);
        if (a1.isSecure && a1.isHttpOnly && "Strict".equalsIgnoreCase(a1.sameSite)) {
            System.out.println("PASS: Test 1: Secure session cookie fully compliant");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Security attributes missing");
        }

        // Test 2: Insecure cookie lacking HttpOnly
        String c2 = "trackingId=xyz; Path=/; Secure; SameSite=Lax";
        Solution.CookieAudit a2 = Solution.audit(c2);
        if (a2.isSecure && !a2.isHttpOnly && "Lax".equalsIgnoreCase(a2.sameSite)) {
            System.out.println("PASS: Test 2: Insecure cookie correctly flagged missing HttpOnly");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Insecure cookie flag failed");
        }

        // Test 3: Missing SameSite default
        String c3 = "pref=dark; Path=/";
        Solution.CookieAudit a3 = Solution.audit(c3);
        if ("Missing".equals(a3.sameSite)) {
            System.out.println("PASS: Test 3: Missing SameSite attribute recognized");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Expected Missing SameSite");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Split the header by semicolon ';' to examine each directive.",
            "HttpOnly prevents JavaScript document.cookie access (mitigating XSS session theft).",
            "Secure ensures transmission only over HTTPS; SameSite=Strict prevents cross-site CSRF requests."
        ],
        "example_java": """public class CookieExample {
    public static void main(String[] args) {
        System.out.println("Always set Secure; HttpOnly; SameSite=Lax on authentication cookies.");
    }
}""",
        "example_kt": """object CookieExample {
    fun main() {
        println("Always set Secure; HttpOnly; SameSite=Lax on authentication cookies.")
    }
}"""
    },
    "0026": {
        "title": "CORS Preflight OPTIONS Evaluator",
        "key": "0026-cors-evaluator",
        "desc": "Implement browser CORS preflight logic: validate Origin and Access-Control-Request-Method against server CORS policy.",
        "starter": """import java.util.*;

public class Solution {
    // Return true if preflight request is permitted:
    // 1. origin is in allowedOrigins (or allowedOrigins contains "*")
    // 2. requestedMethod is in allowedMethods
    public static boolean isPreflightAllowed(String origin, String requestedMethod, Set<String> allowedOrigins, Set<String> allowedMethods) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """import java.util.*;

public class Solution {
    public static boolean isPreflightAllowed(String origin, String requestedMethod, Set<String> allowedOrigins, Set<String> allowedMethods) {
        if (origin == null || requestedMethod == null) return false;
        boolean originAllowed = allowedOrigins.contains("*") || allowedOrigins.contains(origin.trim().toLowerCase());
        boolean methodAllowed = allowedMethods.contains(requestedMethod.trim().toUpperCase());
        return originAllowed && methodAllowed;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        Set<String> origins = Set.of("https://dashboard.example.com", "https://app.example.com");
        Set<String> methods = Set.of("GET", "POST", "PUT", "DELETE");

        // Test 1: Permitted preflight
        boolean ok1 = Solution.isPreflightAllowed("https://dashboard.example.com", "DELETE", origins, methods);
        if (ok1) {
            System.out.println("PASS: Test 1: Legitimate CORS preflight approved");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Permitted preflight rejected");
        }

        // Test 2: Forbidden origin (evil.com)
        boolean ok2 = Solution.isPreflightAllowed("https://evil.com", "POST", origins, methods);
        if (!ok2) {
            System.out.println("PASS: Test 2: Unauthorized origin blocked by CORS");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Evil origin should be blocked");
        }

        // Test 3: Disallowed method (PATCH)
        boolean ok3 = Solution.isPreflightAllowed("https://app.example.com", "PATCH", origins, methods);
        if (!ok3) {
            System.out.println("PASS: Test 3: Disallowed HTTP method blocked");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Disallowed method should be blocked");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "CORS is a browser security mechanism; servers respond with Access-Control-Allow-Origin headers.",
            "Normalize origin to lowercase and method to uppercase.",
            "Wildcard '*' in allowedOrigins matches all origins."
        ],
        "example_java": """public class CorsExample {
    public static void main(String[] args) {
        System.out.println("CORS Preflight OPTIONS requests verify cross-origin permissions before the browser sends custom headers or non-simple methods.");
    }
}""",
        "example_kt": """object CorsExample {
    fun main() {
        println("CORS Preflight OPTIONS requests verify cross-origin permissions before the browser sends custom headers or non-simple methods.")
    }
}"""
    },
    "0027": {
        "title": "HTTP/2 Binary Frame Header Decoder",
        "key": "0027-http2-frame-decoder",
        "desc": "Parse RFC 7540 9-byte HTTP/2 frame header: 24-bit Length, 8-bit Type, 8-bit Flags, and 31-bit Stream Identifier.",
        "starter": """public class Solution {
    public static class H2Frame {
        public int length;
        public int type;
        public int flags;
        public int streamId;
    }

    public static H2Frame parseHeader(byte[] header9Bytes) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class H2Frame {
        public int length;
        public int type;
        public int flags;
        public int streamId;
    }

    public static H2Frame parseHeader(byte[] b) {
        if (b == null || b.length < 9) return null;
        H2Frame f = new H2Frame();
        f.length = ((b[0] & 0xFF) << 16) | ((b[1] & 0xFF) << 8) | (b[2] & 0xFF);
        f.type = b[3] & 0xFF;
        f.flags = b[4] & 0xFF;
        f.streamId = ((b[5] & 0x7F) << 24) | ((b[6] & 0xFF) << 16) | ((b[7] & 0xFF) << 8) | (b[8] & 0xFF);
        return f;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Sample HTTP/2 DATA frame (Type=0x00, Length=16, Flags=0x01 END_STREAM, StreamID=3)
        byte[] frame = {
            0x00, 0x00, 0x10, // Length = 16
            0x00,             // Type = DATA (0)
            0x01,             // Flags = END_STREAM (1)
            0x00, 0x00, 0x00, 0x03 // Stream ID = 3 (R bit 0)
        };

        Solution.H2Frame f = Solution.parseHeader(frame);

        // Test 1: Length = 16
        if (f != null && f.length == 16) {
            System.out.println("PASS: Test 1: 24-bit payload length (16B) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Length decoding failed");
        }

        // Test 2: Frame Type and Flags
        if (f != null && f.type == 0 && f.flags == 1) {
            System.out.println("PASS: Test 2: DATA frame type (0) and END_STREAM flag (1) verified");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Type/Flags decoding failed");
        }

        // Test 3: Stream ID = 3 (masking 1-bit reserved flag)
        if (f != null && f.streamId == 3) {
            System.out.println("PASS: Test 3: 31-bit Stream ID (3) correctly decoded with R-bit stripped");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Stream ID decoding failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Length is 24-bit big endian: (b[0] << 16) | (b[1] << 8) | b[2].",
            "Type is byte 3; Flags is byte 4.",
            "Stream ID is 31 bits: mask the first bit with (b[5] & 0x7F) before shifting."
        ],
        "example_java": """public class Http2Example {
    public static void main(String[] args) {
        System.out.println("HTTP/2 binary framing eliminates ASCII parsing overhead and enables bidirectional multiplexing.");
    }
}""",
        "example_kt": """object Http2Example {
    fun main() {
        println("HTTP/2 binary framing eliminates ASCII parsing overhead and enables bidirectional multiplexing.")
    }
}"""
    },
    "0028": {
        "title": "QUIC Variable-Length Integer Decoder",
        "key": "0028-quic-varint",
        "desc": "Implement RFC 9000 QUIC variable-length integer decoding: inspect 2-bit prefix (00=1B, 01=2B, 10=4B, 11=8B) to unpack values.",
        "starter": """public class Solution {
    // Decode QUIC variable-length integer from start of byte array
    public static long decodeVarint(byte[] b) {
        // TODO: implement
        return 0L;
    }
}""",
        "solution": """public class Solution {
    public static long decodeVarint(byte[] b) {
        if (b == null || b.length == 0) return 0;
        int prefix = (b[0] & 0xC0) >> 6;
        int len = 1 << prefix; // 1, 2, 4, or 8 bytes
        if (b.length < len) return 0;

        long val = b[0] & 0x3F; // Strip 2-bit prefix
        for (int i = 1; i < len; i++) {
            val = (val << 8) | (b[i] & 0xFF);
        }
        return val;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: 1-byte encoding (prefix 00) -> 37
        byte[] b1 = {0x25};
        if (Solution.decodeVarint(b1) == 37L) {
            System.out.println("PASS: Test 1: 1-byte QUIC varint (37) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: 1-byte decode failed");
        }

        // Test 2: 2-byte encoding (prefix 01) -> 15293
        byte[] b2 = {(byte)0x7B, (byte)0xBD};
        if (Solution.decodeVarint(b2) == 15293L) {
            System.out.println("PASS: Test 2: 2-byte QUIC varint (15293) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: 2-byte decode failed");
        }

        // Test 3: 4-byte encoding (prefix 10) -> 494878333
        byte[] b3 = {(byte)0x9D, 0x7F, 0x3E, 0x7D};
        if (Solution.decodeVarint(b3) == 494878333L) {
            System.out.println("PASS: Test 3: 4-byte QUIC varint (494878333) decoded");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: 4-byte decode failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Inspect the top two bits: int prefix = (b[0] & 0xC0) >> 6.",
            "Length in bytes is (1 << prefix): 00 -> 1 byte, 01 -> 2 bytes, 10 -> 4 bytes, 11 -> 8 bytes.",
            "Initial value is (b[0] & 0x3F); then shift left by 8 and bitwise OR subsequent bytes."
        ],
        "example_java": """public class QuicVarintExample {
    public static void main(String[] args) {
        System.out.println("QUIC variable-length encoding packs integers from 0 to 2^62-1 efficiently in 1 to 8 bytes.");
    }
}""",
        "example_kt": """object QuicVarintExample {
    fun main() {
        println("QUIC variable-length encoding packs integers from 0 to 2^62-1 efficiently in 1 to 8 bytes.")
    }
}"""
    },
    "0029": {
        "title": "WebSocket Client-to-Server Frame Unmasker",
        "key": "0029-ws-unmasker",
        "desc": "Implement RFC 6455 WebSocket unmasking: XOR payload bytes against 4-byte masking key (transformed = payload[i] ^ mask[i % 4]).",
        "starter": """public class Solution {
    // Unmask WebSocket payload using 4-byte masking key
    public static byte[] unmask(byte[] maskedPayload, byte[] maskKey) {
        // TODO: implement
        return new byte[0];
    }
}""",
        "solution": """public class Solution {
    public static byte[] unmask(byte[] payload, byte[] maskKey) {
        if (payload == null || maskKey == null || maskKey.length != 4) return new byte[0];
        byte[] out = new byte[payload.length];
        for (int i = 0; i < payload.length; i++) {
            out[i] = (byte) (payload[i] ^ maskKey[i % 4]);
        }
        return out;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Masking key: 0x37, 0xfa, 0x21, 0x3d
        byte[] mask = {0x37, (byte)0xfa, 0x21, 0x3d};
        // Masked payload representing ASCII "Hello" (72, 101, 108, 108, 111)
        byte[] masked = {0x7f, (byte)0x9f, 0x4d, 0x51, 0x58};

        byte[] unmasked = Solution.unmask(masked, mask);
        String text = new String(unmasked);

        // Test 1: Unmasked text is "Hello"
        if ("Hello".equals(text)) {
            System.out.println("PASS: Test 1: WebSocket client payload unmasked to 'Hello'");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 'Hello', got " + text);
        }

        // Test 2: Double unmasking is identity (XOR property)
        byte[] remasked = Solution.unmask(unmasked, mask);
        if (java.util.Arrays.equals(remasked, masked)) {
            System.out.println("PASS: Test 2: XOR involution verified (unmask(unmask(x)) == x)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: XOR involution failed");
        }

        // Test 3: Empty payload
        byte[] empty = Solution.unmask(new byte[0], mask);
        if (empty.length == 0) {
            System.out.println("PASS: Test 3: Zero-length payload handled safely");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Empty payload failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "RFC 6455 requires all client-to-server WebSocket frames to be masked with a random 4-byte key to prevent cache poisoning on intermediate proxies.",
            "The transformation is symmetric XOR: transformed[i] = payload[i] ^ mask[i % 4].",
            "Cast back to (byte) after XOR arithmetic in Java."
        ],
        "example_java": """public class WebSocketMaskExample {
    public static void main(String[] args) {
        System.out.println("WebSocket masking prevents malicious scripts from synthesizing HTTP requests inside frames that mislead caching proxies.");
    }
}""",
        "example_kt": """object WebSocketMaskExample {
    fun main() {
        println("WebSocket masking prevents malicious scripts from synthesizing HTTP requests inside frames that mislead caching proxies.")
    }
}"""
    },
    "0030": {
        "title": "HMAC-SHA256 Signer & Constant-Time Validator",
        "key": "0030-hmac-signer",
        "desc": "Implement HMAC-SHA256 request signing and constant-time signature comparison to defeat timing attacks.",
        "starter": """import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Solution {
    // 1. Compute HMAC-SHA256 and return hex string
    public static String sign(String data, String secret) throws Exception {
        // TODO: implement
        return "";
    }

    // 2. Perform constant-time string comparison to prevent timing attacks
    public static boolean constantTimeEquals(String a, String b) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Solution {
    public static String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] ab = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(ab, bb);
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) throws Exception {
        int passed = 0;
        int total = 3;

        String sig1 = Solution.sign("GET /api/v1/balance", "secretKey123");

        // Test 1: Signature generated
        if (sig1 != null && sig1.length() == 64) {
            System.out.println("PASS: Test 1: HMAC-SHA256 64-char hex signature generated");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: HMAC signature length incorrect");
        }

        // Test 2: Constant-time comparison matches identical signatures
        if (Solution.constantTimeEquals(sig1, sig1)) {
            System.out.println("PASS: Test 2: Constant-time comparison verified for identical signatures");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Equality check failed");
        }

        // Test 3: Mismatched signature rejected
        if (!Solution.constantTimeEquals(sig1, "wrongsignature")) {
            System.out.println("PASS: Test 3: Tampered signature rejected");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Tampered signature should be rejected");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Use javax.crypto.Mac with algorithm \"HmacSHA256\".",
            "Format the resulting 32 bytes as a 64-character lowercase hex string.",
            "Use java.security.MessageDigest.isEqual() to compare signature bytes in constant time, preventing side-channel timing attacks."
        ],
        "example_java": """public class HmacExample {
    public static void main(String[] args) {
        System.out.println("Always use constant-time comparison (MessageDigest.isEqual) when verifying cryptographic signatures.");
    }
}""",
        "example_kt": """object HmacExample {
    fun main() {
        println("Always use constant-time comparison (MessageDigest.isEqual) when verifying cryptographic signatures.")
    }
}"""
    },
    "0031": {
        "title": "X.509 Wildcard SAN Domain Matcher",
        "key": "0031-san-matcher",
        "desc": "Implement RFC 6125 TLS certificate hostname verification: match requested hostnames against Subject Alternative Names (SAN), including wildcard certs (*.example.com).",
        "starter": """public class Solution {
    // Return true if hostname matches certDomainPattern (e.g. "*.example.com" matches "api.example.com")
    public static boolean matches(String certPattern, String hostname) {
        // TODO: implement
        return false;
    }
}""",
        "solution": """public class Solution {
    public static boolean matches(String certPattern, String hostname) {
        if (certPattern == null || hostname == null) return false;
        String cp = certPattern.trim().toLowerCase();
        String hn = hostname.trim().toLowerCase();

        if (cp.equals(hn)) return true;
        if (cp.startsWith("*.")) {
            String suffix = cp.substring(2);
            if (hn.endsWith(suffix) && hn.length() > suffix.length()) {
                String sub = hn.substring(0, hn.length() - suffix.length());
                // Wildcards match exactly one label (no dots allowed in prefix)
                return !sub.contains(".") && !sub.isEmpty();
            }
        }
        return false;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Single-label wildcard match
        if (Solution.matches("*.example.com", "api.example.com")) {
            System.out.println("PASS: Test 1: *.example.com matches api.example.com");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Single-label wildcard match failed");
        }

        // Test 2: Multi-label wildcard does NOT match (RFC 6125 security rule)
        if (!Solution.matches("*.example.com", "sub.api.example.com")) {
            System.out.println("PASS: Test 2: *.example.com correctly rejects nested sub.api.example.com");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Multi-label match should be rejected");
        }

        // Test 3: Exact match
        if (Solution.matches("example.com", "example.com")) {
            System.out.println("PASS: Test 3: Exact hostname match approved");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Exact match failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Normalize both strings with .trim().toLowerCase().",
            "According to RFC 6125, a wildcard (*.domain.com) matches exactly one subdomain label.",
            "\"sub.api.example.com\" must NOT match \"*.example.com\" because the wildcard cannot cross label dots."
        ],
        "example_java": """public class SanMatcherExample {
    public static void main(String[] args) {
        System.out.println("Modern TLS deprecates Common Name (CN); clients verify identity strictly via Subject Alternative Names (SAN).");
    }
}""",
        "example_kt": """object SanMatcherExample {
    fun main() {
        println("Modern TLS deprecates Common Name (CN); clients verify identity strictly via Subject Alternative Names (SAN).")
    }
}"""
    },
    "0032": {
        "title": "TLS 1.3 ClientHello SNI Extractor",
        "key": "0032-tls13-sni-extractor",
        "desc": "Inspect binary TLS ClientHello records and extract the Server Name Indication (SNI) extension string.",
        "starter": """public class Solution {
    // Search raw TLS record bytes for SNI extension (extension type 0x0000) and return hostname
    public static String extractSni(byte[] tlsRecord) {
        // TODO: implement
        return "";
    }
}""",
        "solution": """public class Solution {
    public static String extractSni(byte[] b) {
        if (b == null || b.length < 45) return "";
        // Search for extension type 0x0000 and ServerName list
        for (int i = 40; i < b.length - 6; i++) {
            if (b[i] == 0x00 && b[i + 1] == 0x00) { // Extension Type = SNI (0)
                int extLen = ((b[i + 2] & 0xFF) << 8) | (b[i + 3] & 0xFF);
                if (i + 4 + extLen <= b.length && b[i + 4] == 0x00) { // NameType = host_name (0)
                    int nameLen = ((b[i + 5] & 0xFF) << 8) | (b[i + 6] & 0xFF);
                    if (i + 7 + nameLen <= b.length) {
                        return new String(b, i + 7, nameLen, java.nio.charset.StandardCharsets.US_ASCII);
                    }
                }
            }
        }
        return "";
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Build simulated TLS ClientHello containing SNI = "api.example.com" (15 bytes)
        byte[] hostBytes = "api.example.com".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        byte[] pkt = new byte[70];
        pkt[0] = 0x16; pkt[1] = 0x03; pkt[2] = 0x01; // Handshake record
        int idx = 45;
        pkt[idx] = 0x00; pkt[idx + 1] = 0x00; // Extension: SNI (0)
        pkt[idx + 2] = 0x00; pkt[idx + 3] = (byte)(hostBytes.length + 3); // Ext len
        pkt[idx + 4] = 0x00; // Name type: host_name (0)
        pkt[idx + 5] = 0x00; pkt[idx + 6] = (byte)hostBytes.length;
        System.arraycopy(hostBytes, 0, pkt, idx + 7, hostBytes.length);

        String sni = Solution.extractSni(pkt);

        // Test 1: Extract SNI hostname
        if ("api.example.com".equals(sni)) {
            System.out.println("PASS: Test 1: TLS SNI 'api.example.com' extracted from ClientHello");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected api.example.com, got " + sni);
        }

        // Test 2: Packet without SNI returns empty string
        byte[] noSni = new byte[50];
        if ("".equals(Solution.extractSni(noSni))) {
            System.out.println("PASS: Test 2: Packet without SNI returns empty string");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Empty string expected");
        }

        // Test 3: Null safety
        if ("".equals(Solution.extractSni(null))) {
            System.out.println("PASS: Test 3: Null buffer handled safely");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Null safety failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "TLS 1.3 ClientHello extensions begin after session ID and cipher suites.",
            "The SNI extension has type 0x0000.",
            "Inside the extension: Extension Length (2B), Server Name List Length (2B), Server Name Type 0x00 (host_name), Name Length (2B), Hostname ASCII bytes."
        ],
        "example_java": """public class SniExample {
    public static void main(String[] args) {
        System.out.println("SNI allows a single IP address to host thousands of TLS certificates via virtual hosting.");
    }
}""",
        "example_kt": """object SniExample {
    fun main() {
        println("SNI allows a single IP address to host thousands of TLS certificates via virtual hosting.")
    }
}"""
    },
    "0033": {
        "title": "Protocol Buffers Varint Encoder / Decoder",
        "key": "0033-protobuf-varint",
        "desc": "Implement Google Protocol Buffers Base-128 Varint encoding and decoding: 7 bits of data per byte with MSB continuation bit.",
        "starter": """public class Solution {
    // 1. Encode 64-bit long into Protobuf Base-128 Varint bytes
    public static byte[] encodeVarint(long value) {
        // TODO: implement
        return new byte[0];
    }

    // 2. Decode Protobuf Base-128 Varint bytes into long
    public static long decodeVarint(byte[] bytes) {
        // TODO: implement
        return 0L;
    }
}""",
        "solution": """import java.io.ByteArrayOutputStream;

public class Solution {
    public static byte[] encodeVarint(long value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            if ((value & ~0x7FL) == 0) {
                out.write((int) value);
                break;
            } else {
                out.write((int) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
        return out.toByteArray();
    }

    public static long decodeVarint(byte[] bytes) {
        long result = 0;
        int shift = 0;
        for (byte b : bytes) {
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) break;
            shift += 7;
        }
        return result;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Value 1 fits in 1 byte (0x01)
        byte[] enc1 = Solution.encodeVarint(1L);
        if (enc1.length == 1 && enc1[0] == 0x01 && Solution.decodeVarint(enc1) == 1L) {
            System.out.println("PASS: Test 1: Single-byte Protobuf Varint (1)");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Varint 1 failed");
        }

        // Test 2: Value 300 encodes to 2 bytes (0xAC, 0x02)
        byte[] enc300 = Solution.encodeVarint(300L);
        if (enc300.length == 2 && (enc300[0] & 0xFF) == 0xAC && enc300[1] == 0x02 && Solution.decodeVarint(enc300) == 300L) {
            System.out.println("PASS: Test 2: Two-byte Protobuf Varint (300 -> 0xAC 0x02)");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Varint 300 failed");
        }

        // Test 3: Large integer round-trip
        long val = 123456789L;
        byte[] encBig = Solution.encodeVarint(val);
        if (Solution.decodeVarint(encBig) == val) {
            System.out.println("PASS: Test 3: Large integer round-trip (123456789)");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Large integer round-trip failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "In Protobuf varints, the lowest 7 bits of each byte carry payload; the 8th bit (0x80) is the continuation flag.",
            "If value has more bits, set (value & 0x7F) | 0x80 and shift unsigned value >>>= 7.",
            "When decoding, shift += 7 until a byte with (b & 0x80) == 0 is encountered."
        ],
        "example_java": """public class VarintExample {
    public static void main(String[] args) {
        System.out.println("gRPC uses Protocol Buffers varints to minimize wire serialization overhead.");
    }
}""",
        "example_kt": """object VarintExample {
    fun main() {
        println("gRPC uses Protocol Buffers varints to minimize wire serialization overhead.")
    }
}"""
    },
    "0034": {
        "title": "Exponential Backoff with Full Jitter & Circuit Breaker",
        "key": "0034-client-resilience",
        "desc": "Implement client resilience: Exponential Backoff with Full Jitter and Circuit Breaker state transitions (CLOSED, OPEN, HALF_OPEN).",
        "starter": """public class Solution {
    // 1. Calculate Exponential Backoff with Full Jitter:
    // capped = min(maxDelayMs, baseDelayMs * 2^attempt)
    // Return random value between 0 and capped (for deterministic testing, return capped)
    public static long getBackoffDelay(int attempt, long baseDelayMs, long maxDelayMs) {
        // TODO: implement
        return 0L;
    }

    // 2. Circuit Breaker State Transition:
    // "CLOSED" + 5 consecutive failures -> "OPEN"
    // "OPEN" + cooldown passed -> "HALF_OPEN"
    // "HALF_OPEN" + success -> "CLOSED"
    public static String updateCircuitBreaker(String state, int failCount, boolean cooldownPassed, boolean lastCallSuccess) {
        // TODO: implement
        return state;
    }
}""",
        "solution": """public class Solution {
    public static long getBackoffDelay(int attempt, long baseDelayMs, long maxDelayMs) {
        long multiplier = 1L << Math.min(attempt, 30);
        long delay = baseDelayMs * multiplier;
        return Math.min(delay, maxDelayMs);
    }

    public static String updateCircuitBreaker(String state, int failCount, boolean cooldownPassed, boolean lastCallSuccess) {
        if ("CLOSED".equals(state) && failCount >= 5) {
            return "OPEN";
        }
        if ("OPEN".equals(state) && cooldownPassed) {
            return "HALF_OPEN";
        }
        if ("HALF_OPEN".equals(state)) {
            return lastCallSuccess ? "CLOSED" : "OPEN";
        }
        return state;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Exponential backoff capping
        long d0 = Solution.getBackoffDelay(0, 100, 1000); // 100
        long d2 = Solution.getBackoffDelay(2, 100, 1000); // 400
        long d5 = Solution.getBackoffDelay(5, 100, 1000); // 3200 -> capped at 1000
        if (d0 == 100L && d2 == 400L && d5 == 1000L) {
            System.out.println("PASS: Test 1: Exponential backoff calculation and cap enforced");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Backoff calculation incorrect");
        }

        // Test 2: Circuit breaker opens on 5 failures
        String s1 = Solution.updateCircuitBreaker("CLOSED", 5, false, false);
        if ("OPEN".equals(s1)) {
            System.out.println("PASS: Test 2: Circuit breaker tripped OPEN after 5 failures");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Circuit breaker should trip open");
        }

        // Test 3: Half-open recovery
        String s2 = Solution.updateCircuitBreaker("OPEN", 5, true, false);
        String s3 = Solution.updateCircuitBreaker("HALF_OPEN", 0, false, true);
        if ("HALF_OPEN".equals(s2) && "CLOSED".equals(s3)) {
            System.out.println("PASS: Test 3: Circuit recovered to CLOSED after successful probe");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Recovery transition failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Exponential backoff formula: baseDelay * 2^attempt, capped by maxDelay.",
            "Full jitter picks a uniform random value in [0, cappedDelay] to prevent the thundering herd problem.",
            "Circuit Breaker prevents cascading distributed system collapse by failing fast when upstream services fail."
        ],
        "example_java": """public class ResilienceExample {
    public static void main(String[] args) {
        System.out.println("Always add jitter to exponential retries to prevent synchronized thundering herd spikes on downstream databases.");
    }
}""",
        "example_kt": """object ResilienceExample {
    fun main() {
        println("Always add jitter to exponential retries to prevent synchronized thundering herd spikes on downstream databases.")
    }
}"""
    },
    "0035": {
        "title": "API Gateway Edge Proxy Pipeline",
        "key": "0035-api-gateway",
        "desc": "Implement an end-to-end API Gateway edge validation pipeline: verify TLS SNI hostname, validate Authorization Bearer token, and apply rate limiting.",
        "starter": """public class Solution {
    public static class GatewayResult {
        public boolean allowed;
        public int httpStatus;
        public String upstreamCluster;
    }

    public static GatewayResult process(String host, String authHeader, int currentRps, int maxRps) {
        // TODO: implement
        return null;
    }
}""",
        "solution": """public class Solution {
    public static class GatewayResult {
        public boolean allowed;
        public int httpStatus;
        public String upstreamCluster;
    }

    public static GatewayResult process(String host, String authHeader, int currentRps, int maxRps) {
        GatewayResult res = new GatewayResult();
        if (currentRps > maxRps) {
            res.allowed = false;
            res.httpStatus = 429; // Too Many Requests
            return res;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer secret-token")) {
            res.allowed = false;
            res.httpStatus = 401; // Unauthorized
            return res;
        }
        if ("api.example.com".equalsIgnoreCase(host)) {
            res.allowed = true;
            res.httpStatus = 200;
            res.upstreamCluster = "cluster-api-prod";
            return res;
        }
        res.allowed = false;
        res.httpStatus = 404; // Not Found
        return res;
    }
}""",
        "harness": """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Successful request forwarded to cluster
        Solution.GatewayResult r1 = Solution.process("api.example.com", "Bearer secret-token-123", 50, 100);
        if (r1 != null && r1.allowed && r1.httpStatus == 200 && "cluster-api-prod".equals(r1.upstreamCluster)) {
            System.out.println("PASS: Test 1: Authorized request routed to cluster-api-prod");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: Authorized request routing failed");
        }

        // Test 2: Rate limit exceeded -> 429 Too Many Requests
        Solution.GatewayResult r2 = Solution.process("api.example.com", "Bearer secret-token-123", 150, 100);
        if (r2 != null && !r2.allowed && r2.httpStatus == 429) {
            System.out.println("PASS: Test 2: Rate limit exceeded returned 429 Too Many Requests");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Expected 429 Too Many Requests");
        }

        // Test 3: Unauthorized token -> 401 Unauthorized
        Solution.GatewayResult r3 = Solution.process("api.example.com", "Bearer invalid", 50, 100);
        if (r3 != null && !r3.allowed && r3.httpStatus == 401) {
            System.out.println("PASS: Test 3: Invalid token returned 401 Unauthorized");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Expected 401 Unauthorized");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}""",
        "hints": [
            "Check rate limiting first: if currentRps > maxRps, return 429.",
            "Verify auth header starts with 'Bearer secret-token', returning 401 if invalid.",
            "If host equals 'api.example.com', return 200 with upstreamCluster = 'cluster-api-prod'."
        ],
        "example_java": """public class GatewayPipelineExample {
    public static void main(String[] args) {
        System.out.println("API Gateways terminate TLS, enforce rate limits, authenticate JWTs, and route to microservices.");
    }
}""",
        "example_kt": """object GatewayPipelineExample {
    fun main() {
        println("API Gateways terminate TLS, enforce rate limits, authenticate JWTs, and route to microservices.")
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
        print(f"Warning: could not find insertion target for {num}")
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
