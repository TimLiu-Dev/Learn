// Spring Boot 4.1, adapted from the docs' PersonController (Mapping Requests). Not run here.
@RestController
@RequestMapping("/books")
class BookController(private val books: BookService) {

    @GetMapping("/{id}")
    fun book(@PathVariable id: Long): Book =
        books.get(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id $id")

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody book: NewBook): Book = books.create(book)
}
