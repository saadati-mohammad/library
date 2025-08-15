package ir.iau.library.repository;

import ir.iau.library.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    /**
     * یافتن فایل‌های ضمیمه بر اساس پیام
     */
    List<FileAttachment> findByMessageId(Long messageId);

    /**
     * یافتن فایل بر اساس نام فایل
     */
    Optional<FileAttachment> findByFileName(String fileName);

    /**
     * یافتن فایل‌ها بر اساس نوع محتوا
     */
    List<FileAttachment> findByContentType(String contentType);

    /**
     * یافتن فایل‌های ضمیمه بر اساس وضعیت آپلود
     */
    List<FileAttachment> findByUploadStatus(String uploadStatus);

    /**
     * جستجو در فایل‌ها بر اساس نام اصلی
     */
    @Query("SELECT f FROM FileAttachment f WHERE f.originalFileName LIKE %:fileName%")
    List<FileAttachment> searchByOriginalFileName(@Param("fileName") String fileName);

    /**
     * فایل‌هایی که در بازه زمانی خاصی آپلود شده‌اند
     */
    @Query("SELECT f FROM FileAttachment f WHERE f.createDate BETWEEN :startDate AND :endDate")
    List<FileAttachment> findByUploadDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * فایل‌های بزرگ‌تر از اندازه مشخص
     */
    @Query("SELECT f FROM FileAttachment f WHERE f.fileSize > :size")
    List<FileAttachment> findLargeFiles(@Param("size") Long size);

    /**
     * فایل‌های کوچک‌تر از اندازه مشخص
     */
    @Query("SELECT f FROM FileAttachment f WHERE f.fileSize < :size")
    List<FileAttachment> findSmallFiles(@Param("size") Long size);

    /**
     * محاسبه مجموع حجم فایل‌ها
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileAttachment f")
    Long getTotalFileSize();

    /**
     * محاسبه مجموع حجم فایل‌های یک پیام
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileAttachment f WHERE f.message.id = :messageId")
    Long getTotalFileSizeByMessage(@Param("messageId") Long messageId);

    /**
     * تعداد فایل‌ها بر اساس نوع
     */
    @Query("SELECT f.contentType, COUNT(f) FROM FileAttachment f GROUP BY f.contentType")
    List<Object[]> getFileCountByType();

    /**
     * آخرین فایل‌های آپلود شده
     */
    @Query("SELECT f FROM FileAttachment f ORDER BY f.createDate DESC")
    List<FileAttachment> findLatestUploads();

    /**
     * فایل‌های آپلود نشده یا ناقص
     */
    @Query("SELECT f FROM FileAttachment f WHERE f.uploadStatus IN ('UPLOADING', 'FAILED')")
    List<FileAttachment> findIncompleteUploads();
}