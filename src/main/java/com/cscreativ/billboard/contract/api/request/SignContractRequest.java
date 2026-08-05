package com.cscreativ.billboard.contract.api.request;

public record SignContractRequest(
        String signerName,
        String ipAddress
) {}
