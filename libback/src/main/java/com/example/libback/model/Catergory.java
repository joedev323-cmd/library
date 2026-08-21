package com.example.libback.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Catergory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Catergory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Catergory> subCategories;

    @Column(nullable = false)
    private Integer loanPeriodDays = 14;

    @Column(nullable = false)
    private Integer maxRenewals = 2;

    public Catergory() {}

    public Catergory(Long categoryId, String name, Catergory parent, List<Catergory> subCategories, Integer loanPeriodDays, Integer maxRenewals) {
        this.categoryId = categoryId;
        this.name = name;
        this.parent = parent;
        this.subCategories = subCategories;
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

    public Catergory getParent() {
        return parent;
    }

    public void setParent(Catergory parent) {
        this.parent = parent;
    }

    public List<Catergory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Catergory> subCategories) {
        this.subCategories = subCategories;
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

}