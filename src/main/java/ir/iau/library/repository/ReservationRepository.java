package ir.iau.library.repository;

import ir.iau.library.entity.Reservation;
import ir.iau.library.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByExpiryDateBeforeAndStatus(LocalDate date, ReservationStatus status);
}