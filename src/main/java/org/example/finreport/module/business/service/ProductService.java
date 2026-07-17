package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.Product;
import org.example.finreport.module.business.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper mapper;

    public Product getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(Product product) {
        // 自动生成产品编号
        String maxCode = mapper.selectMaxProductCode();
        int next = 1;
        if (maxCode != null && maxCode.startsWith("P")) {
            next = Integer.parseInt(maxCode.substring(1)) + 1;
        }
        product.setProductCode("P" + String.format("%06d", next));
        mapper.insert(product);
    }

    public void updateById(Product product) {
        mapper.updateById(product);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<Product> list() {
        return mapper.selectList();
    }

    public PageResult<Product> page(int page, int size, String productType, String riskLevel, String status,
                                     String productCode, String productName) {
        PageParam pp = new PageParam(page, size);
        List<Product> list = mapper.selectPage(pp.getOffset(), pp.getSize(), productType, riskLevel, status, productCode, productName);
        long total = mapper.selectCount(productType, riskLevel, status, productCode, productName);
        return PageResult.of(list, total, page, size);
    }
}
