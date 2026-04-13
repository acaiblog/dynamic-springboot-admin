package com.admin.system.repository;

import com.admin.system.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    @Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<SysUser> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.groups WHERE u.username = :username")
    Optional<SysUser> findByUsernameWithRolesAndGroups(@Param("username") String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM SysUser u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<SysUser> findByConditions(@Param("username") String username,
                                   @Param("status") Integer status,
                                   Pageable pageable);
}
