package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.*;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final SparePartRepository sparePartRepository;
    private final CarModelRepository carModelRepository;
    private final ServiceSparePartRepository serviceSparePartRepository;
    private final InventoryRepository inventoryRepository;

    // =================== SPARE PART (Thông tin chung) ===================
    @Override
    @Transactional
    public SparePartDTO createPart(SparePartCreateDTO dto) {
        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new EntityNotFoundException("Car model not found with id: " + dto.getCarModelId()));

        SparePart part = new SparePart();
        part.setPartNumber(dto.getPartNumber());
        part.setPartName(dto.getPartName());
        part.setCategory(dto.getCategory());
        part.setUnitPrice(dto.getUnitPrice());
        part.setCarModel(carModel);

        SparePart savedPart = sparePartRepository.save(part);
        return toSparePartDTO(savedPart);
    }

    @Override
    @Transactional
    public SparePartDTO updatePart(Long partId, SparePartCreateDTO dto) {
        SparePart part = sparePartRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundException("Spare part not found with id: " + partId));

        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new EntityNotFoundException("Car model not found with id: " + dto.getCarModelId()));

        part.setPartNumber(dto.getPartNumber());
        part.setPartName(dto.getPartName());
        part.setCategory(dto.getCategory());
        part.setUnitPrice(dto.getUnitPrice());
        part.setCarModel(carModel);

        SparePart savedPart = sparePartRepository.save(part);
        return toSparePartDTO(savedPart);
    }

    @Override
    @Transactional
    public void deletePart(Long partId) {
        if (!sparePartRepository.existsById(partId)) {
            throw new EntityNotFoundException("Spare part not found with id: " + partId);
        }
        // Cần xóa cả các bản ghi inventory liên quan trước khi xóa phụ tùng
        inventoryRepository.deleteBySparePartId(partId);
        sparePartRepository.deleteById(partId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartDTO> getAllParts() {
        return sparePartRepository.findAll().stream()
                .map(this::toSparePartDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartDTO> getPartsByCarModel(Long carModelId) {
        return sparePartRepository.findByCarModel_Id(carModelId).stream()
                .map(this::toSparePartDTO)
                .collect(Collectors.toList());
    }

    // =================== INVENTORY (Tồn kho theo Center) ===================
    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryByCenter(Long centerId) {
        // Cần thêm findByCenterId vào InventoryRepository
        return inventoryRepository.findByCenterId(centerId).stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryDTO updateStock(Long inventoryId, StockUpdateDTO stockDto) {
        Inventory item = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found: " + inventoryId));

        int newQty = stockDto.getIsAddition()
                ? item.getStockQuantity() + stockDto.getQuantityChange()
                : item.getStockQuantity() - stockDto.getQuantityChange();

        item.setStockQuantity(Math.max(0, newQty));

        // THÊM CHECK NÀY
        if (stockDto.getMinStockLevel() != null) {
            int safeMin = Math.max(1, stockDto.getMinStockLevel());
            item.setMinStockLevel(safeMin);
        }

        return toInventoryDTO(inventoryRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockAlertsByCenter(Long centerId) {
        return inventoryRepository.findLowStockPartsByCenter(centerId).stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getStockAcrossCenters(Long partId) {
        return inventoryRepository.findBySparePartId(partId).stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    // =================== SUGGESTION LOGIC ===================
    @Override
    @Transactional(readOnly = true)
    public List<SparePartSuggestionDTO> getPartSuggestions(Long centerId, Long carModelId, List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ServiceSparePart> mappings = serviceSparePartRepository.findByServiceIdsAndCarModelId(serviceIds, carModelId);

        return mappings.stream().map(mapping -> {
            Inventory inventoryItem = inventoryRepository
                    .findByCenterIdAndSparePartId(centerId, mapping.getSparePart().getId())
                    .orElse(null);
            return toSuggestionDTO(mapping, inventoryItem);
        }).collect(Collectors.toList());
    }

    // =================== HELPER/CONVERTER METHODS ===================
    private SparePartDTO toSparePartDTO(SparePart part) {
        SparePartDTO dto = new SparePartDTO();
        dto.setId(part.getId());
        dto.setPartNumber(part.getPartNumber());
        dto.setPartName(part.getPartName());
        dto.setCategory(part.getCategory());
        dto.setUnitPrice(part.getUnitPrice());
        if (part.getCarModel() != null) {
            dto.setCarModelId(part.getCarModel().getId());
            dto.setCarModelName(part.getCarModel().getCarName());
        }
        return dto;
    }

    private SparePartSuggestionDTO toSuggestionDTO(ServiceSparePart mapping, Inventory inventoryItem) {
        SparePart part = mapping.getSparePart();
        SparePartSuggestionDTO dto = new SparePartSuggestionDTO();
        dto.setPartId(part.getId());
        dto.setPartName(part.getPartName());
        dto.setPartNumber(part.getPartNumber());
        dto.setUnitPrice(part.getUnitPrice());
        dto.setDefaultQuantity(mapping.getDefaultQuantity());
        dto.setServiceId(mapping.getService().getId());
        dto.setStockQuantity(inventoryItem != null ? inventoryItem.getStockQuantity() : 0);
        return dto;
    }

    private InventoryDTO toInventoryDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setCenterName(inventory.getCenter().getCenterName());
        dto.setPartId(inventory.getSparePart().getId());
        dto.setPartName(inventory.getSparePart().getPartName());
        dto.setPartNumber(inventory.getSparePart().getPartNumber());
        dto.setStockQuantity(inventory.getStockQuantity());
        dto.setMinStockLevel(inventory.getMinStockLevel());
        return dto;
    }
}