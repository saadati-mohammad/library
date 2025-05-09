package ir.iau.library.service;

import ir.iau.library.entity.Book;
import ir.iau.library.entity.Person;
import ir.iau.library.entity.Reservation;
import ir.iau.library.entity.ReservationStatus;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.repository.PersonRepository;
import ir.iau.library.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final BookRepository bookRepository;

    public Reservation createReservation(Long personId, Long bookId, LocalDate expiryDate) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Reservation reservation = Reservation.builder()
                .person(person)
                .book(book)
                .reservationDate(LocalDate.now())
                .expiryDate(expiryDate)
                .status(ReservationStatus.ACTIVE)
                .build();

        return reservationRepository.save(reservation);
    }

    public Reservation fulfillReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.FULFILLED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getActiveReservations() {
        return reservationRepository.findByStatus(ReservationStatus.ACTIVE);
    }

    public List<Reservation> expireOverdueReservations() {
        LocalDate today = LocalDate.now();
        List<Reservation> toExpire = reservationRepository.findByExpiryDateBeforeAndStatus(today, ReservationStatus.ACTIVE);
        toExpire.forEach(r -> r.setStatus(ReservationStatus.EXPIRED));
        return reservationRepository.saveAll(toExpire);
    }

    /**
     *  هر روز ساعت ۲ نیمه‌شب وضعیت رزروهای منقضی را به‌روزرسانی می‌کند
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledExpire() {
        expireOverdueReservations();
    }
}