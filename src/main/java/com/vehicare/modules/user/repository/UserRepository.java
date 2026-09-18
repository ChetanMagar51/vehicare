package com.vehicare.modules.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicare.modules.user.entity.Role;
import com.vehicare.modules.user.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByEmail(String email);

    Optional<User> findByRoleAndId(Role role, Long id);

    List<User> findAllByRole(Role role);

    boolean existsByEmail(String email);




}
