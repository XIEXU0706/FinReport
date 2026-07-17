package org.example.finreport.module.system.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.system.entity.SysRole;
import org.example.finreport.module.system.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper mapper;

    public SysRole getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(SysRole role) {
        role.setStatus("NORMAL");
        mapper.insert(role);
    }

    public void updateById(SysRole role) {
        mapper.updateById(role);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<SysRole> list() {
        return mapper.selectList();
    }

    public PageResult<SysRole> page(int page, int size) {
        PageParam pp = new PageParam(page, size);
        List<SysRole> list = mapper.selectPage(pp.getOffset(), pp.getSize());
        long total = mapper.selectCount();
        return PageResult.of(list, total, page, size);
    }
}
