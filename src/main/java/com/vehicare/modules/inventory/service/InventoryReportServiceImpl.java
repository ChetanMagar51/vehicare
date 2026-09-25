package com.vehicare.modules.inventory.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.inventory.api.InventoryReportService;
import com.vehicare.modules.inventory.dto.InventorySummaryDto;
import com.vehicare.modules.inventory.dto.PartDto;
import com.vehicare.modules.inventory.entity.InventoryMovement;
import com.vehicare.modules.inventory.entity.InventoryMovementType;
import com.vehicare.modules.inventory.entity.Part;
import com.vehicare.modules.inventory.repository.InventoryMovementRepository;
import com.vehicare.modules.inventory.repository.PartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

	private final PartRepository partRepository;
	private final InventoryMovementRepository movementRepository;

	@Value("${inventory.slow-moving-days:60}")
	private long slowMovingDays;

	@Value("${inventory.high-demand-days:30}")
	private long highDemandDays;

	@Value("${inventory.high-demand-quantity:20}")
	private int highDemandQuantity;

	@Override
	@Transactional(readOnly = true)
	public List<PartDto> getLowStockParts() {

		return partRepository.findLowStockParts().stream().map(this::mapToDto).toList();

	}

	@Override
	@Transactional(readOnly = true)
	public List<PartDto> getOutOfStockParts() {

		return partRepository.findOutOfStockParts().stream().map(this::mapToDto).toList();

	}

	@Override
	@Transactional(readOnly = true)
	public List<PartDto> getSlowMovingParts() {

		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(slowMovingDays);

		List<Part> activeParts = partRepository.findByActiveTrue();

		List<InventoryMovement> usageMovements = movementRepository
				.findByMovementType(InventoryMovementType.SERVICE_USAGE);

		Map<Long, LocalDateTime> latestUsageByPart = usageMovements.stream()
				.collect(Collectors.groupingBy(InventoryMovement::getPartId,
						Collectors.collectingAndThen(
								Collectors.maxBy(Comparator.comparing(InventoryMovement::getCreatedAt)),
								optional -> optional.map(InventoryMovement::getCreatedAt).orElse(null))));

		return activeParts.stream().filter(part -> {

			LocalDateTime lastUsed = latestUsageByPart.get(part.getId());

			return lastUsed == null || lastUsed.isBefore(cutoffDate);
		}).map(this::mapToDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PartDto> getHighDemandParts() {

		LocalDateTime fromDate = LocalDateTime.now().minusDays(highDemandDays);

		List<InventoryMovement> movements = movementRepository
				.findByMovementTypeAndCreatedAtAfter(InventoryMovementType.SERVICE_USAGE, fromDate);

		Map<Long, Integer> usageByPart = movements.stream().collect(Collectors.groupingBy(InventoryMovement::getPartId,
				Collectors.summingInt(InventoryMovement::getQuantity)));

		Set<Long> highDemandPartIds = usageByPart.entrySet().stream()
				.filter(entry -> entry.getValue() >= highDemandQuantity).map(Map.Entry::getKey)
				.collect(Collectors.toSet());

		return partRepository.findAllById(highDemandPartIds).stream().filter(Part::isActive).map(this::mapToDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public InventorySummaryDto getInventorySummary() {

		List<Part> parts = partRepository.findByActiveTrue();

		long totalParts = parts.size();

		long totalQuantity = parts.stream().mapToLong(Part::getQuantity).sum();

		long lowStockParts = parts.stream().filter(part -> part.getQuantity() <= part.getMinimumStockLevel()).count();

		long outOfStockParts = parts.stream().filter(part -> part.getQuantity() == 0).count();

		long slowMovingParts = getSlowMovingParts().size();

		long highDemandParts = getHighDemandParts().size();

		double totalStockValue = parts.stream().mapToDouble(part -> part.getQuantity() * part.getUnitPrice()).sum();

		return InventorySummaryDto.builder().totalParts(totalParts).totalQuantity(totalQuantity)
				.lowStockParts(lowStockParts).outOfStockParts(outOfStockParts).slowMovingParts(slowMovingParts)
				.highDemandParts(highDemandParts).totalStockValue(totalStockValue).build();
	}

	private PartDto mapToDto(Part part) {

		return PartDto.builder().id(part.getId()).partNumber(part.getPartNumber()).name(part.getName())
				.description(part.getDescription()).quantity(part.getQuantity())
				.minimumStockLevel(part.getMinimumStockLevel()).unitPrice(part.getUnitPrice()).active(part.isActive())
				.build();
	}
}