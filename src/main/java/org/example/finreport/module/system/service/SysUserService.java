package org.example.finreport.module.system.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.system.entity.SysUser;
import org.example.finreport.module.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper mapper;

    public SysUser getById(Long id) {
        return mapper.selectById(id);
    }

    public void save(SysUser user) {
        user.setStatus("NORMAL");
        mapper.insert(user);
    }

    public void updateById(SysUser user) {
        mapper.updateById(user);
    }

    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public List<SysUser> list() {
        return mapper.selectList();
    }

    public SysUser getByUsername(String username) {
        return mapper.selectByUsername(username);
    }

    public PageResult<SysUser> page(int page, int size, String username, String status) {
        PageParam pp = new PageParam(page, size);
        List<SysUser> list = mapper.selectPage(pp.getOffset(), pp.getSize(), username, status);
        long total = mapper.selectCount(username, status);
        return PageResult.of(list, total, page, size);
    }

    public List<String> getUserRoles(Long userId) {
        List<String> roles = mapper.selectUserRoles(userId);
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }
        return roles;
    }
}
