package ir.iau.library.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonExcelRowDto {
    @NotBlank(message = "نام نباید خالی باشد")
    private String firstName;

    @NotBlank(message = "نام‌خانوادگی نباید خالی باشد")
    private String lastName;

    @Email(message = "ایمیل معتبر نیست")
    @NotBlank(message = "ایمیل نباید خالی باشد")
    private String email;

    private String phone;

    @Past(message = "تاریخ تولد باید گذشته باشد")
    private LocalDate birthDate;
}