package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.AppUser;
import io.github.bokalebsson.libraryapplication.entity.Book;
import io.github.bokalebsson.libraryapplication.entity.BookLoan;
import io.github.bokalebsson.libraryapplication.entity.Details;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookLoanRepositoryTest {

    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private Book book1;
    private Book book2;

    private AppUser user1;
    private AppUser user2;

    private BookLoan loan1;
    private BookLoan loan2;
    private BookLoan loan3;

    @BeforeEach
    void setUp() {

        bookLoanRepository.deleteAll();
        bookRepository.deleteAll();
        appUserRepository.deleteAll();

        Details details1 = new Details("john@example.com", "John Doe", LocalDate.of(1990, 1, 1));
        Details details2 = new Details("jane@example.com", "Jane Doe", LocalDate.of(1992, 2, 2));

        user1 = AppUser.builder()
                .username("user1")
                .password("pass1")
                .regDate(LocalDate.now())
                .userDetails(details1)
                .build();

        user2 = AppUser.builder()
                .username("user2")
                .password("pass2")
                .regDate(LocalDate.now())
                .userDetails(details2)
                .build();

        appUserRepository.saveAll(List.of(user1, user2));

        book1 = Book.builder().isbn("111").title("Java Basics").maxLoanDays(10).build();
        book2 = Book.builder().isbn("222").title("Spring Boot for Dummies").maxLoanDays(14).build();
        bookRepository.saveAll(List.of(book1, book2));

        loan1 = BookLoan.builder()
                .book(book1)
                .borrower(user1)
                .loanDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(5))
                .returned(false)
                .build();

        loan2 = BookLoan.builder()
                .book(book1)
                .borrower(user2)
                .loanDate(LocalDate.now().minusDays(15))
                .dueDate(LocalDate.now().minusDays(1))
                .returned(false)
                .build();

        loan3 = BookLoan.builder()
                .book(book2)
                .borrower(user1)
                .loanDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(7))
                .returned(true)
                .build();

        bookLoanRepository.saveAll(List.of(loan1, loan2, loan3));
    }

    @Test
    @DisplayName("Find BookLoans by borrower.")
    void testFindByBorrower() {

        // Act: Retrieve all BookLoans for user1.
        List<BookLoan> found = bookLoanRepository.findByBorrowerId(user1.getId());

        // Assert: Verify that 2 loans are returned for user1.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(bookLoan -> bookLoan.getBook().getTitle())
                .containsExactlyInAnyOrder("Java Basics", "Spring Boot for Dummies");
    }

    @Test
    @DisplayName("Find BookLoans by non-existing borrower, returns empty list.")
    void testFindByBorrowerNotFound() {

        // Act: Try to retrieve loans for a borrowerId that doesn't exist.
        List<BookLoan> found = bookLoanRepository.findByBorrowerId(999);

        // Assert: Should return an empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find BookLoans by bookId.")
    void testFindByBookId() {

        // Act: Retrieve all BookLoans for book1.
        List<BookLoan> found = bookLoanRepository.findByBookId(book1.getId());

        // Assert: Verify that both user1 and user2 have borrowed book1.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(bookLoan -> bookLoan.getBorrower().getUsername())
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("Find BookLoans by non-existing bookId, returns empty list.")
    void testFindByBookIdNotFound() {

        // Act: Try to retrieve loans for a bookId that doesn't exist.
        List<BookLoan> found = bookLoanRepository.findByBookId(999);

        // Assert: Should return an empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find BookLoans where returned is false.")
    void testFindByReturnedFalse() {

        // Act: Retrieve all BookLoans that are not returned yet.
        List<BookLoan> found = bookLoanRepository.findByReturnedFalse();

        // Assert: Verify that loan1 and loan2 are not returned.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(bookLoan -> bookLoan.getBorrower().getUsername())
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("Find BookLoans where all loans are returned, returns empty list.")
    void testFindByReturnedFalseNoneFound() {

        // Arrange: Mark all loans as returned.
        loan1.setReturned(true);
        loan2.setReturned(true);
        loan3.setReturned(true);
        bookLoanRepository.saveAll(List.of(loan1, loan2, loan3));

        // Act: Retrieve all BookLoans that are not returned yet.
        List<BookLoan> found = bookLoanRepository.findByReturnedFalse();

        // Assert: Should return empty list since all are returned.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find overdue BookLoans.")
    void testFindByDueDateBeforeAndReturnedFalse() {

        // Act: Retrieve loans with due date before today and not returned.
        List<BookLoan> found = bookLoanRepository.findByDueDateBeforeAndReturnedFalse(LocalDate.now());

        // Assert: Verify that only loan2 is overdue.
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getBorrower().getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("Find overdue BookLoans when none are overdue, returns empty list.")
    void testFindByDueDateBeforeAndReturnedFalseNoneFound() {

        // Act: Retrieve loans with due date before a date far in the past.
        List<BookLoan> found = bookLoanRepository.findByDueDateBeforeAndReturnedFalse(LocalDate.of(2000, 1, 1));

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find BookLoans between two dates.")
    void testFindByLoanDateBetween() {

        // Act: Retrieve loans with loanDate between 6 days ago and today.
        List<BookLoan> found = bookLoanRepository.findByLoanDateBetween(LocalDate.now().minusDays(6), LocalDate.now());

        // Assert: Verify that loan1 and loan3 fall in this period.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(bookLoan -> bookLoan.getBorrower().getUsername())
                .containsExactlyInAnyOrder("user1", "user1");
    }

    @Test
    @DisplayName("Find BookLoans between dates with no matching loans, returns empty list.")
    void testFindByLoanDateBetweenNoneFound() {

        // Act: Retrieve loans in a future date range with no loans.
        List<BookLoan> found = bookLoanRepository.findByLoanDateBetween(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20)
        );

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Mark a BookLoan as returned.")
    void testMarkAsReturned() {

        // Act: Mark loan1 as returned using the @Modifying query.
        bookLoanRepository.markAsReturned(loan1.getId());

        // Assert: Verify that loan1 is now returned.
        BookLoan updated = bookLoanRepository.findById(loan1.getId()).orElseThrow();
        assertThat(updated.isReturned()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Mark a BookLoan as returned that is already returned")
    void testMarkAsReturnedAlreadyReturned() {

        // Arrange: Ensure loan3 is already returned.
        assertThat(loan3.isReturned()).isTrue();

        // Act: Call markAsReturned on loan3 which is already returned.
        bookLoanRepository.markAsReturned(loan3.getId());

        // Assert: The loan should still be returned (true).
        BookLoan updated = bookLoanRepository.findById(loan3.getId()).orElseThrow();
        assertThat(updated.isReturned()).isTrue();
    }

}
