package org.example.finreport.module.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.system.entity.SysOperLog;
import java.util.List;

@Mapper
public interface SysOperLogMapper {

    SysOperLog selectById(Long id);

    List<SysOperLog> selectList();

    int insert(SysOperLog log);

    int deleteById(Long id);

    List<SysOperLog> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                                @Param("operName") String operName,
                                @Param("businessType") String businessType);

    long selectCount(@Param("operName") String operName,
                     @Param("businessType") String businessType);
}
