package org.example.finreport.module.report.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportAnalysisService {

    private final JdbcTemplate jdbcTemplate;

    public ReportAnalysisService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public byte[] generateExcel(String reportType, String periodStart, String periodEnd) {
        return switch (reportType) {
            case "DAILY" -> generateDailyReport(periodStart, periodEnd);
            case "MONTHLY" -> generateMonthlyReport(periodStart, periodEnd);
            case "LOAN_DAILY" -> generateLoanDailyReport(periodStart, periodEnd);
            case "LOAN_WEEKLY" -> generateLoanWeeklyReport(periodStart, periodEnd);
            case "LOAN_MONTHLY" -> generateLoanMonthlyReport(periodStart, periodEnd);
            default -> generateDailyReport(periodStart, periodEnd);
        };
    }

    private byte[] generateDailyReport(String periodStart, String periodEnd) {
        String sql = """
                SELECT DATE(trans_time) date_day,
                       COUNT(*) trans_count,
                       COALESCE(SUM(CASE WHEN status='SUCCESS' THEN amount ELSE 0 END),0) total_amount,
                       COALESCE(SUM(CASE WHEN status='FAILED' THEN 1 ELSE 0 END),0) fail_count
                FROM trans_log
                WHERE DATE(trans_time) BETWEEN ? AND ?
                GROUP BY DATE(trans_time)
                ORDER BY date_day""";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, periodStart, periodEnd);

        List<DailyReportRow> data = rows.stream().map(r -> new DailyReportRow(
                String.valueOf(r.get("date_day")),
                ((Number) r.get("trans_count")).longValue(),
                ((Number) r.get("total_amount")).doubleValue(),
                ((Number) r.get("fail_count")).longValue()
        )).toList();

        return writeExcel(data, DailyReportRow.class, "日报");
    }

    private byte[] generateMonthlyReport(String periodStart, String periodEnd) {
        String sql = """
                SELECT DATE_FORMAT(trans_time,'%Y-%m') month,
                       COUNT(*) trans_count,
                       COALESCE(SUM(CASE WHEN status='SUCCESS' THEN amount ELSE 0 END),0) amount
                FROM trans_log
                WHERE DATE(trans_time) BETWEEN ? AND ?
                GROUP BY DATE_FORMAT(trans_time,'%Y-%m')
                ORDER BY month""";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, periodStart, periodEnd);

        List<MonthlyReportRow> data = rows.stream().map(r -> new MonthlyReportRow(
                String.valueOf(r.get("month")),
                ((Number) r.get("trans_count")).longValue(),
                ((Number) r.get("amount")).doubleValue()
        )).toList();

        return writeExcel(data, MonthlyReportRow.class, "月报");
    }

    private byte[] generateLoanDailyReport(String periodStart, String periodEnd) {
        String sql = """
                SELECT DATE(loan_date) date_day,
                       COUNT(*) loan_count,
                       COALESCE(SUM(amount),0) total_amount,
                       COALESCE(SUM(CASE WHEN status='OVERDUE' THEN 1 ELSE 0 END),0) overdue_count
                FROM loan_record
                WHERE DATE(loan_date) BETWEEN ? AND ?
                GROUP BY DATE(loan_date)
                ORDER BY date_day""";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, periodStart, periodEnd);

        List<LoanDailyRow> data = rows.stream().map(r -> new LoanDailyRow(
                String.valueOf(r.get("date_day")),
                ((Number) r.get("loan_count")).longValue(),
                ((Number) r.get("total_amount")).doubleValue(),
                ((Number) r.get("overdue_count")).longValue()
        )).toList();

        return writeExcel(data, LoanDailyRow.class, "贷款日报");
    }

    private byte[] generateLoanWeeklyReport(String periodStart, String periodEnd) {
        String sql = """
                SELECT DATE_FORMAT(loan_date,'%Y-%u') week,
                       COUNT(*) loan_count,
                       COALESCE(SUM(amount),0) total_amount,
                       COALESCE(SUM(CASE WHEN status='OVERDUE' THEN 1 ELSE 0 END),0) overdue_count
                FROM loan_record
                WHERE DATE(loan_date) BETWEEN ? AND ?
                GROUP BY DATE_FORMAT(loan_date,'%Y-%u')
                ORDER BY week""";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, periodStart, periodEnd);

        List<LoanWeeklyRow> data = rows.stream().map(r -> new LoanWeeklyRow(
                String.valueOf(r.get("week")),
                ((Number) r.get("loan_count")).longValue(),
                ((Number) r.get("total_amount")).doubleValue(),
                ((Number) r.get("overdue_count")).longValue()
        )).toList();

        return writeExcel(data, LoanWeeklyRow.class, "贷款周报");
    }

    private byte[] generateLoanMonthlyReport(String periodStart, String periodEnd) {
        String sql = """
                SELECT DATE_FORMAT(loan_date,'%Y-%m') month,
                       COUNT(*) loan_count,
                       COALESCE(SUM(amount),0) total_amount,
                       COALESCE(SUM(CASE WHEN status='OVERDUE' THEN 1 ELSE 0 END),0) overdue_count
                FROM loan_record
                WHERE DATE(loan_date) BETWEEN ? AND ?
                GROUP BY DATE_FORMAT(loan_date,'%Y-%m')
                ORDER BY month""";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, periodStart, periodEnd);

        List<LoanMonthlyRow> data = rows.stream().map(r -> new LoanMonthlyRow(
                String.valueOf(r.get("month")),
                ((Number) r.get("loan_count")).longValue(),
                ((Number) r.get("total_amount")).doubleValue(),
                ((Number) r.get("overdue_count")).longValue()
        )).toList();

        return writeExcel(data, LoanMonthlyRow.class, "贷款月报");
    }

    private <T> byte[] writeExcel(List<T> data, Class<T> clazz, String sheetName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EasyExcel.write(bos, clazz).sheet(sheetName).doWrite(data);
        return bos.toByteArray();
    }

    @Data
    @AllArgsConstructor
    public static class DailyReportRow {
        @ExcelProperty("日期")
        private String date;
        @ExcelProperty("交易笔数")
        private Long transCount;
        @ExcelProperty("交易金额")
        private Double amount;
        @ExcelProperty("失败笔数")
        private Long failCount;
    }

    @Data
    @AllArgsConstructor
    public static class MonthlyReportRow {
        @ExcelProperty("月份")
        private String month;
        @ExcelProperty("交易笔数")
        private Long transCount;
        @ExcelProperty("交易金额")
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    public static class LoanDailyRow {
        @ExcelProperty("日期")
        private String date;
        @ExcelProperty("贷款笔数")
        private Long loanCount;
        @ExcelProperty("贷款金额")
        private Double amount;
        @ExcelProperty("逾期笔数")
        private Long overdueCount;
    }

    @Data
    @AllArgsConstructor
    public static class LoanWeeklyRow {
        @ExcelProperty("周")
        private String week;
        @ExcelProperty("贷款笔数")
        private Long loanCount;
        @ExcelProperty("贷款金额")
        private Double amount;
        @ExcelProperty("逾期笔数")
        private Long overdueCount;
    }

    @Data
    @AllArgsConstructor
    public static class LoanMonthlyRow {
        @ExcelProperty("月份")
        private String month;
        @ExcelProperty("贷款笔数")
        private Long loanCount;
        @ExcelProperty("贷款金额")
        private Double amount;
        @ExcelProperty("逾期笔数")
        private Long overdueCount;
    }
}
