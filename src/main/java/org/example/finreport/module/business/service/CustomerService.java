package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.Customer;
import org.example.finreport.module.business.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper mapper;

    public Customer getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(Customer customer) {
        customer.setCustomerNo(generateCustomerNo());
        customer.setStatus("ACTIVE");
        customer.setOpenDate(LocalDateTime.now());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        mapper.insert(customer);
    }

    public void updateById(Customer customer) {
        customer.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(customer);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<Customer> list() {
        return mapper.selectList();
    }

    public PageResult<Customer> page(int page, int size, String name, String phone,
                                     String customerLevel, String status) {
        PageParam pp = new PageParam(page, size);
        List<Customer> list = mapper.selectPage(pp.getOffset(), pp.getSize(), name, phone, customerLevel, status);
        long total = mapper.selectCount(name, phone, customerLevel, status);
        return PageResult.of(list, total, page, size);
    }

    // 生成用户编号
    private String generateCustomerNo() {
        Long max = mapper.selectMaxCustomerNo();
        if (max == null) {
            return "C000001";
        }
        return "C" + String.format("%06d", max + 1);
    }
}
