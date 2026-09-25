package com.vehicare.modules.serviceoperations.api;

import java.util.List;

import com.vehicare.modules.serviceoperations.dto.ServiceRecordDto;
import com.vehicare.modules.serviceoperations.dto.ServiceRecordRequest;
import com.vehicare.modules.serviceoperations.dto.ServiceTaskDto;
import com.vehicare.modules.serviceoperations.dto.ServiceTaskRequest;

public interface ServiceOperationsService {

    ServiceRecordDto createServiceRecord(ServiceRecordRequest request);

    ServiceRecordDto getServiceRecordById(Long id);

    ServiceRecordDto getServiceRecordByAppointmentId(Long appointmentId);

    List<ServiceRecordDto> getServiceRecordsByVehicleId(Long vehicleId);

    List<ServiceRecordDto> getServiceRecordsByOwnerId(Long ownerId);

    ServiceRecordDto startService(Long id);

    ServiceRecordDto completeService(Long id);

    ServiceRecordDto cancelService(Long id);

    ServiceTaskDto addTask(Long serviceRecordId, ServiceTaskRequest request);

    ServiceTaskDto startTask(Long taskId);

    ServiceTaskDto completeTask(Long taskId);

    ServiceTaskDto cancelTask(Long taskId);
}