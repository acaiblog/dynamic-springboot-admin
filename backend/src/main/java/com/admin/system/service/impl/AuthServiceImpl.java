package com.admin.system.service.impl;

import com.admin.system.common.utils.JwtUtils;
import com.admin.system.config.security.LoginUser;
import com.admin.system.dto.LoginDTO;
import com.admin.system.entity.SysMenu;
import com.admin.system.repository.SysMenuRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.AuthService;
import com.admin.system.vo.LoginVO;
import com.admin.system.vo.MenuVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserRepository userRepository;
    private final SysMenuRepository menuRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           SysUserRepository userRepository,
                           SysMenuRepository menuRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysMenu menu = new SysMenu();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", loginUser.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));

        String token = jwtUtils.generateToken(loginUser.getUser().getId(), loginUser.getUsername(), claims);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(loginUser.getUser().getId());
        vo.setUsername(loginUser.getUsername());
        vo.setRealName(loginUser.getUser().getNickname());
        vo.setEmail(loginUser.getUser().getEmail());
        vo.setAvatar(loginUser.getUser().getAvatar());

        // 权限（按钮级别 permission）
        List<SysMenu> allMenus = menuRepository.findMenusByUsername(loginUser.getUsername());
        vo.setPermissions(allMenus.stream()
                .filter(m -> m.getPermission() != null && !m.getPermission().isEmpty())
                .map(SysMenu::getPermission)
                .collect(Collectors.toList()));

        // 路由（目录 + 菜单）
        List<SysMenu> routerMenus = menuRepository.findRouterMenusByUsername(loginUser.getUsername());
        vo.setMenus(buildRoutes(routerMenus));

        return vo;
    }

    @Override
    public LoginVO getUserInfo() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        LoginVO vo = new LoginVO();
        vo.setUserId(loginUser.getUser().getId());
        vo.setUsername(loginUser.getUsername());
        vo.setRealName(loginUser.getUser().getNickname());
        vo.setEmail(loginUser.getUser().getEmail());
        vo.setAvatar(loginUser.getUser().getAvatar());
        vo.setPermissions(loginUser.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return vo;
    }

    private List<Map<String, Object>> buildRoutes(List<SysMenu> menus) {
        Map<Long, List<SysMenu>> childrenMap = menus.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() != 0)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return menus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .map(m -> toRoute(m, childrenMap))
                .collect(Collectors.toList());
    }

    private Map<String, Object> toRoute(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
        Map<String, Object> route = new LinkedHashMap<>();
        route.put("name", menu.getName());
        route.put("path", menu.getPath());
        route.put("component", menu.getComponent());
        route.put("meta", Map.of(
                "title", menu.getName(),
                "icon", menu.getIcon() != null ? menu.getIcon() : "",
                "isCache", menu.getIsCache() == 1,
                "visible", menu.getVisible() == 1
        ));
        if (menu.getType() == 0) {
            route.put("alwaysShow", true);
        }
        List<SysMenu> children = childrenMap.get(menu.getId());
        if (children != null && !children.isEmpty()) {
            route.put("children", children.stream()
                    .map(c -> toRoute(c, childrenMap))
                    .collect(Collectors.toList()));
        }
        return route;
    }
}
