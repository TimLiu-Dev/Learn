public class Colors {
    // The Android colour-int layout: AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB
    static int argb(int a, int r, int g, int b) {
        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    static int alpha(int c) { return c >>> 24; }
    static int red(int c) { return (c >> 16) & 0xff; }
    static int green(int c) { return (c >> 8) & 0xff; }
    static int blue(int c) { return c & 0xff; }

    static int withAlpha(int c, int a) { return (c & 0x00ffffff) | (a & 0xff) << 24; }

    public static void main(String[] args) {
        int blue = argb(255, 0, 0, 255);
        System.out.println("opaque blue = " + Integer.toHexString(blue) + "  as int: " + blue);
        System.out.println("0xff0000ff is an int literal: " + 0xff0000ff);
        int c = argb(128, 18, 52, 86);
        System.out.println("c = " + Integer.toHexString(c) + "  a=" + alpha(c) + " r=" + red(c) + " g=" + green(c) + " b=" + blue(c));
        System.out.println("wrong alpha with >>: " + (blue >> 24) + "   right, with >>>: " + alpha(blue));
        System.out.println("half-transparent blue = " + Integer.toHexString(withAlpha(blue, 0x80)));
    }
}
