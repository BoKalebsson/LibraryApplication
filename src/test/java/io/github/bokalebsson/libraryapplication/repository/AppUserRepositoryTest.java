package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.AppUser;
import io.github.bokalebsson.libraryapplication.entity.Book;
import io.github.bokalebsson.libraryapplication.entity.BookLoan;
import io.github.bokalebsson.libraryapplication.entity.Details;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testAppUser1;
    private AppUser testAppUser2;

    @BeforeEach
    void setUp() {

        appUserRepository.deleteAll();

        Details testUserDetails1 = Details.builder()
                .name("Hans Luhrberg")
                .email("hans.luhrberg@test.nu")
                .birthDate(LocalDate.of(1979, 12, 13))
                .build();

        Details testUserDetails2 = Details.builder()
                .name("Greger Petterson")
                .email("greger.pettersson@test.nu")
                .birthDate(LocalDate.of(1976, 6, 17))
                .build();

        testAppUser1 = AppUser.builder()
                .username("hansluhrberg")
                .password("123456789")
                .regDate(LocalDate.of(2023, 12, 13))
                .userDetails(testUserDetails1)
                .build();

        testAppUser2 = AppUser.builder()
                .username("gregerpettersson")
                .password("verysecret")
                .regDate(LocalDate.of(2025, 9, 18))
                .userDetails(testUserDetails2)
                .build();

        appUserRepository.saveAll(List.of(testAppUser1, testAppUser2));
    }

    @Test
    @DisplayName("Finds an appUser with the provided username.")
    void findByUsername_shouldReturnCorrectAppUser() {

        // Arrange: define the username we want to search for.
        String usernameToFind = "hansluhrberg";

        // Act: call the repository method.
        Optional<AppUser> result = appUserRepository.findByUsername(usernameToFind);

        // Assert: verify that the Optional contains a user with the correct username.
        assertThat(result)
                .isPresent()
                .get()
                .extracting(AppUser::getUsername)
                .isEqualTo(usernameToFind);
    }

    @Test
    @DisplayName("Returns empty Optional when username does not exist")
    void findByUsername_shouldReturnEmptyOptionalWhenNotFound() {

        // Arrange: define a username that does not exist.
        String usernameToFind = "nonexistentuser";

        // Act: call repository method.
        Optional<AppUser> result = appUserRepository.findByUsername(usernameToFind);

        // Assert: verify that no AppUser is found.
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Finds all appUsers registered within a given date range.")
    void findByRegDateBetween_shouldReturnCorrectUsers() {

        // Arrange: define the date range for the search.
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 19);

        // Act: fetch users registered within the date range.
        List<AppUser> result = appUserRepository.findByRegDateBetween(startDate, endDate);

        // Assert: ensure the correct users are returned.
        assertThat(result)
                .hasSize(1)
                .extracting(AppUser::getUsername)
                .containsExactly("gregerpettersson");
    }

    @Test
    @DisplayName("Returns empty list when no users registered in the date range")
    void findByRegDateBetween_shouldReturnEmptyListWhenNoMatch() {

        // Arrange: define a date range with no users.
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);

        // Act: fetch users registered within the date range.
        List<AppUser> result = appUserRepository.findByRegDateBetween(startDate, endDate);

        // Assert: verify that the result list is empty.
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Finds an appUser by the associated Details ID.")
    void findByUserDetailsId_shouldReturnCorrectAppUser() {

        // Arrange: get the Details ID of the second test user.
        Integer detailsId = testAppUser2.getUserDetails().getId();

        // Act: call repository method to find user by Details ID.
        Optional<AppUser> result = appUserRepository.findByUserDetails_Id(detailsId);

        // Assert: verify the Optional contains the correct user.
        assertThat(result)
                .isPresent()
                .get()
                .extracting(AppUser::getUsername)
                .isEqualTo("gregerpettersson");
    }

    @Test
    @DisplayName("Returns empty Optional when no user has the given Details ID")
    void findByUserDetailsId_shouldReturnEmptyOptionalWhenNotFound() {

        // Arrange: define a Details ID that does not exist.
        Integer detailsId = 9999;

        // Act: call repository method.
        Optional<AppUser> result = appUserRepository.findByUserDetails_Id(detailsId);

        // Assert: verify that no AppUser is found.
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Finds an appUser by email, ignoring case.")
    void findByUserDetailsEmailIgnoreCase_shouldReturnCorrectAppUser() {

        // Arrange: define the email to search for (different case to test ignore case).
        String emailToFind = "HANS.LUHRBERG@TEST.NU";

        // Act: call repository method to find user by email.
        Optional<AppUser> result = appUserRepository.findByUserDetails_EmailIgnoreCase(emailToFind);

        // Assert: verify that the correct user was returned
        assertThat(result)
                .isPresent()
                .get()
                .extracting(AppUser::getUsername)
                .isEqualTo("hansluhrberg");
    }

    @Test
    @DisplayName("Returns empty Optional when email does not exist, ignoring case")
    void findByUserDetailsEmailIgnoreCase_shouldReturnEmptyOptionalWhenNotFound() {

        // Arrange: define an email that does not exist.
        String emailToFind = "nonexistent@test.nu";

        // Act: call repository method.
        Optional<AppUser> result = appUserRepository.findByUserDetails_EmailIgnoreCase(emailToFind);

        // Assert: verify that no AppUser is found.
        assertThat(result)
                .isEmpty();
    }

    @Test
    void testAddBookLoan_SetsLoanAndUpdatesBookAvailability() {

        // Arrange: Create book, user, details and loan.
        Book book = Book.builder()
                .id(1)
                .title("Spring Boot Guide")
                .maxLoanDays(7)
                .available(true)
                .build();

        Details details = new Details("alice@example.com", "Alice Smith", LocalDate.of(1990, 1, 1));

        AppUser user = new AppUser();
        user.setUsername("alice123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(book);

        // Act: Add the loan to the user.
        user.addBookLoan(loan);

        // Assert: Verify bidirectional relationship.
        assertTrue(user.getBookLoans().contains(loan), "User should contain the loan");
        assertEquals(user, loan.getBorrower(), "Loan borrower should be set to user");

        // Assert: Verify dueDate is calculated correctly.
        assertNotNull(loan.getLoanDate(), "Loan date should be set");
        assertEquals(loan.getLoanDate().plusDays(book.getMaxLoanDays()), loan.getDueDate(),
                "Due date should be loanDate + maxLoanDays");

        // Assert: Verify book availability.
        assertFalse(book.isAvailable(), "Book should be unavailable after loan");
    }

    @Test
    void testAddBookLoan_ThrowsExceptionIfBookNotAvailable() {

        // Arrange: Create book, user, details and loan.
        Book book = Book.builder()
                .id(2)
                .title("Java Patterns")
                .maxLoanDays(10)
                .available(false)
                .build();

        Details details = new Details("bob@example.com", "Bob Jones", LocalDate.of(1985, 5, 15));

        AppUser user = new AppUser();
        user.setUsername("bob123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(book);

        // Act & Assert:
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> user.addBookLoan(loan));
        assertEquals("Book is not available for loan: Java Patterns", exception.getMessage());
    }

    @Test
    void testAddBookLoan_WithNullBook_ThrowsException() {

        // Arrange: Create user, details and loan.
        Details details = new Details("dave@example.com", "Dave Black", LocalDate.of(1995, 6, 10));

        AppUser user = new AppUser();
        user.setUsername("dave123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(null);

        // Act & Assert: Should throw NullPointerException.
        assertThrows(NullPointerException.class, () -> user.addBookLoan(loan));
    }

    @Test
    void testAddBookLoan_WithExistingLoanDate() {

        // Arrange: Create book, user, details and loan.
        Book book = Book.builder()
                .id(4)
                .title("Kotlin Basics")
                .maxLoanDays(5)
                .available(true)
                .build();

        Details details = new Details("eva@example.com", "Eva Green", LocalDate.of(1993, 9, 12));

        AppUser user = new AppUser();
        user.setUsername("eva123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(book);

        LocalDate pastDate = LocalDate.of(2025, 1, 1);
        loan.setLoanDate(pastDate);

        // Act:
        user.addBookLoan(loan);

        // Assert: Verify dueDate is calculated from existing loanDate
        assertEquals(pastDate, loan.getLoanDate(), "Loan date should remain unchanged");
        assertEquals(pastDate.plusDays(book.getMaxLoanDays()), loan.getDueDate(),
                "Due date should be loanDate + maxLoanDays");
    }

    @Test
    void testRemoveBookLoan_RemovesLoanAndUpdatesBookAvailability() {

        // Arrange: Create book, user, details and loan.
        Book book = Book.builder()
                .id(3)
                .title("Effective Java")
                .maxLoanDays(14)
                .available(true)
                .build();

        Details details = new Details("carol@example.com", "Carol White", LocalDate.of(1992, 3, 20));

        AppUser user = new AppUser();
        user.setUsername("carol123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(book);
        user.addBookLoan(loan);

        // Act:
        user.removeBookLoan(loan);

        // Assert:
        assertFalse(user.getBookLoans().contains(loan), "User should no longer contain the loan");
        assertNull(loan.getBorrower(), "Loan borrower should be null after removal");
        assertTrue(book.isAvailable(), "Book should be available again after loan removed");
    }

    @Test
    void testRemoveBookLoan_NotInList_DoesNothing() {

        // Arrange: Create book, user, details and loan.
        Book book = Book.builder()
                .id(5)
                .title("Python Advanced")
                .maxLoanDays(8)
                .available(true)
                .build();

        Details details = new Details("frank@example.com", "Frank White", LocalDate.of(1988, 7, 22));

        AppUser user = new AppUser();
        user.setUsername("frank123");
        user.setPassword("password");
        user.setRegDate(LocalDate.now());
        user.setUserDetails(details);

        BookLoan loan = new BookLoan();
        loan.setBook(book);

        // Act: Remove a loan that was never added.
        user.removeBookLoan(loan);

        // Assert: Nothing crashes and book remains available.
        assertFalse(user.getBookLoans() != null && user.getBookLoans().contains(loan),
                "User should not contain the loan");
        assertTrue(book.isAvailable(), "Book should remain available");
    }

}
