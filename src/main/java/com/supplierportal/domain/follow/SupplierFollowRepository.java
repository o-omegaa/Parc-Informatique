package com.supplierportal.domain.follow;

import java.util.List;

public interface SupplierFollowRepository {
    SupplierFollow save(SupplierFollow follow);
    void delete(Long followerUserId, Long supplierId);
    boolean exists(Long followerUserId, Long supplierId);
    List<SupplierFollow> findByFollowerUserId(Long userId);
    long countBySupplierId(Long supplierId);
}
