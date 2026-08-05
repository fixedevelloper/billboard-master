package com.cscreativ.billboard.creative.api.mapper;

import com.cscreativ.billboard.creative.api.response.CreativeProofResponse;
import com.cscreativ.billboard.creative.domain.CreativeProof;
import org.springframework.stereotype.Component;

@Component
public class CreativeMapper {

    public CreativeProofResponse toResponse(CreativeProof proof) {
        return new CreativeProofResponse(
                proof.getId(),
                proof.getCampaignId(),
                proof.getVersion(),
                proof.getFileUrl(),
                proof.getDimensions().getWidthInPixels(),
                proof.getDimensions().getHeightInPixels(),
                proof.getStatus().name(),
                proof.getFeedback(),
                proof.getCreatedAt()
        );
    }
}
