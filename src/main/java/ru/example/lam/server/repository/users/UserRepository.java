package ru.example.lam.server.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import ru.example.lam.server.entity.users.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    User findUserById(Long id);
    Set<User> findUsersByRoles(List<String> roleList);
    @Transactional
    long deleteUserById(Long id);
}
