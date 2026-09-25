package com.vehicare.modules.inventory.repository;

import java.util.List;
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

	@Query("""
			    SELECT p
			    FROM Part p
			    WHERE p.active = true
			    AND p.quantity <= p.minimumStockLevel
			""")
	List<Part> findLowStockParts();

	@Query("""
			    SELECT p
			    FROM Part p
			    WHERE p.active = true
			    AND p.quantity = 0
			""")
	List<Part> findOutOfStockParts();
	
	List<Part> findByActiveTrue();

}