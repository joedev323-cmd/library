
package com.example.libback.holiday.service;

public class HolidayNotFoundException extends RuntimeException {

    public HolidayNotFoundException(Long id) {
        super("Holiday not found with ID: " + id);
    }
}
