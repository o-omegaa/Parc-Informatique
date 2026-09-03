package com.supplierportal.application.document.result;

import java.util.List;

public record DocumentComplianceResult(
        Long supplierId,
        boolean compliant,
        List<String> missingDocumentTypes,
        List<String> pendingReviewDocumentTypes,
        List<String> rejectedDocumentTypes,
        List<String> expiredDocumentTypes
) { }
