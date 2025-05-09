package ir.iau.library.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookExcelRowDto {
    @NotBlank(message = "عنوان کتاب نباید خالی باشد")
    private String title;

    @NotBlank(message = "نویسنده نباید خالی باشد")
    private String author;

    private String translator;

    @NotBlank(message = "شابک ۱۳ رقمی الزامی است")
    @Pattern(regexp = "\\d{13}", message = "شابک باید ۱۳ رقم باشد")
    private String isbn13;

    private String subject;

    @PastOrPresent(message = "تاریخ انتشار نمی‌تواند در آینده باشد")
    private LocalDate publicationDate;
}