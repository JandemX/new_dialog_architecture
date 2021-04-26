package com.example.new_dialog_architecture.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


abstract class BaseViewModel<T>(initialState: T?) : ViewModel() {
    val stateSubject: MutableStateFlow<T?> = MutableStateFlow(initialState)
    open val stateStream: StateFlow<T?> = stateSubject.asStateFlow()
}
