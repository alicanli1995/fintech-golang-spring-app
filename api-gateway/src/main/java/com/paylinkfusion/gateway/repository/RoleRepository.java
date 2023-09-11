package com.paylinkfusion.gateway.repository;

import com.paylinkfusion.gateway.models.Role;
import com.paylinkfusion.gateway.models.enums.ERole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole eRole);
}
