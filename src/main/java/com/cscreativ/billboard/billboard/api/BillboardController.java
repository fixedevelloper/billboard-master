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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/billboards")
public class BillboardController {

    private final BillboardService billboardService;
    private final BillboardMapper billboardMapper;

    public BillboardController(BillboardService billboardService, BillboardMapper billboardMapper) {
        this.billboardService = billboardService;
        this.billboardMapper = billboardMapper;
    }

    @PostMapping
    public ResponseEntity<BillboardResponse> createBillboard(@RequestBody CreateBillboardRequest request) {
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
    public ResponseEntity<BillboardResponse> updateBillboard(@PathVariable UUID id, @RequestBody UpdateBillboardRequest request) {
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
                request.currency()
        );
        return ResponseEntity.ok(billboardMapper.toResponse(billboard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillboard(@PathVariable UUID id) {
        billboardService.deleteBillboard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<BillboardImageResponse> addImage(@PathVariable UUID id, @RequestBody AddBillboardImageRequest request) {
        BillboardImage image = billboardService.addImage(id, request.url());
        return ResponseEntity.ok(billboardMapper.toImageResponse(image));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<BillboardImageResponse>> getImages(@PathVariable UUID id) {
        List<BillboardImage> images = billboardService.getImages(id);
        return ResponseEntity.ok(images.stream().map(billboardMapper::toImageResponse).collect(Collectors.toList()));
    }

    @DeleteMapping("/{billboardId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(@PathVariable UUID billboardId, @PathVariable UUID imageId) {
        billboardService.removeImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
