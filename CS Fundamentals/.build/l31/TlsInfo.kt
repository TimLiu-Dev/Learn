import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.cert.X509Certificate
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// What did TLS actually negotiate, and what does a bad certificate look like?
fun main() {
    val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
    (factory.createSocket("example.com", 443) as SSLSocket).use { socket ->
        socket.startHandshake()                            // the TLS handshake happens here
        val session = socket.session
        println("protocol : ${session.protocol}")
        println("cipher   : ${session.cipherSuite}")
        session.peerCertificates.forEachIndexed { i, c ->
            val cert = c as X509Certificate
            println("cert $i   : ${cert.subjectX500Principal.name}")
            println("  issuer : ${cert.issuerX500Principal.name}")
            println("  expires: ${cert.notAfter.toInstant()}")
        }
    }

    // badssl.com serves deliberately broken certificates for exactly this kind of test
    try {
        HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(URI.create("https://expired.badssl.com/")).build(),
            HttpResponse.BodyHandlers.discarding())
    } catch (e: SSLHandshakeException) {
        val root = generateSequence(e as Throwable) { it.cause }.last()
        println("expired  : ${e.javaClass.name}")
        println("  message: ${e.message}")
        println("  root   : ${root.javaClass.simpleName}: ${root.message}")
    }
}
