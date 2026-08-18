package com.cscreativ.billboard.billboard.api.mapper;

import com.cscreativ.billboard.billboard.api.response.BillboardImageResponse;
import com.cscreativ.billboard.billboard.api.response.BillboardResponse;
import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardImage;
import org.springframework.stereotype.Component;

@Component
public class BillboardMapper {

    public BillboardResponse toResponse(Billboard billboard) {
        return new BillboardResponse(
                billboard.getId(),
                billboard.getTitle(),
                billboard.getDescription(),
                billboard.getType().name(),
                billboard.getStatus().name(),
                billboard.getLocation().getAddress(),
                billboard.getLocation().getCity(),
                billboard.getLocation().getLatitude(),
                billboard.getLocation().getLongitude(),
                billboard.getDimensions().getWidth(),
                billboard.getDimensions().getHeight(),
                billboard.getPricing().getDailyRate(),
                billboard.getPricing().getCurrency(),
                billboard.getOwnerId()
        );
    }

    public BillboardImageResponse toImageResponse(BillboardImage image, String url) {
        return new BillboardImageResponse(
                image.getId(),
                image.getBillboardId(),
                url,
                image.getCreatedAt()
        );
    }
}
