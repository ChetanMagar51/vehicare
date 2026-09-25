package com.vehicare.modules.serviceoperations.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehicare.modules.serviceoperations.api.ServiceOperationsService;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceOperationsServiceImpl implements ServiceOperationsService {

	private final ServiceRecordRepository serviceRecordRepository;
	private final ServiceTaskRepository serviceTaskRepository;

	@Override
	@Transactional
	public ServiceRecordDto createServiceRecord(ServiceRecordRequest request) {

		validateServiceRecordRequest(request);

		if (serviceRecordRepository.existsByAppointmentId(request.getAppointmentId())) {
			throw new ServiceRecordAlreadyExistsException(
					"Service record already exists for appointment ID: " + request.getAppointmentId());
		}

		LocalDateTime now = LocalDateTime.now();

		ServiceRecord serviceRecord = ServiceRecord.builder().appointmentId(request.getAppointmentId())
				.vehicleId(request.getVehicleId()).ownerId(request.getOwnerId())
				.serviceAdvisorId(request.getServiceAdvisorId()).description(request.getDescription().trim())
				.status(ServiceStatus.CREATED).createdAt(now).updatedAt(now).build();

		ServiceRecord savedRecord = serviceRecordRepository.save(serviceRecord);

		return mapToDto(savedRecord);
	}

	@Override
	public ServiceRecordDto getServiceRecordById(Long id) {

		ServiceRecord serviceRecord = findServiceRecordById(id);

		return mapToDto(serviceRecord);
	}

	@Override
	public ServiceRecordDto getServiceRecordByAppointmentId(Long appointmentId) {

		validateId(appointmentId, "Appointment ID");

		ServiceRecord serviceRecord = serviceRecordRepository.findByAppointmentId(appointmentId)
				.orElseThrow(() -> new ServiceRecordNotFoundException(
						"Service record not found for appointment ID: " + appointmentId));

		return mapToDto(serviceRecord);
	}

	@Override
	public List<ServiceRecordDto> getServiceRecordsByVehicleId(Long vehicleId) {

		validateId(vehicleId, "Vehicle ID");

		return serviceRecordRepository.findByVehicleId(vehicleId).stream().map(this::mapToDto).toList();
	}

	@Override
	public List<ServiceRecordDto> getServiceRecordsByOwnerId(Long ownerId) {

		validateId(ownerId, "Owner ID");

		return serviceRecordRepository.findByOwnerId(ownerId).stream().map(this::mapToDto).toList();
	}

	@Override
	@Transactional
	public ServiceRecordDto startService(Long id) {

		ServiceRecord serviceRecord = findServiceRecordById(id);

		if (serviceRecord.getStatus() != ServiceStatus.CREATED) {
			throw new InvalidServiceStatusException("Service can only be started when its status is CREATED. "
					+ "Current status: " + serviceRecord.getStatus());
		}

		LocalDateTime now = LocalDateTime.now();

		serviceRecord.setStatus(ServiceStatus.IN_PROGRESS);
		serviceRecord.setStartedAt(now);
		serviceRecord.setUpdatedAt(now);

		return mapToDto(serviceRecordRepository.save(serviceRecord));
	}

	@Override
	@Transactional
	public ServiceRecordDto completeService(Long id) {

		ServiceRecord serviceRecord = findServiceRecordById(id);

		if (serviceRecord.getStatus() != ServiceStatus.IN_PROGRESS) {
			throw new InvalidServiceStatusException("Service can only be completed when its status is IN_PROGRESS. "
					+ "Current status: " + serviceRecord.getStatus());
		}

		validateAllTasksCompleted(serviceRecord.getId());

		LocalDateTime now = LocalDateTime.now();

		serviceRecord.setStatus(ServiceStatus.COMPLETED);
		serviceRecord.setCompletedAt(now);
		serviceRecord.setUpdatedAt(now);

		return mapToDto(serviceRecordRepository.save(serviceRecord));
	}

	@Override
	@Transactional
	public ServiceRecordDto cancelService(Long id) {

		ServiceRecord serviceRecord = findServiceRecordById(id);

		if (serviceRecord.getStatus() == ServiceStatus.COMPLETED) {
			throw new InvalidServiceStatusException("Completed service cannot be cancelled.");
		}

		if (serviceRecord.getStatus() == ServiceStatus.CANCELLED) {
			throw new InvalidServiceStatusException("Service is already cancelled.");
		}

		LocalDateTime now = LocalDateTime.now();

		serviceRecord.setStatus(ServiceStatus.CANCELLED);
		serviceRecord.setUpdatedAt(now);

		cancelPendingTasks(serviceRecord.getId());

		return mapToDto(serviceRecordRepository.save(serviceRecord));
	}

	@Override
	@Transactional
	public ServiceTaskDto addTask(Long serviceRecordId, ServiceTaskRequest request) {

		validateId(serviceRecordId, "Service record ID");
		validateTaskRequest(request);

		ServiceRecord serviceRecord = findServiceRecordById(serviceRecordId);

		if (serviceRecord.getStatus() != ServiceStatus.CREATED
				&& serviceRecord.getStatus() != ServiceStatus.IN_PROGRESS) {

			throw new InvalidServiceStatusException(
					"Tasks cannot be added when service status is " + serviceRecord.getStatus());
		}

		ServiceTask task = ServiceTask.builder().serviceRecordId(serviceRecordId)
				.description(request.getDescription().trim()).status(ServiceTaskStatus.PENDING).build();

		return mapToDto(serviceTaskRepository.save(task));
	}

	@Override
	@Transactional
	public ServiceTaskDto startTask(Long taskId) {

		ServiceTask task = findServiceTaskById(taskId);

		ServiceRecord serviceRecord = findServiceRecordById(task.getServiceRecordId());

		if (serviceRecord.getStatus() != ServiceStatus.IN_PROGRESS) {
			throw new InvalidServiceStatusException("A task can only be started when the service is IN_PROGRESS. "
					+ "Current service status: " + serviceRecord.getStatus());
		}

		if (task.getStatus() != ServiceTaskStatus.PENDING) {
			throw new InvalidServiceTaskStatusException(
					"Task can only be started when its status is PENDING. " + "Current status: " + task.getStatus());
		}

		task.setStatus(ServiceTaskStatus.IN_PROGRESS);
		task.setStartedAt(LocalDateTime.now());

		return mapToDto(serviceTaskRepository.save(task));
	}

	@Override
	@Transactional
	public ServiceTaskDto completeTask(Long taskId) {

		ServiceTask task = findServiceTaskById(taskId);

		if (task.getStatus() != ServiceTaskStatus.IN_PROGRESS) {
			throw new InvalidServiceTaskStatusException("Task can only be completed when its status is IN_PROGRESS. "
					+ "Current status: " + task.getStatus());
		}

		task.setStatus(ServiceTaskStatus.COMPLETED);
		task.setCompletedAt(LocalDateTime.now());

		return mapToDto(serviceTaskRepository.save(task));
	}

	@Override
	@Transactional
	public ServiceTaskDto cancelTask(Long taskId) {

		ServiceTask task = findServiceTaskById(taskId);

		if (task.getStatus() == ServiceTaskStatus.COMPLETED) {
			throw new InvalidServiceTaskStatusException("Completed task cannot be cancelled.");
		}

		if (task.getStatus() == ServiceTaskStatus.CANCELLED) {
			throw new InvalidServiceTaskStatusException("Task is already cancelled.");
		}

		task.setStatus(ServiceTaskStatus.CANCELLED);

		return mapToDto(serviceTaskRepository.save(task));
	}

	// -------------------------------------------------------------------------
	// Private helper methods
	// -------------------------------------------------------------------------

	private ServiceRecord findServiceRecordById(Long id) {

		validateId(id, "Service record ID");

		return serviceRecordRepository.findById(id)
				.orElseThrow(() -> new ServiceRecordNotFoundException("Service record not found with ID: " + id));
	}

	private ServiceTask findServiceTaskById(Long id) {

		validateId(id, "Service task ID");

		return serviceTaskRepository.findById(id)
				.orElseThrow(() -> new ServiceTaskNotFoundException("Service task not found with ID: " + id));
	}

	private void validateAllTasksCompleted(Long serviceRecordId) {

		List<ServiceTask> tasks = serviceTaskRepository.findByServiceRecordId(serviceRecordId);

		boolean hasIncompleteTask = tasks.stream().anyMatch(task -> task.getStatus() != ServiceTaskStatus.COMPLETED
				&& task.getStatus() != ServiceTaskStatus.CANCELLED);

		if (hasIncompleteTask) {
			throw new InvalidServiceStatusException("Service cannot be completed while there are incomplete tasks.");
		}
	}

	private void cancelPendingTasks(Long serviceRecordId) {

		List<ServiceTask> tasks = serviceTaskRepository.findByServiceRecordId(serviceRecordId);

		tasks.stream()
				.filter(task -> task.getStatus() != ServiceTaskStatus.COMPLETED
						&& task.getStatus() != ServiceTaskStatus.CANCELLED)
				.forEach(task -> task.setStatus(ServiceTaskStatus.CANCELLED));

		serviceTaskRepository.saveAll(tasks);
	}

	private void validateServiceRecordRequest(ServiceRecordRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Service record request cannot be null.");
		}

		validateId(request.getAppointmentId(), "Appointment ID");
		validateId(request.getVehicleId(), "Vehicle ID");
		validateId(request.getOwnerId(), "Owner ID");
		validateId(request.getServiceAdvisorId(), "Service advisor ID");

		if (request.getDescription() == null || request.getDescription().isBlank()) {

			throw new IllegalArgumentException("Service description is required.");
		}
	}

	private void validateTaskRequest(ServiceTaskRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Service task request cannot be null.");
		}

		if (request.getDescription() == null || request.getDescription().isBlank()) {

			throw new IllegalArgumentException("Task description is required.");
		}
	}

	private void validateId(Long id, String fieldName) {

		if (id == null || id <= 0) {
			throw new IllegalArgumentException(fieldName + " must be greater than zero.");
		}
	}

	private ServiceRecordDto mapToDto(ServiceRecord serviceRecord) {

		return ServiceRecordDto.builder().id(serviceRecord.getId()).appointmentId(serviceRecord.getAppointmentId())
				.vehicleId(serviceRecord.getVehicleId()).ownerId(serviceRecord.getOwnerId())
				.serviceAdvisorId(serviceRecord.getServiceAdvisorId()).description(serviceRecord.getDescription())
				.status(serviceRecord.getStatus()).startedAt(serviceRecord.getStartedAt())
				.completedAt(serviceRecord.getCompletedAt()).createdAt(serviceRecord.getCreatedAt())
				.updatedAt(serviceRecord.getUpdatedAt()).build();
	}

	private ServiceTaskDto mapToDto(ServiceTask task) {

		return ServiceTaskDto.builder().id(task.getId()).serviceRecordId(task.getServiceRecordId())
				.description(task.getDescription()).status(task.getStatus()).startedAt(task.getStartedAt())
				.completedAt(task.getCompletedAt()).build();
	}
}