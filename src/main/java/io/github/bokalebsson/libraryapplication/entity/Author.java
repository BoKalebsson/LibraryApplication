package io.github.bokalebsson.libraryapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books;

    public void addBook(Book book) {
        if (books == null) {
            books = new HashSet<>();
        }
        books.add(book);

        if (book.getAuthors() == null) {
            book.setAuthors(new HashSet<>());
        }
        if (!book.getAuthors().contains(this)) {
            book.getAuthors().add(this);
        }
    }

    public void removeBook(Book book) {
        if (books != null) {
            books.remove(book);
            if (book.getAuthors() != null) {
                book.getAuthors().remove(this);
            }
        }
    }

}
