import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import javax.net.ssl.*;

// Exercise 2: a TLS inspector.
public class Ex2 {
    static void inspect(String host) throws Exception {
        var factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (var socket = (SSLSocket) factory.createSocket(host, 443)) {
            SSLSession s = socket.getSession();                // getSession() runs the handshake if needed
            var leaf = (X509Certificate) s.getPeerCertificates()[0];
            long days = Duration.between(Instant.now(), leaf.getNotAfter().toInstant()).toDays();
            System.out.println(host + ": " + s.getProtocol() + ", " + s.getCipherSuite());
            System.out.println("  leaf  : " + leaf.getSubjectX500Principal().getName());
            System.out.println("  names : " + leaf.getSubjectAlternativeNames().stream().map(n -> n.get(1)).toList());
            System.out.println("  chain : " + s.getPeerCertificates().length + " certificates, leaf expires in " + days + " days");
        }
    }

    public static void main(String[] args) throws Exception {
        inspect("example.com");
        inspect("example.org");
    }
}
