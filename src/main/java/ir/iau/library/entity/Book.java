package ir.iau.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.iau.library.dto.BookExcelRowDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name="books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String translator;
    private String publisher;
    private String isbn10;
    private String isbn13;
    @Column(length = 2000)
    private String description;
    private String deweyDecimal;
    private String congressClassification;
    private String subject;
    @Column(length = 2000)
    private String summary;
    private LocalDate publicationDate;
    private Integer pageCount;
    private String language;
    @Lob
    @Column(name = "book_cover_file"/*, columnDefinition="BLOB" or "LONGBLOB" or "BYTEA" if needed for specific DB */)
    private byte[] bookCoverFile;
    private String edition;
    private Integer copyCount;
    private String librarySection; // بخش کتابخانه (مثلاً: علوم انسانی، مهندسی)
    private String shelfCode;      // کد یا شماره قفسه (مثلاً: A3, B5)
    private String rowNumber;      // ردیف (مثلاً: ردیف 2)
    private String columnNumber;   // ستون (مثلاً: ستون 4)
    @Column(length = 2000)
    private String positionNote;   // توضیح اضافی درباره مکان یا شرایط خاص نگهداری
    private Boolean active; // soft delete flag

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BookLoan> loans = new ArrayList<>();

    public Book(BookExcelRowDto bookExcelRowDto) {
        this.isbn10 = bookExcelRowDto.getIsbn10();
        this.title = bookExcelRowDto.getTitle();
        this.author = bookExcelRowDto.getAuthor();
        this.translator = bookExcelRowDto.getTranslator();
        this.description = bookExcelRowDto.getDescription();
    }
}
