package ir.iau.library.service;

import ir.iau.library.dto.BookFilterDto;
import ir.iau.library.entity.Book;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.specification.BookSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<Book> findAllFiltered(BookFilterDto filter, Pageable pageable) { // تغییر نوع فیلتر
        return bookRepository.findAll(BookSpecification.filter(filter), pageable);
    }

    public Book createBook(Book book, MultipartFile bookCoverFile) throws IOException {
        if (bookCoverFile != null && !bookCoverFile.isEmpty()) {
            book.setBookCoverFile(bookCoverFile.getBytes());
        }
        return bookRepository.save(book);
    }

    // متد createBook بدون فایل، اگر هنوز استفاده می‌شود
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }


    public Book updateBook(Long id, Book bookDetails, MultipartFile bookCoverFile) throws IOException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setTranslator(bookDetails.getTranslator());
        book.setPublisher(bookDetails.getPublisher());
        book.setIsbn10(bookDetails.getIsbn10());
        book.setIsbn13(bookDetails.getIsbn13());
        book.setDescription(bookDetails.getDescription()); // اضافه شده
        book.setDeweyDecimal(bookDetails.getDeweyDecimal());
        book.setCongressClassification(bookDetails.getCongressClassification());
        book.setSubject(bookDetails.getSubject());
        book.setSummary(bookDetails.getSummary());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setPageCount(bookDetails.getPageCount());
        book.setLanguage(bookDetails.getLanguage());
        book.setEdition(bookDetails.getEdition());
        book.setCopyCount(bookDetails.getCopyCount()); // اضافه شده
        book.setLibrarySection(bookDetails.getLibrarySection()); // اضافه شده
        book.setShelfCode(bookDetails.getShelfCode()); // اضافه شده
        book.setRowNumber(bookDetails.getRowNumber()); // اضافه شده
        book.setColumnNumber(bookDetails.getColumnNumber()); // اضافه شده
        book.setPositionNote(bookDetails.getPositionNote()); // اضافه شده
        book.setActive(bookDetails.getActive()); // اضافه شده

        if (bookCoverFile != null && !bookCoverFile.isEmpty()) {
            book.setBookCoverFile(bookCoverFile.getBytes());
        } else if (bookDetails.getBookCoverFile() == null) { // اگر bookCoverFile null فرستاده شده بود و فایل جدیدی هم نبود، یعنی حذف جلد
            book.setBookCoverFile(null);
        }
        // اگر bookDetails.getBookCoverFile() مقداری داشت و فایل جدیدی هم آپلود نشده بود، جلد قبلی باقی می‌ماند

        return bookRepository.save(book);
    }

    // متد updateBook بدون فایل، اگر هنوز استفاده می‌شود
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        // ... کپی کردن سایر فیلدها مثل بالا
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setTranslator(bookDetails.getTranslator());
        book.setPublisher(bookDetails.getPublisher());
        book.setIsbn10(bookDetails.getIsbn10());
        book.setIsbn13(bookDetails.getIsbn13());
        book.setDescription(bookDetails.getDescription());
        book.setDeweyDecimal(bookDetails.getDeweyDecimal());
        book.setCongressClassification(bookDetails.getCongressClassification());
        book.setSubject(bookDetails.getSubject());
        book.setSummary(bookDetails.getSummary());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setPageCount(bookDetails.getPageCount());
        book.setLanguage(bookDetails.getLanguage());
        book.setEdition(bookDetails.getEdition());
        book.setCopyCount(bookDetails.getCopyCount());
        book.setLibrarySection(bookDetails.getLibrarySection());
        book.setShelfCode(bookDetails.getShelfCode());
        book.setRowNumber(bookDetails.getRowNumber());
        book.setColumnNumber(bookDetails.getColumnNumber());
        book.setPositionNote(bookDetails.getPositionNote());
        book.setActive(bookDetails.getActive());
        // عدم تغییر bookCoverFile در این نسخه از متد
        return bookRepository.save(book);
    }


    public void softDeleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        book.setActive(false);
        bookRepository.save(book);
    }

    public Optional<Book> getBookById(Long id) {
        // در حالت عادی، فیلتر active در getBookById هم می‌تواند مفید باشد.
        // return bookRepository.findById(id).filter(Book::getActive);
        // اما چون ممکن است برای ویرایش کتاب غیرفعال هم به آن نیاز باشد، فعلا بدون فیلتر فعال بودن
        return bookRepository.findById(id);
    }
}