package org.example.finreport.module.system.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.system.entity.SysOperLog;
import org.example.finreport.module.system.mapper.SysOperLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysOperLogService {

    private final SysOperLogMapper mapper;

    public SysOperLog getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(SysOperLog log) {
        mapper.insert(log);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<SysOperLog> list() {
        return mapper.selectList();
    }

    public PageResult<SysOperLog> page(int page, int size, String operName, String businessType) {
        PageParam pp = new PageParam(page, size);
        List<SysOperLog> list = mapper.selectPage(pp.getOffset(), pp.getSize(), operName, businessType);
        long total = mapper.selectCount(operName, businessType);
        return PageResult.of(list, total, page, size);
    }
}
