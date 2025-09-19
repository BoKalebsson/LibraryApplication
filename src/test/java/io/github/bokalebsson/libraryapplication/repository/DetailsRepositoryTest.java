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



}
