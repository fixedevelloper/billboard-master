package com.cscreativ.billboard.campaign.api;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.BookingFacade;
import com.cscreativ.billboard.campaign.api.mapper.CampaignMapper;
import com.cscreativ.billboard.campaign.api.request.CreateCampaignRequest;
import com.cscreativ.billboard.campaign.api.request.RejectCampaignRequest;
import com.cscreativ.billboard.campaign.api.response.CampaignResponse;
import com.cscreativ.billboard.campaign.application.CampaignService;
import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.storage.application.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignMapper campaignMapper;
    private final BookingFacade bookingFacade;
    private final BillboardFacade billboardFacade;
    private final StorageService storageService;

    public CampaignController(CampaignService campaignService, CampaignMapper campaignMapper,
                               BookingFacade bookingFacade, BillboardFacade billboardFacade, StorageService storageService) {
        this.campaignService = campaignService;
        this.campaignMapper = campaignMapper;
        this.bookingFacade = bookingFacade;
        this.billboardFacade = billboardFacade;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@RequestBody CreateCampaignRequest request,
                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(request.advertiserId()));
        Campaign campaign = campaignService.createCampaign(
                request.bookingId(),
                request.advertiserId(),
                request.name(),
                request.description(),
                request.mediaFileId(),
                request.fileType(),
                request.fileSize()
        );
        return ResponseEntity.ok(toResponse(campaign));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitCampaign(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Campaign campaign = campaignService.getCampaignById(id);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(campaign.getAdvertiserId()));
        campaignService.submitCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveCampaign(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfCampaignBillboardOrAdmin(campaignService.getCampaignById(id), currentUser);
        campaignService.approveCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectCampaign(@PathVariable UUID id, @RequestBody RejectCampaignRequest request,
                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfCampaignBillboardOrAdmin(campaignService.getCampaignById(id), currentUser);
        campaignService.rejectCampaign(id, request.reason());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable UUID id,
                                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Campaign campaign = campaignService.getCampaignById(id);
        requirePartyToCampaignOrAdmin(campaign, currentUser);
        return ResponseEntity.ok(toResponse(campaign));
    }

    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<List<CampaignResponse>> getCampaignsByAdvertiser(@PathVariable UUID advertiserId,
                                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId));
        List<Campaign> campaigns = campaignService.getCampaignsByAdvertiser(advertiserId);
        return ResponseEntity.ok(campaigns.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<CampaignResponse>> getCampaignsByBooking(@PathVariable UUID bookingId,
                                                                         @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UUID advertiserId = bookingFacade.findAdvertiserIdByBooking(bookingId).orElse(null);
        UUID billboardId = bookingFacade.findBillboardIdByBooking(bookingId).orElse(null);
        UUID ownerId = billboardId == null ? null : billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId) || currentUser.isOwner(ownerId));
        List<Campaign> campaigns = campaignService.getCampaignsByBooking(bookingId);
        return ResponseEntity.ok(campaigns.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private CampaignResponse toResponse(Campaign campaign) {
        var mediaAsset = campaign.getMediaAsset();
        String mediaUrl = mediaAsset == null ? null : storageService.getFilePresignedUrl(mediaAsset.getFileId());
        return campaignMapper.toResponse(campaign, mediaUrl);
    }

    private void requirePartyToCampaignOrAdmin(Campaign campaign, AuthenticatedUser currentUser) {
        if (currentUser.isAdmin() || currentUser.isAdvertiser(campaign.getAdvertiserId())) {
            return;
        }
        UUID billboardId = bookingFacade.findBillboardIdByBooking(campaign.getBookingId()).orElse(null);
        UUID ownerId = billboardId == null ? null : billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
        currentUser.require(currentUser.isOwner(ownerId));
    }

    private void requireOwnerOfCampaignBillboardOrAdmin(Campaign campaign, AuthenticatedUser currentUser) {
        if (currentUser.isAdmin()) {
            return;
        }
        UUID billboardId = bookingFacade.findBillboardIdByBooking(campaign.getBookingId()).orElse(null);
        UUID ownerId = billboardId == null ? null : billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
        currentUser.require(currentUser.isOwner(ownerId));
    }
}
