package com.supplierportal.domain.follow;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierFollow {
    private Long id;
    private Long followerUserId;
    private Long supplierId;
    private Instant followedAt;
}
