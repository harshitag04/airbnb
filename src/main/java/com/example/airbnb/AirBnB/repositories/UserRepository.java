package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);
}
