package com.supplierportal.infrastructure.web.dto.response;

import java.util.List;

public record DocumentComplianceResponse(
        Long supplierId,
        boolean compliant,
        List<String> missingDocumentTypes,
        List<String> pendingReviewDocumentTypes,
        List<String> rejectedDocumentTypes,
        List<String> expiredDocumentTypes
) { }
