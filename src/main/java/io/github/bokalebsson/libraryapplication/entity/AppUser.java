package io.github.bokalebsson.libraryapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate regDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", nullable = false, unique = true)
    private Details userDetails;

    @OneToMany(mappedBy = "borrower")
    private List<BookLoan> bookLoans;

    public AppUser(String username, String password, LocalDate regDate, Details userDetails) {
        this.username = username;
        this.password = password;
        this.regDate = regDate;
        this.userDetails = userDetails;
    }

    public void addBookLoan(BookLoan loan){

        Book book = loan.getBook();

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for loan: " + book.getTitle());
        }

        if(bookLoans==null){
            bookLoans = new ArrayList<>();
        }
        bookLoans.add(loan);
        loan.setBorrower(this);

        book.setAvailable(false);
    }

    public void removeBookLoan(BookLoan loan){
        if(bookLoans!=null){
            bookLoans.remove(loan);
            loan.setBorrower(null);
        }

        Book book = loan.getBook();
        if (book != null) {
            book.setAvailable(true);
        }
    }
}
