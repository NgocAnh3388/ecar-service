package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.entities.Expense;
import com.ecar.ecarservice.payload.requests.ExpenseRequest;
import com.ecar.ecarservice.payload.responses.ExpenseResponse;
import com.ecar.ecarservice.repositories.ExpenseRepository;
import com.ecar.ecarservice.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setExpenseDate(request.expenseDate());
        // createdBy sẽ được tự động điền bởi Spring Data Auditing

        Expense savedExpense = expenseRepository.save(expense);
        return toExpenseResponse(savedExpense);
    }

    @Override
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable).map(this::toExpenseResponse);
    }

    private ExpenseResponse toExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getCreatedBy()
        );
    }
}