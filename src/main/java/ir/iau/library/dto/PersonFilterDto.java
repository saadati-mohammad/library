package ir.iau.library.dto;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonFilterDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationalId;
    private String membershipType;
    private String address;
    private Boolean active;
    private LocalDate birthDate;
    private LocalDate membershipDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate membershipDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate membershipDateTo;
}
