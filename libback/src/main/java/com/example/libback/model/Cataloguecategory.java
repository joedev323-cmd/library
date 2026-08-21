package com.example.libback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="catalogue_category")
public class Cataloguecategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long catalogue_id;
    private long category_id;
    public Cataloguecategory() {
    }
    public Cataloguecategory(long catalogue_id, long category_id) {
        this.catalogue_id = catalogue_id;
        this.category_id = category_id;
    }
    public long getCatalogue_id() {
        return catalogue_id;
    }
    public void setCatalogue_id(long catalogue_id) {
        this.catalogue_id = catalogue_id;
    }
    public long getCategory_id() {
        return category_id;
    }
    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }
    
}
