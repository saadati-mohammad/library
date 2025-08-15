package ir.iau.library.repository;

import ir.iau.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * یافتن کاربر بر اساس نام کاربری
     */
    Optional<User> findByUsername(String username);

    /**
     * یافتن کاربر بر اساس نام کاربری و وضعیت فعال
     */
    Optional<User> findByUsernameAndIsActiveTrue(String username);

    /**
     * یافتن کاربر بر اساس ایمیل
     */
    Optional<User> findByEmail(String email);

    /**
     * یافتن کاربر بر اساس کد ملی
     */
    Optional<User> findByNationalCode(String nationalCode);

    /**
     * جستجو در کاربران بر اساس نام فارسی
     */
    @Query("SELECT u FROM User u WHERE u.farsiTitle LIKE %:farsiTitle% AND u.isActive = true")
    List<User> findByFarsiTitleContainingAndIsActiveTrue(@Param("farsiTitle") String farsiTitle);

    /**
     * دریافت کاربران آنلاین
     */
    @Query("SELECT u FROM User u WHERE u.onlineStatus = 'ONLINE' AND u.isActive = true")
    List<User> findOnlineUsers();

    /**
     * دریافت کاربران بر اساس وضعیت آنلاین
     */
    List<User> findByOnlineStatusAndIsActiveTrue(String onlineStatus);

    /**
     * بررسی وجود نام کاربری
     */
    boolean existsByUsername(String username);

    /**
     * بررسی وجود ایمیل
     */
    boolean existsByEmail(String email);

    /**
     * به‌روزرسانی زمان آخرین بازدید
     */
    @Modifying
    @Query("UPDATE User u SET u.lastSeen = :lastSeen WHERE u.username = :username")
    void updateLastSeen(@Param("username") String username, @Param("lastSeen") LocalDateTime lastSeen);

    /**
     * به‌روزرسانی وضعیت آنلاین
     */
    @Modifying
    @Query("UPDATE User u SET u.onlineStatus = :status, u.lastSeen = :lastSeen WHERE u.username = :username")
    void updateOnlineStatus(@Param("username") String username, @Param("status") String status, @Param("lastSeen") LocalDateTime lastSeen);

    /**
     * دریافت کاربران فعال
     */
    List<User> findByIsActiveTrue();

    /**
     * جستجوی عمومی کاربران
     */
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.farsiTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "u.isActive = true")
    List<User> searchUsers(@Param("query") String query);

    /**
     * تعداد کاربران آنلاین
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.onlineStatus = 'ONLINE' AND u.isActive = true")
    Long countOnlineUsers();

    /**
     * کاربرانی که اخیراً فعال بوده‌اند
     */
    @Query("SELECT u FROM User u WHERE u.lastSeen >= :since AND u.isActive = true ORDER BY u.lastSeen DESC")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
}