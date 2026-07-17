package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.LoanRecord;
import org.example.finreport.module.business.mapper.LoanRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanRecordService {

    private final LoanRecordMapper mapper;

    public LoanRecord getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(LoanRecord record) {
        // 自动生成贷款编号
        String maxNo = mapper.selectMaxLoanNo();
        int next = 1;
        if (maxNo != null && maxNo.startsWith("L")) {
            next = Integer.parseInt(maxNo.substring(1)) + 1;
        }
        record.setLoanNo("L" + String.format("%06d", next));

        // 计算到期利息（单利）：金额 × 利率% × 期限(月)/12
        BigDecimal rate = record.getInterestRate();
        if (rate != null && record.getAmount() != null && record.getTermMonths() != null) {
            BigDecimal interest = record.getAmount()
                    .multiply(rate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(record.getTermMonths()))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            record.setMaturityInterest(interest);
            record.setMaturityTotal(record.getAmount().add(interest));
        }

        // 默认状态
        if (record.getStatus() == null) {
            record.setStatus("NORMAL");
        }

        mapper.insert(record);
    }

    public void updateById(LoanRecord record) {
        mapper.updateById(record);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<LoanRecord> list() {
        return mapper.selectList();
    }

    public PageResult<LoanRecord> page(int page, int size, String status, LocalDate dateFrom, LocalDate dateTo,
                                        String loanNo, String customerNo, String repayMethod) {
        PageParam pp = new PageParam(page, size);
        List<LoanRecord> list = mapper.selectPage(pp.getOffset(), pp.getSize(), status, dateFrom, dateTo, loanNo, customerNo, repayMethod);
        long total = mapper.selectCount(status, dateFrom, dateTo, loanNo, customerNo, repayMethod);
        return PageResult.of(list, total, page, size);
    }
}
