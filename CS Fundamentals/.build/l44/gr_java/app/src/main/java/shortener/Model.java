package shortener;

import com.fasterxml.jackson.annotation.JsonInclude;
import shortener.LinkStore.Link;

public final class Model {
    private Model() {}

    public record NewLink(String url, String alias, Long ttlSeconds) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LinkView(String code, String url, String shortUrl, Long expiresAt) {}

    public record Stats(String code, String url, long clicks) {}

    public record Problem(String type, String title, int status, String detail) {}

    /** What the cache remembers about a code: a link, or "we checked, there is none" (negative caching). */
    public sealed interface Lookup permits Found, Missing {}
    public record Found(Link link) implements Lookup {}
    public enum Missing implements Lookup { INSTANCE }
}
