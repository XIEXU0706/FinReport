package org.example.finreport.module.alert.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.alert.entity.AlertLog;
import org.example.finreport.module.alert.mapper.AlertLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertLogService {

    private final AlertLogMapper mapper;

    public AlertLog getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(AlertLog log) {
        mapper.insert(log);
    }

    public void updateById(AlertLog log) {
        mapper.updateById(log);
    }

    public List<AlertLog> list() {
        return mapper.selectList();
    }

    public PageResult<AlertLog> page(int page, int size, String severity, String status) {
        PageParam pp = new PageParam(page, size);
        List<AlertLog> list = mapper.selectPage(pp.getOffset(), pp.getSize(), severity, status);
        long total = mapper.selectCount(severity, status);
        return PageResult.of(list, total, page, size);
    }

    public List<AlertLog> latest(int limit) {
        return mapper.selectLatest(limit);
    }

    public long countByStatus(String status) {
        return mapper.selectCountByStatus(status);
    }
}
