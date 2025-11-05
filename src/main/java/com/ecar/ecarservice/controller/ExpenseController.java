package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.requests.ExpenseRequest;
import com.ecar.ecarservice.payload.responses.ExpenseResponse;
import com.ecar.ecarservice.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // <-- BẢO MẬT: CHỈ ADMIN
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * API để Admin tạo một khoản chi phí mới
     */
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * API để Admin xem danh sách chi phí (có phân trang)
     */
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @PageableDefault(sort = "expenseDate",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        Page<ExpenseResponse> responses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(responses);
    }
}