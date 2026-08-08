package com.cscreativ.billboard.city.infrastructure.persistence;

import com.cscreativ.billboard.city.domain.City;
import com.cscreativ.billboard.city.domain.repository.CityRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CityPersistenceAdapter implements CityRepository {

    private final CityJpaRepository jpaRepository;

    public CityPersistenceAdapter(CityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public City save(City city) {
        return toDomain(jpaRepository.save(toEntity(city)));
    }

    @Override
    public Optional<City> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<City> findByNameIgnoreCase(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(this::toDomain);
    }

    @Override
    public List<City> findAll() {
        return jpaRepository.findAllByOrderByNameAsc().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<City> search(String query) {
        return jpaRepository.search(query).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private CityEntity toEntity(City domain) {
        CityEntity entity = new CityEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCountry(domain.getCountry());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private City toDomain(CityEntity entity) {
        return new City(entity.getId(), entity.getName(), entity.getCountry(),
                entity.getLatitude(), entity.getLongitude(), entity.getCreatedAt());
    }
}
