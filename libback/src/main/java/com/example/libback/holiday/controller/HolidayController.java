package com.example.libback.holiday.controller;

import com.example.libback.holiday.dto.HolidayRequest;
import com.example.libback.holiday.dto.HolidayResponse;
import com.example.libback.holiday.service.HolidayService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping
    public List<HolidayResponse> findAll() {
        return holidayService.findAll();
    }

    @GetMapping("/{id}")
    public HolidayResponse findById(@PathVariable Long id) {
        return holidayService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponse create(
            @Valid @RequestBody HolidayRequest request) {
        return holidayService.create(request);
    }

    @PutMapping("/{id}")
    public HolidayResponse update(
            @PathVariable Long id,
            @Valid @RequestBody HolidayRequest request) {
        return holidayService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        holidayService.delete(id);
    }

    @GetMapping("/check")
    public boolean isHoliday(@RequestParam LocalDate date) {
        return holidayService.isHoliday(date);
    }
}
