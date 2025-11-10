package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.SparePartCreateDTO;
import com.ecar.ecarservice.dto.SparePartDTO;
import com.ecar.ecarservice.dto.StockAlertDTO;
import com.ecar.ecarservice.dto.StockUpdateDTO;
import com.ecar.ecarservice.entities.CarModel;
import com.ecar.ecarservice.entities.SparePart;
import com.ecar.ecarservice.repositories.CarModelRepository;
import com.ecar.ecarservice.repositories.SparePartRepository;
import com.ecar.ecarservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final SparePartRepository sparePartRepository;
    private final CarModelRepository carModelRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SparePartDTO> getAllParts() {
        return sparePartRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartDTO> getPartsByCarModel(Long carModelId) {
        return sparePartRepository.findByCarModel_Id(carModelId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertDTO> getLowStockAlerts() {
        return sparePartRepository.findLowStockParts().stream()
                .map(this::toStockAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SparePartDTO createPart(SparePartCreateDTO dto) { // SỬA: Trả về DTO
        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new RuntimeException("Car model not found"));

        SparePart part = new SparePart();
        part.setPartNumber(dto.getPartNumber());
        part.setPartName(dto.getPartName());
        part.setCategory(dto.getCategory());
        part.setUnitPrice(dto.getUnitPrice());
        part.setStockQuantity(dto.getStockQuantity());
        part.setMinStockLevel(dto.getMinStockLevel());
        part.setCarModel(carModel);
        // part.setCreatedAt(LocalDateTime.now()); // XÓA: @CreatedDate sẽ tự động xử lý việc này

        SparePart savedPart = sparePartRepository.save(part);
        return toDTO(savedPart); // SỬA: Chuyển đổi và trả về DTO
    }

    @Override
    @Transactional
    public SparePartDTO updatePart(Long partId, SparePartCreateDTO dto) { // SỬA: Trả về DTO
        SparePart part = sparePartRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new RuntimeException("Car model not found"));

        part.setPartNumber(dto.getPartNumber());
        part.setPartName(dto.getPartName());
        part.setCategory(dto.getCategory());
        part.setUnitPrice(dto.getUnitPrice());
        part.setStockQuantity(dto.getStockQuantity());
        part.setMinStockLevel(dto.getMinStockLevel());
        part.setCarModel(carModel);
        // part.setUpdatedAt(LocalDateTime.now()); // XÓA: @LastModifiedDate sẽ tự động xử lý

        SparePart savedPart = sparePartRepository.save(part);
        return toDTO(savedPart); // SỬA: Chuyển đổi và trả về DTO
    }

    @Override
    @Transactional
    public SparePartDTO updateStock(Long partId, StockUpdateDTO dto) { // SỬA: Trả về DTO
        SparePart part = sparePartRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        // Giả sử stockQuantity không bao giờ null trong DB, nếu có thể null, bạn cần kiểm tra null
        int currentStock = part.getStockQuantity() != null ? part.getStockQuantity() : 0;

        int newQty = dto.getIsAddition() ? currentStock + dto.getQuantityChange()
                : currentStock - dto.getQuantityChange();
        part.setStockQuantity(Math.max(newQty, 0));
        // part.setUpdatedAt(LocalDateTime.now()); // XÓA: @LastModifiedDate sẽ tự động xử lý

        SparePart savedPart = sparePartRepository.save(part);
        return toDTO(savedPart); // SỬA: Chuyển đổi và trả về DTO
    }

    @Override
    @Transactional
    public void deletePart(Long partId) {
        // Kiểm tra xem part có tồn tại không trước khi xóa
        if (!sparePartRepository.existsById(partId)) {
            throw new RuntimeException("Part not found with id: " + partId);
        }
        sparePartRepository.deleteById(partId);
    }

    // ---------------- Helper (Giữ nguyên) ----------------
    // Hàm này bây giờ an toàn để gọi vì nó luôn được gọi từ bên trong các phương thức @Transactional
    private SparePartDTO toDTO(SparePart part) {
        SparePartDTO dto = new SparePartDTO();
        dto.setId(part.getId());
        dto.setPartNumber(part.getPartNumber());
        dto.setPartName(part.getPartName());
        dto.setCategory(part.getCategory());
        dto.setUnitPrice(part.getUnitPrice() != null ? part.getUnitPrice() : 0.0);
        dto.setStockQuantity(part.getStockQuantity() != null ? part.getStockQuantity() : 0);
        dto.setMinStockLevel(part.getMinStockLevel() != null ? part.getMinStockLevel() : 0);
        if (part.getCarModel() != null) {
            dto.setCarModelId(part.getCarModel().getId());
            dto.setCarModelName(part.getCarModel().getCarName()); // An toàn
        }
        return dto;
    }

    private StockAlertDTO toStockAlertDTO(SparePart part) {
        StockAlertDTO dto = new StockAlertDTO();
        dto.setId(part.getId());
        dto.setPartName(part.getPartName());
        dto.setPartNumber(part.getPartNumber());
        dto.setCurrentStock(part.getStockQuantity() != null ? part.getStockQuantity() : 0);
        dto.setMinimumStock(part.getMinStockLevel() != null ? part.getMinStockLevel() : 0);
        int qtyToOrder = dto.getMinimumStock() - dto.getCurrentStock();
        dto.setQuantityToOrder(Math.max(qtyToOrder, 0));
        dto.setAlertMessage(qtyToOrder > 0 ? "Stock is below minimum level" : "");
        if (part.getCarModel() != null) {
            dto.setCarModelId(part.getCarModel().getId());
            dto.setCarModelName(part.getCarModel().getCarName()); // An toàn
        }
        return dto;
    }
}