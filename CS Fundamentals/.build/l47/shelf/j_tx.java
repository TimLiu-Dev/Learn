// Excerpt of L45 Db.java, lines 55–70 (tested there; not a standalone file)
    public <T> T transaction(Work<T> work) {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
