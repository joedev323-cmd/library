package com.example.libback.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @NotBlank
    @Size(min = 10, max = 13)
    @Column(length = 13, nullable = false, updatable = false)
    private String isbn;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String author;

    @Size(max = 255)
    private String publisher;

    @Size(max = 100)
    private String edition;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "item_categories",
            joinColumns = @JoinColumn(name = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Catergory> categories = new HashSet<>();

    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Accession> accessions = new HashSet<>();

    public Item() {
    }

    public Item(String isbn,
                String title,
                String author,
                String publisher,
                String edition,
                String description) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition = edition;
        this.description = description;
    }

    public void addAccession(Accession accession) {
        accessions.add(accession);
        accession.setItem(this);
    }

    public void removeAccession(Accession accession) {
        accessions.remove(accession);
        accession.setItem(null);
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

    public Set<Catergory> getCategories() {
        return categories;
    }

    public void setCategories(Set<Catergory> categories) {
        this.categories = categories;
    }

    public Set<Accession> getAccessions() {
        return accessions;
    }

    public void setAccessions(Set<Accession> accessions) {
        this.accessions = accessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(isbn, item.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}