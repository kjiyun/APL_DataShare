package com.dtest.drools.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByUsername(String username);
}
