package org.example.finreport.module.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.finreport.module.system.entity.SysUser;
import java.util.List;

public interface SysUserMapper {

    SysUser selectById(Long id);

    List<SysUser> selectList();

    int insert(SysUser user);

    int updateById(SysUser user);

    int deleteById(Long id);

    List<SysUser> selectPage(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("username") String username,
                             @Param("status") String status);

    long selectCount(@Param("username") String username,
                     @Param("status") String status);

    SysUser selectByUsername(@Param("username") String username);

    List<String> selectUserRoles(Long userId);
}
