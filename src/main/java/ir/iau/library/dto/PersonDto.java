package ir.iau.library.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String membershipType;
    private Boolean active;
}