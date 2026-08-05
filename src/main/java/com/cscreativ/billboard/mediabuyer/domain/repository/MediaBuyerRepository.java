package com.cscreativ.billboard.mediabuyer.domain.repository;

import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaBuyerRepository {
    MediaBuyer save(MediaBuyer mediaBuyer);
    Optional<MediaBuyer> findById(UUID id);
    Optional<MediaBuyer> findByUserId(UUID userId);
    List<MediaBuyer> findAll();
}
