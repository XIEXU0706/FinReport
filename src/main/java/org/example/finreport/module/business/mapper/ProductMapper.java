package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.business.entity.Product;
import java.util.List;

public interface ProductMapper {

    Product selectById(Long id);

    List<Product> selectList();

    int insert(Product product);

    int updateById(Product product);

    int deleteById(Long id);

    List<Product> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("productType") String productType,
                             @Param("riskLevel") String riskLevel,
                             @Param("status") String status,
                             @Param("productCode") String productCode,
                             @Param("productName") String productName);

    long selectCount(@Param("productType") String productType,
                     @Param("riskLevel") String riskLevel,
                     @Param("status") String status,
                     @Param("productCode") String productCode,
                     @Param("productName") String productName);

    String selectMaxProductCode();
}
