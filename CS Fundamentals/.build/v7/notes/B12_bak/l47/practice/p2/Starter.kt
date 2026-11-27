data class ShelfConfig(val db: String, val port: Int, val poolSize: Int)

/** Spring's relaxed binding for environment variables: "shelf.pool-size" -> "SHELF_POOLSIZE". */
fun envName(property: String): String =
    TODO("dots to underscores, remove dashes, uppercase")

/** Reads each setting from env (under its relaxed name), else props, else the default. */
fun bind(props: Map<String, String>, env: Map<String, String>): ShelfConfig =
    TODO("look up, convert and range-check shelf.db, shelf.port and shelf.pool-size; report every problem at once")
