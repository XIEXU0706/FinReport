package org.example.finreport.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.finreport.module.system.entity.SysUser;
import org.example.finreport.module.system.service.SysUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roles = sysUserService.getUserRoles(user.getId());
        return new LoginUser(user.getId(), user.getUsername(), user.getPassword(), roles);
    }
}
