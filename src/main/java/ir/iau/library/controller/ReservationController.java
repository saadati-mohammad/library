package ir.iau.library.controller;


import ir.iau.library.entity.Reservation;
import ir.iau.library.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestParam Long personId,
            @RequestParam Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        Reservation reservation = reservationService.createReservation(personId, bookId, expiryDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<Reservation> fulfillReservation(@PathVariable Long id) {
        Reservation updated = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/active")
    public List<Reservation> listActive() {
        return reservationService.getActiveReservations();
    }
}