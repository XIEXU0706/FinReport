package org.example.finreport.module.business.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.business.entity.TransLog;
import org.example.finreport.module.business.mapper.TransLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransLogService {

    private final TransLogMapper mapper;

    public TransLog getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(TransLog log) {
        mapper.insert(log);
    }

    public List<TransLog> list() {
        return mapper.selectList();
    }

    public PageResult<TransLog> page(int page, int size, String transType, String channel,
                                     LocalDateTime dateFrom, LocalDateTime dateTo,
                                     String customerNo, String accountNo) {
        PageParam pp = new PageParam(page, size);
        List<TransLog> list = mapper.selectPage(pp.getOffset(), pp.getSize(), transType, channel, dateFrom, dateTo, customerNo, accountNo);
        long total = mapper.selectCount(transType, channel, dateFrom, dateTo, customerNo, accountNo);
        return PageResult.of(list, total, page, size);
    }
}
