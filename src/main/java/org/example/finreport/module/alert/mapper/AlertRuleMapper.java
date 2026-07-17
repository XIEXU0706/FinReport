package org.example.finreport.module.alert.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.alert.entity.AlertRule;
import java.util.List;

public interface AlertRuleMapper {

    AlertRule selectById(Long id);

    List<AlertRule> selectList();

    int insert(AlertRule rule);

    int updateById(AlertRule rule);

    int deleteById(Long id);

    List<AlertRule> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    long selectCount();

    List<AlertRule> selectByStatus(@Param("status") String status);
}
