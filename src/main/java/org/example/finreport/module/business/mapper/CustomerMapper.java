package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.Customer;
import java.util.List;

public interface CustomerMapper {

    Customer selectById(Long id);

    List<Customer> selectList();

    int insert(Customer customer);

    int updateById(Customer customer);

    int deleteById(Long id);

    List<Customer> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("customerLevel") String customerLevel,
                              @Param("status") String status);

    long selectCount(@Param("name") String name,
                     @Param("phone") String phone,
                     @Param("customerLevel") String customerLevel,
                     @Param("status") String status);

    Long selectMaxCustomerNo();
}
