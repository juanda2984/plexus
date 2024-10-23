package com.org.plane.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.org.plane.model.Ship;
import com.org.plane.repository.ShipRepository;
import com.org.plane.service.ShipService;

@Service
public class ShipServiceImpl  implements ShipService{
	@Autowired
    private ShipRepository shipRepository;

    public Page<Ship> findAll(Pageable pageable) {
        return shipRepository.findAll(pageable);
    }

    @Cacheable("ships")
    public Optional<Ship> findById(Long id) {
        return shipRepository.findById(id);
    }

    public Page<Ship> findByName(String name, Pageable pageable) {
        return shipRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    public void delete(Long id) {
        shipRepository.deleteById(id);
    }
}
