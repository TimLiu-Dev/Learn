package shelf.domain;

// The domain: plain data, no JSON or SQL annotations. The other layers translate to and from it.
public record Book(long id, String title, String author, Integer year) {}
