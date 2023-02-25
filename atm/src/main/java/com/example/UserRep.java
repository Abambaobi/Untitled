package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRep extends JpaRepository<AtmUser, Long> {
//    Optional<AtmUser> findByEmail(String email);

    Optional<AtmUser> findByUsername(String username);
//    Optional<User> findByUsername(String username);
//    @Query(value = "SELECT firstname FROM atmuser u  WHERE u.firstname =:h", nativeQuery = true)
//    Optional<AtmUser> findByFirstName(@Param("h") String last);
}
