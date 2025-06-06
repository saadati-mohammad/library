package ir.iau.library.controller;

import ir.iau.library.dto.BookFilterDto;
import ir.iau.library.entity.Book;
import ir.iau.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Page<Book> listBooks(
            BookFilterDto filter, // استفاده از BookFilterDto
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) { // تغییر sort پیشفرض به id,desc

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 ? Sort.Direction.fromString(sortParts[1]) : Sort.Direction.ASC;
        Sort sortOrder = Sort.by(direction, sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return bookService.findAllFiltered(filter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- BEGIN MODIFICATION for file upload ---
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Book> createBook(
            @RequestPart("book") Book book, // JSON part
            @RequestPart(value = "bookCoverFile", required = false) MultipartFile bookCoverFile // File part
    ) throws IOException {
        Book created = bookService.createBook(book, bookCoverFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestPart("book") Book book,
            @RequestPart(value = "bookCoverFile", required = false) MultipartFile bookCoverFile
    ) throws IOException {
        Book updated = bookService.updateBook(id, book, bookCoverFile);
        return ResponseEntity.ok(updated);
    }
    // --- END MODIFICATION for file upload ---

    // متدهای قدیمی Post و Put اگر هنوز از طریق application/json بدون فایل استفاده می‌شوند
    /*
    @PostMapping
    public ResponseEntity<Book> createBookOld(@RequestBody Book book) {
        Book created = bookService.createBook(book); // متد بدون فایل
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/old") // مسیر متفاوت برای جلوگیری از تداخل
    public ResponseEntity<Book> updateBookOld(@PathVariable Long id, @RequestBody Book book) {
        Book updated = bookService.updateBook(id, book); // متد بدون فایل
        return ResponseEntity.ok(updated);
    }
    */


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
