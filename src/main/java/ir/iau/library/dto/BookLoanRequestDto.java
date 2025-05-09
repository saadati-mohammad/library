package ir.iau.library.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookLoanRequestDto {
    private Long personId;
    private Long bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
}
