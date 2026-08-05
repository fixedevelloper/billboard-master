package com.cscreativ.billboard.creative.application;

import com.cscreativ.billboard.creative.CreativeFacade;
import com.cscreativ.billboard.creative.domain.CreativeProof;
import com.cscreativ.billboard.creative.domain.ProofStatus;
import com.cscreativ.billboard.creative.domain.repository.CreativeProofRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreativeFacadeImpl implements CreativeFacade {

    private final CreativeProofRepository proofRepository;

    public CreativeFacadeImpl(CreativeProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    @Override
    public Optional<CreativeProof> findLatestProofByCampaignId(UUID campaignId) {
        return proofRepository.findLatestByCampaignId(campaignId);
    }

    @Override
    public boolean isCreativeApproved(UUID campaignId) {
        return proofRepository.findLatestByCampaignId(campaignId)
                .map(proof -> proof.getStatus() == ProofStatus.APPROVED)
                .orElse(false);
    }
}
