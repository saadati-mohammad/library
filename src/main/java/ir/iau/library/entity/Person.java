package ir.iau.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.iau.library.dto.PersonFilterDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "person", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nationalId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nationalId; // کد ملی، یک فیلد مهم و منحصر به فرد

    private String phone;
    private LocalDate birthDate;
    private LocalDate membershipDate;
    private String membershipType; // STANDARD, PREMIUM, etc.
    private String address;

    @Column(length = 2000)
    private String notes; // یادداشت های اضافی درباره عضو

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture; // عکس پروفایل

    private Boolean active = true; // فلگ برای غیرفعال سازی

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BookLoan> loans = new ArrayList<>();

    // سازنده برای استفاده در فیلترینگ
    public Person(PersonFilterDto filterDto) {
        this.firstName = filterDto.getFirstName();
        this.lastName = filterDto.getLastName();
        this.email = filterDto.getEmail();
        this.phone = filterDto.getPhone();
        this.nationalId = filterDto.getNationalId();
        this.membershipType = filterDto.getMembershipType();
        this.address = filterDto.getAddress();
        this.active = filterDto.getActive();
    }
}