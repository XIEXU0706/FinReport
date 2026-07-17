package org.example.finreport.module.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.report.entity.ReportTask;

import java.util.List;

public interface ReportTaskMapper {

    ReportTask selectById(Long id);

    List<ReportTask> selectList();

    List<ReportTask> selectEnabledList();

    int insert(ReportTask task);

    int updateById(ReportTask task);

    int deleteById(Long id);

    List<ReportTask> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                                @Param("taskName") String taskName,
                                @Param("reportType") String reportType);

    long selectCount(@Param("taskName") String taskName,
                     @Param("reportType") String reportType);
}
