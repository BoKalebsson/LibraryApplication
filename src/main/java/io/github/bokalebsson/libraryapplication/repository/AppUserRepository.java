package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByRegDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<AppUser> findByUserDetails_Id(Integer detailsId);

    Optional<AppUser> findByUserDetails_EmailIgnoreCase(String email);

}
