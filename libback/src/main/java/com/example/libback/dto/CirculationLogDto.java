package com.example.libback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CirculationLogDto {
    private String bookTitle;
    private String memberName;
    private String memberId;
    private String action; // e.g., "Issued" or "Returned"
    private LocalDate dueDate;
    private String status; // "On Loan", "In Library", or "Overdue"

    // Quick helper logic for Tailwind conditional classes
    public boolean getIsOverdue() {
        return "Overdue".equalsIgnoreCase(this.status) ||
                (dueDate != null && dueDate.isBefore(LocalDate.now()) && !"In Library".equalsIgnoreCase(this.status));
    }
}