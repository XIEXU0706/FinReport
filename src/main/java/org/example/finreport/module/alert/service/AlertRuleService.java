package org.example.finreport.module.alert.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.alert.entity.AlertRule;
import org.example.finreport.module.alert.mapper.AlertRuleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleMapper mapper;

    public AlertRule getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(AlertRule rule) {
        mapper.insert(rule);
    }

    public void updateById(AlertRule rule) {
        mapper.updateById(rule);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<AlertRule> list() {
        return mapper.selectList();
    }

    public List<AlertRule> listByStatus(String status) {
        return mapper.selectByStatus(status);
    }

    public PageResult<AlertRule> page(int page, int size) {
        PageParam pp = new PageParam(page, size);
        List<AlertRule> list = mapper.selectPage(pp.getOffset(), pp.getSize());
        long total = mapper.selectCount();
        return PageResult.of(list, total, page, size);
    }
}
