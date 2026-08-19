package com.cscreativ.billboard.billboard.api;

import com.cscreativ.billboard.billboard.api.mapper.BillboardMapper;
import com.cscreativ.billboard.billboard.api.request.AddBillboardImageRequest;
import com.cscreativ.billboard.billboard.api.request.CreateBillboardRequest;
import com.cscreativ.billboard.billboard.api.request.UpdateBillboardRequest;
import com.cscreativ.billboard.billboard.api.response.BillboardImageResponse;
import com.cscreativ.billboard.billboard.api.response.BillboardResponse;
import com.cscreativ.billboard.billboard.application.BillboardService;
import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardImage;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.storage.application.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/billboards")
public class BillboardController {

    private final BillboardService billboardService;
    private final BillboardMapper billboardMapper;
    private final StorageService storageService;

    public BillboardController(BillboardService billboardService, BillboardMapper billboardMapper, StorageService storageService) {
        this.billboardService = billboardService;
        this.billboardMapper = billboardMapper;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<BillboardResponse> createBillboard(@RequestBody CreateBillboardRequest request,
                                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isOwner(request.ownerId()));
        Billboard billboard = billboardService.createBillboard(
                request.title(),
                request.description(),
                request.type(),
                request.address(),
                request.city(),
                request.latitude(),
                request.longitude(),
                request.width(),
                request.height(),
                request.dailyRate(),
                request.currency(),
                request.audience(),
                request.dailyTraffic(),
                request.ownerId()
        );
        return ResponseEntity.ok(billboardMapper.toResponse(billboard));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillboardResponse> getBillboardById(@PathVariable UUID id) {
        Billboard billboard = billboardService.getBillboardById(id);
        return ResponseEntity.ok(billboardMapper.toResponse(billboard));
    }

    @GetMapping
    public ResponseEntity<List<BillboardResponse>> getBillboards(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) UUID ownerId) {
        List<Billboard> billboards;
        if (ownerId != null) {
            billboards = billboardService.getBillboardsByOwner(ownerId);
        } else if (city != null) {
            billboards = billboardService.getBillboardsByCity(city);
        } else {
            billboards = billboardService.getAllBillboards();
        }
        return ResponseEntity.ok(billboards.stream().map(billboardMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillboardResponse> updateBillboard(@PathVariable UUID id, @RequestBody UpdateBillboardRequest request,
                                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfBillboardOrAdmin(id, currentUser);
        Billboard billboard = billboardService.updateBillboard(
                id,
                request.title(),
                request.description(),
                request.type(),
                request.address(),
                request.city(),
                request.latitude(),
                request.longitude(),
                request.width(),
                request.height(),
                request.dailyRate(),
                request.currency(),
                request.audience(),
                request.dailyTraffic()
        );
        return ResponseEntity.ok(billboardMapper.toResponse(billboard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillboard(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfBillboardOrAdmin(id, currentUser);
        billboardService.deleteBillboard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<BillboardImageResponse> addImage(@PathVariable UUID id, @RequestBody AddBillboardImageRequest request,
                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfBillboardOrAdmin(id, currentUser);
        BillboardImage image = billboardService.addImage(id, request.fileId());
        String url = storageService.getFilePresignedUrl(image.getFileId());
        return ResponseEntity.ok(billboardMapper.toImageResponse(image, url));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<BillboardImageResponse>> getImages(@PathVariable UUID id) {
        List<BillboardImage> images = billboardService.getImages(id);
        return ResponseEntity.ok(images.stream()
                .map(image -> billboardMapper.toImageResponse(image, storageService.getFilePresignedUrl(image.getFileId())))
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{billboardId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(@PathVariable UUID billboardId, @PathVariable UUID imageId,
                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfBillboardOrAdmin(billboardId, currentUser);
        billboardService.removeImage(imageId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnerOfBillboardOrAdmin(UUID billboardId, AuthenticatedUser currentUser) {
        Billboard billboard = billboardService.getBillboardById(billboardId);
        currentUser.require(currentUser.isAdmin() || currentUser.isOwner(billboard.getOwnerId()));
    }
}
