package com.admin.system.service.impl;

import com.admin.system.common.exception.BusinessException;
import com.admin.system.config.security.LoginUser;
import com.admin.system.dto.MenuDTO;
import com.admin.system.entity.SysMenu;
import com.admin.system.repository.SysMenuRepository;
import com.admin.system.service.SysMenuService;
import com.admin.system.vo.MenuVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuRepository menuRepository;

    public SysMenuServiceImpl(SysMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<MenuVO> getTree() {
        List<SysMenu> all = menuRepository.findAll();
        return buildTree(all);
    }

    @Override
    public List<Map<String, Object>> getRouters() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Collections.emptyList();
        }
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        List<SysMenu> menus = menuRepository.findRouterMenusByUsername(loginUser.getUsername());
        return buildRoutes(menus);
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

    @Override
    public List<?> getAll() {
        return menuRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Object getById(Long id) {
        return toVO(findById(id));
    }

    private SysMenu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException("菜单不存在"));
    }

    @Override
    @Transactional
    public void save(MenuDTO dto) {
        SysMenu menu = new SysMenu();
        copyToEntity(dto, menu);
        menuRepository.save(menu);
    }

    @Override
    @Transactional
    public void update(Long id, MenuDTO dto) {
        SysMenu menu = findById(id);
        copyToEntity(dto, menu);
        menuRepository.save(menu);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<SysMenu> children = menuRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("该菜单下存在子菜单，请先删除子菜单");
        }
        menuRepository.deleteById(id);
    }

    private void copyToEntity(MenuDTO dto, SysMenu menu) {
        menu.setName(dto.getMenuName() != null ? dto.getMenuName() : dto.getName());
        menu.setPermission(dto.getPermission());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setIcon(dto.getIcon());
        menu.setType(dto.getType() != null ? dto.getType() : 1);
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setIsCache(dto.getIsCache() != null ? dto.getIsCache() : 0);
    }

    private MenuVO toVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setName(menu.getName());
        vo.setMenuName(menu.getName());
        vo.setPermission(menu.getPermission());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setType(menu.getType());
        vo.setParentId(menu.getParentId());
        vo.setOrderNum(menu.getOrderNum());
        vo.setStatus(menu.getStatus());
        vo.setVisible(menu.getVisible());
        vo.setIsCache(menu.getIsCache());
        vo.setCreateTime(menu.getCreateTime());
        return vo;
    }

    private List<MenuVO> buildTree(List<SysMenu> all) {
        Map<Long, List<SysMenu>> childrenMap = all.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() != 0)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return all.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .map(m -> toTreeNode(m, childrenMap))
                .sorted(Comparator.comparingInt(MenuVO::getOrderNum))
                .collect(Collectors.toList());
    }

    private MenuVO toTreeNode(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
        MenuVO vo = toVO(menu);
        List<SysMenu> children = childrenMap.get(menu.getId());
        if (children != null && !children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(c -> toTreeNode(c, childrenMap))
                    .sorted(Comparator.comparingInt(MenuVO::getOrderNum))
                    .collect(Collectors.toList()));
        } else {
            vo.setChildren(new ArrayList<>());
        }
        return vo;
    }
}
