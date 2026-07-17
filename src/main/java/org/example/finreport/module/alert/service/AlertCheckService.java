package org.example.finreport.module.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finreport.module.alert.entity.AlertLog;
import org.example.finreport.module.alert.entity.AlertRule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertCheckService {

    private final AlertRuleService alertRuleService;
    private final AlertLogService alertLogService;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 30 18 * * ?")
    public void checkAllRules() {
        List<AlertRule> rules = alertRuleService.listByStatus("ENABLED");
        for (AlertRule rule : rules) {
            try {
                checkRule(rule);
            } catch (Exception e) {
                log.error("预警规则检查异常: {} - {}", rule.getId(), rule.getRuleName(), e);
            }
        }
    }

    private void checkRule(AlertRule rule) {
        double actual = fetchMetric(rule.getMetric());
        double threshold = Double.parseDouble(rule.getThreshold());
        boolean triggered = compare(actual, threshold, rule.getOperator());

        if (triggered) {
            AlertLog alertLog = new AlertLog();
            alertLog.setRuleId(rule.getId());
            alertLog.setRuleName(rule.getRuleName());
            alertLog.setMetric(rule.getMetric());
            alertLog.setActualValue(actual);
            alertLog.setThreshold(rule.getThreshold());
            alertLog.setSeverity(rule.getSeverity());
            alertLog.setContent(String.format("【%s】%.2f %s %.2f",
                    rule.getRuleName(), actual, rule.getOperator(), threshold));
            alertLog.setStatus("NEW");
            alertLogService.save(alertLog);
            log.info("预警触发: {} (actual={}, threshold={})", rule.getRuleName(), actual, threshold);
        }
    }

    private double fetchMetric(String metric) {
        String sql = switch (metric) {
            case "TRADE_AMOUNT_DAILY" ->
                "SELECT COALESCE(SUM(amount),0) FROM trans_log WHERE DATE(trans_time)=CURDATE() AND status='SUCCESS'";
            case "LOAN_OVERDUE_RATE" ->
                "SELECT ROUND(SUM(CASE WHEN status='OVERDUE' THEN 1 ELSE 0 END)*100.0/NULLIF(COUNT(*),0),2) FROM loan_record WHERE status IN ('NORMAL','OVERDUE')";
            case "NEW_CUSTOMERS_DAILY" ->
                "SELECT COUNT(*) FROM customer WHERE DATE(open_date)=CURDATE()";
            case "LARGE_TRADE_COUNT" ->
                "SELECT COUNT(*) FROM trans_log WHERE DATE(trans_time)=CURDATE() AND amount>100000 AND status='SUCCESS'";
            case "LOAN_AMOUNT_DAILY" ->
                "SELECT COALESCE(SUM(amount),0) FROM loan_record WHERE DATE(loan_date)=CURDATE()";
            default -> throw new IllegalArgumentException("Unknown metric: " + metric);
        };
        Map<String, Object> row = jdbcTemplate.queryForMap(sql);
        return row.values().stream().findFirst().map(v -> ((Number) v).doubleValue()).orElse(0.0);
    }

    private boolean compare(double actual, double threshold, String operator) {
        return switch (operator) {
            case ">" -> actual > threshold;
            case ">=" -> actual >= threshold;
            case "<" -> actual < threshold;
            case "<=" -> actual <= threshold;
            default -> false;
        };
    }
}
