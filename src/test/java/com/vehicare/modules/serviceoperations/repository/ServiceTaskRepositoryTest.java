package com.vehicare.modules.serviceoperations.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vehicare.modules.serviceoperations.entity.ServiceTask;
import com.vehicare.modules.serviceoperations.enumtype.ServiceTaskStatus;

@DataJpaTest
class ServiceTaskRepositoryTest {

	@Autowired
	private ServiceTaskRepository serviceTaskRepository;

	private ServiceTask task1;
	private ServiceTask task2;
	private ServiceTask task3;

	@BeforeEach
	void setUp() {

		task1 = ServiceTask.builder().serviceRecordId(1L).description("Engine oil replacement")
				.status(ServiceTaskStatus.PENDING).build();

		task2 = ServiceTask.builder().serviceRecordId(1L).description("Oil filter replacement")
				.status(ServiceTaskStatus.IN_PROGRESS).build();

		task3 = ServiceTask.builder().serviceRecordId(2L).description("Brake inspection")
				.status(ServiceTaskStatus.COMPLETED).build();

		serviceTaskRepository.saveAll(List.of(task1, task2, task3));
	}

	@Test
	void findByServiceRecordId_shouldReturnAllTasksForServiceRecord() {

		List<ServiceTask> result = serviceTaskRepository.findByServiceRecordId(1L);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertTrue(result.stream().allMatch(task -> task.getServiceRecordId().equals(1L)));
	}

	@Test
	void findByServiceRecordId_shouldReturnSingleTask_whenOnlyOneTaskExists() {

		List<ServiceTask> result = serviceTaskRepository.findByServiceRecordId(2L);

		assertNotNull(result);
		assertEquals(1, result.size());

		ServiceTask task = result.get(0);

		assertEquals(2L, task.getServiceRecordId());
		assertEquals("Brake inspection", task.getDescription());
		assertEquals(ServiceTaskStatus.COMPLETED, task.getStatus());
	}

	@Test
	void findByServiceRecordId_shouldReturnEmpty_whenServiceRecordHasNoTasks() {

		List<ServiceTask> result = serviceTaskRepository.findByServiceRecordId(999L);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}