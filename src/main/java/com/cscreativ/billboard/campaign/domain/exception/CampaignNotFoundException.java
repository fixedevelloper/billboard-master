package com.cscreativ.billboard.campaign.domain.exception;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
