package com.vehicare.modules.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.inventory.entity.InventoryMovement;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

}