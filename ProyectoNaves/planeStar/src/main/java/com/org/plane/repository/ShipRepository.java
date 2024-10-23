package com.org.plane.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.org.plane.model.Ship;

public interface ShipRepository extends JpaRepository<Ship, Long> {
	Page<Ship> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
