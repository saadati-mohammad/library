package ir.iau.library.controller;

import ir.iau.library.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // کلاینت پیام رو به /app/chat.send میفرسته
    @MessageMapping("/chat.send")
    public void sendToRoom(MessageDto message) {
        String destination = "/topic/rooms/" + message.getRoomId();
        // بدون تغییر، پیام رو به همه روم ارسال می‌کنیم
        messagingTemplate.convertAndSend(destination, message);
    }
}
