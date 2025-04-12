package com.example.bookstorev.controller;

import com.example.bookstorev.model.Book;
import com.example.bookstorev.service.BookService;
import com.example.bookstorev.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AIService aiService;

    // Get all books with pagination
    @GetMapping
    public Page<Book> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return bookService.getAllBooks(page, size);
    }

    // Get a book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Add a new book
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book newBook = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
    }

    // Update a book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Optional<Book> book = bookService.updateBook(id, updatedBook);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean isDeleted = bookService.deleteBook(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Search books by title or author
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) {
        return bookService.searchBooks(query);
    }

    // Get recommendations for a book
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<String> getRecommendations(@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.getBookById(id);
        if (bookOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livre non trouvé.");
        }

        Book book = bookOptional.get();
        String recommendations = aiService.getRecommendations(book.getTitle());

        // Mettre à jour le livre avec les recommandations
        book.setRecommendations(recommendations);
        bookService.updateBook(id, book);

        // Afficher les recommandations dans le terminal
        System.out.println("Recommandations pour \"" + book.getTitle() + "\":");
        System.out.println(recommendations);

        return ResponseEntity.ok(recommendations);
    }
}