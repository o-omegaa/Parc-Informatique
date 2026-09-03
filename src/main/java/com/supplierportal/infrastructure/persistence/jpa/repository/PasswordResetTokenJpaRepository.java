package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /** Returns all tokens that are not expired and not used */
    @Query("SELECT t FROM PasswordResetTokenEntity t WHERE t.used = false AND t.expiryDate > :now")
    List<PasswordResetTokenEntity> findAllActiveTokens(@Param("now") LocalDateTime now);
}
