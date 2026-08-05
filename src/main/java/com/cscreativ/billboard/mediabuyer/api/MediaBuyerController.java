package com.cscreativ.billboard.mediabuyer.api;

import com.cscreativ.billboard.mediabuyer.api.mapper.MediaBuyerMapper;
import com.cscreativ.billboard.mediabuyer.api.request.RegisterBuyerRequest;
import com.cscreativ.billboard.mediabuyer.api.request.UpdateCreditLimitRequest;
import com.cscreativ.billboard.mediabuyer.api.response.MediaBuyerResponse;
import com.cscreativ.billboard.mediabuyer.application.MediaBuyerService;
import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media-buyers")
public class MediaBuyerController {

    private final MediaBuyerService mediaBuyerService;
    private final MediaBuyerMapper mediaBuyerMapper;

    public MediaBuyerController(MediaBuyerService mediaBuyerService, MediaBuyerMapper mediaBuyerMapper) {
        this.mediaBuyerService = mediaBuyerService;
        this.mediaBuyerMapper = mediaBuyerMapper;
    }

    @PostMapping
    public ResponseEntity<MediaBuyerResponse> registerBuyer(@RequestBody RegisterBuyerRequest request) {
        MediaBuyer buyer = mediaBuyerService.registerBuyer(
                request.userId(),
                request.companyName(),
                request.taxId(),
                request.contactEmail(),
                request.phoneNumber(),
                request.creditLimit()
        );
        return ResponseEntity.ok(mediaBuyerMapper.toResponse(buyer));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateBuyer(@PathVariable UUID id) {
        mediaBuyerService.activateBuyer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<Void> updateCreditLimit(@PathVariable UUID id, @RequestBody UpdateCreditLimitRequest request) {
        mediaBuyerService.updateCreditLimit(id, request.creditLimit());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaBuyerResponse> getBuyerById(@PathVariable UUID id) {
        MediaBuyer buyer = mediaBuyerService.getBuyerById(id);
        return ResponseEntity.ok(mediaBuyerMapper.toResponse(buyer));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MediaBuyerResponse> getBuyerByUserId(@PathVariable UUID userId) {
        MediaBuyer buyer = mediaBuyerService.getBuyerByUserId(userId);
        return ResponseEntity.ok(mediaBuyerMapper.toResponse(buyer));
    }

    @GetMapping
    public ResponseEntity<List<MediaBuyerResponse>> getAllBuyers() {
        List<MediaBuyer> buyers = mediaBuyerService.getAllBuyers();
        return ResponseEntity.ok(buyers.stream().map(mediaBuyerMapper::toResponse).collect(Collectors.toList()));
    }
}
