package ir.iau.library.controller;

import ir.iau.library.dto.*;
import ir.iau.library.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    /**
     * دریافت پیام‌های مکالمه بین دو کاربر
     *
     * مثال درخواست:
     * GET /api/v1/messages/conversation?senderUsername=user1&recipientUsername=user2&page=0&size=15
     *
     * مثال پاسخ:
     * {
     *   "success": true,
     *   "message": "Messages retrieved successfully",
     *   "data": {
     *     "content": [...],
     *     "pageable": {...},
     *     "totalElements": 25,
     *     "totalPages": 2,
     *     "last": false,
     *     "first": true
     *   },
     *   "hasMore": true,
     *   "totalElements": 25,
     *   "totalPages": 2
     * }
     */
    @GetMapping("/conversation")
    @Operation(summary = "دریافت پیام‌های مکالمه", description = "دریافت پیام‌های مکالمه بین دو کاربر با صفحه‌بندی")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "پیام‌ها با موفقیت دریافت شدند"),
            @ApiResponse(responseCode = "400", description = "پارامترهای ورودی نامعتبر"),
            @ApiResponse(responseCode = "500", description = "خطای سرور")
    })
    public ResponseEntity<ir.iau.library.dto.ApiResponse<Page<MessageDto>>> getConversationMessages(
            @Parameter(description = "نام کاربری فرستنده", required = true)
            @RequestParam String senderUsername,

            @Parameter(description = "نام کاربری گیرنده", required = true)
            @RequestParam String recipientUsername,

            @Parameter(description = "شماره صفحه")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "تعداد آیتم در هر صفحه")
            @RequestParam(defaultValue = "15") int size) {

        ConversationCriteria criteria = ConversationCriteria.builder()
                .senderUsername(senderUsername)
                .recipientUsername(recipientUsername)
                .page(page)
                .size(size)
                .build();

        ir.iau.library.dto.ApiResponse<Page<MessageDto>> response = messageService.getConversationMessages(criteria);
        return ResponseEntity.ok(response);
    }

    /**
     * ارسال پیام جدید
     *
     * مثال درخواست:
     * POST /api/v1/messages/send
     * {
     *   "sender": "user1",
     *   "senderFarsiTitle": "کاربر یک",
     *   "recipient": "user2",
     *   "recipientFarsiTitle": "کاربر دو",
     *   "subject": "اختصاصی",
     *   "message": "سلام، چطوری؟",
     *   "priority": "normal",
     *   "enableSendSms": false
     * }
     *
     * مثال پاسخ:
     * {
     *   "success": true,
     *   "message": "Message sent successfully",
     *   "data": {
     *     "id": 123,
     *     "sender": "user1",
     *     "senderFarsiTitle": "کاربر یک",
     *     "recipient": "user2",
     *     "message": "سلام، چطوری؟",
     *     "createDate": "2024-01-15T10:30:00",
     *     "messageStatus": "SENT"
     *   }
     * }
     */
    @PostMapping("/send")
    @Operation(summary = "ارسال پیام جدید", description = "ارسال پیام جدید با امکانات مختلف")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<MessageDto>> sendMessage(
            @Valid @RequestBody MessageSendRequest request) {

        ir.iau.library.dto.ApiResponse<MessageDto> response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ویرایش پیام
     *
     * مثال درخواست:
     * PUT /api/v1/messages/update
     * {
     *   "id": 123,
     *   "message": "سلام، چطوری؟ امیدوارم حالت خوب باشه"
     * }
     */
    @PutMapping("/update")
    @Operation(summary = "ویرایش پیام", description = "ویرایش محتوای یک پیام موجود")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<MessageDto>> updateMessage(
            @Valid @RequestBody MessageUpdateRequest request) {

        ir.iau.library.dto.ApiResponse<MessageDto> response = messageService.updateMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * حذف پیام
     *
     * مثال درخواست:
     * DELETE /api/v1/messages/delete/123
     *
     * مثال پاسخ:
     * {
     *   "success": true,
     *   "message": "Message deleted successfully",
     *   "data": "Message deleted successfully"
     * }
     */
    @DeleteMapping("/delete/{messageId}")
    @Operation(summary = "حذف پیام", description = "حذف نرم یک پیام")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<String>> deleteMessage(
            @Parameter(description = "شناسه پیام", required = true)
            @PathVariable Long messageId) {

        ir.iau.library.dto.ApiResponse<String> response = messageService.deleteMessage(messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * جستجو در پیام‌ها
     *
     * مثال درخواست:
     * GET /api/v1/messages/search?query=سلام&sender=user1&priority=high&page=0&size=10
     *
     * مثال پاسخ:
     * {
     *   "success": true,
     *   "message": "Search completed successfully",
     *   "data": {
     *     "content": [...],
     *     "totalElements": 5
     *   },
     *   "hasMore": false
     * }
     */
    @GetMapping("/search")
    @Operation(summary = "جستجو در پیام‌ها", description = "جستجوی پیشرفته در پیام‌ها با فیلترهای مختلف")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<Page<MessageDto>>> searchMessages(
            @Parameter(description = "کلمه کلیدی جستجو")
            @RequestParam(required = false) String query,

            @Parameter(description = "نام کاربری فرستنده")
            @RequestParam(required = false) String sender,

            @Parameter(description = "نام کاربری گیرنده")
            @RequestParam(required = false) String recipient,

            @Parameter(description = "موضوع پیام")
            @RequestParam(required = false) String subject,

            @Parameter(description = "اولویت پیام")
            @RequestParam(required = false) String priority,

            @Parameter(description = "وضعیت فعال بودن")
            @RequestParam(required = false) Boolean isActive,

            @Parameter(description = "تاریخ شروع")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "تاریخ پایان")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "شماره صفحه")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "تعداد آیتم در هر صفحه")
            @RequestParam(defaultValue = "15") int size) {

        SearchCriteria criteria = SearchCriteria.builder()
                .query(query)
                .sender(sender)
                .recipient(recipient)
                .subject(subject)
                .priority(priority)
                .isActive(isActive)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        ir.iau.library.dto.ApiResponse<Page<MessageDto>> response = messageService.searchMessages(criteria);
        return ResponseEntity.ok(response);
    }

    /**
     * دریافت پیام‌های ارسال شده کاربر
     */
    @GetMapping("/sent")
    @Operation(summary = "پیام‌های ارسال شده", description = "دریافت لیست پیام‌های ارسال شده توسط کاربر")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<Page<MessageDto>>> getSentMessages(
            @Parameter(description = "نام کاربری", required = true)
            @RequestParam String username,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        ir.iau.library.dto.ApiResponse<Page<MessageDto>> response = messageService.getSentMessages(username, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * دریافت پیام‌های دریافت شده کاربر
     */
    @GetMapping("/received")
    @Operation(summary = "پیام‌های دریافت شده", description = "دریافت لیست پیام‌های دریافت شده توسط کاربر")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<Page<MessageDto>>> getReceivedMessages(
            @Parameter(description = "نام کاربری", required = true)
            @RequestParam String username,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        ir.iau.library.dto.ApiResponse<Page<MessageDto>> response = messageService.getReceivedMessages(username, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * دریافت جزئیات یک پیام
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "جزئیات پیام", description = "دریافت جزئیات کامل یک پیام")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<MessageDto>> getMessageById(
            @PathVariable Long messageId) {

        ir.iau.library.dto.ApiResponse<MessageDto> response = messageService.getMessageById(messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    @PostMapping("/mark-read/{messageId}")
    @Operation(summary = "علامت‌گذاری به عنوان خوانده شده", description = "علامت‌گذاری یک پیام به عنوان خوانده شده")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<String>> markAsRead(
            @PathVariable Long messageId) {

        ir.iau.library.dto.ApiResponse<String> response = messageService.markAsRead(messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * دریافت آمار پیام‌ها
     *
     * مثال پاسخ:
     * {
     *   "success": true,
     *   "data": {
     *     "sentCount": 45,
     *     "receivedCount": 32,
     *     "unreadCount": 5,
     *     "totalCount": 77,
     *     "todayCount": 8,
     *     "highPriorityCount": 3
     *   }
     * }
     */
    @GetMapping("/stats")
    @Operation(summary = "آمار پیام‌ها", description = "دریافت آمار کامل پیام‌های کاربر")
    public ResponseEntity<ir.iau.library.dto.ApiResponse<MessageStats>> getMessageStats(
            @Parameter(description = "نام کاربری", required = true)
            @RequestParam String username) {

        ir.iau.library.dto.ApiResponse<MessageStats> response = messageService.getMessageStats(username);
        return ResponseEntity.ok(response);
    }
}