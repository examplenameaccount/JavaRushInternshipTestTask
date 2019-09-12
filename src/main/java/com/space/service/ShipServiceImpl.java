package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService {
    private ShipRepository repository;

    @Autowired
    public void setShipRepository(ShipRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Long aLong) {
        return repository.existsById(aLong);
    }

    @Override
    public Long size() {
        return repository.count();
    }

    @Override
    public Page<Ship> findAll(Specification<Ship> spec, Pageable pageable) { return repository.findAll(spec, pageable);}

    @Override
    public List<Ship> findAll(Specification<Ship> spec) {
        return repository.findAll(spec);
    }

    @Override
    public Ship getShipById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public void saveShip(Ship ship) {
        repository.save(ship);
    }

    @Override
    public void updateShip(Ship ship) {
        repository.save(ship);
    }

    @Override
    public void deleteShipById(Long id) {
        repository.deleteById(id);
    }
}
