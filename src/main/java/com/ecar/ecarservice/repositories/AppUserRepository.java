package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findBySub(String sub);
    Optional<AppUser> findByEmail(String email);
}
