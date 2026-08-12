package com.cscreativ.billboard.mediabuyer.api;

import com.cscreativ.billboard.mediabuyer.api.mapper.MediaBuyerMapper;
import com.cscreativ.billboard.mediabuyer.api.request.RegisterBuyerRequest;
import com.cscreativ.billboard.mediabuyer.api.request.UpdateCreditLimitRequest;
import com.cscreativ.billboard.mediabuyer.api.response.MediaBuyerResponse;
import com.cscreativ.billboard.mediabuyer.application.MediaBuyerService;
import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * Public (voir SecurityConfiguration) : appelé juste après /auth/register, avant toute
     * connexion, donc sans JWT — currentUser est alors null. Le contrôle self-or-admin ne
     * s'applique que si l'appelant est déjà authentifié (ex. become-mediabuyer depuis un compte
     * existant, ou un admin créant le profil pour quelqu'un d'autre).
     */
    @PostMapping
    public ResponseEntity<MediaBuyerResponse> registerBuyer(@RequestBody RegisterBuyerRequest request,
                                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (currentUser != null) {
            currentUser.requireSelfOrAdmin(request.userId());
        }
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
    public ResponseEntity<Void> activateBuyer(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        mediaBuyerService.activateBuyer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<Void> updateCreditLimit(@PathVariable UUID id, @RequestBody UpdateCreditLimitRequest request,
                                                   @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        mediaBuyerService.updateCreditLimit(id, request.creditLimit());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaBuyerResponse> getBuyerById(@PathVariable UUID id) {
        MediaBuyer buyer = mediaBuyerService.getBuyerById(id);
        return ResponseEntity.ok(mediaBuyerMapper.toResponse(buyer));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MediaBuyerResponse> getBuyerByUserId(@PathVariable UUID userId,
                                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        MediaBuyer buyer = mediaBuyerService.getBuyerByUserId(userId);
        return ResponseEntity.ok(mediaBuyerMapper.toResponse(buyer));
    }

    @GetMapping
    public ResponseEntity<List<MediaBuyerResponse>> getAllBuyers() {
        List<MediaBuyer> buyers = mediaBuyerService.getAllBuyers();
        return ResponseEntity.ok(buyers.stream().map(mediaBuyerMapper::toResponse).collect(Collectors.toList()));
    }
}
