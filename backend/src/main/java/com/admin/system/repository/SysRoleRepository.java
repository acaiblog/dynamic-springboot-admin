package com.admin.system.repository;

import com.admin.system.entity.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByCode(String code);

    boolean existsByCode(String code);

    List<SysRole> findByStatus(Integer status);

    @Query("SELECT r FROM SysRole r WHERE " +
           "(:name IS NULL OR r.name LIKE %:name%) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<SysRole> findByConditions(@Param("name") String name,
                                   @Param("status") Integer status,
                                   Pageable pageable);
}
