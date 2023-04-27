package ru.example.lam.server.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.lam.server.entity.users.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
