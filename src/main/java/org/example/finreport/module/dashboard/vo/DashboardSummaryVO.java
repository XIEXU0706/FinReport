package org.example.finreport.module.dashboard.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DashboardSummaryVO implements Serializable {
    private BigDecimal todayTradeAmount;
    private BigDecimal yesterdayTradeAmount;
    private BigDecimal todayLoanAmount;
    private BigDecimal yesterdayLoanAmount;
    private long todayNewCustomers;
    private long yesterdayNewCustomers;
    private BigDecimal todayRepaymentAmount;
    private BigDecimal yesterdayRepaymentAmount;
    private double tradeGrowthRate;
    private double loanGrowthRate;
    private double customerGrowthRate;
    private double repaymentGrowthRate;
}
