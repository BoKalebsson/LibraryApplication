package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Author;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    List<Author> findByFirstName(String firstName);

    List<Author> findByLastName(String lastName);

    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Author SET firstName = :firstName, lastName = :lastName WHERE id = :id")
    void updateNameById(int id, String firstName, String lastName);

    @Modifying
    @Transactional
    @Query("DELETE FROM Author WHERE id = :id")
    void deleteById(int id);

}
