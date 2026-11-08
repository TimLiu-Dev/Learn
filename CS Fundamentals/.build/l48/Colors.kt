// The Android colour-int layout: AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB
fun argb(a: Int, r: Int, g: Int, b: Int): Int =
    ((a and 0xff) shl 24) or ((r and 0xff) shl 16) or ((g and 0xff) shl 8) or (b and 0xff)

fun alpha(c: Int) = c ushr 24
fun red(c: Int) = (c shr 16) and 0xff
fun green(c: Int) = (c shr 8) and 0xff
fun blue(c: Int) = c and 0xff

fun withAlpha(c: Int, a: Int) = (c and 0x00ffffff) or ((a and 0xff) shl 24)

fun main() {
    val blue = argb(255, 0, 0, 255)
    println("opaque blue = ${blue.toUInt().toString(16)}  as Int: $blue")
    println("0xff0000ff is a ${(0xff0000ff)::class.simpleName}; .toInt() gives ${0xff0000ff.toInt()}")
    val c = argb(128, 18, 52, 86)
    println("c = ${c.toUInt().toString(16)}  a=${alpha(c)} r=${red(c)} g=${green(c)} b=${blue(c)}")
    println("wrong alpha with shr: ${blue shr 24}   right, with ushr: ${alpha(blue)}")
    println("half-transparent blue = ${withAlpha(blue, 0x80).toUInt().toString(16)}")
}
