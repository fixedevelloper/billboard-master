package com.cscreativ.billboard.billboard.infrastructure.persistence;

import com.cscreativ.billboard.billboard.domain.BillboardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BillboardJpaRepository extends JpaRepository<BillboardEntity, UUID> {
    List<BillboardEntity> findByCity(String city);
    List<BillboardEntity> findByStatus(BillboardStatus status);
}
