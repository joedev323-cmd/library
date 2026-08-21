package com.example.libback.controller;

import com.example.libback.dto.ReportsResponseDto;
import com.example.libback.model.Accession;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    private final ReportService reportService;
    private final AccessionRepository accessionRepository;

    public ReportApiController(
            ReportService reportService,
            AccessionRepository accessionRepository) {

        this.reportService = reportService;
        this.accessionRepository = accessionRepository;
    }

    @GetMapping
    public ResponseEntity<ReportsResponseDto> getReports() {

        ReportsResponseDto report =
                reportService.generateReport();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/export-inventory")
    public void exportInventory(
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=inventory_audit_manifest.csv"
        );

        List<Accession> allCopies =
                accessionRepository.findAll();

        PrintWriter writer =
                response.getWriter();

        writer.println(
                "Accession ID,Barcode,ISBN,Title,Shelf Location,Availability Status"
        );

        for (Accession copy : allCopies) {

            writer.printf(
                    "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",

                    csv(copy.getAccessionId()),

                    csv(copy.getBarcode()),

                    csv(copy.getBook().getIsbn()),

                    csv(copy.getBook().getTitle()),

                    csv(copy.getShelfLocation()),

                    csv(
                            copy.getAvailabilityStatus()
                                    .name()
                    )
            );
        }

        writer.flush();
    }

    private String csv(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\"", "\"\"");
    }
}
