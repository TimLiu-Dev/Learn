import java.io.IOException

// The same ideas WITHOUT the interop annotations
class Greeter {
    fun greet(name: String, punctuation: String = "!") = "Hello, $name$punctuation"

    fun load(file: String) {
        throw IOException("missing: $file")
    }

    companion object {
        fun create() = Greeter()
    }
}
