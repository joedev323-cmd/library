package com.example.libback.service;

import com.example.libback.dto.CategoryDemandDto;
import com.example.libback.dto.OverdueLoanDto;
import com.example.libback.dto.ReportMetricsDto;
import com.example.libback.dto.ReportsResponseDto;
import com.example.libback.model.Loan;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.MemberRepository;
import com.example.libback.repository.BookRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.model.Accession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

        private final LoanRepository loanRepository;
        private final AccessionRepository accessionRepository;
        private final MemberRepository borrowerRepository;
        private final BookRepository itemRepository;

        public ReportService(
                        LoanRepository loanRepository,
                        AccessionRepository accessionRepository,
                        MemberRepository borrowerRepository,
                        BookRepository itemRepository) {

                this.loanRepository = loanRepository;
                this.accessionRepository = accessionRepository;
                this.borrowerRepository = borrowerRepository;
                this.itemRepository = itemRepository;
        }

        /**
         * Main reports endpoint.
         *
         * Everything returned here is based on actual database data.
         */
        public ReportsResponseDto generateReport() {

                ReportMetricsDto summary = generateSystemMetrics();

                List<CategoryDemandDto> popularCategories = generatePopularCategories();

                List<OverdueLoanDto> overdueLoans = generateOverdueLoans();

                return new ReportsResponseDto(
                                summary,
                                popularCategories,
                                overdueLoans);
        }

        /**
         * Generates the main report metrics.
         */
        public ReportMetricsDto generateSystemMetrics() {

                ReportMetricsDto metrics = new ReportMetricsDto();

                /*
                 * ============================
                 * CATALOGUE
                 * ============================
                 */

                long totalBooks = itemRepository.count();

                long totalCopies = accessionRepository.count();

                /*
                 * ============================
                 * MEMBERS
                 * ============================
                 */

                long totalMembers = borrowerRepository.count();

                /*
                 * ============================
                 * CIRCULATION
                 * ============================
                 */

                long totalLoans = loanRepository.count();

                long activeLoans = loanRepository.countByStatus(
                                LoanStatus.ACTIVE);

                LocalDateTime now = LocalDateTime.now();

                long overdueLoans = loanRepository.countByStatusAndDueDateBefore(
                                LoanStatus.ACTIVE,
                                now);

                long returnedLoans = loanRepository.countByStatus(
                                LoanStatus.RETURNED);

                /*
                 * ============================
                 * INVENTORY
                 * ============================
                 */

                long availableCopies = accessionRepository.countByAvailabilityStatus(
                                AvailabilityStatus.AVAILABLE);

                /*
                 * ============================
                 * FINANCIAL
                 * ============================
                 */

                LocalDateTime monthStart = LocalDate.now()
                                .withDayOfMonth(1)
                                .atStartOfDay();

                LocalDateTime nextMonthStart = monthStart.plusMonths(1);

                BigDecimal finesCollected = loanRepository.sumFinesCollectedBetween(
                                monthStart,
                                nextMonthStart);

                if (finesCollected == null) {
                        finesCollected = BigDecimal.ZERO;
                }

                /*
                 * ============================
                 * POPULATE DTO
                 * ============================
                 */

                metrics.setTotalBooks(totalBooks);
                metrics.setTotalCopies(totalCopies);
                metrics.setTotalMembers(totalMembers);

                metrics.setTotalLoans(totalLoans);
                metrics.setActiveLoans(activeLoans);
                metrics.setOverdueLoans(overdueLoans);
                metrics.setReturnedLoans(returnedLoans);

                metrics.setAvailableCopies(availableCopies);

                metrics.setFinesCollectedMtd(
                                finesCollected);

                /*
                 * ============================
                 * INVENTORY PERCENTAGES
                 * ============================
                 *
                 * Zero copies is NOT considered
                 * 100% available.
                 *
                 * The frontend can display:
                 *
                 * "No inventory yet"
                 *
                 * instead of:
                 *
                 * "100% in library"
                 */

                if (totalCopies > 0) {

                        metrics.setInventoryAvailable(true);

                        double availablePercentage = ((double) availableCopies / totalCopies) * 100;

                        double activeLoanPercentage = ((double) activeLoans / totalCopies) * 100;

                        double overduePercentage = ((double) overdueLoans / totalCopies) * 100;

                        metrics.setAvailablePercentage(
                                        roundPercentage(availablePercentage));

                        metrics.setActiveLoanPercentage(
                                        roundPercentage(activeLoanPercentage));

                        metrics.setOverduePercentage(
                                        roundPercentage(overduePercentage));

                } else {

                        metrics.setInventoryAvailable(false);

                        /*
                         * These values are deliberately zero.
                         *
                         * React should check inventoryAvailable before
                         * displaying them as percentages.
                         */

                        metrics.setAvailablePercentage(0);
                        metrics.setActiveLoanPercentage(0);
                        metrics.setOverduePercentage(0);
                }

                /*
                 * ============================
                 * FULFILLMENT RATE
                 * ============================
                 *
                 * Removed for now.
                 *
                 * We do not have a clearly defined business rule
                 * for "fulfillment rate", so we should NOT invent
                 * a number such as 94.2%.
                 */

                return metrics;
        }

        /**
         * Returns categories with the highest number
         * of currently active loans.
         *
         * This is based on real circulation activity.
         */
        private List<CategoryDemandDto> generatePopularCategories() {

                return loanRepository
                                .findPopularCategories()
                                .stream()
                                .limit(5)
                                .map(row -> {

                                        String category = (String) row[0];

                                        long activeLoans = ((Number) row[1]).longValue();

                                        return new CategoryDemandDto(
                                                        category,
                                                        activeLoans);
                                })
                                .toList();
        }

        /**
         * Returns the five oldest currently overdue loans.
         */
        private List<OverdueLoanDto> generateOverdueLoans() {

                LocalDateTime now = LocalDateTime.now();

                List<Loan> loans = loanRepository
                                .findTop5ByStatusAndDueDateBeforeOrderByDueDateAsc(
                                                LoanStatus.ACTIVE,
                                                now);

                return loans
                                .stream()
                                .map(this::toOverdueLoanDto)
                                .toList();
        }

        /**
         * Converts Loan entity into an API-safe DTO.
         */
        private OverdueLoanDto toOverdueLoanDto(
                        Loan loan) {

                OverdueLoanDto dto = new OverdueLoanDto();

                dto.setLoanId(
                                loan.getLoanId());

                /*
                 * ============================
                 * BORROWER
                 * ============================
                 */

                dto.setMemberId(
                                loan.getMember()
                                                .getMemberId());

                dto.setMemberName(
                                loan.getMember()
                                                .getName());

                dto.setMemberEmail(
                                loan.getMember()
                                                .getEmail());

                /*
                 * ============================
                 * BOOK
                 * ============================
                 */

                dto.setIsbn(
                                loan.getAccession()
                                                .getBook()
                                                .getIsbn());

                dto.setTitle(
                                loan.getAccession()
                                                .getBook()
                                                .getTitle());

                dto.setBarcode(
                                loan.getAccession()
                                                .getBarcode());

                /*
                 * ============================
                 * LOAN
                 * ============================
                 */

                dto.setDueDate(
                                loan.getDueDate());

                dto.setFineAccrued(
                                loan.getFineAccrued());

                long daysOverdue = ChronoUnit.DAYS.between(
                                loan.getDueDate(),
                                LocalDateTime.now());

                dto.setDaysOverdue(
                                Math.max(daysOverdue, 0));

                return dto;
        }

        private double roundPercentage(
                        double value) {

                return Math.round(value * 10.0) / 10.0;
        }

        public String generateInventoryCsv() {

                List<Accession> allCopies = accessionRepository.findAll();

                StringBuilder csv = new StringBuilder();

                csv.append(
                                "Accession ID,Barcode,ISBN,Title,Shelf Location,Availability Status");

                csv.append("\n");

                for (Accession copy : allCopies) {

                        String isbn = "";
                        String title = "";

                        if (copy.getBook() != null) {
                                isbn = copy.getBook().getIsbn();
                                title = copy.getBook().getTitle();
                        }

                        String availability = "";

                        if (copy.getAvailabilityStatus() != null) {
                                availability = copy.getAvailabilityStatus().name();
                        }

                        csv.append("\"")
                                        .append(escapeCsv(copy.getAccessionId()))
                                        .append("\",\"")
                                        .append(escapeCsv(copy.getBarcode()))
                                        .append("\",\"")
                                        .append(escapeCsv(isbn))
                                        .append("\",\"")
                                        .append(escapeCsv(title))
                                        .append("\",\"")
                                        .append(escapeCsv(copy.getShelfLocation()))
                                        .append("\",\"")
                                        .append(escapeCsv(availability))
                                        .append("\"")
                                        .append("\n");
                }

                return csv.toString();
        }

        private String escapeCsv(String value) {

                if (value == null) {
                        return "";
                }

                return value.replace("\"", "\"\"");
        }

        public List<OverdueLoanDto> getOverdueLoans() {
                return generateOverdueLoans();
        }

}
