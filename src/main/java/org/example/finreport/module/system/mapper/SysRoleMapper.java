package org.example.finreport.module.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.system.entity.SysRole;
import java.util.List;

public interface SysRoleMapper {

    SysRole selectById(Long id);

    List<SysRole> selectList();

    int insert(SysRole role);

    int updateById(SysRole role);

    int deleteById(Long id);

    List<SysRole> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    long selectCount();
}
