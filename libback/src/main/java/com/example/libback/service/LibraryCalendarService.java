package com.example.libback.service;

import com.example.libback.holiday.repository.HolidayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LibraryCalendarService {

    private final HolidayRepository holidayRepository;

    public LibraryCalendarService(
            HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Transactional(readOnly = true)
    public boolean isClosed(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY
                || holidayRepository.existsByHolidayDate(date);
    }

    @Transactional(readOnly = true)
    public LocalDateTime moveToNextOpenDate(
            LocalDateTime dueDate) {

        LocalDateTime adjusted = dueDate;

        while (isClosed(adjusted.toLocalDate())) {
            adjusted = adjusted.plusDays(1);
        }

        return adjusted;
    }

    @Transactional(readOnly = true)
    public long countChargeableDays(
            LocalDateTime dueDate,
            LocalDateTime returnedAt) {

        if (dueDate == null || returnedAt == null
                || !returnedAt.isAfter(dueDate)) {
            return 0;
        }

        LocalDate date = dueDate.toLocalDate().plusDays(1);
        LocalDate lastDate = returnedAt.toLocalDate();
        long chargeableDays = 0;

        while (!date.isAfter(lastDate)) {
            if (!isClosed(date)) {
                chargeableDays++;
            }

            date = date.plusDays(1);
        }

        return chargeableDays;
    }
}
