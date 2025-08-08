package ir.iau.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String id;        // شناسه یکتا (local یا سرور)
    private String from;      // فرستنده
    private String roomId;    // شناسه روم (مثلاً room-1)
    private String content;   // متن پیام
    private long timestamp;   // اختیاری
    private String status;    // pending, sent, failed
}