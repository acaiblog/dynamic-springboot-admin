package com.admin.system.service.impl;

import com.admin.system.dto.ConfigDTO;
import com.admin.system.entity.SysConfig;
import com.admin.system.repository.SysConfigRepository;
import com.admin.system.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigRepository configRepository;

    public SysConfigServiceImpl(SysConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigDTO> getAll() {
        return configRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfigDTO> getById(Long id) {
        return configRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getValueByKey(String key) {
        return configRepository.findByConfigKey(key).map(SysConfig::getConfigValue);
    }

    @Override
    @Transactional
    public void save(ConfigDTO dto) {
        SysConfig config = new SysConfig();
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setRemark(dto.getRemark());
        config.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        configRepository.save(config);
    }

    @Override
    @Transactional
    public void update(Long id, ConfigDTO dto) {
        configRepository.findById(id).ifPresent(config -> {
            config.setConfigValue(dto.getConfigValue());
            config.setRemark(dto.getRemark());
            config.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            configRepository.save(config);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        configRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveOrUpdate(String key, String value) {
        Optional<SysConfig> existing = configRepository.findByConfigKey(key);
        if (existing.isPresent()) {
            existing.get().setConfigValue(value);
            configRepository.save(existing.get());
        } else {
            SysConfig config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setStatus(1);
            configRepository.save(config);
        }
    }

    private ConfigDTO toDTO(SysConfig config) {
        ConfigDTO dto = new ConfigDTO();
        dto.setId(config.getId());
        dto.setConfigKey(config.getConfigKey());
        dto.setConfigValue(config.getConfigValue());
        dto.setRemark(config.getRemark());
        dto.setStatus(config.getStatus());
        return dto;
    }
}
