package com.cscreativ.billboard.owner.api;

import com.cscreativ.billboard.owner.api.mapper.OwnerMapper;
import com.cscreativ.billboard.owner.api.request.RegisterOwnerRequest;
import com.cscreativ.billboard.owner.api.request.UpdateRevenueShareRequest;
import com.cscreativ.billboard.owner.api.response.BillboardOwnerResponse;
import com.cscreativ.billboard.owner.application.OwnerService;
import com.cscreativ.billboard.owner.domain.BillboardOwner;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;

    public OwnerController(OwnerService ownerService, OwnerMapper ownerMapper) {
        this.ownerService = ownerService;
        this.ownerMapper = ownerMapper;
    }

    /**
     * Public (voir SecurityConfiguration) : appelé juste après /auth/register, avant toute
     * connexion, donc sans JWT — currentUser est alors null. Le contrôle self-or-admin ne
     * s'applique que si l'appelant est déjà authentifié (ex. become-owner depuis un compte
     * existant, ou un admin créant le profil pour quelqu'un d'autre).
     */
    @PostMapping
    public ResponseEntity<BillboardOwnerResponse> registerOwner(@RequestBody RegisterOwnerRequest request,
                                                                 @AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (currentUser != null) {
            currentUser.requireSelfOrAdmin(request.userId());
        }
        BillboardOwner owner = ownerService.registerOwner(
                request.userId(),
                request.companyName(),
                request.registrationNumber(),
                request.contactEmail(),
                request.phoneNumber()
        );
        return ResponseEntity.ok(ownerMapper.toResponse(owner));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateOwner(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        ownerService.activateOwner(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/revenue-share")
    public ResponseEntity<Void> updateRevenueShareRate(@PathVariable UUID id, @RequestBody UpdateRevenueShareRequest request,
                                                        @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        ownerService.updateRevenueShareRate(id, request.revenueShareRate());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillboardOwnerResponse> getOwnerById(@PathVariable UUID id) {
        BillboardOwner owner = ownerService.getOwnerById(id);
        return ResponseEntity.ok(ownerMapper.toResponse(owner));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BillboardOwnerResponse> getOwnerByUserId(@PathVariable UUID userId,
                                                                    @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        BillboardOwner owner = ownerService.getOwnerByUserId(userId);
        return ResponseEntity.ok(ownerMapper.toResponse(owner));
    }

    @GetMapping
    public ResponseEntity<List<BillboardOwnerResponse>> getAllOwners() {
        List<BillboardOwner> owners = ownerService.getAllOwners();
        return ResponseEntity.ok(owners.stream().map(ownerMapper::toResponse).collect(Collectors.toList()));
    }
}
