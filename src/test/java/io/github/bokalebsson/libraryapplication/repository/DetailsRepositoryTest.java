package io.github.bokalebsson.libraryapplication.repository;

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

@DataJpaTest
@ActiveProfiles("test")
public class DetailsRepositoryTest {

    @Autowired
    private DetailsRepository detailsRepository;

    private Details details1;
    private Details details2;
    private Details details3;

    @BeforeEach
    void setUp() {

        detailsRepository.deleteAll();

        details1 = Details.builder()
                .name("Hans Luhrberg")
                .email("hans.luhrberg@test.nu")
                .birthDate(LocalDate.of(1979, 12, 13))
                .build();

        details2 = Details.builder()
                .name("Greger Petterson")
                .email("greger.pettersson@test.nu")
                .birthDate(LocalDate.of(1976, 6, 17))
                .build();

        details3 = Details.builder()
                .name("Hans Peterson")
                .email("hans.peterson@test.nu")
                .birthDate(LocalDate.of(1980, 3, 5))
                .build();

        detailsRepository.saveAll(List.of(details1, details2, details3));
    }

    @Test
    @DisplayName("Finds Details by exact email")
    void findByEmail_shouldReturnCorrectDetails() {

        // Arrange: define the email we want to search for.
        String emailToFind = "hans.luhrberg@test.nu";

        // Act: call repository method.
        Optional<Details> result = detailsRepository.findByEmail(emailToFind);

        // Assert: verify the Optional contains the correct Details.
        assertThat(result)
                .isPresent()
                .get()
                .extracting(Details::getEmail)
                .isEqualTo(emailToFind);
    }

    @Test
    @DisplayName("Returns empty Optional when email does not exist")
    void findByEmail_shouldReturnEmptyOptionalWhenEmailNotFound() {

        // Arrange: define a non-existent email.
        String emailToFind = "nonexistent@test.nu";

        // Act: call repository method.
        Optional<Details> result = detailsRepository.findByEmail(emailToFind);

        // Assert: verify that no Details object is found.
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Finds all Details containing part of a name")
    void findByNameContaining_shouldReturnCorrectDetails() {

        // Arrange: define partial name to search for.
        String partOfName = "Pet";

        // Act: fetch all Details with names containing the partial string.
        List<Details> result = detailsRepository.findByNameContaining(partOfName);

        // Assert: check that the correct users are returned.
        assertThat(result)
                .hasSize(2)
                .extracting(Details::getName)
                .containsExactlyInAnyOrder("Greger Petterson", "Hans Peterson");
    }

    @Test
    @DisplayName("Returns empty list when no name contains the search term")
    void findByNameContaining_shouldReturnEmptyListWhenNoMatch() {

        // Arrange: define a search term that matches no names.
        String partOfName = "xyz";

        // Act: fetch all Details containing the search term.
        List<Details> result = detailsRepository.findByNameContaining(partOfName);

        // Assert: verify that the result list is empty.
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Finds all Details by name, ignoring case")
    void findByNameIgnoreCase_shouldReturnCorrectDetails() {

        // Arrange: define name with different case to test ignoreCase.
        String nameToFind = "hans luhrberg";

        // Act: call repository method.
        List<Details> result = detailsRepository.findByNameIgnoreCase(nameToFind);

        // Assert: verify the correct Details object is returned.
        assertThat(result)
                .hasSize(1)
                .extracting(Details::getName)
                .containsExactly("Hans Luhrberg");
    }

    @Test
    @DisplayName("Returns empty list when name does not match ignoring case")
    void findByNameIgnoreCase_shouldReturnEmptyListWhenNoMatch() {

        // Arrange: define a name that does not exist.
        String nameToFind = "nonexistent name";

        // Act: call repository method.
        List<Details> result = detailsRepository.findByNameIgnoreCase(nameToFind);

        // Assert: verify that no Details object is returned.
        assertThat(result)
                .isEmpty();
    }
}
