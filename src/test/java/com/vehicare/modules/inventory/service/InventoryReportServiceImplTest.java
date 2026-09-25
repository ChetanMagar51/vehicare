package com.vehicare.modules.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.vehicare.modules.inventory.dto.InventorySummaryDto;
import com.vehicare.modules.inventory.dto.PartDto;
import com.vehicare.modules.inventory.entity.InventoryMovement;
import com.vehicare.modules.inventory.entity.InventoryMovementType;
import com.vehicare.modules.inventory.entity.Part;
import com.vehicare.modules.inventory.repository.InventoryMovementRepository;
import com.vehicare.modules.inventory.repository.PartRepository;

@ExtendWith(MockitoExtension.class)
class InventoryReportServiceImplTest {

	@Mock
	private PartRepository partRepository;

	@Mock
	private InventoryMovementRepository movementRepository;

	@InjectMocks
	private InventoryReportServiceImpl inventoryReportService;

	private Part lowStockPart;
	private Part outOfStockPart;
	private Part normalStockPart;
	private Part inactivePart;

	@BeforeEach
	void setUp() {

		// Default values for @Value fields
		ReflectionTestUtils.setField(inventoryReportService, "slowMovingDays", 60L);
		ReflectionTestUtils.setField(inventoryReportService, "highDemandDays", 30L);
		ReflectionTestUtils.setField(inventoryReportService, "highDemandQuantity", 20);

		lowStockPart = Part.builder().id(1L).partNumber("LOW-001").name("Brake Pad").description("Brake pad")
				.quantity(5).minimumStockLevel(10).unitPrice(500.0).active(true).build();

		outOfStockPart = Part.builder().id(2L).partNumber("OUT-001").name("Oil Filter").description("Oil filter")
				.quantity(0).minimumStockLevel(5).unitPrice(300.0).active(true).build();

		normalStockPart = Part.builder().id(3L).partNumber("NORMAL-001").name("Air Filter").description("Air filter")
				.quantity(50).minimumStockLevel(10).unitPrice(700.0).active(true).build();

		inactivePart = Part.builder().id(4L).partNumber("INACTIVE-001").name("Clutch Plate").description("Clutch plate")
				.quantity(2).minimumStockLevel(10).unitPrice(1500.0).active(false).build();
	}

	// ---------------------------------------------------------
	// getLowStockParts()
	// ---------------------------------------------------------

	@Test
	void getLowStockParts_shouldReturnLowStockParts() {

		when(partRepository.findLowStockParts()).thenReturn(List.of(lowStockPart, outOfStockPart));

		List<PartDto> result = inventoryReportService.getLowStockParts();

		assertNotNull(result);
		assertEquals(2, result.size());

		assertEquals("LOW-001", result.get(0).getPartNumber());
		assertEquals(5, result.get(0).getQuantity());

		assertEquals("OUT-001", result.get(1).getPartNumber());
		assertEquals(0, result.get(1).getQuantity());

		verify(partRepository).findLowStockParts();
		verifyNoInteractions(movementRepository);
	}

	@Test
	void getLowStockParts_shouldReturnEmptyList_whenNoLowStockPartsExist() {

		when(partRepository.findLowStockParts()).thenReturn(List.of());

		List<PartDto> result = inventoryReportService.getLowStockParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(partRepository).findLowStockParts();
		verifyNoInteractions(movementRepository);
	}

	// ---------------------------------------------------------
	// getOutOfStockParts()
	// ---------------------------------------------------------

	@Test
	void getOutOfStockParts_shouldReturnOutOfStockParts() {

		when(partRepository.findOutOfStockParts()).thenReturn(List.of(outOfStockPart));

		List<PartDto> result = inventoryReportService.getOutOfStockParts();

		assertNotNull(result);
		assertEquals(1, result.size());

		PartDto dto = result.get(0);

		assertEquals(2L, dto.getId());
		assertEquals("OUT-001", dto.getPartNumber());
		assertEquals("Oil Filter", dto.getName());
		assertEquals(0, dto.getQuantity());
		assertEquals(5, dto.getMinimumStockLevel());
		assertEquals(300.0, dto.getUnitPrice());
		assertTrue(dto.isActive());

		verify(partRepository).findOutOfStockParts();
		verifyNoInteractions(movementRepository);
	}

	@Test
	void getOutOfStockParts_shouldReturnEmptyList_whenNoOutOfStockPartsExist() {

		when(partRepository.findOutOfStockParts()).thenReturn(List.of());

		List<PartDto> result = inventoryReportService.getOutOfStockParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(partRepository).findOutOfStockParts();
		verifyNoInteractions(movementRepository);
	}

	// ---------------------------------------------------------
	// getSlowMovingParts()
	// ---------------------------------------------------------

	@Test
	void getSlowMovingParts_shouldReturnPart_whenItHasNoUsageHistory() {

		when(partRepository.findByActiveTrue()).thenReturn(List.of(normalStockPart));

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE)).thenReturn(List.of());

		List<PartDto> result = inventoryReportService.getSlowMovingParts();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("NORMAL-001", result.get(0).getPartNumber());

		verify(partRepository).findByActiveTrue();
		verify(movementRepository).findByMovementType(InventoryMovementType.SERVICE_USAGE);
	}

	@Test
	void getSlowMovingParts_shouldReturnPart_whenLastUsageIsOlderThanConfiguredPeriod() {

		InventoryMovement oldUsage = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(2).quantityBefore(20).quantityAfter(18)
				.createdAt(LocalDateTime.now().minusDays(70)).build();

		when(partRepository.findByActiveTrue()).thenReturn(List.of(normalStockPart));

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE)).thenReturn(List.of(oldUsage));

		List<PartDto> result = inventoryReportService.getSlowMovingParts();

		assertEquals(1, result.size());
		assertEquals("NORMAL-001", result.get(0).getPartNumber());
	}

	@Test
	void getSlowMovingParts_shouldNotReturnPart_whenLastUsageIsRecent() {

		InventoryMovement recentUsage = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(2).quantityBefore(20).quantityAfter(18)
				.createdAt(LocalDateTime.now().minusDays(10)).build();

		when(partRepository.findByActiveTrue()).thenReturn(List.of(normalStockPart));

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE))
				.thenReturn(List.of(recentUsage));

		List<PartDto> result = inventoryReportService.getSlowMovingParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void getSlowMovingParts_shouldUseLatestUsage_whenMultipleUsagesExist() {

		InventoryMovement oldUsage = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(2).quantityBefore(30).quantityAfter(28)
				.createdAt(LocalDateTime.now().minusDays(90)).build();

		InventoryMovement recentUsage = InventoryMovement.builder().id(2L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(3).quantityBefore(28).quantityAfter(25)
				.createdAt(LocalDateTime.now().minusDays(10)).build();

		when(partRepository.findByActiveTrue()).thenReturn(List.of(normalStockPart));

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE))
				.thenReturn(List.of(oldUsage, recentUsage));

		List<PartDto> result = inventoryReportService.getSlowMovingParts();

		assertTrue(result.isEmpty());
	}

	@Test
	void getSlowMovingParts_shouldIgnoreInactiveParts() {

		when(partRepository.findByActiveTrue()).thenReturn(List.of(normalStockPart));

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE)).thenReturn(List.of());

		List<PartDto> result = inventoryReportService.getSlowMovingParts();

		assertEquals(1, result.size());

		assertFalse(result.stream().anyMatch(dto -> dto.getPartNumber().equals("INACTIVE-001")));

		verify(partRepository).findByActiveTrue();
	}

	// ---------------------------------------------------------
	// getHighDemandParts()
	// ---------------------------------------------------------

	@Test
	void getHighDemandParts_shouldReturnParts_whenUsageMeetsThreshold() {

		InventoryMovement movement = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(20).quantityBefore(50).quantityAfter(30)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of(movement));

		when(partRepository.findAllById(Set.of(normalStockPart.getId()))).thenReturn(List.of(normalStockPart));

		List<PartDto> result = inventoryReportService.getHighDemandParts();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("NORMAL-001", result.get(0).getPartNumber());

		verify(movementRepository).findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class));

		verify(partRepository).findAllById(Set.of(normalStockPart.getId()));
	}

	@Test
	void getHighDemandParts_shouldNotReturnPart_whenUsageIsBelowThreshold() {

		InventoryMovement movement = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(19).quantityBefore(50).quantityAfter(31)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of(movement));

		List<PartDto> result = inventoryReportService.getHighDemandParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(partRepository).findAllById(Set.of());
	}

	@Test
	void getHighDemandParts_shouldSumMultipleUsagesForSamePart() {

		InventoryMovement movement1 = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(12).quantityBefore(50).quantityAfter(38)
				.createdAt(LocalDateTime.now().minusDays(10)).build();

		InventoryMovement movement2 = InventoryMovement.builder().id(2L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(8).quantityBefore(38).quantityAfter(30)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of(movement1, movement2));

		when(partRepository.findAllById(Set.of(normalStockPart.getId()))).thenReturn(List.of(normalStockPart));

		List<PartDto> result = inventoryReportService.getHighDemandParts();

		assertEquals(1, result.size());
		assertEquals("NORMAL-001", result.get(0).getPartNumber());
	}

	@Test
	void getHighDemandParts_shouldIgnoreInactiveParts() {

		InventoryMovement movement = InventoryMovement.builder().id(1L).partId(inactivePart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(25).quantityBefore(30).quantityAfter(5)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of(movement));

		when(partRepository.findAllById(Set.of(inactivePart.getId()))).thenReturn(List.of(inactivePart));

		List<PartDto> result = inventoryReportService.getHighDemandParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void getHighDemandParts_shouldReturnEmptyList_whenNoUsageExists() {

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of());

		List<PartDto> result = inventoryReportService.getHighDemandParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(partRepository).findAllById(Set.of());
	}

	// ---------------------------------------------------------
	// getInventorySummary()
	// ---------------------------------------------------------

	@Test
	void getInventorySummary_shouldCalculateAllValuesCorrectly() {

		when(partRepository.findByActiveTrue()).thenReturn(List.of(lowStockPart, outOfStockPart, normalStockPart));

		/*
		 * Slow-moving: lowStockPart -> no usage -> slow outOfStockPart -> no usage ->
		 * slow normalStockPart -> recent usage -> not slow
		 */
		InventoryMovement recentUsage = InventoryMovement.builder().id(1L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(5).quantityBefore(55).quantityAfter(50)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE))
				.thenReturn(List.of(recentUsage));

		/*
		 * High demand: 25 usages for normalStockPart
		 */
		InventoryMovement highDemandUsage = InventoryMovement.builder().id(2L).partId(normalStockPart.getId())
				.movementType(InventoryMovementType.SERVICE_USAGE).quantity(25).quantityBefore(75).quantityAfter(50)
				.createdAt(LocalDateTime.now().minusDays(5)).build();

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of(highDemandUsage));

		when(partRepository.findAllById(Set.of(normalStockPart.getId()))).thenReturn(List.of(normalStockPart));

		InventorySummaryDto result = inventoryReportService.getInventorySummary();

		assertNotNull(result);

		assertEquals(3, result.getTotalParts());

		// 5 + 0 + 50
		assertEquals(55, result.getTotalQuantity());

		// lowStockPart + outOfStockPart
		assertEquals(2, result.getLowStockParts());

		// outOfStockPart
		assertEquals(1, result.getOutOfStockParts());

		// lowStockPart + outOfStockPart
		assertEquals(2, result.getSlowMovingParts());

		// normalStockPart
		assertEquals(1, result.getHighDemandParts());

		// 5 * 500 + 0 * 300 + 50 * 700
		assertEquals(37500.0, result.getTotalStockValue());
	}

	@Test
	void getInventorySummary_shouldReturnZeroValues_whenNoActivePartsExist() {

		when(partRepository.findByActiveTrue()).thenReturn(List.of());

		when(movementRepository.findByMovementType(InventoryMovementType.SERVICE_USAGE)).thenReturn(List.of());

		when(movementRepository.findByMovementTypeAndCreatedAtAfter(eq(InventoryMovementType.SERVICE_USAGE),
				any(LocalDateTime.class))).thenReturn(List.of());

		when(partRepository.findAllById(Set.of())).thenReturn(List.of());

		InventorySummaryDto result = inventoryReportService.getInventorySummary();

		assertNotNull(result);

		assertEquals(0, result.getTotalParts());
		assertEquals(0, result.getTotalQuantity());
		assertEquals(0, result.getLowStockParts());
		assertEquals(0, result.getOutOfStockParts());
		assertEquals(0, result.getSlowMovingParts());
		assertEquals(0, result.getHighDemandParts());
		assertEquals(0.0, result.getTotalStockValue());
	}
}