package com.cscreativ.billboard.campaign.api;

import com.cscreativ.billboard.campaign.api.mapper.CampaignMapper;
import com.cscreativ.billboard.campaign.api.request.CreateCampaignRequest;
import com.cscreativ.billboard.campaign.api.request.RejectCampaignRequest;
import com.cscreativ.billboard.campaign.api.response.CampaignResponse;
import com.cscreativ.billboard.campaign.application.CampaignService;
import com.cscreativ.billboard.campaign.domain.Campaign;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignMapper campaignMapper;

    public CampaignController(CampaignService campaignService, CampaignMapper campaignMapper) {
        this.campaignService = campaignService;
        this.campaignMapper = campaignMapper;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@RequestBody CreateCampaignRequest request) {
        Campaign campaign = campaignService.createCampaign(
                request.bookingId(),
                request.advertiserId(),
                request.name(),
                request.description(),
                request.mediaUrl(),
                request.fileType(),
                request.fileSize()
        );
        return ResponseEntity.ok(campaignMapper.toResponse(campaign));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitCampaign(@PathVariable UUID id) {
        campaignService.submitCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveCampaign(@PathVariable UUID id) {
        campaignService.approveCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectCampaign(@PathVariable UUID id, @RequestBody RejectCampaignRequest request) {
        campaignService.rejectCampaign(id, request.reason());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable UUID id) {
        Campaign campaign = campaignService.getCampaignById(id);
        return ResponseEntity.ok(campaignMapper.toResponse(campaign));
    }

    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<List<CampaignResponse>> getCampaignsByAdvertiser(@PathVariable UUID advertiserId) {
        List<Campaign> campaigns = campaignService.getCampaignsByAdvertiser(advertiserId);
        return ResponseEntity.ok(campaigns.stream().map(campaignMapper::toResponse).collect(Collectors.toList()));
    }
}
