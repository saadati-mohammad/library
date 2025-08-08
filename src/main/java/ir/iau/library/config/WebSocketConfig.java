package ir.iau.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint for SockJS clients
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // dev: allow all origins. Prod: محدود کن
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // داخلی: /app برای پیام‌های ورودی (MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        // broker ساده: ارسال به کلاینت‌ها تحت /topic و /queue
        config.enableSimpleBroker("/topic", "/queue");
    }
}