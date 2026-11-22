public class LastRead {
    // A stand-in for ResultSet: wasNull() reports on the LAST column read.
    static class Row {
        private final Object[] cols;
        private boolean lastWasNull;

        Row(Object... cols) { this.cols = cols; }

        int getInt(int i) {
            Object v = cols[i - 1];
            lastWasNull = v == null;
            return v == null ? 0 : (Integer) v;
        }

        String getString(int i) {
            Object v = cols[i - 1];
            lastWasNull = v == null;
            return (String) v;
        }

        boolean wasNull() { return lastWasNull; }
    }

    record Book(String title, Integer year) {}

    static Book book(int year, String title, boolean yearWasNull) {
        return new Book(title, yearWasNull ? null : year);
    }

    public static void main(String[] args) {
        Row rs = new Row(null, "Beowulf");          // column 1, the year, is SQL NULL
        System.out.println(book(rs.getInt(1), rs.getString(2), rs.wasNull()));
    }
}
