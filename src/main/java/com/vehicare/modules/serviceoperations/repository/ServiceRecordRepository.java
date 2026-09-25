package com.vehicare.modules.serviceoperations.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.serviceoperations.entity.ServiceRecord;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord,Long> {
	Optional<ServiceRecord> findByAppointmentId(Long appointmentId);

    List<ServiceRecord> findByVehicleId(Long vehicleId);

    List<ServiceRecord> findByOwnerId(Long ownerId);

    boolean existsByAppointmentId(Long appointmentId);
}
