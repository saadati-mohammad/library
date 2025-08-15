package ir.iau.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// معیارهای مکالمه
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationCriteria {
    private String senderUsername;
    private String recipientUsername;
    private int page = 0;
    private int size = 15;
}