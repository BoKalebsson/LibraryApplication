package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.AppUser;
import io.github.bokalebsson.libraryapplication.entity.Details;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

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
