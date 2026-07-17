package org.example.finreport.module.system.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.module.system.entity.SysMenu;
import org.example.finreport.module.system.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final SysMenuMapper mapper;

    public SysMenu getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(SysMenu menu) {
        mapper.insert(menu);
    }

    public void updateById(SysMenu menu) {
        mapper.updateById(menu);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<SysMenu> list() {
        return mapper.selectList();
    }

    public List<SysMenu> getMenuTree() {
        List<SysMenu> all = mapper.selectList();
        List<SysMenu> roots = all.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .sorted((a, b) -> Integer.compare(
                        a.getSortOrder() != null ? a.getSortOrder() : 0,
                        b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());
        for (SysMenu root : roots) {
            buildChildren(root, all);
        }
        return roots;
    }

    private void buildChildren(SysMenu parent, List<SysMenu> all) {
        List<SysMenu> children = all.stream()
                .filter(m -> parent.getId().equals(m.getParentId()))
                .sorted((a, b) -> Integer.compare(
                        a.getSortOrder() != null ? a.getSortOrder() : 0,
                        b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());
    }
}
