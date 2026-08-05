package com.cscreativ.billboard.contract.api.mapper;

import com.cscreativ.billboard.contract.api.response.ContractResponse;
import com.cscreativ.billboard.contract.domain.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getBookingId(),
                contract.getOwnerId(),
                contract.getAdvertiserId(),
                contract.getTermsAndConditions(),
                contract.getStatus().name(),
                contract.getOwnerSignature() != null,
                contract.getAdvertiserSignature() != null,
                contract.getCreatedAt()
        );
    }
}
