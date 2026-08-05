package com.cscreativ.billboard.mediabuyer.api.mapper;

import com.cscreativ.billboard.mediabuyer.api.response.MediaBuyerResponse;
import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import org.springframework.stereotype.Component;

@Component
public class MediaBuyerMapper {

    public MediaBuyerResponse toResponse(MediaBuyer buyer) {
        return new MediaBuyerResponse(
                buyer.getId(),
                buyer.getUserId(),
                buyer.getCompanyDetails().getCompanyName(),
                buyer.getCompanyDetails().getTaxId(),
                buyer.getCompanyDetails().getContactEmail(),
                buyer.getCompanyDetails().getPhoneNumber(),
                buyer.getCreditLimit(),
                buyer.getCurrentSpent(),
                buyer.getStatus().name(),
                buyer.getCreatedAt()
        );
    }
}
