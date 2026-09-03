package com.supplierportal.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class ApiErrorResponse {
    private int status;
    private String title;
    private String detail;
    private String instance;
    private Instant timestamp;
}
