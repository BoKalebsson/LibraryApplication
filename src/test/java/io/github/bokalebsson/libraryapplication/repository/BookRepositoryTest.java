package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeEach
    void setUp() {

        bookRepository.deleteAll();

        book1 = Book.builder().isbn("123-ABC").title("Spring Boot for Dummies").maxLoanDays(14).build();
        book2 = Book.builder().isbn("111").title("Java Basics").maxLoanDays(10).build();
        book3 = Book.builder().isbn("222").title("Advanced Java").maxLoanDays(20).build();

        bookRepository.saveAll(List.of(book1, book2, book3));
    }

    @Test
    @DisplayName("Find book by ISBN ignoring case")
    void testFindByIsbnIgnoreCase() {

        // Act: Search for the book with different case.
        Optional<Book> found = bookRepository.findByIsbnIgnoreCase("123-abc");

        // Assert: Verify the book is found.
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Spring Boot for Dummies");
    }

    @Test
    @DisplayName("Find book by non-existing ISBN returns empty")
    void testFindByIsbnIgnoreCaseNotFound() {

        // Act: Search for a non-existing ISBN.
        Optional<Book> found = bookRepository.findByIsbnIgnoreCase("999-XYZ");

        // Assert: Should return empty.
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Find books by title containing substring ignoring case")
    void testFindByTitleContainingIgnoreCase() {

        // Act: Search for books with "java" in title.
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase("java");

        // Assert: Verify correct books are returned.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java Basics", "Advanced Java");
    }

    @Test
    @DisplayName("Find books by title containing substring not present returns empty list")
    void testFindByTitleContainingIgnoreCaseNotFound() {

        // Act: Search for books with a substring that does not exist.
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase("Python");

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find books with maxLoanDays less than a value")
    void testFindByMaxLoanDaysLessThan() {

        // Act: Find books with maxLoanDays < 15.
        List<Book> found = bookRepository.findByMaxLoanDaysLessThan(15);

        // Assert: Verify correct books are returned.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Spring Boot for Dummies", "Java Basics");
    }

    @Test
    @DisplayName("Find books with maxLoanDays less than a value lower than all returns empty list")
    void testFindByMaxLoanDaysLessThanNoneFound() {

        // Act: Find books with maxLoanDays < 5.
        List<Book> found = bookRepository.findByMaxLoanDaysLessThan(5);

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

}
