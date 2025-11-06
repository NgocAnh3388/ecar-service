package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.requests.ExpenseRequest;
import com.ecar.ecarservice.payload.responses.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request);
    Page<ExpenseResponse> getAllExpenses(Pageable pageable);
}