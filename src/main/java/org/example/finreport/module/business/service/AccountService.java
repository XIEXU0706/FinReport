package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.Account;
import org.example.finreport.module.business.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.jar.JarOutputStream;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper mapper;

    public Account getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(Account account) {
        account.setStatus("ACTIVE");
        account.setAccountNo(generateAccountNo());
        account.setCreatedAt(LocalDateTime.now());
        mapper.insert(account);
    }

    public void updateById(Account account) {
        mapper.updateById(account);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<Account> list() {
        return mapper.selectList();
    }

    public PageResult<Account> page(int page, int size, String accountNo, String customerNo,
                                     String accountType, String currency, String status) {
        PageParam pp = new PageParam(page, size);
        List<Account> list = mapper.selectPage(pp.getOffset(), pp.getSize(), accountNo, customerNo, accountType, currency, status);
        long total = mapper.selectCount(accountNo, customerNo, accountType, currency, status);
        return PageResult.of(list, total, page, size);
    }

    private String generateAccountNo() {
        Long max = mapper.selectMaxAccountNo();
        if (max == null) {
            return "A000001";
        }
        return "A" + String.format("%06d", max + 1);
    }
}
