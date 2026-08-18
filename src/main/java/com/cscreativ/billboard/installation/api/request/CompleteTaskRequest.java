package com.cscreativ.billboard.installation.api.request;

import java.util.UUID;

public record CompleteTaskRequest(
        UUID photoFileId,
        String notes
) {}
