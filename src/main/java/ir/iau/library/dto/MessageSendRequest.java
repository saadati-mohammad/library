package ir.iau.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// درخواست ارسال پیام
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSendRequest {
    private String sender;
    private String senderFarsiTitle;
    private String recipient;
    private String recipientFarsiTitle;
    private String subject;
    private String message;
    private Long parentMessageId;
    private String priority;
    private String nationalCode;
    private String recipients;
    private Boolean enableSendSms;
}