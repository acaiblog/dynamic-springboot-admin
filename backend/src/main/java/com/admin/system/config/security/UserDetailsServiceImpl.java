package com.admin.system.config.security;

import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserRepository userRepository;

    public UserDetailsServiceImpl(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsernameWithRolesAndGroups(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        return new LoginUser(user);
    }
}
