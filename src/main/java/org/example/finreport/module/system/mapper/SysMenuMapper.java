package org.example.finreport.module.system.mapper;

import org.example.finreport.module.system.entity.SysMenu;
import java.util.List;

public interface SysMenuMapper {

    SysMenu selectById(Long id);

    List<SysMenu> selectList();

    int insert(SysMenu menu);

    int updateById(SysMenu menu);

    int deleteById(Long id);
}
