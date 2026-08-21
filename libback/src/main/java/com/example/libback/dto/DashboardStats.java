package com.example.libback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStats {
    private long totalBooks;
    private long booksOut;
    private long overdueCount;
    private long activeMembers;
}