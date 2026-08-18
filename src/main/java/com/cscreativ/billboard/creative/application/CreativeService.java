package com.cscreativ.billboard.creative.application;

import com.cscreativ.billboard.creative.domain.CreativeProof;
import com.cscreativ.billboard.creative.domain.exception.CreativeProofNotFoundException;
import com.cscreativ.billboard.creative.domain.repository.CreativeProofRepository;
import com.cscreativ.billboard.creative.domain.valueobject.ProofDimensions;
import com.cscreativ.billboard.creative.events.CreativeProofApprovedEvent;
import com.cscreativ.billboard.creative.events.CreativeProofSubmittedEvent;
import com.cscreativ.billboard.creative.events.CreativeRevisionRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CreativeService {

    private final CreativeProofRepository proofRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreativeService(CreativeProofRepository proofRepository, ApplicationEventPublisher eventPublisher) {
        this.proofRepository = proofRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CreativeProof submitProof(UUID campaignId, UUID fileId, int width, int height) {
        int nextVersion = proofRepository.findLatestByCampaignId(campaignId)
                .map(p -> p.getVersion() + 1)
                .orElse(1);

        ProofDimensions dimensions = new ProofDimensions(width, height);
        CreativeProof proof = CreativeProof.create(campaignId, nextVersion, fileId, dimensions);
        CreativeProof saved = proofRepository.save(proof);

        eventPublisher.publishEvent(new CreativeProofSubmittedEvent(saved.getId(), saved.getCampaignId(), saved.getVersion(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void approveProof(UUID proofId) {
        CreativeProof proof = getProofById(proofId);
        proof.approve();
        proofRepository.save(proof);

        eventPublisher.publishEvent(new CreativeProofApprovedEvent(proof.getId(), proof.getCampaignId(), LocalDateTime.now()));
    }

    @Transactional
    public void requestRevision(UUID proofId, String feedback) {
        CreativeProof proof = getProofById(proofId);
        proof.requestRevision(feedback);
        proofRepository.save(proof);

        eventPublisher.publishEvent(new CreativeRevisionRequestedEvent(proof.getId(), proof.getCampaignId(), feedback, LocalDateTime.now()));
    }

    public CreativeProof getProofById(UUID id) {
        return proofRepository.findById(id)
                .orElseThrow(() -> new CreativeProofNotFoundException("Épreuve visuelle non trouvée avec l'id : " + id));
    }

    public List<CreativeProof> getProofsByCampaign(UUID campaignId) {
        return proofRepository.findByCampaignId(campaignId);
    }
}
