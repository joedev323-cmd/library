package com.example.libback.controller;

import com.example.libback.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Legacy Thymeleaf reports page.
     *
     * React uses /api/reports instead.
     */
    @GetMapping("/reports")
    public String showReportsPage(Model model) {

        model.addAttribute(
                "metrics",
                reportService.generateSystemMetrics()
        );

        model.addAttribute(
                "overdueLoans",
                reportService.getOverdueLoans()
        );
        
        return "reports";
    }

    /**
     * Legacy Thymeleaf inventory export.
     *
     * The actual CSV generation is handled by ReportService.
     */
    @GetMapping("/reports/export-inventory")
    public void exportInventoryManifest(
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=inventory_audit_manifest.csv"
        );

        String csv = reportService.generateInventoryCsv();

        PrintWriter writer = response.getWriter();
        writer.write(csv);
        writer.flush();
    }
}
