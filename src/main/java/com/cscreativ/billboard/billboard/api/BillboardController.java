package com.cscreativ.billboard.billboard.api;

import com.cscreativ.billboard.billboard.api.mapper.BillboardMapper;
import com.cscreativ.billboard.billboard.api.request.CreateBillboardRequest;
import com.cscreativ.billboard.billboard.api.response.BillboardResponse;
import com.cscreativ.billboard.billboard.application.BillboardService;
import com.cscreativ.billboard.billboard.domain.Billboard;
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
    public ResponseEntity<List<BillboardResponse>> getBillboardsByCity(@RequestParam(required = false) String city) {
        List<Billboard> billboards = (city != null) ? billboardService.getBillboardsByCity(city) : List.of();
        return ResponseEntity.ok(billboards.stream().map(billboardMapper::toResponse).collect(Collectors.toList()));
    }
}
