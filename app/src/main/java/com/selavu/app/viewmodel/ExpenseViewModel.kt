package com.selavu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selavu.app.data.repository.ExpenseRepository
import com.selavu.app.model.DateFilterOption
import com.selavu.app.model.Expense
import com.selavu.app.model.ExpenseCategory
import com.selavu.app.model.PaymentMethod
import com.selavu.app.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class ExpenseFormState(
    val id: String? = null,
    val title: String = "",
    val amount: String = "",
    val category: ExpenseCategory = ExpenseCategory.FOOD,
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _currentUserId = MutableStateFlow("")

    // Form State
    private val _formState = MutableStateFlow(ExpenseFormState())
    val formState: StateFlow<ExpenseFormState> = _formState.asStateFlow()

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ExpenseCategory?>(null)
    val selectedCategory: StateFlow<ExpenseCategory?> = _selectedCategory.asStateFlow()

    private val _selectedDateFilter = MutableStateFlow(DateFilterOption.ALL)
    val selectedDateFilter: StateFlow<DateFilterOption> = _selectedDateFilter.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    // Real-time user expenses stream from Cloud Firestore
    val allExpenses: StateFlow<List<Expense>> = _currentUserId.flatMapLatest { userId ->
        if (userId.isBlank()) flowOf(emptyList())
        else expenseRepository.getAllExpenses(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's total calculated from actual records
    val todayTotal: StateFlow<Double> = allExpenses.map { expenses ->
        val start = getStartOfDay()
        val end = getEndOfDay()
        expenses.filter { it.date in start..end }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Month's total calculated from actual records
    val monthTotal: StateFlow<Double> = allExpenses.map { expenses ->
        val start = getStartOfMonth()
        val end = getEndOfMonth()
        expenses.filter { it.date in start..end }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total transactions
    val totalTransactions: StateFlow<Int> = allExpenses.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered and sorted expenses stream
    val filteredExpenses: StateFlow<List<Expense>> = combine(
        allExpenses,
        _searchQuery,
        _selectedCategory,
        _selectedDateFilter,
        _selectedSortOption
    ) { expenses, query, catFilter, dateFilter, sortOpt ->
        var list = expenses

        // 1. Text Search (title, category, notes)
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        (it.notes?.lowercase()?.contains(q) == true)
            }
        }

        // 2. Category Filter
        if (catFilter != null) {
            list = list.filter {
                it.category.equals(catFilter.name, ignoreCase = true) ||
                        it.category.equals(catFilter.displayName, ignoreCase = true)
            }
        }

        // 3. Date Filter
        list = when (dateFilter) {
            DateFilterOption.ALL -> list
            DateFilterOption.TODAY -> {
                val start = getStartOfDay()
                val end = getEndOfDay()
                list.filter { it.date in start..end }
            }
            DateFilterOption.THIS_WEEK -> {
                val start = getStartOfWeek()
                val end = getEndOfWeek()
                list.filter { it.date in start..end }
            }
            DateFilterOption.THIS_MONTH -> {
                val start = getStartOfMonth()
                val end = getEndOfMonth()
                list.filter { it.date in start..end }
            }
        }

        // 4. Sorting
        when (sortOpt) {
            SortOption.NEWEST_FIRST -> list.sortedWith(
                compareByDescending<Expense> { it.date }.thenByDescending { it.id }
            )
            SortOption.OLDEST_FIRST -> list.sortedWith(
                compareBy<Expense> { it.date }.thenBy { it.id }
            )
            SortOption.HIGHEST_AMOUNT -> list.sortedByDescending { it.amount }
            SortOption.LOWEST_AMOUNT -> list.sortedBy { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCurrentUserId(userId: String) {
        if (_currentUserId.value != userId) {
            _currentUserId.value = userId
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterChange(category: ExpenseCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onDateFilterChange(filter: DateFilterOption) {
        _selectedDateFilter.value = filter
    }

    fun onSortOptionChange(sort: SortOption) {
        _selectedSortOption.value = sort
    }

    // Form Handlers
    fun onTitleChange(title: String) {
        _formState.update { it.copy(title = title, errorMessage = null) }
    }

    fun onAmountChange(amount: String) {
        val clean = amount.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } <= 1) {
            _formState.update { it.copy(amount = clean, errorMessage = null) }
        }
    }

    fun onCategoryChange(category: ExpenseCategory) {
        _formState.update { it.copy(category = category, errorMessage = null) }
    }

    fun onDateChange(dateMillis: Long) {
        _formState.update { it.copy(date = dateMillis, errorMessage = null) }
    }

    fun onPaymentMethodChange(method: PaymentMethod) {
        _formState.update { it.copy(paymentMethod = method, errorMessage = null) }
    }

    fun onNotesChange(notes: String) {
        _formState.update { it.copy(notes = notes, errorMessage = null) }
    }

    fun resetForm() {
        _formState.value = ExpenseFormState()
    }

    fun loadExpenseForEdit(expenseId: String, userId: String) {
        viewModelScope.launch {
            val expense = expenseRepository.getExpenseById(expenseId, userId)
            if (expense != null) {
                _formState.value = ExpenseFormState(
                    id = expense.id,
                    title = expense.title,
                    amount = if (expense.amount % 1.0 == 0.0) expense.amount.toLong().toString() else expense.amount.toString(),
                    category = ExpenseCategory.fromName(expense.category),
                    date = expense.date,
                    paymentMethod = PaymentMethod.fromName(expense.paymentMethod),
                    notes = expense.notes ?: "",
                    errorMessage = null
                )
            }
        }
    }

    fun saveExpense(userId: String, onSuccess: () -> Unit) {
        val form = _formState.value
        val titleTrimmed = form.title.trim()
        if (titleTrimmed.isEmpty()) {
            _formState.update { it.copy(errorMessage = "Expense title is required.") }
            return
        }

        val amountVal = form.amount.toDoubleOrNull()
        if (amountVal == null || amountVal <= 0.0) {
            _formState.update { it.copy(errorMessage = "Please enter a valid amount greater than 0.") }
            return
        }

        if (userId.isBlank()) {
            _formState.update { it.copy(errorMessage = "User session expired. Please re-login.") }
            return
        }

        _formState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                if (form.id.isNullOrBlank()) {
                    // Create New Expense in Firestore under users/{uid}/expenses
                    val newExpense = Expense(
                        userId = userId,
                        title = titleTrimmed,
                        amount = amountVal,
                        category = form.category.name,
                        date = form.date,
                        paymentMethod = form.paymentMethod.name,
                        notes = form.notes.trim().ifEmpty { null }
                    )
                    expenseRepository.insertExpense(newExpense, userId)
                } else {
                    // Update Existing Expense in Firestore
                    val updatedExpense = Expense(
                        id = form.id,
                        userId = userId,
                        title = titleTrimmed,
                        amount = amountVal,
                        category = form.category.name,
                        date = form.date,
                        paymentMethod = form.paymentMethod.name,
                        notes = form.notes.trim().ifEmpty { null }
                    )
                    expenseRepository.updateExpense(updatedExpense, userId)
                }

                _formState.update { it.copy(isSaving = false, isSuccess = true) }
                onSuccess()
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.localizedMessage ?: "Failed to save expense."
                    )
                }
            }
        }
    }

    fun deleteExpense(expense: Expense, onDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            if (expense.id.isNotBlank() && expense.userId.isNotBlank()) {
                expenseRepository.deleteExpense(expense.id, expense.userId)
            }
            onDeleted?.invoke()
        }
    }

    fun deleteExpenseById(id: String, userId: String, onDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id, userId)
            onDeleted?.invoke()
        }
    }

    // Date range helpers
    private fun getStartOfDay(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    private fun getStartOfWeek(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun getEndOfWeek(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
        c.add(Calendar.DAY_OF_WEEK, 6)
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }
}
