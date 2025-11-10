package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findBySub(String sub);
    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByActiveTrueOrderByCreatedAtDesc();

    Optional<AppUser> findByIdAndActiveTrue(Long id);

    @Query("SELECT au FROM AppUser au " +
            "LEFT JOIN FETCH au.vehicles v " +
            "LEFT JOIN FETCH v.carModel cm " +  // thêm alias cm để Hibernate chắc chắn nhận dạng đúng
            "WHERE au.id = :id AND au.active = true")
    Optional<AppUser> findByIdWithVehicles(@Param("id") Long id);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.vehicles v LEFT JOIN FETCH v.carModel WHERE u.email = :email")
    Optional<AppUser> findByEmailWithVehicles(@Param("email") String email);

    /**
     * BƯỚC 1: Tìm kiếm và chỉ trả về ID của các user. Rất nhanh và hiệu quả.
     */
    @Query(value = "SELECT au.id FROM app_user au WHERE " +
            "LOWER(unaccent(au.full_name)) LIKE LOWER(unaccent(CONCAT('%', :searchValue, '%'))) " +
            "OR LOWER(au.email) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
            "OR au.phone_no LIKE CONCAT('%', :searchValue, '%')",
            countQuery = "SELECT count(*) FROM app_user au WHERE " +
                    "LOWER(unaccent(au.full_name)) LIKE LOWER(unaccent(CONCAT('%', :searchValue, '%'))) " +
                    "OR LOWER(au.email) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
                    "OR au.phone_no LIKE CONCAT('%', :searchValue, '%')",
            nativeQuery = true)
    Page<Long> searchUserIdsByValue(@Param("searchValue") String searchValue, Pageable pageable);

    /**
     * BƯỚC 2: Từ danh sách ID, lấy đầy đủ thông tin User, Roles, và Vehicles trong 1 query duy nhất.
     */
    @Query("SELECT DISTINCT u FROM AppUser u " +
            "LEFT JOIN FETCH u.roles " +
            "LEFT JOIN FETCH u.vehicles v " +
            "LEFT JOIN FETCH v.carModel " +
            "WHERE u.id IN :ids")
    List<AppUser> findAllWithDetailsByIds(@Param("ids") List<Long> ids);

    List<AppUser> findByRoles(AppRole role);
}