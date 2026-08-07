package com.cscreativ.billboard.billboard.domain.repository;

import com.cscreativ.billboard.billboard.domain.BillboardImage;

import java.util.List;
import java.util.UUID;

public interface BillboardImageRepository {
    BillboardImage save(BillboardImage image);
    List<BillboardImage> findByBillboardId(UUID billboardId);
    void deleteById(UUID id);
}
