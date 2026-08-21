package com.example.libback.model;

import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.ConditionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "accessions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"isbn", "copyNumber"}),
                @UniqueConstraint(columnNames = "barcode")
        }
)
public class Accession {

    @Id
    @NotBlank
    @Column(length = 50, nullable = false, updatable = false)
    private String accessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isbn", nullable = false)
    private Item item;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer copyNumber;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String barcode;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String shelfLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConditionStatus conditionStatus = ConditionStatus.GOOD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @NotNull
    @Column(nullable = false)
    private LocalDate purchaseDate;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal replacementCost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Accession() {
    }

    public Accession(String accessionId,
                     Item item,
                     Integer copyNumber,
                     String barcode,
                     String shelfLocation,
                     LocalDate purchaseDate,
                     BigDecimal replacementCost) {

        this.accessionId = accessionId;
        this.item = item;
        this.copyNumber = copyNumber;
        this.barcode = barcode;
        this.shelfLocation = shelfLocation;
        this.purchaseDate = purchaseDate;
        this.replacementCost = replacementCost;
    }

    public boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public ConditionStatus getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ConditionStatus conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Accession that)) return false;
        return Objects.equals(accessionId, that.accessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessionId);
    }
}