package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Details;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

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



}
