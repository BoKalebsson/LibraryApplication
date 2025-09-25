package io.github.bokalebsson.libraryapplication.repository;

import io.github.bokalebsson.libraryapplication.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByIsbnIgnoreCase(String isbn);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByMaxLoanDaysLessThan(int maxLoanDays);

}
