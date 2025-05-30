package ir.iau.library.repository;

import ir.iau.library.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long>, JpaSpecificationExecutor<BookLoan> {
    List<BookLoan> findByStatus(ir.iau.library.entity.LoanStatus status);
    List<BookLoan> findByDueDateBeforeAndStatus(LocalDate date, ir.iau.library.entity.LoanStatus status);
    long countByLoanDateBetween(LocalDate start, LocalDate end);
}