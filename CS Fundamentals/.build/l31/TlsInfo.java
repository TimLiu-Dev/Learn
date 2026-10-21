import java.net.URI;
import java.net.http.*;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

// What did TLS actually negotiate, and what does a bad certificate look like?
public class TlsInfo {
    public static void main(String[] args) throws Exception {
        var factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (var socket = (SSLSocket) factory.createSocket("example.com", 443)) {
            socket.startHandshake();                       // the TLS handshake happens here
            SSLSession session = socket.getSession();
            System.out.println("protocol : " + session.getProtocol());
            System.out.println("cipher   : " + session.getCipherSuite());
            var chain = session.getPeerCertificates();
            for (int i = 0; i < chain.length; i++) {
                var cert = (X509Certificate) chain[i];
                System.out.println("cert " + i + "   : " + cert.getSubjectX500Principal().getName());
                System.out.println("  issuer : " + cert.getIssuerX500Principal().getName());
                System.out.println("  expires: " + cert.getNotAfter().toInstant());
            }
        }

        // badssl.com serves deliberately broken certificates for exactly this kind of test
        try {
            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create("https://expired.badssl.com/")).build(),
                    HttpResponse.BodyHandlers.discarding());
        } catch (SSLHandshakeException e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            System.out.println("expired  : " + e.getClass().getName());
            System.out.println("  message: " + e.getMessage());
            System.out.println("  root   : " + root.getClass().getSimpleName() + ": " + root.getMessage());
        }
    }
}
