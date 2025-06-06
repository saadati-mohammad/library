package ir.iau.library.dto;

import ir.iau.library.entity.LoanStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
public class BookLoanFilterDto {
    private String personNationalId;
    private String bookIsbn;
    private LoanStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateTo;
}