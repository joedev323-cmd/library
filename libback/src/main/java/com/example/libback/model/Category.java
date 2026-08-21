package com.example.libback.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
    }
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer loanPeriodDays = 14;

    @Column(nullable = false)
    private Integer maxRenewals = 2;

    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();

    public Category() {
    }

    public Category(
            String name,
            Integer loanPeriodDays,
            Integer maxRenewals
    ) {
        this.name = name;
        this.loanPeriodDays = loanPeriodDays;
        this.maxRenewals = maxRenewals;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(Integer loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    public Integer getMaxRenewals() {
        return maxRenewals;
    }

    public void setMaxRenewals(Integer maxRenewals) {
        this.maxRenewals = maxRenewals;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
