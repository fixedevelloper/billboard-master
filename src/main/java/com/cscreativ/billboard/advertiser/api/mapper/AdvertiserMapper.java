package com.cscreativ.billboard.advertiser.api.mapper;

import com.cscreativ.billboard.advertiser.api.response.AdvertiserResponse;
import com.cscreativ.billboard.advertiser.domain.Advertiser;
import org.springframework.stereotype.Component;

@Component
public class AdvertiserMapper {

    public AdvertiserResponse toResponse(Advertiser advertiser) {
        return new AdvertiserResponse(
                advertiser.getId(),
                advertiser.getUserId(),
                advertiser.getCompanyName().getValue(),
                advertiser.getTaxNumber().getValue(),
                advertiser.getContactEmail(),
                advertiser.getContactPhone(),
                advertiser.getStatus().name()
        );
    }
}
