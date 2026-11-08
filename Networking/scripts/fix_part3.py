#!/usr/bin/env python3
"""
fix_part3.py — Fixes the 4 edge cases in Part 3 lessons (0024, 0026, 0031, 0032).
"""

import os
import re

LESSONS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "lessons")

# 1. Fix 0024: quote escaping
p24 = os.path.join(LESSONS_DIR, "0024-http-semantics.html")
with open(p24, "r", encoding="utf-8") as f:
    c24 = f.read()

fixed_sol_24 = """public class Solution {
    public static int evaluateCache(String ifNoneMatch, String serverEtag) {
        if (ifNoneMatch == null || serverEtag == null) return 200;
        String clientTag = cleanEtag(ifNoneMatch);
        String srvTag = cleanEtag(serverEtag);
        return clientTag.equals(srvTag) ? 304 : 200;
    }

    private static String cleanEtag(String tag) {
        String s = tag.trim();
        if (s.startsWith("W/")) s = s.substring(2);
        if (s.startsWith("\\\"") && s.endsWith("\\\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}"""

fixed_harness_24 = """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        // Test 1: Exact match -> 304 Not Modified
        int c1 = Solution.evaluateCache("\\\"v1.2.3\\\"", "\\\"v1.2.3\\\"");
        if (c1 == 304) {
            System.out.println("PASS: Test 1: Matching ETag returns 304 Not Modified");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected 304, got " + c1);
        }

        // Test 2: Weak validator comparison (W/\"hash\") -> 304
        int c2 = Solution.evaluateCache("W/\\\"abc1234\\\"", "\\\"abc1234\\\"");
        if (c2 == 304) {
            System.out.println("PASS: Test 2: Weak validator W/ correctly stripped and matched");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: expected 304, got " + c2);
        }

        // Test 3: Stale cache -> 200 OK
        int c3 = Solution.evaluateCache("\\\"old-v1\\\"", "\\\"new-v2\\\"");
        if (c3 == 200) {
            System.out.println("PASS: Test 3: Changed ETag triggers 200 OK full response");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: expected 200, got " + c3);
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}"""

c24 = re.sub(r'<script type="text/plain" class="solution-code">.*?</script>', f'<script type="text/plain" class="solution-code">{fixed_sol_24}</script>', c24, flags=re.DOTALL)
c24 = re.sub(r'<script type="text/plain" class="test-harness">.*?</script>', f'<script type="text/plain" class="test-harness">\n{fixed_harness_24}\n</script>', c24, flags=re.DOTALL)
with open(p24, "w", encoding="utf-8") as f:
    f.write(c24)
print("Fixed 0024")

# 2. Fix 0026: import java.util.* in Lab
p26 = os.path.join(LESSONS_DIR, "0026-cors-and-sop.html")
with open(p26, "r", encoding="utf-8") as f:
    c26 = f.read()
c26 = c26.replace("// __STUDENT_CODE__\n\npublic class Lab {", "// __STUDENT_CODE__\nimport java.util.*;\n\npublic class Lab {")
with open(p26, "w", encoding="utf-8") as f:
    f.write(c26)
print("Fixed 0026")

# 3. Fix 0031: wildcard dot matching
p31 = os.path.join(LESSONS_DIR, "0031-pki-and-certificates.html")
with open(p31, "r", encoding="utf-8") as f:
    c31 = f.read()

fixed_sol_31 = """public class Solution {
    public static boolean matches(String certPattern, String hostname) {
        if (certPattern == null || hostname == null) return false;
        String cp = certPattern.trim().toLowerCase();
        String hn = hostname.trim().toLowerCase();

        if (cp.equals(hn)) return true;
        if (cp.startsWith("*.")) {
            String suffix = cp.substring(1); // ".example.com"
            if (hn.endsWith(suffix) && hn.length() > suffix.length()) {
                String sub = hn.substring(0, hn.length() - suffix.length());
                return !sub.contains(".") && !sub.isEmpty();
            }
        }
        return false;
    }
}"""
c31 = re.sub(r'<script type="text/plain" class="solution-code">.*?</script>', f'<script type="text/plain" class="solution-code">{fixed_sol_31}</script>', c31, flags=re.DOTALL)
c31 = re.sub(r'<textarea class="practice-editor".*?</textarea>', f'<textarea class="practice-editor" spellcheck="false" autocomplete="off" autocapitalize="off" rows="15">{fixed_sol_31}</textarea>', c31, flags=re.DOTALL)
with open(p31, "w", encoding="utf-8") as f:
    f.write(c31)
print("Fixed 0031")

# 4. Fix 0032: SNI extractor logic and harness
p32 = os.path.join(LESSONS_DIR, "0032-tls13-handshake.html")
with open(p32, "r", encoding="utf-8") as f:
    c32 = f.read()

fixed_sol_32 = """public class Solution {
    public static String extractSni(byte[] b) {
        if (b == null || b.length < 10) return "";
        for (int i = 0; i <= b.length - 8; i++) {
            // Extension Type 0x0000 (SNI)
            if (b[i] == 0x00 && b[i + 1] == 0x00) {
                int extLen = ((b[i + 2] & 0xFF) << 8) | (b[i + 3] & 0xFF);
                if (i + 4 + extLen <= b.length) {
                    int nameType = b[i + 4] & 0xFF;
                    if (nameType == 0) { // host_name
                        int nameLen = ((b[i + 5] & 0xFF) << 8) | (b[i + 6] & 0xFF);
                        if (i + 7 + nameLen <= b.length) {
                            return new String(b, i + 7, nameLen, java.nio.charset.StandardCharsets.US_ASCII);
                        }
                    }
                }
            }
        }
        return "";
    }
}"""

fixed_harness_32 = """// __STUDENT_CODE__

public class Lab {
    public static void main(String[] args) {
        int passed = 0;
        int total = 3;

        byte[] hostBytes = "api.example.com".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        byte[] pkt = new byte[50];
        int idx = 10;
        pkt[idx] = 0x00; pkt[idx + 1] = 0x00; // Ext type = SNI (0)
        pkt[idx + 2] = 0x00; pkt[idx + 3] = (byte)(hostBytes.length + 3); // Ext len
        pkt[idx + 4] = 0x00; // name_type: host_name
        pkt[idx + 5] = 0x00; pkt[idx + 6] = (byte)hostBytes.length; // name len
        System.arraycopy(hostBytes, 0, pkt, idx + 7, hostBytes.length);

        String sni = Solution.extractSni(pkt);

        if ("api.example.com".equals(sni)) {
            System.out.println("PASS: Test 1: TLS SNI 'api.example.com' extracted from ClientHello");
            passed++;
        } else {
            System.out.println("FAIL: Test 1: expected api.example.com, got " + sni);
        }

        byte[] noSni = new byte[20];
        if ("".equals(Solution.extractSni(noSni))) {
            System.out.println("PASS: Test 2: Packet without SNI returns empty string");
            passed++;
        } else {
            System.out.println("FAIL: Test 2: Empty string expected");
        }

        if ("".equals(Solution.extractSni(null))) {
            System.out.println("PASS: Test 3: Null buffer handled safely");
            passed++;
        } else {
            System.out.println("FAIL: Test 3: Null safety failed");
        }

        System.out.println("SUMMARY:" + passed + "/" + total);
        if (passed != total) System.exit(1);
    }
}"""

c32 = re.sub(r'<script type="text/plain" class="solution-code">.*?</script>', f'<script type="text/plain" class="solution-code">{fixed_sol_32}</script>', c32, flags=re.DOTALL)
c32 = re.sub(r'<script type="text/plain" class="test-harness">.*?</script>', f'<script type="text/plain" class="test-harness">\n{fixed_harness_32}\n</script>', c32, flags=re.DOTALL)
with open(p32, "w", encoding="utf-8") as f:
    f.write(c32)
print("Fixed 0032")
