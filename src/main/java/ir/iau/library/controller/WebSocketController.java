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
     * ارسال پیام خصوصی بین دو کاربر - اصلاح شده برای حل مشکل ارسال دوبار
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received WebSocket message from: {} to: {}", message.getSender(), message.getRecipient());

            // تبدیل WebSocketMessage به MessageSendRequest
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

            // ذخیره پیام در پایگاه داده
            var response = messageService.sendMessage(sendRequest);

            if (response.isSuccess()) {
                // به‌روزرسانی ID پیام با ID واقعی از پایگاه داده
                message.setId(response.getData().getId().toString());

                // ارسال پیام فقط به گیرنده (نه به فرستنده)
                webSocketService.sendMessageToUser(message.getRecipient(), message);

                // ارسال تأیید به فرستنده با وضعیت 'sent'
                WebSocketMessage confirmationMessage = WebSocketMessage.builder()
                        .id(message.getId())
                        .sender(message.getSender())
                        .senderFarsiTitle(message.getSenderFarsiTitle())
                        .recipient(message.getRecipient())
                        .recipientFarsiTitle(message.getRecipientFarsiTitle())
                        .subject(message.getSubject())
                        .message(message.getMessage())
                        .parentMessageId(message.getParentMessageId())
                        .timestamp(message.getTimestamp())
                        .messageType("sent_confirmation")
                        .priority(message.getPriority())
                        .nationalCode(message.getNationalCode())
                        .recipients(message.getRecipients())
                        .enableSendSms(message.getEnableSendSms())
                        .build();

                webSocketService.sendMessageToUser(message.getSender(), confirmationMessage);

                log.info("Message sent successfully via WebSocket with ID: {}", response.getData().getId());
            } else {
                // ارسال خطا به فرستنده
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
     * ویرایش پیام - اصلاح شده
     */
    @MessageMapping("/chat.edit")
    public void editMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received edit request for message: {} by user: {}",
                    message.getOriginalMessageId(), message.getSender());

            if (message.getOriginalMessageId() == null) {
                log.error("Original message ID is required for edit operation");
                sendErrorToUser(message.getSender(), "شناسه پیام برای ویرایش الزامی است");
                return;
            }

            // بررسی مالکیت پیام قبل از ویرایش
            var existingMessage = messageService.getMessageById(Long.parseLong(message.getOriginalMessageId()));
            if (!existingMessage.isSuccess() || existingMessage.getData() == null) {
                log.error("Message not found for edit: {}", message.getOriginalMessageId());
                sendErrorToUser(message.getSender(), "پیام مورد نظر یافت نشد");
                return;
            }

            // بررسی مالکیت
            if (!existingMessage.getData().getSender().equals(message.getSender())) {
                log.error("User {} attempted to edit message owned by {}",
                        message.getSender(), existingMessage.getData().getSender());
                sendErrorToUser(message.getSender(), "شما فقط می‌توانید پیام‌های خود را ویرایش کنید");
                return;
            }

            // انجام ویرایش
            MessageUpdateRequest updateRequest = new MessageUpdateRequest();
            updateRequest.setId(Long.parseLong(message.getOriginalMessageId()));
            updateRequest.setMessage(message.getMessage());

            var response = messageService.updateMessage(updateRequest);

            if (response.isSuccess()) {
                // ارسال پیام ویرایش شده به گیرنده
                String recipientUsername = existingMessage.getData().getRecipient();
                if (recipientUsername != null && !recipientUsername.equals(message.getSender())) {
                    webSocketService.sendMessageToUser(recipientUsername, message);
                }

                // ارسال تأیید به فرستنده
                webSocketService.sendMessageToUser(message.getSender(), message);

                log.info("Message {} edited successfully via WebSocket", message.getOriginalMessageId());
            } else {
                log.error("Failed to edit message via WebSocket: {}", response.getMessage());
                sendErrorToUser(message.getSender(), "خطا در ویرایش پیام: " + response.getMessage());
            }

        } catch (NumberFormatException e) {
            log.error("Invalid message ID format: {}", message.getOriginalMessageId());
            sendErrorToUser(message.getSender(), "شناسه پیام نامعتبر است");
        } catch (Exception e) {
            log.error("Error editing message via WebSocket: {}", e.getMessage(), e);
            sendErrorToUser(message.getSender(), "خطای داخلی سرور");
        }
    }

    /**
     * حذف پیام - اصلاح شده
     */
    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Received delete request for message: {} by user: {}",
                    message.getOriginalMessageId(), message.getSender());

            if (message.getOriginalMessageId() == null) {
                log.error("Original message ID is required for delete operation");
                sendErrorToUser(message.getSender(), "شناسه پیام برای حذف الزامی است");
                return;
            }

            // بررسی مالکیت پیام قبل از حذف
            var existingMessage = messageService.getMessageById(Long.parseLong(message.getOriginalMessageId()));
            if (!existingMessage.isSuccess() || existingMessage.getData() == null) {
                log.error("Message not found for delete: {}", message.getOriginalMessageId());
                sendErrorToUser(message.getSender(), "پیام مورد نظر یافت نشد");
                return;
            }

            // بررسی مالکیت
            if (!existingMessage.getData().getSender().equals(message.getSender())) {
                log.error("User {} attempted to delete message owned by {}",
                        message.getSender(), existingMessage.getData().getSender());
                sendErrorToUser(message.getSender(), "شما فقط می‌توانید پیام‌های خود را حذف کنید");
                return;
            }

            // انجام حذف
            var response = messageService.deleteMessage(Long.parseLong(message.getOriginalMessageId()));

            if (response.isSuccess()) {
                // ارسال اطلاع حذف به گیرنده
                String recipientUsername = existingMessage.getData().getRecipient();
                if (recipientUsername != null && !recipientUsername.equals(message.getSender())) {
                    webSocketService.sendMessageToUser(recipientUsername, message);
                }

                // ارسال تأیید به فرستنده
                webSocketService.sendMessageToUser(message.getSender(), message);

                log.info("Message {} deleted successfully via WebSocket", message.getOriginalMessageId());
            } else {
                log.error("Failed to delete message via WebSocket: {}", response.getMessage());
                sendErrorToUser(message.getSender(), "خطا در حذف پیام: " + response.getMessage());
            }

        } catch (NumberFormatException e) {
            log.error("Invalid message ID format: {}", message.getOriginalMessageId());
            sendErrorToUser(message.getSender(), "شناسه پیام نامعتبر است");
        } catch (Exception e) {
            log.error("Error deleting message via WebSocket: {}", e.getMessage(), e);
            sendErrorToUser(message.getSender(), "خطای داخلی سرور");
        }
    }

    /**
     * متد کمکی برای ارسال خطا به کاربر
     */
    private void sendErrorToUser(String username, String errorMessage) {
        WebSocketMessage error = WebSocketMessage.builder()
                .messageType("error")
                .message(errorMessage)
                .timestamp(System.currentTimeMillis())
                .build();
        webSocketService.sendMessageToUser(username, error);
    }

    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("Marking message as read: {} by user: {}",
                    message.getOriginalMessageId(), message.getSender());

            if (message.getOriginalMessageId() == null) {
                log.error("Message ID is required for mark read operation");
                return;
            }

            var response = messageService.markAsRead(Long.parseLong(message.getOriginalMessageId()));

            if (response.isSuccess()) {
                // اطلاع‌رسانی به فرستنده اصلی پیام
                var originalMessage = messageService.getMessageById(Long.parseLong(message.getOriginalMessageId()));
                if (originalMessage.isSuccess() && originalMessage.getData() != null) {
                    WebSocketMessage readConfirmation = WebSocketMessage.builder()
                            .id(message.getId())
                            .originalMessageId(message.getOriginalMessageId())
                            .messageType("read_confirmation")
                            .sender(message.getSender())
                            .timestamp(System.currentTimeMillis())
                            .build();

                    webSocketService.sendMessageToUser(originalMessage.getData().getSender(), readConfirmation);
                }

                log.info("Message marked as read successfully");
            } else {
                log.error("Failed to mark message as read: {}", response.getMessage());
            }

        } catch (NumberFormatException e) {
            log.error("Invalid message ID format: {}", message.getOriginalMessageId());
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

    /**
     * اتصال کاربر
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.info("User connecting: {}", message.getSender());

            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().put("username", message.getSender());
            }

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

            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().remove("username");
            }

            webSocketService.notifyUserConnection(message.getSender(), false);

        } catch (Exception e) {
            log.error("Error removing user: {}", e.getMessage(), e);
        }
    }
}