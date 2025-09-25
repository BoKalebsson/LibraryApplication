package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Integer> {

    List<BookLoan> findByBorrowerId(int borrowerId);

    List<BookLoan> findByBookId(int bookId);

    List<BookLoan> findByReturnedFalse();

    List<BookLoan> findByDueDateBeforeAndReturnedFalse(LocalDate date);

    List<BookLoan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query("UPDATE BookLoan SET returned = true WHERE id = :loanId")
    void markAsReturned(int loanId);

}
