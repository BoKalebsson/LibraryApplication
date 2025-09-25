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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private Author author1;
    private Author author2;
    private Author author3;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {

        authorRepository.deleteAll();
        bookRepository.deleteAll();

        book1 = Book.builder()
                .title("Book One")
                .isbn("123-ABC")
                .maxLoanDays(14)
                .build();

        book2 = Book.builder()
                .title("Book Two")
                .isbn("456-DEF")
                .maxLoanDays(7)
                .build();

        bookRepository.saveAll(List.of(book1, book2));

        author1 = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .books(Set.of(book1))
                .build();

        author2 = Author.builder()
                .firstName("Jane")
                .lastName("Doe")
                .books(Set.of(book2))
                .build();

        author3 = Author.builder()
                .firstName("Alice")
                .lastName("Smith")
                .books(Set.of())
                .build();

        authorRepository.saveAll(List.of(author1, author2, author3));
    }

    @Test
    @DisplayName("Find authors by first name.")
    void testFindByFirstName() {

        // Act: Retrieve all authors with firstName "John".
        List<Author> found = authorRepository.findByFirstName("John");

        // Assert: Only author1 should be returned.
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Find by first name with null, should return empty list.")
    void testFindByFirstNameNull() {

        // Act: Search with null firstName.
        List<Author> found = authorRepository.findByFirstName(null);

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find by first name with empty string, should return empty list")
    void testFindByFirstNameEmpty() {

        // Act: Search with empty string
        List<Author> found = authorRepository.findByFirstName("");

        // Assert: Should return empty list
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find authors by first name ignoring case (edge case).")
    void testFindByFirstNameIgnoreCase() {

        // Act: Retrieve all authors with firstName "john" (lowercase, original is "John").
        List<Author> found = authorRepository.findByFirstName("john");

        // Assert: Should return empty because findByFirstName is case-sensitive.
        // This documents the behavior in case someone expects case-insensitive search.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find authors by last name.")
    void testFindByLastName() {

        // Act: Retrieve all authors with lastName "Doe".
        List<Author> found = authorRepository.findByLastName("Doe");

        // Assert: Both author1 and author2 should be returned.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Author::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    @DisplayName("Find authors by first or last name containing keyword (ignore case).")
    void testFindByFirstOrLastNameContainingIgnoreCase() {

        // Act: Retrieve authors whose firstName or lastName contains "jo" or "smith" (case-insensitive).
        List<Author> found = authorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("jo", "smith");

        // Assert: author1 and author3 should be returned.
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Author::getFirstName)
                .containsExactlyInAnyOrder("John", "Alice");
    }

    @Test
    @DisplayName("Find by first or last name containing keyword with no match, should return empty list")
    void testFindByFirstOrLastNameContainingIgnoreCaseNoneFound() {

        // Act: Search with keywords that do not match any author.
        List<Author> found = authorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("xyz", "abc");

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find authors by books' ID - single book.")
    void testFindByBooksIdSingleBook() {

        // Act: Find authors who have book1.
        List<Author> found = authorRepository.findByBooks_Id(book1.getId());

        // Assert: The returned author should be "John" since author1 is associated with book1
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Find authors by books' ID - no match.")
    void testFindByBooksIdNoMatch() {

        // Act: Use a non-existing book ID.
        List<Author> found = authorRepository.findByBooks_Id(999);

        // Assert: Should return empty list.
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Update author name by ID.")
    void testUpdateNameById() {

        // Act: Update author1's name using updateNameById.
        authorRepository.updateNameById(author1.getId(), "Johnny", "Doe-Smith");

        // Assert: Retrieve author1 again and check that the name has been updated.
        Optional<Author> updated = authorRepository.findById(author1.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstName()).isEqualTo("Johnny");
        assertThat(updated.get().getLastName()).isEqualTo("Doe-Smith");
    }

    @Test
    @DisplayName("Update author name by ID with invalid ID, should not throw")
    void testUpdateNameByIdInvalidId() {

        // Act: Try to update a non-existing author.
        authorRepository.updateNameById(999, "Ghost", "Author");

        // Assert: Repository should remain unchanged.
        List<Author> allAuthors = authorRepository.findAll();
        assertThat(allAuthors).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("Delete author by ID.")
    void testDeleteById() {

        // Arrange: Retrieve the author entity to be deleted so that Hibernate manages it.
        Author authorToDelete = authorRepository.findById(author2.getId()).orElseThrow();

        // Act: Delete the author and flush changes to the database.
        authorRepository.delete(authorToDelete);
        authorRepository.flush();

        // Assert: Verify that the author no longer exists in the repository.
        Optional<Author> deleted = authorRepository.findById(author2.getId());
        assertThat(deleted).isNotPresent();

        // Assert: Verify that the remaining authors are still present.
        List<Author> remaining = authorRepository.findAll();
        assertThat(remaining).hasSize(2);
    }

    @Test
    @Transactional
    @DisplayName("Delete author by invalid ID should not throw.")
    void testDeleteByIdInvalid() {

        // Act: Attempt to delete an author that does not exist.
        authorRepository.deleteById(999);
        authorRepository.flush();

        // Assert: Repository should remain unchanged.
        List<Author> allAuthors = authorRepository.findAll();
        assertThat(allAuthors).hasSize(3);
    }


}