package com.example.new_dialog_architecture.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_dialog_architecture.arch.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state: MutableStateFlow<String> = MutableStateFlow("")
    val state: StateFlow<String> = _state.asStateFlow()

    fun bind(interactor: Interactor<MainDialogEvents>) {
        viewModelScope.launch {
            interactor.eventStream.onEach { event ->
                when (event) {
                    is MainDialogEvents.BottomSheetCheckboxEvent.Positive -> {
                        _state.value = event.data
                    }
                    is MainDialogEvents.DialogCheckboxEvent.Positive -> {
                        _state.value = event.data
                    }
                    is MainDialogEvents.BottomSheetListEvent.Positive -> {
                        _state.value = event.data
                    }
                    MainDialogEvents.BottomSheetListEvent.Negative -> TODO()
                }
            }.flowOn(Dispatchers.Main).collect()
        }
    }
}
