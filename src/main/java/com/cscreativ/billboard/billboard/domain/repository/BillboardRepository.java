package com.cscreativ.billboard.billboard.domain.repository;

import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillboardRepository {
    Billboard save(Billboard billboard);
    Optional<Billboard> findById(UUID id);
    List<Billboard> findAllByCity(String city);
    List<Billboard> findAllByStatus(BillboardStatus status);
    List<Billboard> findAllByOwnerId(UUID ownerId);
    List<Billboard> findAll();
    void deleteById(UUID id);
}
