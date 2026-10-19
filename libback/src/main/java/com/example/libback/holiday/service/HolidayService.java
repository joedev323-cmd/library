
package com.example.libback.holiday.service;

import com.example.libback.holiday.dto.HolidayRequest;
import com.example.libback.holiday.dto.HolidayResponse;
import com.example.libback.holiday.entity.Holiday;
import com.example.libback.holiday.repository.HolidayRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public HolidayResponse create(HolidayRequest request) {
        if (holidayRepository.existsByHolidayDate(
                request.holidayDate())) {
            throw new IllegalArgumentException(
                    "A holiday already exists on this date"
            );
        }

        Holiday holiday = new Holiday(
                request.holidayDate(),
                request.name().trim(),
                normalizeDescription(request.description())
        );

        return HolidayResponse.from(
                holidayRepository.save(holiday)
        );
    }

    @Transactional(readOnly = true)
    public List<HolidayResponse> findAll() {
        return holidayRepository.findAllByOrderByHolidayDateAsc()
                .stream()
                .map(HolidayResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HolidayResponse findById(Long id) {
        return HolidayResponse.from(getHoliday(id));
    }

    public HolidayResponse update(
            Long id,
            HolidayRequest request) {

        Holiday holiday = getHoliday(id);

        boolean dateChanged = !holiday.getHolidayDate()
                .equals(request.holidayDate());

        if (dateChanged && holidayRepository
                .existsByHolidayDate(request.holidayDate())) {
            throw new IllegalArgumentException(
                    "A holiday already exists on this date"
            );
        }

        holiday.setHolidayDate(request.holidayDate());
        holiday.setName(request.name().trim());
        holiday.setDescription(
                normalizeDescription(request.description())
        );

        return HolidayResponse.from(
                holidayRepository.save(holiday)
        );
    }

    public void delete(Long id) {
        Holiday holiday = getHoliday(id);
        holidayRepository.delete(holiday);
    }

    @Transactional(readOnly = true)
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date);
    }

    private Holiday getHoliday(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new HolidayNotFoundException(id));
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}
