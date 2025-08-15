package ir.iau.library.service;

import ir.iau.library.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    // SimpMessagingTemplate یک ابزار اصلی برای ارسال پیام به broker در Spring است.
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ارسال یک پیام WebSocket به یک کاربر مشخص.
     * این متد برای چت خصوصی بین دو کاربر استفاده می‌شود.
     *
     * @param username نام کاربری که پیام را دریافت خواهد کرد.
     * @param message  شیء پیام که باید ارسال شود.
     */
    public void sendMessageToUser(String username, WebSocketMessage message) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Cannot send WebSocket message to a null or empty username.");
            return;
        }

        // مقصد پیام بر اساس نام کاربری ساخته می‌شود.
        // این الگو با الگوی اشتراک (subscription) در فرانت‌اند مطابقت دارد: /topic/user/{username}
        String destination = "/topic/user/" + username;

        try {
            log.info("Sending message with ID '{}' to user destination: {}", message.getId(), destination);
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to user {}: {}", username, e.getMessage(), e);
        }
    }

    /**
     * [ویژگی ادغام شده] ارسال پیام به یک اتاق (Room) خاص.
     * این متد برای چت‌های گروهی یا کانال‌های خاص استفاده می‌شود.
     *
     * @param roomId  شناسه اتاق.
     * @param message پیامی که باید ارسال شود.
     */
    public void sendMessageToRoom(String roomId, WebSocketMessage message) {
        if (roomId == null || roomId.trim().isEmpty()) {
            log.warn("Cannot send message to a null or empty roomId.");
            return;
        }

        // مقصد پیام بر اساس شناسه اتاق ساخته می‌شود: /topic/rooms/{roomId}
        String destination = "/topic/rooms/" + roomId;
        try {
            log.info("Sending message to room '{}' from sender '{}'", roomId, message.getSender());
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("Failed to send message to room {}: {}", roomId, e.getMessage(), e);
        }
    }

    /**
     * [ویژگی ادغام شده] ارسال پیام همگانی (Broadcast) به تمام کاربران.
     * این متد برای ارسال اعلان‌های عمومی و سیستمی استفاده می‌شود.
     *
     * @param message پیامی که باید به همه ارسال شود.
     */
    public void broadcastMessage(WebSocketMessage message) {
        // مقصد پیام همگانی: /topic/broadcast
        String destination = "/topic/broadcast";
        try {
            log.info("Broadcasting message from sender '{}'", message.getSender());
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("Failed to broadcast message: {}", e.getMessage(), e);
        }
    }

    /**
     * اطلاع‌رسانی وضعیت اتصال یا قطع اتصال یک کاربر به همه.
     * این متد می‌تواند برای نمایش لیست کاربران آنلاین استفاده شود.
     *
     * @param username  نام کاربری که وضعیت آن تغییر کرده است.
     * @param isConnected  وضعیت اتصال (true برای متصل شدن، false برای قطع شدن).
     */
    public void notifyUserConnection(String username, boolean isConnected) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        // ساخت یک پیام سیستمی برای اطلاع‌رسانی
        WebSocketMessage statusMessage = WebSocketMessage.builder()
                .sender("system")
                .message(username + (isConnected ? " is now online" : " has disconnected"))
                .messageType(isConnected ? "user_connected" : "user_disconnected")
                .timestamp(System.currentTimeMillis())
                .build();

        // ارسال این پیام به یک تاپیک عمومی که همه کاربران به آن گوش می‌دهند.
        String destination = "/topic/public";

        try {
            log.info("Notifying connection status for user {}: {}", username, isConnected);
            messagingTemplate.convertAndSend(destination, statusMessage);
        } catch (Exception e) {
            log.error("Failed to notify connection status for user {}: {}", username, e.getMessage(), e);
        }
    }
}