import java.security.cert.X509Certificate
import java.time.Duration
import java.time.Instant
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// Exercise 2: a TLS inspector.
fun inspect(host: String) {
    val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
    (factory.createSocket(host, 443) as SSLSocket).use { socket ->
        val s = socket.session                             // getSession() runs the handshake if needed
        val leaf = s.peerCertificates[0] as X509Certificate
        val days = Duration.between(Instant.now(), leaf.notAfter.toInstant()).toDays()
        println("$host: ${s.protocol}, ${s.cipherSuite}")
        println("  leaf  : ${leaf.subjectX500Principal.name}")
        println("  names : ${leaf.subjectAlternativeNames.map { it[1] }}")
        println("  chain : ${s.peerCertificates.size} certificates, leaf expires in $days days")
    }
}

fun main() {
    inspect("example.com")
    inspect("example.org")
}
