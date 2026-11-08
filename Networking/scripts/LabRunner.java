package scripts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * LabRunner — High-speed, zero-dependency local Java 25 execution daemon for interactive networking lessons.
 *
 * Listens on http://127.0.0.1:8765 to compile and run student lab exercises from the browser with ~40ms latency.
 */
public class LabRunner {
    private static final int DEFAULT_PORT = 8765;
    private static final String JAVA_BIN;

    static {
        // Locate Java 25 or fallback to java in PATH
        String candidate = "/opt/homebrew/opt/openjdk@25/bin/java";
        if (Files.isExecutable(Path.of(candidate))) {
            JAVA_BIN = candidate;
        } else {
            JAVA_BIN = "java";
        }
    }

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        server.createContext("/health", new HealthHandler(port));
        server.createContext("/run", new RunHandler());

        server.start();
        System.out.println("🚀 LabRunner active on http://127.0.0.1:" + port);
        System.out.println("   Using Java runtime: " + JAVA_BIN + " (" + System.getProperty("java.version") + ")");
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Accept");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static class HealthHandler implements HttpHandler {
        private final int port;
        HealthHandler(int port) { this.port = port; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String json = String.format(
                "{\"status\":\"ok\",\"port\":%d,\"java\":\"%s\"}",
                port,
                escapeJson(System.getProperty("java.version"))
            );
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class RunHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            String source = extractJsonField(body, "source");
            String code = extractJsonField(body, "code");
            String harness = extractJsonField(body, "harness");

            String completeSource;
            if (source != null && !source.isBlank()) {
                completeSource = source;
            } else if (harness != null && code != null) {
                if (harness.contains("// __STUDENT_CODE__")) {
                    completeSource = harness.replace("// __STUDENT_CODE__", code);
                } else {
                    completeSource = code + "\n\n" + harness;
                }
            } else if (code != null) {
                completeSource = code;
            } else {
                sendJsonResponse(exchange, 400, "{\"error\":\"Missing source or code payload\"}");
                return;
            }

            // Execute in isolated temp workspace
            Path tempDir = Files.createTempDirectory("net-lab-exec-");
            try {
                java.util.List<String> filesToCompile = new java.util.ArrayList<>();

                if (harness != null && !harness.isBlank()) {
                    // Two-file mode: Solution.java and Lab.java
                    Path solFile = tempDir.resolve("Solution.java");
                    Files.writeString(solFile, code, StandardCharsets.UTF_8);
                    filesToCompile.add(solFile.toString());

                    Path labFile = tempDir.resolve("Lab.java");
                    // If harness had placeholder, remove it since Solution.java is in same package
                    String labSource = harness.replace("// __STUDENT_CODE__", "");
                    Files.writeString(labFile, labSource, StandardCharsets.UTF_8);
                    filesToCompile.add(labFile.toString());
                } else {
                    Path srcFile = tempDir.resolve("Lab.java");
                    Files.writeString(srcFile, completeSource, StandardCharsets.UTF_8);
                    filesToCompile.add(srcFile.toString());
                }

                // Compile using OpenJDK Compiler API
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                int compileResult = compiler.run(null, outStream, errStream, filesToCompile.toArray(new String[0]));
                if (compileResult != 0) {
                    String compileErrors = errStream.toString(StandardCharsets.UTF_8);
                    // Filter out absolute path noise for cleaner student output
                    compileErrors = compileErrors.replace(tempDir.toString() + "/", "");
                    String json = String.format(
                        "{\"success\":false,\"compileError\":\"%s\",\"output\":\"\",\"passed\":0,\"total\":0}",
                        escapeJson(compileErrors)
                    );
                    sendJsonResponse(exchange, 200, json);
                    return;
                }

                // Run compiled bytecode with subprocess isolation and 3-second hard timeout
                ProcessBuilder pb = new ProcessBuilder(JAVA_BIN, "-cp", tempDir.toString(), "Lab");
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                boolean finished = proc.waitFor(3, TimeUnit.SECONDS);
                if (!finished) {
                    proc.destroyForcibly();
                    String json = "{\"success\":false,\"timeout\":true,\"output\":\"⏱ Execution timed out (3.0s limit). Please check for infinite loops.\",\"passed\":0,\"total\":0}";
                    sendJsonResponse(exchange, 200, json);
                    return;
                }

                int exitCode = proc.exitValue();
                String runOutput;
                try (InputStream pis = proc.getInputStream()) {
                    runOutput = new String(pis.readAllBytes(), StandardCharsets.UTF_8);
                }

                int passed = 0;
                int total = 0;
                Pattern summaryPattern = Pattern.compile("SUMMARY:(\\d+)/(\\d+)");
                Matcher m = summaryPattern.matcher(runOutput);
                if (m.find()) {
                    passed = Integer.parseInt(m.group(1));
                    total = Integer.parseInt(m.group(2));
                } else {
                    // Count PASS: and FAIL: lines if explicit SUMMARY tag was omitted
                    Pattern passPat = Pattern.compile("(?m)^PASS:");
                    Pattern failPat = Pattern.compile("(?m)^FAIL:");
                    Matcher pm = passPat.matcher(runOutput);
                    while (pm.find()) passed++;
                    Matcher fm = failPat.matcher(runOutput);
                    int failed = 0;
                    while (fm.find()) failed++;
                    total = passed + failed;
                }

                boolean success = (exitCode == 0 && (total == 0 || passed == total));

                String json = String.format(
                    "{\"success\":%b,\"exitCode\":%d,\"passed\":%d,\"total\":%d,\"output\":\"%s\"}",
                    success,
                    exitCode,
                    passed,
                    total,
                    escapeJson(runOutput)
                );
                sendJsonResponse(exchange, 200, json);

            } catch (Exception e) {
                String json = String.format(
                    "{\"success\":false,\"error\":\"%s\"}",
                    escapeJson(e.getMessage() != null ? e.getMessage() : e.toString())
                );
                sendJsonResponse(exchange, 500, json);
            } finally {
                // Delete temporary files safely
                try {
                    Files.walk(tempDir)
                         .sorted((a, b) -> b.compareTo(a))
                         .forEach(p -> { try { Files.delete(p); } catch (Exception ignored) {} });
                } catch (Exception ignored) {}
            }
        }
    }

    private static String extractJsonField(String json, String field) {
        // Minimal zero-dependency JSON string extraction
        Pattern pat = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher m = pat.matcher(json);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return null;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String unescapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                char next = s.charAt(++i);
                switch (next) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (i + 4 < len) {
                            String hex = s.substring(i + 1, i + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        }
                    }
                    default -> sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
