package com.vehicare.modules.appointment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByServiceAdvisorId(Long serviceAdvisorId);

	Optional<Appointment> findByIdAndServiceAdvisorId(Long id, Long serviceAdvisorId);

	List<Appointment> findByOwnerId(Long ownerId);

	List<Appointment> findByVehicleId(Long vehicleId);
}