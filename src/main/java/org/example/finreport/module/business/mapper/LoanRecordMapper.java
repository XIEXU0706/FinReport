package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.LoanRecord;
import java.time.LocalDate;
import java.util.List;

public interface LoanRecordMapper {

    LoanRecord selectById(Long id);

    List<LoanRecord> selectList();

    int insert(LoanRecord record);

    int updateById(LoanRecord record);

    int deleteById(Long id);

    List<LoanRecord> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                                @Param("status") String status,
                                @Param("dateFrom") LocalDate dateFrom,
                                @Param("dateTo") LocalDate dateTo,
                                @Param("loanNo") String loanNo,
                                @Param("customerNo") String customerNo,
                                @Param("repayMethod") String repayMethod);

    long selectCount(@Param("status") String status,
                     @Param("dateFrom") LocalDate dateFrom,
                     @Param("dateTo") LocalDate dateTo,
                     @Param("loanNo") String loanNo,
                     @Param("customerNo") String customerNo,
                     @Param("repayMethod") String repayMethod);

    String selectMaxLoanNo();
}
