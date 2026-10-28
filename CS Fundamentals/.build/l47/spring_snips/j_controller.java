// Spring Boot 4.1, adapted from the docs' PersonController (Mapping Requests). Not run here.
@RestController
@RequestMapping("/books")
class BookController {
    private final BookService books;

    BookController(BookService books) { this.books = books; }

    @GetMapping("/{id}")
    public Book book(@PathVariable long id) {
        return books.get(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book add(@RequestBody NewBook book) { return books.create(book); }
}
