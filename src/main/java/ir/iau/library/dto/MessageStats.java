package ir.iau.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// آمار پیام‌ها
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageStats {
    private Long sentCount;
    private Long receivedCount;
    private Long unreadCount;
    private Long totalCount;
    private Long todayCount;
    private Long highPriorityCount;
}