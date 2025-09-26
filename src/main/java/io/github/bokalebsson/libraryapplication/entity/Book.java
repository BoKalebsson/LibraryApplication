package io.github.bokalebsson.libraryapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int maxLoanDays;

    @ManyToMany(mappedBy = "books")
    private Set<Author> authors;

    @Column(nullable = false)
    private boolean available = true;

    public void addAuthor(Author author) {
        if (authors == null) {
            authors = new HashSet<>();
        }
        authors.add(author);

        if (author.getBooks() == null) {
            author.setBooks(new HashSet<>());
        }
        if (!author.getBooks().contains(this)) {
            author.getBooks().add(this);
        }
    }

    public void removeAuthor(Author author) {
        if (authors != null) {
            authors.remove(author);
            if (author.getBooks() != null) {
                author.getBooks().remove(this);
            }
        }
    }

}
