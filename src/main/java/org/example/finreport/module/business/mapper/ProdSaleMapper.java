package org.example.finreport.module.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProdSaleMapper {

    @Select("<script>" +
            "SELECT s.sale_no AS saleNo, s.product_no AS productNo, p.product_name AS productName, " +
            "       s.customer_no AS customerNo, c.name AS customerName, s.amount, s.sale_date AS saleDate, " +
            "       s.status, b.branch_name AS branchName " +
            "FROM prod_sale s " +
            "LEFT JOIN product p ON s.product_no = p.product_code " +
            "LEFT JOIN customer c ON s.customer_no = c.customer_no " +
            "LEFT JOIN branch b ON s.branch_id = b.id " +
            "ORDER BY s.sale_date DESC " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<Map<String, Object>> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM prod_sale")
    long selectCount();
}
