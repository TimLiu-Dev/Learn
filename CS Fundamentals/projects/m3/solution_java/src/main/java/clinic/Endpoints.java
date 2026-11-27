package clinic;

public final class Endpoints {
    private Endpoints() {
    }

    /**
     * "GET", "/api/orders/123?x=1" -> "GET /api/orders/{id}".
     *
     * FIX (regex + String.format): same result as replaceAll("/[0-9]+(?=/|$)", "/{id}") and
     * String.format("%s %s", ...), without compiling a regex or parsing a format string per line.
     */
    public static String endpointOf(String method, String path) {
        int q = path.indexOf('?');
        int end = q < 0 ? path.length() : q;
        StringBuilder sb = new StringBuilder(method.length() + 1 + end).append(method).append(' ');
        int i = 0;
        while (i < end) {
            // path.charAt(i) == '/': find the end of this segment
            int j = i + 1;
            while (j < end && path.charAt(j) != '/') j++;
            sb.append('/');
            if (j > i + 1 && LineParser.allIn(path, i + 1, j, '0', '9')) sb.append("{id}");
            else sb.append(path, i + 1, j);
            i = j;
        }
        return sb.toString();
    }
}
