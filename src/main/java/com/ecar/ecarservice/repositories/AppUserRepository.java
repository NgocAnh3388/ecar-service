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

/**
 * Repository chịu trách nhiệm cho các thao tác CRUD và truy vấn phức tạp
 * liên quan đến thực thể AppUser.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // =================== TRUY VẤN CƠ BẢN ===================
    Optional<AppUser> findBySub(String sub);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByActiveTrueOrderByCreatedAtDesc();

    Optional<AppUser> findByIdAndActiveTrue(Long id);

    // =================== TRUY VẤN KÈM DỮ LIỆU LIÊN QUAN (FETCHING) ===================
    @Query("SELECT au FROM AppUser au LEFT JOIN FETCH au.vehicles v LEFT JOIN FETCH v.carModel cm WHERE au.id = :id AND au.active = true")
    Optional<AppUser> findByIdWithVehicles(@Param("id") Long id);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.vehicles v LEFT JOIN FETCH v.carModel WHERE u.email = :email")
    Optional<AppUser> findByEmailWithVehicles(@Param("email") String email);

    // =================== TÌM KIẾM PHÂN TRANG HIỆU NĂNG CAO ===================

    /**
     * BƯỚC 1: Tìm kiếm (không dấu) theo Tên, Email, SĐT và chỉ trả về ID của các user khớp.
     * Sử dụng nativeQuery để tận dụng hàm unaccent của PostgreSQL.
     */
    @Query(value = "SELECT au.id FROM app_user au WHERE " +
            "LOWER(unaccent(au.full_name)) LIKE LOWER(unaccent(CONCAT('%', :searchValue, '%'))) " +
            "OR LOWER(au.email) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
            "OR au.phone_no LIKE CONCAT('%', :searchValue, '%')",
            countQuery = "SELECT count(*) FROM app_user au WHERE ...", // Giữ nguyên
            nativeQuery = true)
    Page<Long> searchUserIdsByValue(@Param("searchValue") String searchValue, Pageable pageable);

    /**
     * BƯỚC 2: Từ danh sách ID, lấy đầy đủ thông tin chi tiết (bao gồm cả quan hệ) trong 1 query.
     */
    @Query("SELECT DISTINCT u FROM AppUser u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.vehicles v LEFT JOIN FETCH v.carModel WHERE u.id IN :ids")
    List<AppUser> findAllWithDetailsByIds(@Param("ids") List<Long> ids);

    // =================== TRUY VẤN THEO VAI TRÒ VÀ TRUNG TÂM ===================

    /**
     * Tìm người dùng theo một vai trò cụ thể.
     */
    List<AppUser> findByRoles(AppRole role);

    /**
     * Tìm người dùng chứa một vai trò cụ thể VÀ thuộc về một center cụ thể.
     * Dùng cho việc lọc Technician/Staff theo Center.
     */
    List<AppUser> findByRolesContainingAndCenterId(AppRole role, Long centerId);
    List<AppUser> findByRolesContainingAndCenterIdAndActiveTrue(AppRole role, Long centerId);
}