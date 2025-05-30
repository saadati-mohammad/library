package ir.iau.library.dto;

import ir.iau.library.entity.LoanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookLoanFilterDto {
    private String bookTitle;
    private String personName;
    private Long personId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LoanStatus status;
}
