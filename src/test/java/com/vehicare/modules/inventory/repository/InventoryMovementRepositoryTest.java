package com.vehicare.modules.inventory.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.inventory.entity.InventoryMovement;
import com.vehicare.modules.inventory.entity.InventoryMovementType;

@DataJpaTest
class InventoryMovementRepositoryTest {

	@Autowired
	private InventoryMovementRepository movementRepository;

	private InventoryMovement purchaseMovement;
	private InventoryMovement serviceUsageMovement;
	private InventoryMovement oldPurchaseMovement;

	@BeforeEach
	void setUp() {

		LocalDateTime now = LocalDateTime.now();

		purchaseMovement = InventoryMovement.builder().partId(1L).movementType(InventoryMovementType.PURCHASE)
				.quantity(10).quantityBefore(5).quantityAfter(15).reason("Stock purchased").createdAt(now.minusDays(1))
				.build();

		serviceUsageMovement = InventoryMovement.builder().partId(1L).movementType(InventoryMovementType.SERVICE_USAGE)
				.quantity(2).quantityBefore(15).quantityAfter(13).referenceId(100L).reason("Part used for service")
				.createdAt(now).build();

		oldPurchaseMovement = InventoryMovement.builder().partId(2L).movementType(InventoryMovementType.PURCHASE)
				.quantity(20).quantityBefore(0).quantityAfter(20).reason("Stock purchased").createdAt(now.minusDays(10))
				.build();

		movementRepository.saveAll(List.of(purchaseMovement, serviceUsageMovement, oldPurchaseMovement));
	}

	@Test
	void findByMovementTypeAndCreatedAtAfter_shouldReturnMatchingMovements() {

		LocalDateTime from = LocalDateTime.now().minusDays(2);

		List<InventoryMovement> result = movementRepository
				.findByMovementTypeAndCreatedAtAfter(InventoryMovementType.PURCHASE, from);

		assertEquals(1, result.size());

		assertEquals(InventoryMovementType.PURCHASE, result.get(0).getMovementType());

		assertEquals(1L, result.get(0).getPartId());
	}
	
	@Test
	void findByMovementTypeAndCreatedAtAfter_shouldReturnNewestFirst() {

	    InventoryMovement newerPurchase = InventoryMovement.builder()
	            .partId(3L)
	            .movementType(InventoryMovementType.PURCHASE)
	            .quantity(5)
	            .quantityBefore(10)
	            .quantityAfter(15)
	            .reason("New purchase")
	            .createdAt(LocalDateTime.now().plusMinutes(1))
	            .build();

	    movementRepository.save(newerPurchase);

	    List<InventoryMovement> result =
	            movementRepository.findByMovementTypeAndCreatedAtAfter(
	                    InventoryMovementType.PURCHASE,
	                    LocalDateTime.now().minusDays(2)
	            );

	    assertEquals(2, result.size());

	    assertEquals(
	            3L,
	            result.get(0).getPartId()
	    );

	    assertEquals(
	            1L,
	            result.get(1).getPartId()
	    );
	}
	
	@Test
	void findLatestMovementByType_shouldReturnLatestMovementForEachPart() {

	    InventoryMovement newerPurchaseForPart1 =
	            InventoryMovement.builder()
	                    .partId(1L)
	                    .movementType(InventoryMovementType.PURCHASE)
	                    .quantity(5)
	                    .quantityBefore(15)
	                    .quantityAfter(20)
	                    .reason("New purchase")
	                    .createdAt(LocalDateTime.now().plusMinutes(1))
	                    .build();

	    movementRepository.save(newerPurchaseForPart1);

	    List<InventoryMovement> result =
	            movementRepository.findLatestMovementByType(
	                    InventoryMovementType.PURCHASE
	            );

	    assertEquals(2, result.size());

	    assertTrue(
	            result.stream()
	                    .anyMatch(m ->
	                            m.getPartId().equals(1L)
	                            && m.getQuantity() == 5
	                    )
	    );

	    assertTrue(
	            result.stream()
	                    .anyMatch(m ->
	                            m.getPartId().equals(2L)
	                            && m.getQuantity() == 20
	                    )
	    );
	}
	
	@Test
	void findByMovementType_shouldReturnOnlyRequestedMovementType() {

	    List<InventoryMovement> result =
	            movementRepository.findByMovementType(
	                    InventoryMovementType.PURCHASE
	            );

	    assertEquals(2, result.size());

	    assertTrue(
	            result.stream()
	                    .allMatch(m ->
	                            m.getMovementType()
	                                    == InventoryMovementType.PURCHASE
	                    )
	    );
	}
	
	@Test
	void findByMovementType_shouldReturnEmptyList_whenNoMatchingMovementExists() {

	    List<InventoryMovement> result =
	            movementRepository.findByMovementType(
	                    InventoryMovementType.RETURN
	            );

	    assertNotNull(result);
	    assertTrue(result.isEmpty());
	}
	
	
	
}