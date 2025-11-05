package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findBySub(String sub);
    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByActiveTrue();
    Optional<AppUser> findByIdAndActiveTrue(Long id);

    @Query("SELECT au FROM AppUser au WHERE au.email LIKE %:searchValue%")
    Page<AppUser> searchAppUserByValue(@Param("searchValue") String searchValue,
                                       Pageable pageable);

    List<AppUser> findByRoles(AppRole role);
}
