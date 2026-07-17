package org.example.finreport.module.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finreport.common.aop.Log;
import org.example.finreport.common.oss.OssService;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.report.entity.Report;
import org.example.finreport.module.report.service.ReportAnalysisService;
import org.example.finreport.module.report.service.ReportService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "报表中心")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportAnalysisService analysisService;
    private final OssService ossService;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询报表")
    public Result<PageResult<Report>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String reportType,
                                           @RequestParam(required = false) String status) {
        return Result.ok(reportService.page(page, size, title, reportType, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取报表详情")
    public Result<Report> get(@PathVariable Long id) {
        return Result.ok(reportService.getById(id));
    }

    @PostMapping("/generate")
    @Operation(summary = "生成报表（支持异步/同步回退）")
    @Log(title = "生成报表", businessType = "INSERT")
    public Result<Void> generate(@RequestBody Report report) {
        report.setStatus("GENERATING");
        report.setGeneratedTime(LocalDateTime.now());
        reportService.save(report);

        if (rabbitTemplate != null) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("reportId", report.getId());
            msg.put("reportType", report.getReportType());
            msg.put("periodStart", report.getPeriodStart());
            msg.put("periodEnd", report.getPeriodEnd());
            rabbitTemplate.convertAndSend("report.exchange", "report.generate", msg);
            log.info("RabbitMQ 消息已发送: reportId={}", report.getId());
        } else {
            log.info("RabbitMQ 不可用，同步生成报表: reportId={}", report.getId());
            generateSync(report);
        }
        return Result.ok();
    }

    private void generateSync(Report report) {
        try {
            byte[] excelBytes = analysisService.generateExcel(
                    report.getReportType(), report.getPeriodStart(), report.getPeriodEnd());
            String objectKey = ossService.buildKey(report.getReportType(), report.getId());
            String fileUrl = ossService.upload(objectKey, excelBytes);

            report.setFileUrl(fileUrl);
            report.setStatus("GENERATED");
            reportService.updateById(report);
            log.info("同步报表生成完成: id={}", report.getId());
        } catch (Exception e) {
            log.error("同步报表生成失败: id={}", report.getId(), e);
            report.setStatus("FAILED");
            reportService.updateById(report);
        }
    }

    @PutMapping("/approve/{id}")
    @Operation(summary = "审核报表")
    @Log(title = "审核报表", businessType = "UPDATE")
    public Result<Void> approve(@PathVariable Long id, @RequestParam String approvedBy) {
        Report r = reportService.getById(id);
        if (r != null) {
            r.setStatus("APPROVED");
            r.setApprovedBy(approvedBy);
            r.setApprovedTime(LocalDateTime.now());
            reportService.updateById(r);
        }
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除报表（含 OSS 文件）")
    @Log(title = "删除报表", businessType = "DELETE")
    public Result<Void> delete(@PathVariable Long id) {
        Report r = reportService.getById(id);
        if (r != null && r.getFileUrl() != null) {
            String key = ossService.buildKey(r.getReportType(), id);
            ossService.delete(key);
        }
        reportService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载报表文件")
    @Log(title = "下载报表", businessType = "SELECT")
    public void download(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Report r = reportService.getById(id);
        if (r == null || r.getFileUrl() == null) {
            response.setStatus(404);
            return;
        }
        String objectKey = ossService.buildKey(r.getReportType(), id);
        String fileName = URLEncoder.encode(r.getTitle(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        try (var in = ossService.download(objectKey); var out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }

    @GetMapping("/export")
    @Operation(summary = "导出报表 (EasyExcel)")
    @Log(title = "导出报表", businessType = "EXPORT")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String reportType) throws Exception {
        List<Report> list = reportService.list(reportType);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("报表列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        com.alibaba.excel.EasyExcel.write(response.getOutputStream(), Report.class)
                .sheet("报表列表").doWrite(list);
    }
}
