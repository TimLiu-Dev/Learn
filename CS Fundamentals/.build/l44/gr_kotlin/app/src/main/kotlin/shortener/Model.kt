package shortener

import kotlinx.serialization.Serializable

@Serializable data class NewLink(val url: String? = null, val alias: String? = null, val ttlSeconds: Long? = null)
@Serializable data class LinkView(val code: String, val url: String, val shortUrl: String, val expiresAt: Long? = null)
@Serializable data class Stats(val code: String, val url: String, val clicks: Long)
@Serializable data class Problem(val type: String, val title: String, val status: Int, val detail: String)

/** What the cache remembers about a code: a link, or "we checked, there is none" (negative caching). */
sealed interface Lookup {
    data class Found(val link: Link) : Lookup
    data object Missing : Lookup
}
