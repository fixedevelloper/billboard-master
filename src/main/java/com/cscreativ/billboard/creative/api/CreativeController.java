package com.cscreativ.billboard.creative.api;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.BookingFacade;
import com.cscreativ.billboard.campaign.CampaignFacade;
import com.cscreativ.billboard.creative.api.mapper.CreativeMapper;
import com.cscreativ.billboard.creative.api.request.RevisionRequest;
import com.cscreativ.billboard.creative.api.request.SubmitProofRequest;
import com.cscreativ.billboard.creative.api.response.CreativeProofResponse;
import com.cscreativ.billboard.creative.application.CreativeService;
import com.cscreativ.billboard.creative.domain.CreativeProof;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.storage.application.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/creatives")
public class CreativeController {

    private final CreativeService creativeService;
    private final CreativeMapper creativeMapper;
    private final CampaignFacade campaignFacade;
    private final BookingFacade bookingFacade;
    private final BillboardFacade billboardFacade;
    private final StorageService storageService;

    public CreativeController(CreativeService creativeService, CreativeMapper creativeMapper,
                               CampaignFacade campaignFacade, BookingFacade bookingFacade, BillboardFacade billboardFacade,
                               StorageService storageService) {
        this.creativeService = creativeService;
        this.creativeMapper = creativeMapper;
        this.campaignFacade = campaignFacade;
        this.bookingFacade = bookingFacade;
        this.billboardFacade = billboardFacade;
        this.storageService = storageService;
    }

    @PostMapping("/proofs")
    public ResponseEntity<CreativeProofResponse> submitProof(@RequestBody SubmitProofRequest request,
                                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UUID advertiserId = campaignFacade.findAdvertiserIdByCampaign(request.campaignId()).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId));
        CreativeProof proof = creativeService.submitProof(
                request.campaignId(),
                request.fileId(),
                request.width(),
                request.height()
        );
        return ResponseEntity.ok(toResponse(proof));
    }

    @PutMapping("/proofs/{id}/approve")
    public ResponseEntity<Void> approveProof(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfProofBillboardOrAdmin(creativeService.getProofById(id), currentUser);
        creativeService.approveProof(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/proofs/{id}/request-revision")
    public ResponseEntity<Void> requestRevision(@PathVariable UUID id, @RequestBody RevisionRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfProofBillboardOrAdmin(creativeService.getProofById(id), currentUser);
        creativeService.requestRevision(id, request.feedback());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proofs/{id}")
    public ResponseEntity<CreativeProofResponse> getProofById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        CreativeProof proof = creativeService.getProofById(id);
        requirePartyToCampaignOrAdmin(proof.getCampaignId(), currentUser);
        return ResponseEntity.ok(toResponse(proof));
    }

    @GetMapping("/campaign/{campaignId}/proofs")
    public ResponseEntity<List<CreativeProofResponse>> getProofsByCampaign(@PathVariable UUID campaignId,
                                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToCampaignOrAdmin(campaignId, currentUser);
        List<CreativeProof> proofs = creativeService.getProofsByCampaign(campaignId);
        return ResponseEntity.ok(proofs.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private CreativeProofResponse toResponse(CreativeProof proof) {
        return creativeMapper.toResponse(proof, storageService.getFilePresignedUrl(proof.getFileId()));
    }

    private void requirePartyToCampaignOrAdmin(UUID campaignId, AuthenticatedUser currentUser) {
        UUID advertiserId = campaignFacade.findAdvertiserIdByCampaign(campaignId).orElse(null);
        if (currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId)) {
            return;
        }
        currentUser.require(currentUser.isOwner(resolveOwnerIdForCampaign(campaignId)));
    }

    private void requireOwnerOfProofBillboardOrAdmin(CreativeProof proof, AuthenticatedUser currentUser) {
        if (currentUser.isAdmin()) {
            return;
        }
        currentUser.require(currentUser.isOwner(resolveOwnerIdForCampaign(proof.getCampaignId())));
    }

    private UUID resolveOwnerIdForCampaign(UUID campaignId) {
        UUID bookingId = campaignFacade.findBookingIdByCampaign(campaignId).orElse(null);
        if (bookingId == null) {
            return null;
        }
        UUID billboardId = bookingFacade.findBillboardIdByBooking(bookingId).orElse(null);
        return billboardId == null ? null : billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
    }
}
