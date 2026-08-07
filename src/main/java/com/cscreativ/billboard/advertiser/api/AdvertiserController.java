package com.cscreativ.billboard.advertiser.api;

import com.cscreativ.billboard.advertiser.api.mapper.AdvertiserMapper;
import com.cscreativ.billboard.advertiser.api.request.RegisterAdvertiserRequest;
import com.cscreativ.billboard.advertiser.api.response.AdvertiserResponse;
import com.cscreativ.billboard.advertiser.application.AdvertiserService;
import com.cscreativ.billboard.advertiser.domain.Advertiser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/advertisers")
public class AdvertiserController {

    private final AdvertiserService advertiserService;
    private final AdvertiserMapper advertiserMapper;

    public AdvertiserController(AdvertiserService advertiserService, AdvertiserMapper advertiserMapper) {
        this.advertiserService = advertiserService;
        this.advertiserMapper = advertiserMapper;
    }

    @PostMapping
    public ResponseEntity<AdvertiserResponse> registerAdvertiser(@RequestBody RegisterAdvertiserRequest request) {
        Advertiser advertiser = advertiserService.registerAdvertiser(
                request.userId(),
                request.companyName(),
                request.taxNumber(),
                request.contactEmail(),
                request.contactPhone()
        );
        return ResponseEntity.ok(advertiserMapper.toResponse(advertiser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertiserResponse> getAdvertiserById(@PathVariable UUID id) {
        Advertiser advertiser = advertiserService.getAdvertiserById(id);
        return ResponseEntity.ok(advertiserMapper.toResponse(advertiser));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<Void> verifyAdvertiser(@PathVariable UUID id) {
        advertiserService.verifyAdvertiser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AdvertiserResponse>> getAllAdvertisers() {
        return ResponseEntity.ok(advertiserService.getAllAdvertisers().stream()
                .map(advertiserMapper::toResponse)
                .collect(Collectors.toList()));
    }
}
