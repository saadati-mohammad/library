package ir.iau.library.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookFilterDto {
    private String title;
    private String author;
    private String subject;
    private LocalDate publishedAfter;
    private LocalDate publishedBefore;
    private Boolean available;
}
