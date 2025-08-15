package ir.iau.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// درخواست به‌روزرسانی پیام
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdateRequest {
    private Long id;
    private String message;
}