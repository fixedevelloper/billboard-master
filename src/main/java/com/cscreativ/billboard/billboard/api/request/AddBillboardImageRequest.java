package com.cscreativ.billboard.billboard.api.request;

import java.util.UUID;

public record AddBillboardImageRequest(
        UUID fileId
) {}
