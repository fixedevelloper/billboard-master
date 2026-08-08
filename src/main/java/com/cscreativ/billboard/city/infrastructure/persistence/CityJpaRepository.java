package com.cscreativ.billboard.city.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityJpaRepository extends JpaRepository<CityEntity, UUID> {
    Optional<CityEntity> findByNameIgnoreCase(String name);

    @Query("select c from CityEntity c where lower(c.name) like lower(concat('%', :query, '%')) order by c.name asc")
    List<CityEntity> search(@Param("query") String query);

    List<CityEntity> findAllByOrderByNameAsc();
}
