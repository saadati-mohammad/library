package ir.iau.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * در این متد، یک endpoint برای اتصال کلاینت‌های وب‌سوکت ثبت می‌شود.
     * این همان آدرسی است که کلاینت (فرانت‌اند) برای برقراری ارتباط اولیه استفاده می‌کند.
     *
     * @param registry رجیستری برای ثبت endpoint ها
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint اصلی برای اتصال وب‌سوکت.
        // آدرس '/ws-chat' با چیزی که در فایل environment.ts فرانت‌اند تعریف شده مطابقت دارد.
        registry.addEndpoint("/ws-chat")
                // تنظیم منابع مجاز برای اتصال به وب‌سوکت.
                // بهتر است از همان مقادیر CorsConfig استفاده شود تا هماهنگی حفظ شود.
                .setAllowedOriginPatterns(
                        "http://localhost:4200",
                        "https://lms-iau-ac.liara.run",
                        "https://lms-iau.vercel.app"
                )
                // فعال‌سازی SockJS به عنوان یک جایگزین (fallback) برای مرورگرهایی
                // که از وب‌سوکت به صورت کامل پشتیبانی نمی‌کنند.
                .withSockJS();
    }

    /**
     * در این متد، یک message broker برای مسیریابی پیام‌ها پیکربندی می‌شود.
     * broker مسئول ارسال پیام‌ها به کلاینت‌های مشترک (subscribed) است.
     *
     * @param registry رجیستری برای پیکربندی broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // پیشوند مقصد برای پیام‌هایی که از سمت سرور به کلاینت‌ها ارسال می‌شوند (مانند چت روم‌ها، اعلان‌ها و...).
        // در پروژه شما، پیام‌ها به مقصدهایی مانند /topic/user/{username} ارسال می‌شوند.
        registry.enableSimpleBroker("/topic");

        // پیشوند مقصد برای پیام‌هایی که از کلاینت به سرور ارسال می‌شوند.
        // این پیشوند به متدهای با انوتیشن @MessageMapping در کنترلرها متصل می‌شود.
        // برای مثال، وقتی فرانت‌اند پیامی به /app/chat.send می‌فرستد، متد مربوطه در WebSocketController اجرا می‌شود.
        registry.setApplicationDestinationPrefixes("/app");
    }
}