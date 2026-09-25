package com.vehicare.modules.serviceoperations.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.serviceoperations.entity.ServiceTask;

public interface ServiceTaskRepository extends JpaRepository<ServiceTask,Long> {
	
	 List<ServiceTask> findByServiceRecordId(Long serviceRecordId);

}
