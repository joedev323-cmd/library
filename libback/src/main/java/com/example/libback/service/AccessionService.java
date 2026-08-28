package com.example.libback.service;

import com.example.libback.model.Accession;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.ConditionStatus;
import com.example.libback.repository.AccessionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessionService {

    private final AccessionRepository accessionRepository;

    public Page<Accession> searchRegistry(
            String search,
            AvailabilityStatus status,
            ConditionStatus condition,
            int page,
            int size) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        /*
         * Prevent unnecessarily large page requests.
         */
        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "accessionId"
                )
        );

        return accessionRepository.searchRegistry(
                normalizeSearch(search),
                status,
                condition,
                pageable
        );
    }

    private String normalizeSearch(String search) {

        if (search == null) {
            return null;
        }

        String value = search.trim();

        return value.isEmpty()
                ? null
                : value;
    }
}
