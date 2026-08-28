package com.example.libback.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "isbn"
                )
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(
            nullable = false,
            unique = true,
            length = 20
    )
    private String isbn;

    @Column(
            nullable = false,
            length = 255
    )
    private String title;

    @Column(
            nullable = false,
            length = 255
    )
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(length = 100)
    private String edition;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    /*
     * This is a read/navigation relationship.
     *
     * Accession owns the relationship.
     *
     * There is intentionally NO CascadeType.ALL.
     */
    @OneToMany(
            mappedBy = "book",
            fetch = FetchType.LAZY
    )
    private Set<Accession> accessions =
            new HashSet<>();

    public Book() {
    }

    public Book(
            String isbn,
            String title,
            String author,
            String publisher,
            String edition,
            String description,
            Category category
    ) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition = edition;
        this.description = description;
        this.category = category;
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Accession> getAccessions() {
        return accessions;
    }

    public void setAccessions(
            Set<Accession> accessions
    ) {
        this.accessions = accessions;
    }
}