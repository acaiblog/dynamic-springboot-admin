package com.admin.system.service;

import com.admin.system.dto.ConfigDTO;

import java.util.List;
import java.util.Optional;

public interface SysConfigService {
    List<ConfigDTO> getAll();
    Optional<ConfigDTO> getById(Long id);
    Optional<String> getValueByKey(String key);
    void save(ConfigDTO dto);
    void update(Long id, ConfigDTO dto);
    void delete(Long id);
    void saveOrUpdate(String key, String value);
}
