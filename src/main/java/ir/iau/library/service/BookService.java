package ir.iau.library.service;

import ir.iau.library.dto.BookFilterDto;
import ir.iau.library.entity.Book;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.specification.BookSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<Book> findAllFiltered(BookFilterDto filter, Pageable pageable) {
        return bookRepository.findAll(BookSpecification.filter(filter), pageable);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setTranslator(bookDetails.getTranslator());
        book.setPublisher(bookDetails.getPublisher());
        book.setIsbn10(bookDetails.getIsbn10());
        book.setIsbn13(bookDetails.getIsbn13());
        book.setDeweyDecimal(bookDetails.getDeweyDecimal());
        book.setCongressClassification(bookDetails.getCongressClassification());
        book.setSubject(bookDetails.getSubject());
        book.setSummary(bookDetails.getSummary());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setPageCount(bookDetails.getPageCount());
        book.setLanguage(bookDetails.getLanguage());
        book.setEdition(bookDetails.getEdition());
        return bookRepository.save(book);
    }

    public void softDeleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
        book.setActive(false);
        bookRepository.save(book);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id).filter(Book::getActive);
    }
}
