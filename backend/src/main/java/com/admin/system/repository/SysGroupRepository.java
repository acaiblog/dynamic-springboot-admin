package com.admin.system.repository;

import com.admin.system.entity.SysGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SysGroupRepository extends JpaRepository<SysGroup, Long> {

    Optional<SysGroup> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT g FROM SysGroup g WHERE " +
           "(:name IS NULL OR g.name LIKE %:name%) AND " +
           "(:status IS NULL OR g.status = :status)")
    Page<SysGroup> findByConditions(@Param("name") String name,
                                     @Param("status") Integer status,
                                     Pageable pageable);
}
