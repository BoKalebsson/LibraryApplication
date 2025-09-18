package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Details;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailsRepository extends JpaRepository<Details, Integer> {

    Optional<Details> findByEmail(String email);

    List<Details> findByNameContaining(String mail);

    List<Details> findByNameIgnoreCase(String name);

}
