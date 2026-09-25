package com.vehicare.modules.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

	@Mock
	private PartRepository partRepository;

	@Mock
	private InventoryMovementRepository movementRepository;

	@InjectMocks
	private InventoryServiceImpl inventoryService;

	private Part part;
	private PartRequest request;

	@BeforeEach
	void setUp() {

		part = Part.builder().id(1L).partNumber("BRK-001").name("Brake Pad").description("Front brake pad").quantity(10)
				.unitPrice(1500.0).active(true).build();

		request = PartRequest.builder().partNumber("BRK-001").name("Brake Pad").description("Front brake pad")
				.quantity(10).unitPrice(1500.0).build();
	}

	@Test
	void createPart_shouldCreatePartAndOpeningMovement_whenQuantityGreaterThanZero() {

		when(partRepository.save(any(Part.class))).thenReturn(part);

		PartDto result = inventoryService.createPart(request);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("BRK-001", result.getPartNumber());
		assertEquals("Brake Pad", result.getName());
		assertEquals(10, result.getQuantity());
		assertEquals(1500.0, result.getUnitPrice());
		assertTrue(result.isActive());

		verify(partRepository).save(any(Part.class));

		ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);

		verify(movementRepository).save(captor.capture());

		InventoryMovement movement = captor.getValue();

		assertEquals(1L, movement.getPartId());
		assertEquals(InventoryMovementType.OPENING_STOCK, movement.getMovementType());
		assertEquals(10, movement.getQuantity());
		assertEquals(0, movement.getQuantityBefore());
		assertEquals(10, movement.getQuantityAfter());
		assertEquals("Initial stock", movement.getReason());
		assertNotNull(movement.getCreatedAt());
	}

	@Test
	void createPart_shouldNotCreateMovement_whenQuantityIsZero() {

		request.setQuantity(0);

		Part zeroStockPart = Part.builder().id(1L).partNumber("BRK-001").name("Brake Pad")
				.description("Front brake pad").quantity(0).unitPrice(1500.0).active(true).build();

		when(partRepository.save(any(Part.class))).thenReturn(zeroStockPart);

		PartDto result = inventoryService.createPart(request);

		assertNotNull(result);
		assertEquals(0, result.getQuantity());

		verify(partRepository).save(any(Part.class));
		verifyNoInteractions(movementRepository);
	}

	// getpartById

	@Test
	void getPartById_shouldReturnPart_whenPartExists() {

		when(partRepository.findById(1L)).thenReturn(Optional.of(part));

		PartDto result = inventoryService.getPartById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("BRK-001", result.getPartNumber());
		assertEquals("Brake Pad", result.getName());
		assertEquals(10, result.getQuantity());
		assertTrue(result.isActive());

		verify(partRepository).findById(1L);
	}

	@Test
	void getPartById_shouldThrowException_whenPartDoesNotExist() {

		when(partRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(PartNotFoundException.class, () -> inventoryService.getPartById(99L));

		verify(partRepository).findById(99L);
	}

	// get all part
	@Test
	void getAllParts_shouldReturnAllParts() {

		Part secondPart = Part.builder().id(2L).partNumber("OIL-001").name("Engine Oil").description("5W-30")
				.quantity(20).unitPrice(800.0).active(true).build();

		when(partRepository.findAll()).thenReturn(List.of(part, secondPart));

		List<PartDto> result = inventoryService.getAllParts();

		assertEquals(2, result.size());

		assertEquals("BRK-001", result.get(0).getPartNumber());
		assertEquals("OIL-001", result.get(1).getPartNumber());

		verify(partRepository).findAll();
	}

	@Test
	void getAllParts_shouldReturnEmptyList_whenNoPartsExist() {

		when(partRepository.findAll()).thenReturn(List.of());

		List<PartDto> result = inventoryService.getAllParts();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(partRepository).findAll();
	}

	// add stock
	@Test
	void addStock_shouldIncreaseQuantityAndRecordMovement() {

		when(partRepository.findWithLockById(1L)).thenReturn(Optional.of(part));

		when(partRepository.save(any(Part.class))).thenReturn(part);

		inventoryService.addStock(1L, 5);

		assertEquals(15, part.getQuantity());

		verify(partRepository).findWithLockById(1L);
		verify(partRepository).save(part);

		ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);

		verify(movementRepository).save(captor.capture());

		InventoryMovement movement = captor.getValue();

		assertEquals(1L, movement.getPartId());
		assertEquals(InventoryMovementType.PURCHASE, movement.getMovementType());
		assertEquals(5, movement.getQuantity());
		assertEquals(10, movement.getQuantityBefore());
		assertEquals(15, movement.getQuantityAfter());
		assertEquals("Stock purchased", movement.getReason());
		assertNotNull(movement.getCreatedAt());
	}

	@Test
	void addStock_shouldThrowException_whenQuantityIsInvalid() {

		assertThrows(InvalidStockQuantityException.class, () -> inventoryService.addStock(1L, 0));

		verifyNoInteractions(partRepository, movementRepository);
	}

	@Test
	void addStock_shouldThrowException_whenQuantityIsNegative() {

		assertThrows(InvalidStockQuantityException.class, () -> inventoryService.addStock(1L, -5));

		verifyNoInteractions(partRepository, movementRepository);
	}

	@Test
	void addStock_shouldThrowException_whenQuantityIsNull() {

		assertThrows(InvalidStockQuantityException.class, () -> inventoryService.addStock(1L, null));

		verifyNoInteractions(partRepository, movementRepository);
	}

	@Test
	void addStock_shouldThrowException_whenPartIsInactive() {

		part.setActive(false);

		when(partRepository.findWithLockById(1L)).thenReturn(Optional.of(part));

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> inventoryService.addStock(1L, 5));

		assertEquals("Cannot add stock to an inactive part", exception.getMessage());

		verify(partRepository).findWithLockById(1L);
		verify(partRepository, never()).save(any());
		verifyNoInteractions(movementRepository);
	}

	@Test
	void addStock_shouldThrowException_whenPartDoesNotExist() {

		when(partRepository.findWithLockById(99L)).thenReturn(Optional.empty());

		assertThrows(PartNotFoundException.class, () -> inventoryService.addStock(99L, 5));

		verify(partRepository).findWithLockById(99L);
		verify(partRepository, never()).save(any());
		verifyNoInteractions(movementRepository);
	}

	// stock available
	@Test
	void isStockAvailable_shouldReturnTrue_whenEnoughStockExists() {

		when(partRepository.findById(1L)).thenReturn(Optional.of(part));

		boolean result = inventoryService.isStockAvailable(1L, 5);

		assertTrue(result);

		verify(partRepository).findById(1L);
	}

	@Test
	void isStockAvailable_shouldReturnFalse_whenStockIsInsufficient() {

		when(partRepository.findById(1L)).thenReturn(Optional.of(part));

		boolean result = inventoryService.isStockAvailable(1L, 20);

		assertFalse(result);
	}

	@Test
	void isStockAvailable_shouldReturnFalse_whenPartIsInactive() {

		part.setActive(false);

		when(partRepository.findById(1L)).thenReturn(Optional.of(part));

		boolean result = inventoryService.isStockAvailable(1L, 5);

		assertFalse(result);
	}

	@Test
	void isStockAvailable_shouldThrowException_whenQuantityIsInvalid() {

		assertThrows(InvalidStockQuantityException.class, () -> inventoryService.isStockAvailable(1L, 0));

		verifyNoInteractions(partRepository);
	}

	// use stock

	@Test
	void useStock_shouldDecreaseQuantityAndRecordMovement() {

		when(partRepository.findWithLockById(1L)).thenReturn(Optional.of(part));

		inventoryService.useStock(1L, 4, 100L);

		assertEquals(6, part.getQuantity());

		verify(partRepository).findWithLockById(1L);
		verify(partRepository).save(part);

		ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);

		verify(movementRepository).save(captor.capture());

		InventoryMovement movement = captor.getValue();

		assertEquals(1L, movement.getPartId());
		assertEquals(InventoryMovementType.SERVICE_USAGE, movement.getMovementType());
		assertEquals(4, movement.getQuantity());
		assertEquals(10, movement.getQuantityBefore());
		assertEquals(6, movement.getQuantityAfter());
		assertEquals(100L, movement.getReferenceId());
		assertEquals("Part used for service", movement.getReason());
		assertNotNull(movement.getCreatedAt());
	}
	
	@Test
	void useStock_shouldThrowException_whenStockIsInsufficient() {

	    when(partRepository.findWithLockById(1L))
	            .thenReturn(Optional.of(part));

	    InsufficientStockException exception = assertThrows(
	            InsufficientStockException.class,
	            () -> inventoryService.useStock(1L, 20, 100L)
	    );

	    assertTrue(exception.getMessage().contains("Insufficient stock"));

	    assertEquals(10, part.getQuantity());

	    verify(partRepository, never()).save(any());
	    verifyNoInteractions(movementRepository);
	}
	
	@Test
	void useStock_shouldThrowException_whenPartIsInactive() {

	    part.setActive(false);

	    when(partRepository.findWithLockById(1L))
	            .thenReturn(Optional.of(part));

	    assertThrows(
	            IllegalStateException.class,
	            () -> inventoryService.useStock(1L, 5, 100L)
	    );

	    verify(partRepository, never()).save(any());
	    verifyNoInteractions(movementRepository);
	}
	
	@Test
	void useStock_shouldThrowException_whenServiceRecordIdIsInvalid() {

	    assertThrows(
	            IllegalArgumentException.class,
	            () -> inventoryService.useStock(1L, 5, null)
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	
	@Test
	void useStock_shouldThrowException_whenServiceRecordIdIsZero() {

	    assertThrows(
	            IllegalArgumentException.class,
	            () -> inventoryService.useStock(1L, 5, 0L)
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	
	
	
	//return stock
	@Test
	void returnStock_shouldIncreaseQuantityAndRecordMovement() {

	    when(partRepository.findWithLockById(1L))
	            .thenReturn(Optional.of(part));

	    inventoryService.returnStock(1L, 5, 100L);

	    assertEquals(15, part.getQuantity());

	    verify(partRepository).findWithLockById(1L);
	    verify(partRepository).save(part);

	    ArgumentCaptor<InventoryMovement> captor =
	            ArgumentCaptor.forClass(InventoryMovement.class);

	    verify(movementRepository).save(captor.capture());

	    InventoryMovement movement = captor.getValue();

	    assertEquals(InventoryMovementType.RETURN,
	            movement.getMovementType());

	    assertEquals(5, movement.getQuantity());
	    assertEquals(10, movement.getQuantityBefore());
	    assertEquals(15, movement.getQuantityAfter());
	    assertEquals(100L, movement.getReferenceId());
	    assertEquals("Part returned from service", movement.getReason());
	    assertNotNull(movement.getCreatedAt());
	}
	
	@Test
	void returnStock_shouldThrowException_whenServiceRecordIdIsInvalid() {

	    assertThrows(
	            IllegalArgumentException.class,
	            () -> inventoryService.returnStock(1L, 5, null)
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	@Test
	void returnStock_shouldThrowException_whenQuantityIsInvalid() {

	    assertThrows(
	            InvalidStockQuantityException.class,
	            () -> inventoryService.returnStock(1L, 0, 100L)
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	
	@Test
	void returnStock_shouldThrowException_whenPartDoesNotExist() {

	    when(partRepository.findWithLockById(99L))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            PartNotFoundException.class,
	            () -> inventoryService.returnStock(99L, 5, 100L)
	    );

	    verify(partRepository).findWithLockById(99L);
	    verify(partRepository, never()).save(any());
	    verifyNoInteractions(movementRepository);
	}
	
	
	//active and diactive 
	
	@Test
	void activatePart_shouldActivatePart() {

	    part.setActive(false);

	    when(partRepository.findById(1L))
	            .thenReturn(Optional.of(part));

	    inventoryService.activatePart(1L);

	    assertTrue(part.isActive());

	    verify(partRepository).findById(1L);
	    verify(partRepository).save(part);
	}
	
	@Test
	void deactivatePart_shouldDeactivatePart() {

	    part.setActive(true);

	    when(partRepository.findById(1L))
	            .thenReturn(Optional.of(part));

	    inventoryService.deactivatePart(1L);

	    assertFalse(part.isActive());

	    verify(partRepository).findById(1L);
	    verify(partRepository).save(part);
	}
	
	@Test
	void activatePart_shouldThrowException_whenPartDoesNotExist() {

	    when(partRepository.findById(99L))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            PartNotFoundException.class,
	            () -> inventoryService.activatePart(99L)
	    );

	    verify(partRepository).findById(99L);
	    verify(partRepository, never()).save(any());
	}
	
	@Test
	void deactivatePart_shouldThrowException_whenPartDoesNotExist() {

	    when(partRepository.findById(99L))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            PartNotFoundException.class,
	            () -> inventoryService.deactivatePart(99L)
	    );

	    verify(partRepository).findById(99L);
	    verify(partRepository, never()).save(any());
	}
	
	
	
	//update 
	
	@Test
	void updatePart_shouldUpdatePartDetailsWithoutChangingQuantity() {

	    when(partRepository.findById(1L))
	            .thenReturn(Optional.of(part));

	    when(partRepository.save(any(Part.class)))
	            .thenReturn(part);

	    PartRequest updateRequest = PartRequest.builder()
	            .partNumber("BRK-002")
	            .name("Premium Brake Pad")
	            .description("Updated description")
	            .quantity(999)
	            .unitPrice(2000.0)
	            .build();

	    PartDto result = inventoryService.updatePart(1L, updateRequest);

	    assertEquals("BRK-002", result.getPartNumber());
	    assertEquals("Premium Brake Pad", result.getName());
	    assertEquals("Updated description", result.getDescription());
	    assertEquals(2000.0, result.getUnitPrice());

	    // Quantity must not be changed by updatePart()
	    assertEquals(10, result.getQuantity());

	    verify(partRepository).findById(1L);
	    verify(partRepository).save(part);
	}
	
	@Test
	void updatePart_shouldThrowException_whenPartDoesNotExist() {

	    when(partRepository.findById(99L))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            PartNotFoundException.class,
	            () -> inventoryService.updatePart(99L, request)
	    );

	    verify(partRepository).findById(99L);
	    verify(partRepository, never()).save(any());
	}
	
	
	
	///remove stock admin opration
	@Test
	void removeStock_shouldDecreaseQuantityAndRecordAdjustmentMovement() {

	    when(partRepository.findWithLockById(1L))
	            .thenReturn(Optional.of(part));

	    inventoryService.removeStock(1L, 3, "Damaged parts");

	    assertEquals(7, part.getQuantity());

	    verify(partRepository).findWithLockById(1L);
	    verify(partRepository).save(part);

	    ArgumentCaptor<InventoryMovement> captor =
	            ArgumentCaptor.forClass(InventoryMovement.class);

	    verify(movementRepository).save(captor.capture());

	    InventoryMovement movement = captor.getValue();

	    assertEquals(InventoryMovementType.ADJUSTMENT,
	            movement.getMovementType());

	    assertEquals(3, movement.getQuantity());
	    assertEquals(10, movement.getQuantityBefore());
	    assertEquals(7, movement.getQuantityAfter());
	    assertEquals("Damaged parts", movement.getReason());
	    assertNotNull(movement.getCreatedAt());
	}
	@Test
	void removeStock_shouldThrowException_whenStockIsInsufficient() {

	    when(partRepository.findWithLockById(1L))
	            .thenReturn(Optional.of(part));

	    assertThrows(
	            InsufficientStockException.class,
	            () -> inventoryService.removeStock(1L, 20, "Damaged parts")
	    );

	    assertEquals(10, part.getQuantity());

	    verify(partRepository, never()).save(any());
	    verifyNoInteractions(movementRepository);
	}
	
	@Test
	void removeStock_shouldThrowException_whenReasonIsBlank() {

	    assertThrows(
	            IllegalArgumentException.class,
	            () -> inventoryService.removeStock(1L, 5, " ")
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	
	@Test
	void removeStock_shouldThrowException_whenReasonIsNull() {

	    assertThrows(
	            IllegalArgumentException.class,
	            () -> inventoryService.removeStock(1L, 5, null)
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
	
	@Test
	void removeStock_shouldThrowException_whenQuantityIsInvalid() {

	    assertThrows(
	            InvalidStockQuantityException.class,
	            () -> inventoryService.removeStock(1L, 0, "Damaged parts")
	    );

	    verifyNoInteractions(partRepository, movementRepository);
	}
}