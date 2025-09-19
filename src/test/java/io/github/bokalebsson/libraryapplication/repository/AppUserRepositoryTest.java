package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.AppUser;
import io.github.bokalebsson.libraryapplication.entity.Details;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    @DisplayName("Save appUser")
    void saveAppUser() {

        // Arrange: create an appUser instance
        Details details = new Details("email@example.com", "John Doe", LocalDate.of(1990,1,1));
        AppUser appUser = new AppUser("johndoe", "secret", LocalDate.now(), details);

        // Act: save the appUser to the in-memory DB
        AppUser savedAppUser = appUserRepository.save(appUser);

        // Assert: verify that the appUser was assigned an ID (i.e., persisted)
        assertThat(savedAppUser.getId()).isNotNull();
    }





}
