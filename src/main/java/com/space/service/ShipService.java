package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    boolean existsById(Long aLong);

    Long size();

    List<Ship> findAll(Specification<Ship> spec);

    Page<Ship> findAll(Specification<Ship> spec, Pageable pageable);

    Ship getShipById(Long id);

    void saveShip(Ship ship);

    void updateShip(Ship ship);

    void deleteShipById(Long id);
}
