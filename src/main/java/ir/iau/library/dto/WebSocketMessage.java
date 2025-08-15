package ir.iau.library.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WebSocketMessage {
    private String id;
    private String sender;
    private String senderFarsiTitle;
    private String recipient;
    private String recipientFarsiTitle;
    private String subject;
    private String message;
    private String messageType; // chat, typing, read_confirmation, error, system_notification, etc.
    private String originalMessageId; // برای عملیات edit/delete
    private String parentMessageId; // برای reply
    private String priority;
    private String nationalCode;
    private String recipients;
    private Boolean enableSendSms;
    private Long timestamp;
    private String roomId;

    /**
     * متد برای دریافت یا تولید roomId
     * اگر roomId تنظیم نشده باشد، بر اساس sender و recipient تولید می‌شود
     */
    public String getRoomId() {
        if (roomId != null && !roomId.trim().isEmpty()) {
            return roomId;
        }

        // تولید roomId بر اساس sender و recipient
        if (sender != null && recipient != null) {
            // مرتب کردن نام‌ها برای اطمینان از یکسان بودن roomId برای هر دو طرف
            if (sender.compareTo(recipient) < 0) {
                return sender + "_" + recipient;
            } else {
                return recipient + "_" + sender;
            }
        }

        return null;
    }

    /**
     * متد برای تنظیم roomId
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * متد کمکی برای تنظیم timestamp اگر موجود نباشد
     */
    public void ensureTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * متد کمکی برای بررسی معتبر بودن پیام
     */
    public boolean isValid() {
        return sender != null && !sender.trim().isEmpty() &&
                recipient != null && !recipient.trim().isEmpty();
    }
}