package com.vehicare.modules.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.inventory.api.InventoryService;
import com.vehicare.modules.inventory.dto.PartDto;
import com.vehicare.modules.inventory.dto.PartRequest;
import com.vehicare.modules.inventory.entity.InventoryMovement;
import com.vehicare.modules.inventory.entity.InventoryMovementType;
import com.vehicare.modules.inventory.entity.Part;
import com.vehicare.modules.inventory.exception.InsufficientStockException;
import com.vehicare.modules.inventory.exception.InvalidStockQuantityException;
import com.vehicare.modules.inventory.exception.PartNotFoundException;
import com.vehicare.modules.inventory.repository.InventoryMovementRepository;
import com.vehicare.modules.inventory.repository.PartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final PartRepository partRepository;
	private final InventoryMovementRepository movementRepository;

	// --------------------------------------------------
	// Create Part
	// --------------------------------------------------

	@Override
	@Transactional
	public PartDto createPart(PartRequest request) {

		Part part = Part.builder().partNumber(request.getPartNumber()).name(request.getName())
				.description(request.getDescription()).quantity(request.getQuantity()).unitPrice(request.getUnitPrice())
				.active(true).build();

		Part savedPart = partRepository.save(part);

		// Record initial stock
		if (savedPart.getQuantity() > 0) {

			InventoryMovement movement = InventoryMovement.builder().partId(savedPart.getId())
					.movementType(InventoryMovementType.OPENING_STOCK).quantity(savedPart.getQuantity())
					.quantityBefore(0).quantityAfter(savedPart.getQuantity()).reason("Initial stock")
					.createdAt(LocalDateTime.now()).build();

			movementRepository.save(movement);
		}

		return mapToDto(savedPart);
	}

	// --------------------------------------------------
	// Get Part By ID
	// --------------------------------------------------

	@Override
	public PartDto getPartById(Long id) {

		Part part = findPartById(id);

		return mapToDto(part);
	}

	// --------------------------------------------------
	// Get All Parts
	// --------------------------------------------------

	@Override
	public List<PartDto> getAllParts() {

		return partRepository.findAll().stream().map(this::mapToDto).toList();
	}

	// --------------------------------------------------
	// Add Stock
	// --------------------------------------------------

	@Override
	@Transactional
	public void addStock(Long partId, Integer quantity) {

		validateQuantity(quantity);

		Part part = findPartWithLock(partId);

		if (!part.isActive()) {
			throw new IllegalStateException("Cannot add stock to an inactive part");
		}

		int quantityBefore = part.getQuantity();

		int quantityAfter = quantityBefore + quantity;

		part.setQuantity(quantityAfter);

		partRepository.save(part);

		InventoryMovement movement = InventoryMovement.builder().partId(part.getId())
				.movementType(InventoryMovementType.PURCHASE).quantity(quantity).quantityBefore(quantityBefore)
				.quantityAfter(quantityAfter).reason("Stock purchased").createdAt(LocalDateTime.now()).build();

		movementRepository.save(movement);
	}

	// --------------------------------------------------
	// Check Stock Availability
	// --------------------------------------------------

	@Override
	public boolean isStockAvailable(Long partId, Integer quantity) {

		validateQuantity(quantity);

		Part part = findPartById(partId);

		if (!part.isActive()) {
			return false;
		}

		return part.getQuantity() >= quantity;
	}

	// --------------------------------------------------
	// Use Stock For Service
	// --------------------------------------------------

	@Override
	@Transactional
	public void useStock(Long partId, Integer quantity, Long serviceRecordId) {

		validateQuantity(quantity);

		if (serviceRecordId == null || serviceRecordId <= 0) {
			throw new IllegalArgumentException("Service record id must be greater than zero");
		}

		/*
		 * Lock the part row before checking and changing the stock quantity.
		 */
		Part part = findPartWithLock(partId);

		if (!part.isActive()) {
			throw new IllegalStateException("Cannot use an inactive part");
		}

		int quantityBefore = part.getQuantity();

		if (quantityBefore < quantity) {

			throw new InsufficientStockException("Insufficient stock for part: " + part.getName() + ". Available: "
					+ quantityBefore + ", requested: " + quantity);
		}

		int quantityAfter = quantityBefore - quantity;

		part.setQuantity(quantityAfter);

		partRepository.save(part);

		InventoryMovement movement = InventoryMovement.builder().partId(part.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(quantity).quantityBefore(quantityBefore)
				.quantityAfter(quantityAfter).referenceId(serviceRecordId).reason("Part used for service")
				.createdAt(LocalDateTime.now()).build();

		movementRepository.save(movement);
	}

	@Override
	@Transactional
	public void returnStock(Long partId, Integer quantity, Long serviceRecordId) {

		validateQuantity(quantity);

		if (serviceRecordId == null || serviceRecordId <= 0) {
			throw new IllegalArgumentException("Service record id must be greater than zero");
		}

		Part part = findPartWithLock(partId);

		int quantityBefore = part.getQuantity();
		int quantityAfter = quantityBefore + quantity;

		part.setQuantity(quantityAfter);

		partRepository.save(part);

		InventoryMovement movement = InventoryMovement.builder().partId(part.getId())
				.movementType(InventoryMovementType.RETURN).quantity(quantity).quantityBefore(quantityBefore)
				.quantityAfter(quantityAfter).referenceId(serviceRecordId).reason("Part returned from service")
				.createdAt(LocalDateTime.now()).build();

		movementRepository.save(movement);
	}

	// --------------------------------------------------
	// Activate Part
	// --------------------------------------------------

	@Override
	@Transactional
	public void activatePart(Long id) {

		Part part = findPartById(id);

		part.setActive(true);

		partRepository.save(part);
	}

	// --------------------------------------------------
	// Deactivate Part
	// --------------------------------------------------

	@Override
	@Transactional
	public void deactivatePart(Long id) {

		Part part = findPartById(id);

		part.setActive(false);

		partRepository.save(part);
	}

	// --------------------------------------------------
	// Helper Methods
	// --------------------------------------------------

	private Part findPartById(Long id) {

		return partRepository.findById(id)
				.orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));
	}

	private Part findPartWithLock(Long id) {

		return partRepository.findWithLockById(id)
				.orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));
	}

	private void validateQuantity(Integer quantity) {

		if (quantity == null || quantity <= 0) {

			throw new InvalidStockQuantityException("Quantity must be greater than zero");
		}
	}

	private PartDto mapToDto(Part part) {

		return PartDto.builder().id(part.getId()).partNumber(part.getPartNumber()).name(part.getName())
				.description(part.getDescription()).quantity(part.getQuantity()).unitPrice(part.getUnitPrice())
				.active(part.isActive()).build();
	}

	// --------------------------------------------------
	// update part detail
	// --------------------------------------------------

	@Override
	@Transactional
	public PartDto updatePart(Long id, PartRequest request) {

		Part part = findPartById(id);

		part.setPartNumber(request.getPartNumber());
		part.setName(request.getName());
		part.setDescription(request.getDescription());
		part.setUnitPrice(request.getUnitPrice());

		/*
		 * can't update quantity here.
		 *
		 * Stock changes must happen through: addStock() useStock()
		 *
		 * This ensures every stock change creates an InventoryMovement.
		 */

		Part updatedPart = partRepository.save(part);

		return mapToDto(updatedPart);
	}

	// --------------------------------------------------
	// remove Stock for no-service
	// --------------------------------------------------

	@Override
	@Transactional
	public void removeStock(Long partId, Integer quantity, String reason) {

		validateQuantity(quantity);

		if (reason == null || reason.isBlank()) {
			throw new IllegalArgumentException("Reason is required for stock removal");
		}

		Part part = findPartWithLock(partId);

		int quantityBefore = part.getQuantity();

		if (quantityBefore < quantity) {
			throw new InsufficientStockException("Insufficient stock for part: " + part.getName() + ". Available: "
					+ quantityBefore + ", requested: " + quantity);
		}

		int quantityAfter = quantityBefore - quantity;

		part.setQuantity(quantityAfter);

		partRepository.save(part);

		InventoryMovement movement = InventoryMovement.builder().partId(part.getId())
				.movementType(InventoryMovementType.ADJUSTMENT).quantity(quantity).quantityBefore(quantityBefore)
				.quantityAfter(quantityAfter).reason(reason).createdAt(LocalDateTime.now()).build();

		movementRepository.save(movement);
	}

}