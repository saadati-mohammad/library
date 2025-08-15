package ir.iau.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String sender;

    @Column(name = "sender_farsi_title", nullable = false, length = 200)
    private String senderFarsiTitle;

    @Column(length = 100)
    private String recipient;

    @Column(name = "recipient_farsi_title", length = 200)
    private String recipientFarsiTitle;

    @Column(length = 200)
    private String subject;

    @Column(nullable = false, length = 4000) // <--- این خط را جایگزین کنید
    private String message;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    @Builder.Default
    @Column(name = "data_state")
    private Integer dataState = 1;

    @Column(name = "create_user", length = 100)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "modify_user", length = 100)
    private String modifyUser;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "delete_user", length = 100)
    private String deleteUser;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Builder.Default
    @Column(name = "enable_send_sms")
    private Boolean enableSendSms = false;

    @Column(length = 50)
    private String priority;

    @Column(name = "national_code", length = 10)
    private String nationalCode;

    @Column(length = 500)
    private String recipients;

    @Column(name = "message_status", length = 20)
    private String messageStatus;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.dataState == null) {
            this.dataState = 1;
        }
        if (this.enableSendSms == null) {
            this.enableSendSms = false;
        }
        if (this.messageStatus == null) {
            this.messageStatus = "SENT";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }
}