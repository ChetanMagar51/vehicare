package com.vehicare.modules.serviceadvisor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.serviceadvisor.entity.ServiceAdvisorProfile;

public interface ServiceAdvisorProfileRepository extends JpaRepository<ServiceAdvisorProfile, Long> {

	Optional<ServiceAdvisorProfile> findByUserId(Long userId);

	Optional<ServiceAdvisorProfile> findByEmployeeId(String employeeId);

	boolean existsByUserId(Long userId);

	boolean existsByEmployeeId(String employeeId);
}