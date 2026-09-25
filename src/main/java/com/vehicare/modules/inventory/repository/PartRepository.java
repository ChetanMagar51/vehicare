package com.vehicare.modules.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.vehicare.modules.inventory.entity.Part;

import jakarta.persistence.LockModeType;

public interface PartRepository extends JpaRepository<Part, Long> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Part p WHERE p.id = :id")
	Optional<Part> findWithLockById(Long id);

}