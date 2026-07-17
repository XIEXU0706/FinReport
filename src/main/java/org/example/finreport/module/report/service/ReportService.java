package org.example.finreport.module.report.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.report.entity.Report;
import org.example.finreport.module.report.mapper.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper mapper;

    public Report getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(Report report) {
        mapper.insert(report);
    }

    public void updateById(Report report) {
        mapper.updateById(report);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<Report> list() {
        return mapper.selectList();
    }

    public PageResult<Report> page(int page, int size, String title, String reportType, String status) {
        PageParam pp = new PageParam(page, size);
        List<Report> list = mapper.selectPage(pp.getOffset(), pp.getSize(), title, reportType, status);
        long total = mapper.selectCount(title, reportType, status);
        return PageResult.of(list, total, page, size);
    }

    public List<Report> list(String reportType) {
        return mapper.selectListByType(reportType);
    }
}
