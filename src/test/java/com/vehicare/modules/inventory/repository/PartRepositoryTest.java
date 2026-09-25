package com.vehicare.modules.inventory.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.inventory.entity.Part;

@DataJpaTest
class PartRepositoryTest {

	@Autowired
	private PartRepository partRepository;

	private Part activeLowStock;
	private Part activeOutOfStock;
	private Part activeNormalStock;
	private Part inactiveLowStock;

	@BeforeEach
	void setUp() {

		activeLowStock = Part.builder().partNumber("LOW-001").name("Low Stock Part").description("Low stock")
				.quantity(5).minimumStockLevel(10).unitPrice(500.0).active(true).build();

		activeOutOfStock = Part.builder().partNumber("OUT-001").name("Out Of Stock Part").description("No stock")
				.quantity(0).minimumStockLevel(10).unitPrice(700.0).active(true).build();

		activeNormalStock = Part.builder().partNumber("NORMAL-001").name("Normal Stock Part")
				.description("Normal stock").quantity(50).minimumStockLevel(10).unitPrice(1000.0).active(true).build();

		inactiveLowStock = Part.builder().partNumber("INACTIVE-001").name("Inactive Part")
				.description("Inactive low stock").quantity(2).minimumStockLevel(10).unitPrice(300.0).active(false)
				.build();

		partRepository.saveAll(List.of(activeLowStock, activeOutOfStock, activeNormalStock, inactiveLowStock));
	}

	@Test
	void findWithLockById_shouldReturnPart_whenPartExists() {

		Long id = activeLowStock.getId();

		Optional<Part> result = partRepository.findWithLockById(id);

		assertTrue(result.isPresent());

		assertEquals(id, result.get().getId());
		assertEquals("LOW-001", result.get().getPartNumber());
	}

	@Test
	void findWithLockById_shouldReturnEmpty_whenPartDoesNotExist() {

		Optional<Part> result = partRepository.findWithLockById(9999L);

		assertTrue(result.isEmpty());
	}

	@Test
	void findLowStockParts_shouldReturnActivePartsAtOrBelowMinimumLevel() {

		List<Part> result = partRepository.findLowStockParts();

		assertEquals(2, result.size());

		assertTrue(result.stream().anyMatch(p -> p.getPartNumber().equals("LOW-001")));

		assertTrue(result.stream().anyMatch(p -> p.getPartNumber().equals("OUT-001")));

		assertFalse(result.stream().anyMatch(p -> p.getPartNumber().equals("NORMAL-001")));

		assertFalse(result.stream().anyMatch(p -> p.getPartNumber().equals("INACTIVE-001")));
	}

	@Test
	void findOutOfStockParts_shouldReturnActivePartsWithZeroQuantity() {

		List<Part> result = partRepository.findOutOfStockParts();

		assertEquals(1, result.size());

		assertEquals("OUT-001", result.get(0).getPartNumber());
	}

	@Test
	void findOutOfStockParts_shouldNotReturnInactiveParts() {

		Part inactiveOutOfStock = Part.builder().partNumber("INACTIVE-OUT-001").name("Inactive Out Of Stock")
				.description("Inactive").quantity(0).minimumStockLevel(10).unitPrice(400.0).active(false).build();

		partRepository.save(inactiveOutOfStock);

		List<Part> result = partRepository.findOutOfStockParts();

		assertEquals(1, result.size());

		assertTrue(result.stream().allMatch(Part::isActive));
	}

	@Test
	void findByActiveTrue_shouldReturnOnlyActiveParts() {

		List<Part> result = partRepository.findByActiveTrue();

		assertEquals(3, result.size());

		assertTrue(result.stream().allMatch(Part::isActive));

		assertFalse(result.stream().anyMatch(p -> p.getPartNumber().equals("INACTIVE-001")));
	}

	@Test
	void findByActiveTrue_shouldReturnEmptyList_whenNoActivePartsExist() {

		partRepository.deleteAll();

		Part inactivePart = Part.builder().partNumber("INACTIVE-EMPTY-001").name("Inactive Part")
				.description("Inactive").quantity(5).minimumStockLevel(10).unitPrice(300.0).active(false).build();

		partRepository.save(inactivePart);

		List<Part> result = partRepository.findByActiveTrue();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}