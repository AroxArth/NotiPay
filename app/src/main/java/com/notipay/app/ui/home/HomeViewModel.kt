package com.notipay.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notipay.app.NotiPayApp
import com.notipay.app.data.local.PaymentEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NotiPayApp
    private val paymentRepository = app.paymentRepository
    private val settingsRepository = app.settingsRepository

    private val _filter = MutableStateFlow(DateFilter.TODAY)
    val filter: StateFlow<DateFilter> = _filter.asStateFlow()

    val isPaused: StateFlow<Boolean> = settingsRepository.isPaused

    @OptIn(ExperimentalCoroutinesApi::class)
    val payments: StateFlow<List<PaymentEntity>> = _filter
        .flatMapLatest { paymentRepository.observePayments(it.startMillis(), Long.MAX_VALUE) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalCents: StateFlow<Long> = _filter
        .flatMapLatest { paymentRepository.observeTotal(it.startMillis(), Long.MAX_VALUE) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    fun setFilter(filter: DateFilter) {
        _filter.value = filter
    }

    fun setPaused(paused: Boolean) {
        settingsRepository.setPaused(paused)
    }
}
