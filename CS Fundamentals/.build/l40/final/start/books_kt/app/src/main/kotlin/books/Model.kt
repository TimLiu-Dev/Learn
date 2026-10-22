package books

import kotlinx.serialization.Serializable

@Serializable
data class Book(val id: Long, val title: String, val year: Int)

@Serializable                                   // what clients send: no id, fields may be missing
data class NewBook(val title: String? = null, val year: Int? = null)

@Serializable
data class Page(val items: List<Book>, val limit: Int, val offset: Int, val total: Int, val next: String? = null)

@Serializable                                   // RFC 9457 Problem Details (+ "errors" extension)
data class Problem(val type: String, val title: String, val status: Int, val detail: String,
                   val errors: List<String>? = null)
