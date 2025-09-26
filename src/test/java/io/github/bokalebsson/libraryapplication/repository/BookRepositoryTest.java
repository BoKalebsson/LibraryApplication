package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Author;
import io.github.bokalebsson.libraryapplication.entity.Book;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Book book1;
    private Book book2;
    private Book book3;

    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {

        bookRepository.deleteAll();
        authorRepository.deleteAll();

        book1 = Book.builder().isbn("123-ABC").title("Spring Boot for Dummies").maxLoanDays(14).authors(new HashSet<>()).build();
        book2 = Book.builder().isbn("111").title("Java Basics").maxLoanDays(10).authors(new HashSet<>()).build();
        book3 = Book.builder().isbn("222").title("Advanced Java").maxLoanDays(20).authors(new HashSet<>()).build();

        bookRepository.saveAll(List.of(book1, book2, book3));

        author1 = Author.builder().firstName("John").lastName("Doe").books(new HashSet<>(List.of(book1))).build();
        author2 = Author.builder().firstName("Jane").lastName("Doe").books(new HashSet<>(List.of(book2))).build();

        authorRepository.saveAll(List.of(author1, author2));

        book1.getAuthors().add(author1);
        book2.getAuthors().add(author2);
        bookRepository.saveAll(List.of(book1, book2));
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

    @Test
    @DisplayName("Add author to book bi-directionally.")
    void testAddAuthorToBookBiDirectional() {

        // Arrange: Create a new book and new author.
        Book newBook = Book.builder().title("New Book").isbn("999-XYZ").maxLoanDays(12).authors(new HashSet<>()).build();
        bookRepository.save(newBook);
        Author newAuthor = Author.builder().firstName("Alice").lastName("Smith").books(new HashSet<>()).build();
        authorRepository.save(newAuthor);

        // Act: Add author to book and vice versa.
        newBook.getAuthors().add(newAuthor);
        newAuthor.getBooks().add(newBook);
        bookRepository.save(newBook);
        authorRepository.save(newAuthor);

        // Assert: Book should contain the author.
        Book updatedBook = bookRepository.findById(newBook.getId()).orElseThrow();
        assertThat(updatedBook.getAuthors()).contains(newAuthor);

        // Assert: Author should contain the book.
        Author updatedAuthor = authorRepository.findById(newAuthor.getId()).orElseThrow();
        assertThat(updatedAuthor.getBooks()).contains(newBook);
    }

    @Test
    @DisplayName("Adding the same author twice to a book does not duplicate.")
    void testAddSameAuthorTwice() {

        // Arrange: Ensure book1 has author1.
        Book book = bookRepository.findById(book1.getId()).orElseThrow();

        // Act: Add the same author again.
        book.getAuthors().add(author1);
        author1.getBooks().add(book);
        bookRepository.save(book);
        authorRepository.save(author1);

        // Assert: Author list should contain author1 only once.
        Book updatedBook = bookRepository.findById(book1.getId()).orElseThrow();
        assertThat(updatedBook.getAuthors()).hasSize(1).contains(author1);
    }

    @Test
    @DisplayName("Remove author from book bi-directionally.")
    @Transactional
    void testRemoveAuthorFromBookBiDirectional() {

        // Arrange: Ensure book1 has author1.
        Book book = bookRepository.findById(book1.getId()).orElseThrow();
        assertThat(book.getAuthors()).contains(author1);

        // Act: Remove author1 from book1 and vice versa.
        book.getAuthors().remove(author1);
        author1.getBooks().remove(book);
        bookRepository.save(book);

        // Assert: Book no longer contains author1.
        Book updatedBook = bookRepository.findById(book1.getId()).orElseThrow();
        assertThat(updatedBook.getAuthors()).doesNotContain(author1);

        // Assert: Author no longer contains book1.
        Author updatedAuthor = authorRepository.findById(author1.getId()).orElseThrow();
        assertThat(updatedAuthor.getBooks()).doesNotContain(book);
    }

    @Test
    @DisplayName("Removing a non-associated author from a book does not throw.")
    void testRemoveNonExistingAuthor() {

        // Arrange: Create an author not associated with book3.
        Author newAuthor = Author.builder().firstName("Ghost").lastName("Writer").books(new HashSet<>()).build();
        authorRepository.save(newAuthor);
        Book book = bookRepository.findById(book3.getId()).orElseThrow();

        // Act: Remove the author which is not in book3.
        book.getAuthors().remove(newAuthor);
        newAuthor.getBooks().remove(book);
        bookRepository.save(book);

        // Assert: Book's authors remain unchanged (still empty).
        Book updatedBook = bookRepository.findById(book3.getId()).orElseThrow();
        assertThat(updatedBook.getAuthors()).isEmpty();
    }

}
