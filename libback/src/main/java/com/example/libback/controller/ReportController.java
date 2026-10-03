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

    @GetMapping("/reports")
    public String showReportsPage(Model model) {

        var report = reportService.generateReport();

        model.addAttribute(
                "metrics",
                report.getSummary()
        );

        model.addAttribute(
                "popularCategories",
                report.getPopularCategories()
        );

        model.addAttribute(
                "overdueLoans",
                report.getOverdueLoans()
        );

        model.addAttribute(
                "pageTitle",
                "Management Reports"
        );

        return "reports/index";
    }

    @GetMapping("/reports/export-inventory")
    public void exportInventoryManifest(
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=inventory_audit_manifest.csv"
        );

        String csv =
                reportService.generateInventoryCsv();

        PrintWriter writer =
                response.getWriter();

        writer.write(csv);
        writer.flush();
    }
}