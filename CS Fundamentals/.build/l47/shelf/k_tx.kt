// Excerpt of L45 Db.kt, lines 18–28 (tested there; not a standalone file)
    inline fun <T> read(block: (Connection) -> T): T = connect().use(block)

    inline fun <T> transaction(block: (Connection) -> T): T = connect().use { c ->
        c.autoCommit = false
        try {
            block(c).also { c.commit() }
        } catch (e: Throwable) {
            c.rollback()
            throw e
        }
    }
