package com.org.plane.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.org.plane.model.Ship;
import com.org.plane.repository.ShipRepository;

@Service
public interface ShipService  {
    public Page<Ship> findAll(Pageable pageable);

    public Optional<Ship> findById(Long id);

    public Page<Ship> findByName(String name, Pageable pageable);

    public Ship save(Ship ship);

    public void delete(Long id);
}
