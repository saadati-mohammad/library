package ir.iau.library.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "farsi_title", nullable = false, length = 200)
    private String farsiTitle;

    @Column(length = 100)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "national_code", length = 10)
    private String nationalCode;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "online_status", length = 20)
    @Builder.Default
    private String onlineStatus = "OFFLINE";

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }
}