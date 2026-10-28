// Excerpt of L45 Router.kt, lines 81–102 (tested there; not a standalone file)
    // HttpServer calls this on a virtual thread for every request.
    fun handle(exchange: HttpExchange) = exchange.use {
        val path = exchange.requestURI.path
        val response = try {
            when (val m = match(exchange.requestMethod, path)) {
                is Match.Found -> m.handler(Request(exchange, m.params))
                is Match.WrongMethod -> problem(                       // RFC 9110: a 405 MUST send Allow
                    ApiError(405, "Method Not Allowed", "${exchange.requestMethod} is not supported on $path"),
                    mapOf("Allow" to m.allowed.joinToString(", ")),
                )
                Match.NoRoute -> problem(ApiError.notFound(path))
            }
        } catch (e: ApiError) {
            problem(e)
        } catch (e: SerializationException) {                        // malformed JSON or wrong types
            problem(ApiError(400, "Bad Request", e.message?.lineSequence()?.first() ?: "Malformed JSON"))
        } catch (e: Exception) {                                      // log the details, never send them (L33)
            System.err.println("ERROR ${exchange.requestMethod} $path: $e")
            problem(ApiError(500, "Internal Server Error", "Something went wrong"))
        }
        send(exchange, response)
    }
