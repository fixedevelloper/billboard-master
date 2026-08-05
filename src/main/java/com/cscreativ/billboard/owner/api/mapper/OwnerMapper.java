package com.cscreativ.billboard.owner.api.mapper;

import com.cscreativ.billboard.owner.api.response.BillboardOwnerResponse;
import com.cscreativ.billboard.owner.domain.BillboardOwner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {

    public BillboardOwnerResponse toResponse(BillboardOwner owner) {
        return new BillboardOwnerResponse(
                owner.getId(),
                owner.getUserId(),
                owner.getOwnerDetails().getCompanyName(),
                owner.getOwnerDetails().getRegistrationNumber(),
                owner.getOwnerDetails().getContactEmail(),
                owner.getOwnerDetails().getPhoneNumber(),
                owner.getRevenueShareRate(),
                owner.getStatus().name(),
                owner.getCreatedAt()
        );
    }
}
