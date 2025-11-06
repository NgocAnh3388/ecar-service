package com.ecar.ecarservice.controller;

// import com.ecar.ecarservice.entities.SparePart; // Không cần thiết nữa

import com.ecar.ecarservice.dto.SparePartCreateDTO;
import com.ecar.ecarservice.dto.SparePartDTO;
import com.ecar.ecarservice.dto.StockAlertDTO;
import com.ecar.ecarservice.dto.StockUpdateDTO;
import com.ecar.ecarservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spare-parts")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // --- Lấy danh sách tất cả phụ tùng ---
    @GetMapping
    public ResponseEntity<List<SparePartDTO>> getAllParts() {
        return ResponseEntity.ok(inventoryService.getAllParts());
    }

    // --- Lấy danh sách phụ tùng theo xe ---
    @GetMapping("/by-car/{carModelId}")
    public ResponseEntity<List<SparePartDTO>> getPartsByCar(@PathVariable Long carModelId) {
        return ResponseEntity.ok(inventoryService.getPartsByCarModel(carModelId));
    }

    // --- Lấy danh sách phụ tùng sắp hết kho ---
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<StockAlertDTO>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }

    // --- Tạo mới phụ tùng ---
    @PostMapping
    public ResponseEntity<SparePartDTO> createPart(@Valid @RequestBody SparePartCreateDTO createDto) {
        // SỬA: Service giờ đã trả về DTO
        SparePartDTO createdDto = inventoryService.createPart(createDto);
        return ResponseEntity.ok(createdDto);
    }

    // --- Cập nhật phụ tùng ---
    @PutMapping("/{id}")
    public ResponseEntity<SparePartDTO> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody SparePartCreateDTO updateDto) {
        // SỬA: Service giờ đã trả về DTO
        SparePartDTO updatedDto = inventoryService.updatePart(id, updateDto);
        return ResponseEntity.ok(updatedDto);
    }

    // --- Cập nhật tồn kho ---
    @PatchMapping("/{id}/stock")
    public ResponseEntity<SparePartDTO> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO stockDto) {
        // SỬA: Service giờ đã trả về DTO
        SparePartDTO updatedDto = inventoryService.updateStock(id, stockDto);
        return ResponseEntity.ok(updatedDto);
    }

    // --- Xóa phụ tùng ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        inventoryService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------- Helper -------------------
    // XÓA: Hàm private toDTO(SparePart part) đã được di chuyển vào Service
    // và không còn cần thiết ở đây nữa.
}
