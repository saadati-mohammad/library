package ir.iau.library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FileAttachmentDto {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String uploadStatus;
    private LocalDateTime createDate;
    private String fileUrl; // URL برای دسترسی به فایل
    private String fileType; // image, document, video, etc.
    private String description; // توضیحات فایل
    private Boolean isActive;

    /**
     * متد کمکی برای تبدیل اندازه فایل به فرمت قابل خواندن
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize.doubleValue();

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * بررسی اینکه فایل یک تصویر است یا نه
     */
    public boolean isImage() {
        if (contentType == null) return false;
        return contentType.toLowerCase().startsWith("image/");
    }

    /**
     * بررسی اینکه فایل یک سند است یا نه
     */
    public boolean isDocument() {
        if (contentType == null) return false;
        String type = contentType.toLowerCase();
        return type.contains("pdf") ||
                type.contains("document") ||
                type.contains("text") ||
                type.contains("msword") ||
                type.contains("sheet") ||
                type.contains("presentation");
    }
}