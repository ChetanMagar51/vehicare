package com.vehicare.modules.serviceoperations.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.serviceoperations.entity.ServiceRecord;
import com.vehicare.modules.serviceoperations.enumtype.ServiceStatus;

@DataJpaTest
class ServiceRecordRepositoryTest {

	@Autowired
	private ServiceRecordRepository serviceRecordRepository;

	private ServiceRecord serviceRecord1;
	private ServiceRecord serviceRecord2;
	private ServiceRecord serviceRecord3;

	@BeforeEach
	void setUp() {

		serviceRecord1 = ServiceRecord.builder().appointmentId(101L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("Regular service").status(ServiceStatus.CREATED).build();

		serviceRecord2 = ServiceRecord.builder().appointmentId(102L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(402L).description("Oil change").status(ServiceStatus.IN_PROGRESS).build();

		serviceRecord3 = ServiceRecord.builder().appointmentId(103L).vehicleId(202L).ownerId(302L)
				.serviceAdvisorId(401L).description("Brake service").status(ServiceStatus.COMPLETED).build();

		serviceRecordRepository.saveAll(List.of(serviceRecord1, serviceRecord2, serviceRecord3));
	}

	@Test
	void findByAppointmentId_shouldReturnServiceRecord_whenAppointmentExists() {

		Optional<ServiceRecord> result = serviceRecordRepository.findByAppointmentId(101L);

		assertTrue(result.isPresent());

		ServiceRecord record = result.get();

		assertEquals(101L, record.getAppointmentId());
		assertEquals(201L, record.getVehicleId());
		assertEquals(301L, record.getOwnerId());
		assertEquals(ServiceStatus.CREATED, record.getStatus());
	}

	@Test
	void findByAppointmentId_shouldReturnEmpty_whenAppointmentDoesNotExist() {

		Optional<ServiceRecord> result = serviceRecordRepository.findByAppointmentId(999L);

		assertTrue(result.isEmpty());
	}

	@Test
	void findByVehicleId_shouldReturnAllServiceRecordsForVehicle() {

		List<ServiceRecord> result = serviceRecordRepository.findByVehicleId(201L);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertTrue(result.stream().allMatch(record -> record.getVehicleId().equals(201L)));
	}

	@Test
	void findByVehicleId_shouldReturnEmpty_whenVehicleHasNoServiceRecords() {

		List<ServiceRecord> result = serviceRecordRepository.findByVehicleId(999L);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void findByOwnerId_shouldReturnAllServiceRecordsForOwner() {

		List<ServiceRecord> result = serviceRecordRepository.findByOwnerId(301L);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertTrue(result.stream().allMatch(record -> record.getOwnerId().equals(301L)));
	}

	@Test
	void findByOwnerId_shouldReturnEmpty_whenOwnerHasNoServiceRecords() {

		List<ServiceRecord> result = serviceRecordRepository.findByOwnerId(999L);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void existsByAppointmentId_shouldReturnTrue_whenAppointmentExists() {

		boolean result = serviceRecordRepository.existsByAppointmentId(101L);

		assertTrue(result);
	}

	@Test
	void existsByAppointmentId_shouldReturnFalse_whenAppointmentDoesNotExist() {

		boolean result = serviceRecordRepository.existsByAppointmentId(999L);

		assertFalse(result);
	}
}