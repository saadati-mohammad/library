package ir.iau.library.dto;

import lombok.Data;

@Data
public class CreateLoanRequestDto {
    private Long personId;
    private Long bookId;
    private String notes;
}