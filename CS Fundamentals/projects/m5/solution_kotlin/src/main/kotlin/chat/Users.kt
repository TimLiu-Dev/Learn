package chat

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

val USER_NAME = Regex("[a-z0-9_]{1,16}")

fun pbkdf2(password: String, salt: ByteArray, iterations: Int): ByteArray =
    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        .generateSecret(PBEKeySpec(password.toCharArray(), salt, iterations, 256))
        .encoded

/** The users file: one `name:iterations:saltBase64:hashBase64` per line (L33's PBKDF2 storage). */
class UserStore(private val users: Map<String, Credential>) {
    class Credential(val iterations: Int, val salt: ByteArray, val hash: ByteArray)

    // Unknown names are checked against this, so both kinds of failure cost the same (L55).
    private val dummy = Credential(users.values.maxOfOrNull { it.iterations } ?: 600_000, ByteArray(16), ByteArray(32))

    fun verify(name: String, password: String): Boolean {
        val c = users[name] ?: dummy
        val matches = MessageDigest.isEqual(pbkdf2(password, c.salt, c.iterations), c.hash)
        return matches && name in users
    }

    companion object {
        fun load(path: Path): UserStore {
            val lines = try {
                Files.readAllLines(path)
            } catch (e: IOException) {
                throw UsageError("cannot read users file $path")
            }
            val users = mutableMapOf<String, Credential>()
            lines.forEachIndexed { index, raw ->
                val line = raw.trim()
                if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
                fun bad(why: String): Nothing = throw UsageError("$path line ${index + 1}: $why")
                val parts = line.split(':')
                if (parts.size != 4) bad("expected name:iterations:salt:hash")
                val (name, iterations, salt, hash) = parts
                if (!USER_NAME.matches(name)) bad("bad user name '$name'")
                if (name in users) bad("duplicate user '$name'")
                val n = iterations.toIntOrNull()?.takeIf { it > 0 } ?: bad("bad iteration count '$iterations'")
                val saltBytes = runCatching { Base64.getDecoder().decode(salt) }.getOrNull() ?: bad("salt is not Base64")
                val hashBytes = runCatching { Base64.getDecoder().decode(hash) }.getOrNull() ?: bad("hash is not Base64")
                if (hashBytes.size != 32) bad("hash must be 32 bytes")
                users[name] = Credential(n, saltBytes, hashBytes)
            }
            return UserStore(users)
        }
    }
}
