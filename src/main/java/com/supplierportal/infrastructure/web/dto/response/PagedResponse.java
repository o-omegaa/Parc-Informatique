package com.supplierportal.infrastructure.web.dto.response;

import java.util.List;

/** Uniform response contract for all paginated list APIs. */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) { }
