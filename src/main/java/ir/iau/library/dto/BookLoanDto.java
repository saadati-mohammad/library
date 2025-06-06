package ir.iau.library.dto;


import ir.iau.library.entity.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookLoanDto {
    private Long id;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private String notes;

    // Person Info
    private Long personId;
    private String personFirstName;
    private String personLastName;

    // Book Info
    private Long bookId;
    private String bookTitle;
}