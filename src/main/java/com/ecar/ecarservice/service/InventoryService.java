package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.SparePartCreateDTO;
import com.ecar.ecarservice.dto.SparePartDTO;
import com.ecar.ecarservice.dto.StockAlertDTO;
import com.ecar.ecarservice.dto.StockUpdateDTO;

import java.util.List;

public interface InventoryService {

    List<SparePartDTO> getAllParts();
    List<SparePartDTO> getPartsByCarModel(Long carModelId);
    List<StockAlertDTO> getLowStockAlerts();

    // SỬA: Thay đổi kiểu trả về từ SparePart sang SparePartDTO
    SparePartDTO createPart(SparePartCreateDTO dto);
    SparePartDTO updatePart(Long partId, SparePartCreateDTO dto);
    SparePartDTO updateStock(Long partId, StockUpdateDTO dto);

    void deletePart(Long partId);
}
