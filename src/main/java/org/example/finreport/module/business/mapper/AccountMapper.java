package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.Account;
import java.util.List;

public interface AccountMapper {

    Account selectById(Long id);

    List<Account> selectList();

    int insert(Account account);

    int updateById(Account account);

    int deleteById(Long id);

    List<Account> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("accountNo") String accountNo,
                             @Param("customerNo") String customerNo,
                             @Param("accountType") String accountType,
                             @Param("currency") String currency,
                             @Param("status") String status);

    long selectCount(@Param("accountNo") String accountNo,
                     @Param("customerNo") String customerNo,
                     @Param("accountType") String accountType,
                     @Param("currency") String currency,
                     @Param("status") String status);

    Long selectMaxAccountNo();
}
