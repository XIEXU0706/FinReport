package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.mapper.ProdSaleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProdSaleService {

    private final ProdSaleMapper prodSaleMapper;

    public PageResult<Map<String, Object>> page(int page, int size) {
        PageParam pp = new PageParam(page, size);
        List<Map<String, Object>> list = prodSaleMapper.selectPage(pp.getOffset(), pp.getSize());
        long total = prodSaleMapper.selectCount();
        return PageResult.of(list, total, page, size);
    }
}
