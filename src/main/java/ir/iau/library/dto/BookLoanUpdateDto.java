package ir.iau.library.dto;

import ir.iau.library.entity.LoanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookLoanUpdateDto {
    private Long loanId;

    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private Double lateFee;

    private Long personId;
    private Long bookId;
}