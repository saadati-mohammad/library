package ir.iau.library.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String subject;
    private String isbn13;
    private LocalDate publicationDate;
    private Boolean active;
}