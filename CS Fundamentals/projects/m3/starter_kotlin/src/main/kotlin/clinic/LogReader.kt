package clinic

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/** Reads a log file into memory, one string per line, without the line endings. */
class LogReader(private val file: File) {

    fun readLines(): List<String> {
        val lines = ArrayList<String>()
        InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
            var line = ""
            while (true) {
                val c = reader.read()
                if (c == -1) break
                val ch = c.toChar()
                if (ch == '\n') {
                    lines.add(line)
                    line = ""
                } else if (ch != '\r') { // files copied from Windows servers end lines with \r\n
                    line += ch
                }
            }
            if (line.isNotEmpty()) lines.add(line) // last line without a final newline
        }
        return lines
    }
}
