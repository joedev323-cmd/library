package com.example.libback.dto;

public class BookSearchResultDto {
    private String title;
    private String author;
    private String isbn;
    private String shelfLocation;
    private long availableCount;
    private long totalCount;

    public BookSearchResultDto(String title, String author, String isbn, String shelfLocation, long availableCount,
            long totalCount) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.shelfLocation = shelfLocation;
        this.availableCount = availableCount;
        this.totalCount = totalCount;
    }

    // Getters and Setters
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public long getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(long availableCount) {
        this.availableCount = availableCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}