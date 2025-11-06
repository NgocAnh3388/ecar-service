package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.SparePartCreateDTO;
import com.ecar.ecarservice.dto.SparePartDTO;
import com.ecar.ecarservice.dto.StockUpdateDTO;
import com.ecar.ecarservice.entities.CarModel;
import com.ecar.ecarservice.entities.SparePart;
import com.ecar.ecarservice.repositories.CarModelRepository;
import com.ecar.ecarservice.repositories.SparePartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private SparePartRepository sparePartRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private SparePartCreateDTO createDTO;
    private CarModel testCarModel;

    @BeforeEach
    void setUp() {
        createDTO = new SparePartCreateDTO();
        createDTO.setPartNumber("VF8-FILTER-01");
        createDTO.setPartName("Lọc gió điều hòa VF8");
        createDTO.setCarModelId(1L);
        createDTO.setStockQuantity(100);
        createDTO.setMinStockLevel(20);

        testCarModel = new CarModel();
        testCarModel.setId(1L);
        testCarModel.setCarName("VF8");
    }

    @Test
    @DisplayName("createPart_Success: Should save and return DTO of the new part")
    void testCreatePart_Success() {
        // Arrange
        SparePart savedPart = new SparePart(); // Đối tượng giả lập sau khi lưu
        savedPart.setId(101L);
        savedPart.setPartName(createDTO.getPartName());
        savedPart.setCarModel(testCarModel);

        when(carModelRepository.findById(1L)).thenReturn(Optional.of(testCarModel));
        when(sparePartRepository.save(any(SparePart.class))).thenReturn(savedPart);

        // Act
        SparePartDTO result = inventoryService.createPart(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Lọc gió điều hòa VF8", result.getPartName());
        assertEquals("VF8", result.getCarModelName());
        verify(sparePartRepository, times(1)).save(any(SparePart.class));
    }

    @Test
    @DisplayName("createPart_CarModelNotFound: Should throw RuntimeException")
    void testCreatePart_CarModelNotFound_ThrowsException() {
        // Arrange
        when(carModelRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.createPart(createDTO);
        });

        assertEquals("Car model not found", exception.getMessage());
        verify(sparePartRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateStock_AddQuantity: Should correctly add to stock")
    void testUpdateStock_AddQuantity_Success() {
        // Arrange
        SparePart existingPart = new SparePart();
        existingPart.setId(1L);
        existingPart.setStockQuantity(10);

        StockUpdateDTO updateDTO = new StockUpdateDTO();
        updateDTO.setIsAddition(true);
        updateDTO.setQuantityChange(5);

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(existingPart));
        when(sparePartRepository.save(any(SparePart.class))).thenReturn(existingPart);

        // Act
        inventoryService.updateStock(1L, updateDTO);

        // Assert
        ArgumentCaptor<SparePart> partCaptor = ArgumentCaptor.forClass(SparePart.class);
        verify(sparePartRepository).save(partCaptor.capture());
        SparePart savedPart = partCaptor.getValue();

        assertEquals(15, savedPart.getStockQuantity());
    }

    @Test
    @DisplayName("updateStock_SubtractQuantity: Should correctly subtract from stock")
    void testUpdateStock_SubtractQuantity_Success() {
        // Arrange
        SparePart existingPart = new SparePart();
        existingPart.setId(1L);
        existingPart.setStockQuantity(10);

        StockUpdateDTO updateDTO = new StockUpdateDTO();
        updateDTO.setIsAddition(false);
        updateDTO.setQuantityChange(3);

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(existingPart));
        when(sparePartRepository.save(any(SparePart.class))).thenReturn(existingPart);

        // Act
        inventoryService.updateStock(1L, updateDTO);

        // Assert
        ArgumentCaptor<SparePart> partCaptor = ArgumentCaptor.forClass(SparePart.class);
        verify(sparePartRepository).save(partCaptor.capture());
        SparePart savedPart = partCaptor.getValue();

        assertEquals(7, savedPart.getStockQuantity());
    }

    @Test
    @DisplayName("updateStock_SubtractQuantity_BecomesZero: Should not go below zero")
    void testUpdateStock_SubtractQuantity_BecomesZero() {
        // Arrange
        SparePart existingPart = new SparePart();
        existingPart.setStockQuantity(5);

        StockUpdateDTO updateDTO = new StockUpdateDTO();
        updateDTO.setIsAddition(false);
        updateDTO.setQuantityChange(10); // Trừ nhiều hơn số lượng có

        when(sparePartRepository.findById(anyLong())).thenReturn(Optional.of(existingPart));
        when(sparePartRepository.save(any(SparePart.class))).thenReturn(existingPart);

        // Act
        inventoryService.updateStock(1L, updateDTO);

        // Assert
        ArgumentCaptor<SparePart> partCaptor = ArgumentCaptor.forClass(SparePart.class);
        verify(sparePartRepository).save(partCaptor.capture());
        SparePart savedPart = partCaptor.getValue();

        assertEquals(0, savedPart.getStockQuantity()); // Đảm bảo không bị số âm
    }
}