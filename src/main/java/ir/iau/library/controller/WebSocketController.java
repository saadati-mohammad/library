package ir.iau.library.websocket;

import ir.iau.library.dto.MessageSendRequest;
import ir.iau.library.dto.MessageUpdateRequest;
import ir.iau.library.dto.WebSocketMessage;
import ir.iau.library.service.MessageService;
import ir.iau.library.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketService webSocketService;
    private final MessageService messageService;

    /**
     * [مسئولیت اصلی] ارسال پیام خصوصی بین دو کاربر.
     * این پیام در پایگاه داده ذخیره شده و سپس برای گیرنده و فرستنده ارسال می‌شود.
     * این endpoint اصلی است که توسط فرانت‌اند شما استفاده می‌شود.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received WebSocket message from: {} to: {}", message.getSender(), message.getRecipient());

            // تبدیل WebSocketMessage به MessageSendRequest برای ذخیره در پایگاه داده
            MessageSendRequest sendRequest = MessageSendRequest.builder()
                    .sender(message.getSender())
                    .senderFarsiTitle(message.getSenderFarsiTitle())
                    .recipient(message.getRecipient())
                    .recipientFarsiTitle(message.getRecipientFarsiTitle())
                    .subject(message.getSubject())
                    .message(message.getMessage())
                    .parentMessageId(message.getParentMessageId() != null ? Long.parseLong(message.getParentMessageId()) : null)
                    .priority(message.getPriority())
                    .nationalCode(message.getNationalCode())
                    .recipients(message.getRecipients())
                    .enableSendSms(message.getEnableSendSms())
                    .build();

            // ارسال پیام به سرویس برای ذخیره‌سازی
            var response = messageService.sendMessage(sendRequest);

            if (response.isSuccess()) {
                // ارسال پیام به گیرنده از طریق WebSocket
                webSocketService.sendMessageToUser(message.getRecipient(), message);

                // ارسال تأیید (همان پیام) به فرستنده
                webSocketService.sendMessageToUser(message.getSender(), message);

                log.info("Message sent successfully via WebSocket with ID: {}", response.getData().getId());
            } else {
                // ارسال خطا به فرستنده در صورت عدم موفقیت
                WebSocketMessage errorMessage = WebSocketMessage.builder()
                        .id(message.getId())
                        .messageType("error")
                        .message("Failed to send message: " + response.getMessage())
                        .timestamp(System.currentTimeMillis())
                        .build();

                webSocketService.sendMessageToUser(message.getSender(), errorMessage);
                log.error("Failed to send message via WebSocket: {}", response.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing WebSocket message: {}", e.getMessage(), e);

            // ارسال خطای کلی به فرستنده
            WebSocketMessage errorMessage = WebSocketMessage.builder()
                    .id(message.getId())
                    .messageType("error")
                    .message("Internal server error")
                    .timestamp(System.currentTimeMillis())
                    .build();

            webSocketService.sendMessageToUser(message.getSender(), errorMessage);
        }
    }

    /**
     * ویرایش پیام از طریق WebSocket
     */
    @MessageMapping("/chat.edit")
    public void editMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received edit request for message: {}", message.getOriginalMessageId());

            if (message.getOriginalMessageId() == null) {
                log.error("Original message ID is required for edit operation");
                return;
            }

            // تبدیل به MessageUpdateRequest
            MessageUpdateRequest updateRequest = new MessageUpdateRequest();
            updateRequest.setId(Long.parseLong(message.getOriginalMessageId()));
            updateRequest.setMessage(message.getMessage());

            // ویرایش پیام در پایگاه داده
            var response = messageService.updateMessage(updateRequest);

            if (response.isSuccess()) {
                // ارسال پیام ویرایش شده به دریافت‌کننده
                if (message.getRecipient() != null) {
                    webSocketService.sendMessageToUser(message.getRecipient(), message);
                }

                // ارسال تأیید به فرستنده
                webSocketService.sendMessageToUser(message.getSender(), message);

                log.info("Message edited successfully via WebSocket");
            } else {
                log.error("Failed to edit message via WebSocket: {}", response.getMessage());
            }

        } catch (Exception e) {
            log.error("Error editing message via WebSocket: {}", e.getMessage(), e);
        }
    }

    /**
     * حذف پیام از طریق WebSocket
     */
    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received delete request for message: {}", message.getOriginalMessageId());

            if (message.getOriginalMessageId() == null) {
                log.error("Original message ID is required for delete operation");
                return;
            }

            // حذف پیام از پایگاه داده
            var response = messageService.deleteMessage(Long.parseLong(message.getOriginalMessageId()));

            if (response.isSuccess()) {
                // ارسال اطلاع حذف به دریافت‌کننده
                if (message.getRecipient() != null) {
                    webSocketService.sendMessageToUser(message.getRecipient(), message);
                }

                // ارسال تأیید به فرستنده
                webSocketService.sendMessageToUser(message.getSender(), message);

                log.info("Message deleted successfully via WebSocket");
            } else {
                log.error("Failed to delete message via WebSocket: {}", response.getMessage());
            }

        } catch (Exception e) {
            log.error("Error deleting message via WebSocket: {}", e.getMessage(), e);
        }
    }

    /**
     * [ویژگی ادغام شده] ارسال پیام به یک اتاق (Room) خاص.
     * این پیام در پایگاه داده ذخیره نمی‌شود و فقط برای اعضای آنلاین اتاق ارسال می‌شود.
     */
    @MessageMapping("/chat.sendToRoom")
    public void sendToRoom(@Payload WebSocketMessage message) {
        log.info("Received message for room: {} from sender: {}", message.getRoomId(), message.getSender());
        // در اینجا می‌توانید منطق اعتبارسنجی (مثلاً آیا کاربر عضو اتاق است) را اضافه کنید
        webSocketService.sendMessageToRoom(message.getRoomId(), message);
    }

    /**
     * [ویژگی ادغام شده] ارسال پیام همگانی (Broadcast) برای همه کاربران متصل.
     * مناسب برای اعلان‌های سیستمی.
     */
    @MessageMapping("/chat.broadcast")
    public void broadcastMessage(@Payload WebSocketMessage message) {
        log.info("Received broadcast message from sender: {}", message.getSender());
        // در اینجا می‌توانید منطق اعتبارسنجی (مثلاً آیا کاربر ادمین است) را اضافه کنید
        webSocketService.broadcastMessage(message);
    }

    /**
     * اتصال کاربر و ثبت session
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("User connecting: {}", message.getSender());

            // ذخیره نام کاربری در session
            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().put("username", message.getSender());
            }

            // اطلاع‌رسانی اتصال کاربر
            webSocketService.notifyUserConnection(message.getSender(), true);

        } catch (Exception e) {
            log.error("Error adding user: {}", e.getMessage(), e);
        }
    }

    /**
     * قطع اتصال کاربر
     */
    @MessageMapping("/chat.removeUser")
    public void removeUser(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("User disconnecting: {}", message.getSender());

            // حذف نام کاربری از session
            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().remove("username");
            }

            // اطلاع‌رسانی قطع اتصال کاربر
            webSocketService.notifyUserConnection(message.getSender(), false);

        } catch (Exception e) {
            log.error("Error removing user: {}", e.getMessage(), e);
        }
    }

    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Marking message as read: {}", message.getOriginalMessageId());

            if (message.getOriginalMessageId() == null) {
                log.error("Message ID is required for mark read operation");
                return;
            }

            // علامت‌گذاری پیام به عنوان خوانده شده
            var response = messageService.markAsRead(Long.parseLong(message.getOriginalMessageId()));

            if (response.isSuccess()) {
                // اطلاع‌رسانی به فرستنده اصلی پیام
                WebSocketMessage readConfirmation = WebSocketMessage.builder()
                        .id(message.getId())
                        .originalMessageId(message.getOriginalMessageId())
                        .messageType("read_confirmation")
                        .sender(message.getSender())
                        .timestamp(System.currentTimeMillis())
                        .build();

                webSocketService.sendMessageToUser(message.getRecipient(), readConfirmation);

                log.info("Message marked as read successfully");
            } else {
                log.error("Failed to mark message as read: {}", response.getMessage());
            }

        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage(), e);
        }
    }

    /**
     * اعلام وضعیت تایپ کاربر
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.debug("User {} is typing to {}", message.getSender(), message.getRecipient());

            // ارسال وضعیت تایپ به گیرنده
            WebSocketMessage typingMessage = WebSocketMessage.builder()
                    .id(message.getId())
                    .sender(message.getSender())
                    .senderFarsiTitle(message.getSenderFarsiTitle())
                    .messageType("typing")
                    .timestamp(System.currentTimeMillis())
                    .build();

            webSocketService.sendMessageToUser(message.getRecipient(), typingMessage);

        } catch (Exception e) {
            log.error("Error handling typing status: {}", e.getMessage(), e);
        }
    }
}