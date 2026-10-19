package com.example.libback.holiday.dto;

import com.example.libback.holiday.entity.Holiday;

import java.time.LocalDate;

public record HolidayResponse(
        Long id,
        LocalDate holidayDate,
        String name,
        String description
) {
    public static HolidayResponse from(Holiday holiday) {
        return new HolidayResponse(
                holiday.getId(),
                holiday.getHolidayDate(),
                holiday.getName(),
                holiday.getDescription()
        );
    }
}
