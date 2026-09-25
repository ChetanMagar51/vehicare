package com.vehicare.modules.serviceoperations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicare.modules.serviceoperations.dto.ServiceRecordDto;
import com.vehicare.modules.serviceoperations.dto.ServiceRecordRequest;
import com.vehicare.modules.serviceoperations.dto.ServiceTaskDto;
import com.vehicare.modules.serviceoperations.dto.ServiceTaskRequest;
import com.vehicare.modules.serviceoperations.entity.ServiceRecord;
import com.vehicare.modules.serviceoperations.entity.ServiceTask;
import com.vehicare.modules.serviceoperations.enumtype.ServiceStatus;
import com.vehicare.modules.serviceoperations.enumtype.ServiceTaskStatus;
import com.vehicare.modules.serviceoperations.exception.InvalidServiceStatusException;
import com.vehicare.modules.serviceoperations.exception.InvalidServiceTaskStatusException;
import com.vehicare.modules.serviceoperations.exception.ServiceRecordAlreadyExistsException;
import com.vehicare.modules.serviceoperations.exception.ServiceRecordNotFoundException;
import com.vehicare.modules.serviceoperations.exception.ServiceTaskNotFoundException;
import com.vehicare.modules.serviceoperations.repository.ServiceRecordRepository;
import com.vehicare.modules.serviceoperations.repository.ServiceTaskRepository;

@ExtendWith(MockitoExtension.class)
class ServiceOperationsServiceImplTest {

	@Mock
	private ServiceRecordRepository serviceRecordRepository;

	@Mock
	private ServiceTaskRepository serviceTaskRepository;

	@InjectMocks
	private ServiceOperationsServiceImpl serviceOperationsService;

	private ServiceRecord serviceRecord;
	private ServiceTask pendingTask;
	private ServiceTask inProgressTask;
	private ServiceTask completedTask;
	private ServiceTask cancelledTask;

	@BeforeEach
	void setUp() {

		serviceRecord = ServiceRecord.builder().id(1L).appointmentId(101L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("Regular vehicle service").status(ServiceStatus.CREATED).build();

		pendingTask = ServiceTask.builder().id(1L).serviceRecordId(1L).description("Engine oil replacement")
				.status(ServiceTaskStatus.PENDING).build();

		inProgressTask = ServiceTask.builder().id(2L).serviceRecordId(1L).description("Brake inspection")
				.status(ServiceTaskStatus.IN_PROGRESS).build();

		completedTask = ServiceTask.builder().id(3L).serviceRecordId(1L).description("Oil filter replacement")
				.status(ServiceTaskStatus.COMPLETED).build();

		cancelledTask = ServiceTask.builder().id(4L).serviceRecordId(1L).description("Air filter replacement")
				.status(ServiceTaskStatus.CANCELLED).build();
	}

	// =========================================================
	// createServiceRecord()
	// =========================================================

	@Test
	void createServiceRecord_shouldCreateRecordSuccessfully() {

		ServiceRecordRequest request = ServiceRecordRequest.builder().appointmentId(101L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("  Regular vehicle service  ").build();

		when(serviceRecordRepository.existsByAppointmentId(101L)).thenReturn(false);

		when(serviceRecordRepository.save(any(ServiceRecord.class))).thenAnswer(invocation -> {
			ServiceRecord record = invocation.getArgument(0);
			record.setId(1L);
			return record;
		});

		ServiceRecordDto result = serviceOperationsService.createServiceRecord(request);

		assertNotNull(result);

		assertEquals(1L, result.getId());
		assertEquals(101L, result.getAppointmentId());
		assertEquals(201L, result.getVehicleId());
		assertEquals(301L, result.getOwnerId());
		assertEquals(401L, result.getServiceAdvisorId());

		assertEquals("Regular vehicle service", result.getDescription());
		assertEquals(ServiceStatus.CREATED, result.getStatus());

		assertNotNull(result.getCreatedAt());
		assertNotNull(result.getUpdatedAt());

		verify(serviceRecordRepository).existsByAppointmentId(101L);
		verify(serviceRecordRepository).save(any(ServiceRecord.class));
	}

	@Test
	void createServiceRecord_shouldThrowException_whenAppointmentAlreadyHasServiceRecord() {

		ServiceRecordRequest request = ServiceRecordRequest.builder().appointmentId(101L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("Regular service").build();

		when(serviceRecordRepository.existsByAppointmentId(101L)).thenReturn(true);

		assertThrows(ServiceRecordAlreadyExistsException.class,
				() -> serviceOperationsService.createServiceRecord(request));

		verify(serviceRecordRepository).existsByAppointmentId(101L);
		verify(serviceRecordRepository, never()).save(any());
	}

	@Test
	void createServiceRecord_shouldThrowException_whenRequestIsNull() {

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.createServiceRecord(null));

		verifyNoInteractions(serviceRecordRepository);
	}

	@Test
	void createServiceRecord_shouldThrowException_whenAppointmentIdIsInvalid() {

		ServiceRecordRequest request = ServiceRecordRequest.builder().appointmentId(0L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("Regular service").build();

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.createServiceRecord(request));

		verifyNoInteractions(serviceRecordRepository);
	}

	@Test
	void createServiceRecord_shouldThrowException_whenDescriptionIsBlank() {

		ServiceRecordRequest request = ServiceRecordRequest.builder().appointmentId(101L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(401L).description("   ").build();

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.createServiceRecord(request));

		verifyNoInteractions(serviceRecordRepository);
	}

	// =========================================================
	// getServiceRecordById()
	// =========================================================

	@Test
	void getServiceRecordById_shouldReturnRecord_whenExists() {

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		ServiceRecordDto result = serviceOperationsService.getServiceRecordById(1L);

		assertNotNull(result);

		assertEquals(1L, result.getId());
		assertEquals(101L, result.getAppointmentId());
		assertEquals(201L, result.getVehicleId());
		assertEquals(301L, result.getOwnerId());
		assertEquals(401L, result.getServiceAdvisorId());
		assertEquals("Regular vehicle service", result.getDescription());
		assertEquals(ServiceStatus.CREATED, result.getStatus());

		verify(serviceRecordRepository).findById(1L);
	}

	@Test
	void getServiceRecordById_shouldThrowException_whenRecordDoesNotExist() {

		when(serviceRecordRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceRecordNotFoundException.class, () -> serviceOperationsService.getServiceRecordById(999L));

		verify(serviceRecordRepository).findById(999L);
	}

	@Test
	void getServiceRecordById_shouldThrowException_whenIdIsInvalid() {

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.getServiceRecordById(0L));

		verifyNoInteractions(serviceRecordRepository);
	}

	// =========================================================
	// getServiceRecordByAppointmentId()
	// =========================================================

	@Test
	void getServiceRecordByAppointmentId_shouldReturnRecord_whenExists() {

		when(serviceRecordRepository.findByAppointmentId(101L)).thenReturn(Optional.of(serviceRecord));

		ServiceRecordDto result = serviceOperationsService.getServiceRecordByAppointmentId(101L);

		assertNotNull(result);
		assertEquals(101L, result.getAppointmentId());
		assertEquals(ServiceStatus.CREATED, result.getStatus());

		verify(serviceRecordRepository).findByAppointmentId(101L);
	}

	@Test
	void getServiceRecordByAppointmentId_shouldThrowException_whenNotFound() {

		when(serviceRecordRepository.findByAppointmentId(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceRecordNotFoundException.class,
				() -> serviceOperationsService.getServiceRecordByAppointmentId(999L));

		verify(serviceRecordRepository).findByAppointmentId(999L);
	}

	@Test
	void getServiceRecordByAppointmentId_shouldThrowException_whenIdIsInvalid() {

		assertThrows(IllegalArgumentException.class,
				() -> serviceOperationsService.getServiceRecordByAppointmentId(0L));

		verifyNoInteractions(serviceRecordRepository);
	}

	// =========================================================
	// getServiceRecordsByVehicleId()
	// =========================================================

	@Test
	void getServiceRecordsByVehicleId_shouldReturnRecords() {

		ServiceRecord secondRecord = ServiceRecord.builder().id(2L).appointmentId(102L).vehicleId(201L).ownerId(301L)
				.serviceAdvisorId(402L).description("Oil change").status(ServiceStatus.COMPLETED).build();

		when(serviceRecordRepository.findByVehicleId(201L)).thenReturn(List.of(serviceRecord, secondRecord));

		List<ServiceRecordDto> result = serviceOperationsService.getServiceRecordsByVehicleId(201L);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertTrue(result.stream().allMatch(record -> record.getVehicleId().equals(201L)));

		verify(serviceRecordRepository).findByVehicleId(201L);
	}

	@Test
	void getServiceRecordsByVehicleId_shouldReturnEmptyList_whenNoRecordsExist() {

		when(serviceRecordRepository.findByVehicleId(999L)).thenReturn(List.of());

		List<ServiceRecordDto> result = serviceOperationsService.getServiceRecordsByVehicleId(999L);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(serviceRecordRepository).findByVehicleId(999L);
	}

	@Test
	void getServiceRecordsByVehicleId_shouldThrowException_whenIdIsInvalid() {

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.getServiceRecordsByVehicleId(0L));

		verifyNoInteractions(serviceRecordRepository);
	}

	// =========================================================
	// getServiceRecordsByOwnerId()
	// =========================================================

	@Test
	void getServiceRecordsByOwnerId_shouldReturnRecords() {

		ServiceRecord secondRecord = ServiceRecord.builder().id(2L).appointmentId(102L).vehicleId(202L).ownerId(301L)
				.serviceAdvisorId(402L).description("Oil change").status(ServiceStatus.COMPLETED).build();

		when(serviceRecordRepository.findByOwnerId(301L)).thenReturn(List.of(serviceRecord, secondRecord));

		List<ServiceRecordDto> result = serviceOperationsService.getServiceRecordsByOwnerId(301L);

		assertNotNull(result);
		assertEquals(2, result.size());

		assertTrue(result.stream().allMatch(record -> record.getOwnerId().equals(301L)));

		verify(serviceRecordRepository).findByOwnerId(301L);
	}

	@Test
	void getServiceRecordsByOwnerId_shouldReturnEmptyList_whenNoRecordsExist() {

		when(serviceRecordRepository.findByOwnerId(999L)).thenReturn(List.of());

		List<ServiceRecordDto> result = serviceOperationsService.getServiceRecordsByOwnerId(999L);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(serviceRecordRepository).findByOwnerId(999L);
	}

	// =========================================================
	// startService()
	// =========================================================

	@Test
	void startService_shouldChangeStatusToInProgress() {

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceRecordRepository.save(any(ServiceRecord.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ServiceRecordDto result = serviceOperationsService.startService(1L);

		assertEquals(ServiceStatus.IN_PROGRESS, result.getStatus());
		assertNotNull(result.getStartedAt());
		assertNotNull(result.getUpdatedAt());

		verify(serviceRecordRepository).save(serviceRecord);
	}

	@Test
	void startService_shouldThrowException_whenServiceIsNotCreated() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.startService(1L));

		verify(serviceRecordRepository, never()).save(any());
	}

	@Test
	void startService_shouldThrowException_whenServiceDoesNotExist() {

		when(serviceRecordRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceRecordNotFoundException.class, () -> serviceOperationsService.startService(999L));
	}

	// =========================================================
	// completeService()
	// =========================================================

	@Test
	void completeService_shouldCompleteService_whenAllTasksAreCompleted() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.findByServiceRecordId(1L)).thenReturn(List.of(completedTask, cancelledTask));

		when(serviceRecordRepository.save(any(ServiceRecord.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ServiceRecordDto result = serviceOperationsService.completeService(1L);

		assertEquals(ServiceStatus.COMPLETED, result.getStatus());
		assertNotNull(result.getCompletedAt());
		assertNotNull(result.getUpdatedAt());

		verify(serviceTaskRepository).findByServiceRecordId(1L);
		verify(serviceRecordRepository).save(serviceRecord);
	}

	@Test
	void completeService_shouldThrowException_whenServiceIsNotInProgress() {

		serviceRecord.setStatus(ServiceStatus.CREATED);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.completeService(1L));

		verify(serviceTaskRepository, never()).findByServiceRecordId(anyLong());
		verify(serviceRecordRepository, never()).save(any());
	}

	@Test
	void completeService_shouldThrowException_whenIncompleteTaskExists() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.findByServiceRecordId(1L)).thenReturn(List.of(completedTask, pendingTask));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.completeService(1L));

		verify(serviceTaskRepository).findByServiceRecordId(1L);
		verify(serviceRecordRepository, never()).save(any());
	}

	@Test
	void completeService_shouldAllowCompletion_whenOnlyCancelledTasksExist() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.findByServiceRecordId(1L)).thenReturn(List.of(cancelledTask));

		when(serviceRecordRepository.save(any(ServiceRecord.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ServiceRecordDto result = serviceOperationsService.completeService(1L);

		assertEquals(ServiceStatus.COMPLETED, result.getStatus());
	}

	@Test
	void completeService_shouldAllowCompletion_whenNoTasksExist() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.findByServiceRecordId(1L)).thenReturn(List.of());

		when(serviceRecordRepository.save(any(ServiceRecord.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ServiceRecordDto result = serviceOperationsService.completeService(1L);

		assertEquals(ServiceStatus.COMPLETED, result.getStatus());
	}

	// =========================================================
	// cancelService()
	// =========================================================

	@Test
	void cancelService_shouldCancelServiceAndPendingTasks() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.findByServiceRecordId(1L))
				.thenReturn(List.of(pendingTask, inProgressTask, completedTask, cancelledTask));

		when(serviceRecordRepository.save(any(ServiceRecord.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ServiceRecordDto result = serviceOperationsService.cancelService(1L);

		assertEquals(ServiceStatus.CANCELLED, result.getStatus());
		assertNotNull(result.getUpdatedAt());

		assertEquals(ServiceTaskStatus.CANCELLED, pendingTask.getStatus());
		assertEquals(ServiceTaskStatus.CANCELLED, inProgressTask.getStatus());

		assertEquals(ServiceTaskStatus.COMPLETED, completedTask.getStatus());
		assertEquals(ServiceTaskStatus.CANCELLED, cancelledTask.getStatus());

		verify(serviceTaskRepository).findByServiceRecordId(1L);
		verify(serviceTaskRepository).saveAll(anyList());
		verify(serviceRecordRepository).save(serviceRecord);
	}

	@Test
	void cancelService_shouldThrowException_whenServiceIsCompleted() {

		serviceRecord.setStatus(ServiceStatus.COMPLETED);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.cancelService(1L));

		verify(serviceTaskRepository, never()).findByServiceRecordId(anyLong());
		verify(serviceRecordRepository, never()).save(any());
	}

	@Test
	void cancelService_shouldThrowException_whenServiceIsAlreadyCancelled() {

		serviceRecord.setStatus(ServiceStatus.CANCELLED);

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.cancelService(1L));

		verify(serviceTaskRepository, never()).findByServiceRecordId(anyLong());
		verify(serviceRecordRepository, never()).save(any());
	}

	// =========================================================
	// addTask()
	// =========================================================

	@Test
	void addTask_shouldCreatePendingTask() {

		ServiceTaskRequest request = ServiceTaskRequest.builder().description("  Engine oil replacement  ").build();

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> {
			ServiceTask task = invocation.getArgument(0);
			task.setId(10L);
			return task;
		});

		ServiceTaskDto result = serviceOperationsService.addTask(1L, request);

		assertNotNull(result);
		assertEquals(10L, result.getId());
		assertEquals(1L, result.getServiceRecordId());
		assertEquals("Engine oil replacement", result.getDescription());
		assertEquals(ServiceTaskStatus.PENDING, result.getStatus());

		verify(serviceTaskRepository).save(any(ServiceTask.class));
	}

	@Test
	void addTask_shouldAllowTask_whenServiceIsInProgress() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		ServiceTaskRequest request = ServiceTaskRequest.builder().description("Brake inspection").build();

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ServiceTaskDto result = serviceOperationsService.addTask(1L, request);

		assertEquals(ServiceTaskStatus.PENDING, result.getStatus());

		verify(serviceTaskRepository).save(any(ServiceTask.class));
	}

	@Test
	void addTask_shouldThrowException_whenServiceIsCompleted() {

		serviceRecord.setStatus(ServiceStatus.COMPLETED);

		ServiceTaskRequest request = ServiceTaskRequest.builder().description("Brake inspection").build();

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.addTask(1L, request));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void addTask_shouldThrowException_whenRequestIsNull() {

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.addTask(1L, null));

		verifyNoInteractions(serviceRecordRepository);
		verifyNoInteractions(serviceTaskRepository);
	}

	@Test
	void addTask_shouldThrowException_whenDescriptionIsBlank() {

		ServiceTaskRequest request = ServiceTaskRequest.builder().description("   ").build();

		assertThrows(IllegalArgumentException.class, () -> serviceOperationsService.addTask(1L, request));

		verifyNoInteractions(serviceRecordRepository);
		verifyNoInteractions(serviceTaskRepository);
	}

	// =========================================================
	// startTask()
	// =========================================================

	@Test
	void startTask_shouldChangeTaskToInProgress() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		when(serviceTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ServiceTaskDto result = serviceOperationsService.startTask(1L);

		assertEquals(ServiceTaskStatus.IN_PROGRESS, result.getStatus());
		assertNotNull(result.getStartedAt());

		verify(serviceTaskRepository).save(pendingTask);
	}

	@Test
	void startTask_shouldThrowException_whenServiceIsNotInProgress() {

		serviceRecord.setStatus(ServiceStatus.CREATED);

		when(serviceTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceStatusException.class, () -> serviceOperationsService.startTask(1L));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void startTask_shouldThrowException_whenTaskIsNotPending() {

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		inProgressTask.setId(1L);

		when(serviceTaskRepository.findById(1L)).thenReturn(Optional.of(inProgressTask));

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		assertThrows(InvalidServiceTaskStatusException.class, () -> serviceOperationsService.startTask(1L));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void startTask_shouldThrowException_whenTaskDoesNotExist() {

		when(serviceTaskRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceTaskNotFoundException.class, () -> serviceOperationsService.startTask(999L));

		verify(serviceRecordRepository, never()).findById(anyLong());
	}

	// =========================================================
	// completeTask()
	// =========================================================

	@Test
	void completeTask_shouldChangeTaskToCompleted() {

		when(serviceTaskRepository.findById(2L)).thenReturn(Optional.of(inProgressTask));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ServiceTaskDto result = serviceOperationsService.completeTask(2L);

		assertEquals(ServiceTaskStatus.COMPLETED, result.getStatus());
		assertNotNull(result.getCompletedAt());

		verify(serviceTaskRepository).save(inProgressTask);
	}

	@Test
	void completeTask_shouldThrowException_whenTaskIsNotInProgress() {

		when(serviceTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));

		assertThrows(InvalidServiceTaskStatusException.class, () -> serviceOperationsService.completeTask(1L));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void completeTask_shouldThrowException_whenTaskDoesNotExist() {

		when(serviceTaskRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceTaskNotFoundException.class, () -> serviceOperationsService.completeTask(999L));
	}

	// =========================================================
	// cancelTask()
	// =========================================================

	@Test
	void cancelTask_shouldCancelPendingTask() {

		when(serviceTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ServiceTaskDto result = serviceOperationsService.cancelTask(1L);

		assertEquals(ServiceTaskStatus.CANCELLED, result.getStatus());

		verify(serviceTaskRepository).save(pendingTask);
	}

	@Test
	void cancelTask_shouldCancelInProgressTask() {

		when(serviceTaskRepository.findById(2L)).thenReturn(Optional.of(inProgressTask));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ServiceTaskDto result = serviceOperationsService.cancelTask(2L);

		assertEquals(ServiceTaskStatus.CANCELLED, result.getStatus());

		verify(serviceTaskRepository).save(inProgressTask);
	}

	@Test
	void cancelTask_shouldThrowException_whenTaskIsCompleted() {

		when(serviceTaskRepository.findById(3L)).thenReturn(Optional.of(completedTask));

		assertThrows(InvalidServiceTaskStatusException.class, () -> serviceOperationsService.cancelTask(3L));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void cancelTask_shouldThrowException_whenTaskIsAlreadyCancelled() {

		when(serviceTaskRepository.findById(4L)).thenReturn(Optional.of(cancelledTask));

		assertThrows(InvalidServiceTaskStatusException.class, () -> serviceOperationsService.cancelTask(4L));

		verify(serviceTaskRepository, never()).save(any());
	}

	@Test
	void cancelTask_shouldThrowException_whenTaskDoesNotExist() {

		when(serviceTaskRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ServiceTaskNotFoundException.class, () -> serviceOperationsService.cancelTask(999L));
	}

	// =========================================================
	// DTO MAPPING
	// =========================================================

	@Test
	void getServiceRecordById_shouldMapAllFieldsToDto() {

		serviceRecord.setStartedAt(java.time.LocalDateTime.of(2026, 9, 25, 10, 0));

		serviceRecord.setCompletedAt(java.time.LocalDateTime.of(2026, 9, 25, 12, 0));

		serviceRecord.setCreatedAt(java.time.LocalDateTime.of(2026, 9, 24, 9, 0));

		serviceRecord.setUpdatedAt(java.time.LocalDateTime.of(2026, 9, 25, 12, 0));

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		ServiceRecordDto result = serviceOperationsService.getServiceRecordById(1L);

		assertEquals(serviceRecord.getId(), result.getId());
		assertEquals(serviceRecord.getAppointmentId(), result.getAppointmentId());
		assertEquals(serviceRecord.getVehicleId(), result.getVehicleId());
		assertEquals(serviceRecord.getOwnerId(), result.getOwnerId());
		assertEquals(serviceRecord.getServiceAdvisorId(), result.getServiceAdvisorId());
		assertEquals(serviceRecord.getDescription(), result.getDescription());
		assertEquals(serviceRecord.getStatus(), result.getStatus());
		assertEquals(serviceRecord.getStartedAt(), result.getStartedAt());
		assertEquals(serviceRecord.getCompletedAt(), result.getCompletedAt());
		assertEquals(serviceRecord.getCreatedAt(), result.getCreatedAt());
		assertEquals(serviceRecord.getUpdatedAt(), result.getUpdatedAt());
	}

	@Test
	void addTask_shouldMapAllFieldsToDto() {

		ServiceTask task = ServiceTask.builder().id(10L).serviceRecordId(1L).description("Engine inspection")
				.status(ServiceTaskStatus.IN_PROGRESS).startedAt(java.time.LocalDateTime.of(2026, 9, 25, 10, 0))
				.completedAt(null).build();

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);

		ServiceTaskRequest request = ServiceTaskRequest.builder().description("Engine inspection").build();

		when(serviceRecordRepository.findById(1L)).thenReturn(Optional.of(serviceRecord));

		when(serviceTaskRepository.save(any(ServiceTask.class))).thenReturn(task);

		ServiceTaskDto result = serviceOperationsService.addTask(1L, request);

		assertEquals(task.getId(), result.getId());
		assertEquals(task.getServiceRecordId(), result.getServiceRecordId());
		assertEquals(task.getDescription(), result.getDescription());
		assertEquals(task.getStatus(), result.getStatus());
		assertEquals(task.getStartedAt(), result.getStartedAt());
		assertEquals(task.getCompletedAt(), result.getCompletedAt());
	}
}