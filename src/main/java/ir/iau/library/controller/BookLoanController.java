package ir.iau.library.controller;

import ir.iau.library.dto.BookLoanRequestDto;
import ir.iau.library.entity.BookLoan;
import ir.iau.library.service.BookLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class BookLoanController {

    @Autowired
    private BookLoanService loanService;

    @PostMapping
    public ResponseEntity<BookLoan> loanBook(@RequestBody BookLoanRequestDto request) {
        BookLoan loan = loanService.loanBook(request);
        return ResponseEntity.status(201).body(loan);
    }

    @PutMapping("/{loanId}/return")
    public ResponseEntity<BookLoan> returnBook(
            @PathVariable Long loanId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        BookLoan updated = loanService.returnBook(loanId, returnDate);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/overdue")
    public List<BookLoan> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }

    @GetMapping("/report")
    public long countLoansBetween(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return loanService.countLoansBetween(start, end);
    }

    @PostMapping("/reminders")
    public ResponseEntity<Void> sendReminders() {
        loanService.sendDueReminders();
        return ResponseEntity.ok().build();
    }
}