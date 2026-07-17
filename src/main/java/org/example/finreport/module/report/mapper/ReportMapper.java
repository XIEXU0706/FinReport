package org.example.finreport.module.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.report.entity.Report;
import java.util.List;

public interface ReportMapper {

    Report selectById(Long id);

    List<Report> selectList();

    int insert(Report report);

    int updateById(Report report);

    int deleteById(Long id);

    List<Report> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                            @Param("title") String title,
                            @Param("reportType") String reportType,
                            @Param("status") String status);

    long selectCount(@Param("title") String title,
                     @Param("reportType") String reportType,
                     @Param("status") String status);

    List<Report> selectListByType(@Param("reportType") String reportType);
}
