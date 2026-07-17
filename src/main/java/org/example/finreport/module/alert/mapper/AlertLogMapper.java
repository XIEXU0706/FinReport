package org.example.finreport.module.alert.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.alert.entity.AlertLog;
import java.util.List;

public interface AlertLogMapper {

    AlertLog selectById(Long id);

    List<AlertLog> selectList();

    int insert(AlertLog log);

    int updateById(AlertLog log);

    int deleteById(Long id);

    List<AlertLog> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("severity") String severity,
                              @Param("status") String status);

    long selectCount(@Param("severity") String severity,
                     @Param("status") String status);

    List<AlertLog> selectLatest(@Param("limit") int limit);

    long selectCountByStatus(@Param("status") String status);
}
