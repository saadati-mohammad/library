package ir.iau.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookFilterDto {
    private String title;
    private String author;
    private String translator;
    private String publisher;
    private String isbn10;
    private String isbn13;
    private String description;
    private String deweyDecimal;
    private String congressClassification;
    private String subject;
    private String summary;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publicationDate; // برای جستجوی تاریخ دقیق

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publicationDateAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publicationDateBefore;

    private Integer pageCount;
    private String language;
    private String edition;
    private Boolean active;
    private Integer copyCount;
    private String librarySection;
    private String shelfCode;
    private String rowNumbers;
    private String columnNumber;
    private String positionNote;

    // اگر نیاز بود می‌توان آن را هم اضافه کرد.
}