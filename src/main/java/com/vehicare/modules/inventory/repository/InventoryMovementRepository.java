package com.vehicare.modules.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vehicare.modules.inventory.entity.InventoryMovement;
import com.vehicare.modules.inventory.entity.InventoryMovementType;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

	@Query("""
			    SELECT m
			    FROM InventoryMovement m
			    WHERE m.movementType = :movementType
			    AND m.createdAt >= :from
			    ORDER BY m.createdAt DESC
			""")
	List<InventoryMovement> findByMovementTypeAndCreatedAtAfter(
			@Param("movementType") InventoryMovementType movementType, @Param("from") LocalDateTime from);

	@Query("""
			    SELECT m
			    FROM InventoryMovement m
			    WHERE m.movementType = :movementType
			    AND m.createdAt = (
			        SELECT MAX(m2.createdAt)
			        FROM InventoryMovement m2
			        WHERE m2.partId = m.partId
			        AND m2.movementType = :movementType
			    )
			""")
	List<InventoryMovement> findLatestMovementByType(@Param("movementType") InventoryMovementType movementType);

	List<InventoryMovement> findByMovementType(InventoryMovementType movementType);

}