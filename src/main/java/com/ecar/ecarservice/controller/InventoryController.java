package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.*;
import com.ecar.ecarservice.scheduler.InventoryAlertScheduler;
import com.ecar.ecarservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryAlertScheduler inventoryAlertScheduler;

    // =================== SPARE PART (Thông tin chung) ===================

    // --- Lấy danh sách tất cả phụ tùng ---
//    @GetMapping("/parts/all")
    @GetMapping("/parts")
    public ResponseEntity<List<SparePartDTO>> getAllParts() {
        return ResponseEntity.ok(inventoryService.getAllParts());
    }


    // --- Lấy danh sách phụ tùng theo xe ---
    @GetMapping("/parts/by-car/{carModelId}")
    public ResponseEntity<List<SparePartDTO>> getPartsByCar(@PathVariable Long carModelId) {
        return ResponseEntity.ok(inventoryService.getPartsByCarModel(carModelId));
    }

    // --- Tạo mới phụ tùng ---
    @PostMapping("/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SparePartDTO> createPart(@Valid @RequestBody SparePartCreateDTO createDto) {
        return ResponseEntity.ok(inventoryService.createPart(createDto));
    }

    // --- Cập nhật phụ tùng ---
    @PutMapping("/parts/{partId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SparePartDTO> updatePart(@PathVariable Long partId, @Valid @RequestBody SparePartCreateDTO updateDto) {
        return ResponseEntity.ok(inventoryService.updatePart(partId, updateDto));
    }

    // --- Xóa phụ tùng ---
    @DeleteMapping("/parts/{partId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deletePart(@PathVariable Long partId) {
        inventoryService.deletePart(partId);
        return ResponseEntity.noContent().build();
    }

    // =================== INVENTORY (Tồn kho theo Center) ===================

    // API Lấy danh sách tồn kho theo Center (Mới)
    @GetMapping("/by-center/{centerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<InventoryDTO>> getInventoryByCenter(@PathVariable Long centerId) {
        return ResponseEntity.ok(inventoryService.getInventoryByCenter(centerId));
    }

    // --- Cập nhật tồn kho (Nhập kho/Điều chỉnh) ---
    @PatchMapping("/{inventoryId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InventoryDTO> updateStock(@PathVariable Long inventoryId, @Valid @RequestBody StockUpdateDTO stockDto) {
        return ResponseEntity.ok(inventoryService.updateStock(inventoryId, stockDto));
    }

    // --- Lấy danh sách phụ tùng sắp hết kho ---
    @GetMapping("/low-stock/by-center/{centerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<InventoryDTO>> getLowStockByCenter(@PathVariable Long centerId) {
        return ResponseEntity.ok(inventoryService.getLowStockAlertsByCenter(centerId));
    }

    @GetMapping("/by-part/{partId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<InventoryDTO>> getStockAcrossCenters(@PathVariable Long partId) {
        return ResponseEntity.ok(inventoryService.getStockAcrossCenters(partId));
    }

    // =================== SUGGESTIONS ===================
    @GetMapping("/suggestions")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<SparePartSuggestionDTO>> getPartSuggestions(
            @RequestParam Long centerId,
            @RequestParam Long carModelId,
            @RequestParam List<Long> serviceIds) {
        return ResponseEntity.ok(inventoryService.getPartSuggestions(centerId, carModelId, serviceIds));
    }

    @GetMapping("/used-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<UsedPartHistoryDTO>> getUsedPartsHistory() {
        return ResponseEntity.ok(inventoryService.getUsedPartsHistory());
    }

    @GetMapping("/test/trigger-report") // Test-only endpoint
    public ResponseEntity<String> triggerReport() {
        inventoryAlertScheduler.sendDailyLowStockReport();
        return ResponseEntity.ok("Daily low stock report job triggered successfully.");
    }
}
