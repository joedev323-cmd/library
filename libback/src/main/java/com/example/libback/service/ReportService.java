package com.example.libback.service;

import com.example.libback.dto.CategoryDemandDto;
import com.example.libback.dto.OverdueLoanDto;
import com.example.libback.dto.ReportMetricsDto;
import com.example.libback.dto.ReportsResponseDto;
import com.example.libback.model.Accession;
import com.example.libback.model.Loan;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.BookRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;

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
         * Main report.
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
         * Generates the main operational metrics.
         */
        public ReportMetricsDto generateSystemMetrics() {

                ReportMetricsDto metrics = new ReportMetricsDto();

                LocalDateTime now = LocalDateTime.now();

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

                long activeLoans = loanRepository.countByStatus(LoanStatus.ACTIVE);

                long overdueLoans = loanRepository.countByStatusAndDueDateBefore(
                                LoanStatus.ACTIVE,
                                now);

                long returnedLoans = loanRepository.countByStatus(LoanStatus.RETURNED);

                /*
                 * ============================
                 * CIRCULATION PERFORMANCE
                 * ============================
                 */

                double returnRate = totalLoans > 0
                                ? ((double) returnedLoans / totalLoans) * 100
                                : 0;

                double overdueRate = activeLoans > 0
                                ? ((double) overdueLoans / activeLoans) * 100
                                : 0;

                metrics.setReturnRate(
                                roundPercentage(returnRate));

                metrics.setOverdueRate(
                                roundPercentage(overdueRate));

                /*
                 * ============================
                 * INVENTORY
                 * ============================
                 */

                long availableCopies = accessionRepository.countByAvailabilityStatus(
                                AvailabilityStatus.AVAILABLE);

                metrics.setInventoryAvailable(totalCopies > 0);

                if (totalCopies > 0) {

                        double availablePercentage = ((double) availableCopies / totalCopies) * 100;

                        double activeLoanPercentage = ((double) activeLoans / totalCopies) * 100;

                        metrics.setAvailablePercentage(
                                        roundPercentage(availablePercentage));

                        metrics.setActiveLoanPercentage(
                                        roundPercentage(activeLoanPercentage));

                } else {

                        metrics.setAvailablePercentage(0);
                        metrics.setActiveLoanPercentage(0);
                }

                /*
                 * ============================
                 * FINANCIAL
                 * ============================
                 */

                LocalDateTime monthStart = LocalDate.now()
                                .withDayOfMonth(1)
                                .atStartOfDay();

                /*
                 * IMPORTANT:
                 *
                 * MTD means month start -> NOW,
                 * not month start -> next month.
                 */
                BigDecimal finesCollected = loanRepository.sumFinesCollectedBetween(
                                monthStart,
                                now);

                if (finesCollected == null) {
                        finesCollected = BigDecimal.ZERO;
                }

                BigDecimal outstandingFines = loanRepository.sumOutstandingFines();

                if (outstandingFines == null) {
                        outstandingFines = BigDecimal.ZERO;
                }

                long outstandingFineLoans = loanRepository.countLoansWithOutstandingFines();

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

                metrics.setFinesCollectedMtd(finesCollected);
                metrics.setOutstandingFines(outstandingFines);
                metrics.setOutstandingFineLoans(outstandingFineLoans);

                return metrics;
        }

        /**
         * Categories with the highest number of active loans.
         */
        public List<CategoryDemandDto> generatePopularCategories() {

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
         * Five oldest currently overdue loans.
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
         * Converts Loan into report DTO.
         */
        private OverdueLoanDto toOverdueLoanDto(Loan loan) {

                OverdueLoanDto dto = new OverdueLoanDto();

                /*
                 * LOAN
                 */

                dto.setLoanId(
                                loan.getLoanId());

                /*
                 * MEMBER
                 */

                dto.setMemberId(
                                loan.getMember().getMemberId());

                dto.setMemberName(
                                loan.getMember().getName());

                dto.setMemberEmail(
                                loan.getMember().getEmail());

                /*
                 * BOOK
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
                 * DATES / FINE
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

        private double roundPercentage(double value) {

                return Math.round(value * 10.0) / 10.0;
        }

        /**
         * Generates the physical inventory CSV.
         */
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