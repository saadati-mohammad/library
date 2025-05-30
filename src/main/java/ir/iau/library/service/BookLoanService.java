package ir.iau.library.service;

import ir.iau.library.dto.BookLoanFilterDto;
import ir.iau.library.dto.BookLoanRequestDto;
import ir.iau.library.entity.Book;
import ir.iau.library.entity.BookLoan;
import ir.iau.library.entity.LoanStatus;
import ir.iau.library.entity.Person;
import ir.iau.library.repository.BookLoanRepository;
import ir.iau.library.repository.BookRepository;
import ir.iau.library.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookLoanService {

    @Autowired
    private BookLoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JavaMailSender mailSender;

    public BookLoan loanBook(BookLoanRequestDto request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found with id " + request.getPersonId()));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id " + request.getBookId()));

        BookLoan loan = BookLoan.builder()
                .person(person)
                .book(book)
                .loanDate(request.getLoanDate())
                .dueDate(request.getDueDate())
                .status(LoanStatus.ON_LOAN)
                .lateFee(0.0)
                .build();

        return loanRepository.save(loan);
    }
    public List<BookLoan> searchLoans(BookLoanFilterDto filter) {
        Specification<BookLoan> spec = Specification.where(null);

        if (filter.getBookTitle() != null && !filter.getBookTitle().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("book").get("title")), "%" + filter.getBookTitle().toLowerCase() + "%"));
        }

        if (filter.getPersonName() != null && !filter.getPersonName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("person").get("firstName")), "%" + filter.getPersonName().toLowerCase() + "%"));
        }

        if (filter.getPersonId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("person").get("id"), filter.getPersonId()));
        }

        if (filter.getLoanDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("loanDate"), filter.getLoanDate()));
        }

        if (filter.getDueDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("dueDate"), filter.getDueDate()));
        }

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }

        return loanRepository.findAll(spec);
    }

    public BookLoan returnBook(Long loanId, LocalDate returnDate) {
        BookLoan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + loanId));
        loan.setReturnDate(returnDate);

        if (returnDate.isAfter(loan.getDueDate())) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
            loan.setLateFee(daysOverdue * 1.0);
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        return loanRepository.save(loan);
    }

    public List<BookLoan> getOverdueLoans() {
        return loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ON_LOAN);
    }

    public long countLoansBetween(LocalDate startDate, LocalDate endDate) {
        return loanRepository.countByLoanDateBetween(startDate, endDate);
    }

    public void sendDueReminders() {
        List<BookLoan> overdue = getOverdueLoans();
        for (BookLoan loan : overdue) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(loan.getPerson().getEmail());
            message.setSubject("یادآوری بازگشت کتاب");
            message.setText(String.format("سلام %s،\n\nکتاب «%s» با شناسه امانت %d در تاریخ %s موعد بازگشت داشته و هنوز برگشت نشده.\nلطفاً در اسرع وقت بازگردانید.\n\nبا احترام، کتابخانه",
                    loan.getPerson().getFirstName(),
                    loan.getBook().getTitle(),
                    loan.getId(),
                    loan.getDueDate()));
            mailSender.send(message);
        }
    }
}