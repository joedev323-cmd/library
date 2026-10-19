package com.example.libback.holiday.repository;

import com.example.libback.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository
        extends JpaRepository<Holiday, Long> {

    boolean existsByHolidayDate(LocalDate holidayDate);

    boolean existsByHolidayDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Holiday> findAllByOrderByHolidayDateAsc();
}
