import java.io.DataInputStream
import java.io.File
import java.security.MessageDigest
import java.util.Base64
import java.util.HexFormat

// What's inside an SSH public key line: "ssh-ed25519 <base64 blob> comment"
fun main(args: Array<String>) {
    val (type, b64, comment) = File(args[0]).readText().trim().split(" ", limit = 3)
    val blob = Base64.getDecoder().decode(b64)
    val input = DataInputStream(blob.inputStream())
    // An SSH "string": a 4-byte big-endian length, then that many bytes (RFC 4251)
    fun readString() = ByteArray(input.readInt()).also { input.readFully(it) }
    val name = String(readString())
    val key = readString()
    println("line:        $type, comment '$comment'")
    println("blob:        ${blob.size} bytes = 4 + ${name.length} + 4 + ${key.size}")
    println("key type:    $name")
    println("public key:  ${HexFormat.of().formatHex(key)}")
    val digest = MessageDigest.getInstance("SHA-256").digest(blob)
    println("fingerprint: SHA256:${Base64.getEncoder().withoutPadding().encodeToString(digest)}")
}
