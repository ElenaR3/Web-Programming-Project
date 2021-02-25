package com.example.project_web.model;

import com.example.project_web.model.enumerations.FacultyChoice;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookName;
    private int yearPublished;
    private String description;

    @Enumerated(EnumType.STRING)
    private FacultyChoice whichFaculty;   // za koj fakultet e knigata

    public Book(String bookName, int yearPublished, FacultyChoice whichFaculty, List<Author> authors, User user, String description) {
        this.bookName = bookName;
        this.yearPublished = yearPublished;
        this.whichFaculty = whichFaculty;
        this.authors = authors;
        this.user = user;
        this.description = description;
    }

    @ManyToMany(cascade=CascadeType.ALL)
    private List<Author> authors;

    // edna kniga od kolku users moze da bide objavena za prodazba
    @ManyToOne
    private User user;

    @ManyToOne
    private User userBuyer;

    @ManyToOne(cascade = CascadeType.ALL)
    private ShoppingCart shoppingCart;

    public Book() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public FacultyChoice getWhichFaculty() {
        return whichFaculty;
    }

    public void setWhichFaculty(FacultyChoice whichFaculty) {
        this.whichFaculty = whichFaculty;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUserBuyer() {
        return userBuyer;
    }

    public void setUserBuyer(User userBuyer) {
        this.userBuyer = userBuyer;
    }
}
