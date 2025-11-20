package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InventoryService {
    // === Spare Part (Thông tin chung) ===
    SparePartDTO createPart(SparePartCreateDTO dto);
    SparePartDTO updatePart(Long partId, SparePartCreateDTO dto);
    void deletePart(Long partId);
    List<SparePartDTO> getAllParts();
    List<SparePartDTO> getPartsByCarModel(Long carModelId);

    // === Inventory (Tồn kho theo Center) ===
    List<InventoryDTO> getInventoryByCenter(Long centerId);
    InventoryDTO updateStock(Long inventoryId, StockUpdateDTO stockDto);
    List<InventoryDTO> getLowStockAlertsByCenter(Long centerId);
    List<InventoryDTO> getStockAcrossCenters(Long partId); // Lấy tồn kho của 1 phụ tùng ở mọi center

    // === Inventory đã sử dụng ===
    List<UsedPartHistoryDTO> getUsedPartsHistory();


    // === Suggestion Logic ===
    List<SparePartSuggestionDTO> getPartSuggestions(Long centerId, Long carModelId, List<Long> serviceIds);

}
