package com.cscreativ.billboard.creative.api;

import com.cscreativ.billboard.creative.api.mapper.CreativeMapper;
import com.cscreativ.billboard.creative.api.request.RevisionRequest;
import com.cscreativ.billboard.creative.api.request.SubmitProofRequest;
import com.cscreativ.billboard.creative.api.response.CreativeProofResponse;
import com.cscreativ.billboard.creative.application.CreativeService;
import com.cscreativ.billboard.creative.domain.CreativeProof;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/creatives")
public class CreativeController {

    private final CreativeService creativeService;
    private final CreativeMapper creativeMapper;

    public CreativeController(CreativeService creativeService, CreativeMapper creativeMapper) {
        this.creativeService = creativeService;
        this.creativeMapper = creativeMapper;
    }

    @PostMapping("/proofs")
    public ResponseEntity<CreativeProofResponse> submitProof(@RequestBody SubmitProofRequest request) {
        CreativeProof proof = creativeService.submitProof(
                request.campaignId(),
                request.fileUrl(),
                request.width(),
                request.height()
        );
        return ResponseEntity.ok(creativeMapper.toResponse(proof));
    }

    @PutMapping("/proofs/{id}/approve")
    public ResponseEntity<Void> approveProof(@PathVariable UUID id) {
        creativeService.approveProof(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/proofs/{id}/request-revision")
    public ResponseEntity<Void> requestRevision(@PathVariable UUID id, @RequestBody RevisionRequest request) {
        creativeService.requestRevision(id, request.feedback());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proofs/{id}")
    public ResponseEntity<CreativeProofResponse> getProofById(@PathVariable UUID id) {
        CreativeProof proof = creativeService.getProofById(id);
        return ResponseEntity.ok(creativeMapper.toResponse(proof));
    }

    @GetMapping("/campaign/{campaignId}/proofs")
    public ResponseEntity<List<CreativeProofResponse>> getProofsByCampaign(@PathVariable UUID campaignId) {
        List<CreativeProof> proofs = creativeService.getProofsByCampaign(campaignId);
        return ResponseEntity.ok(proofs.stream().map(creativeMapper::toResponse).collect(Collectors.toList()));
    }
}
