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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
}
