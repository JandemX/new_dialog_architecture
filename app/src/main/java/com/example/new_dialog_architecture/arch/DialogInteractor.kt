package com.example.new_dialog_architecture.arch

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.new_dialog_architecture.arch.dialogs.DialogInteraction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class Interactor<Event> : ViewModel() {
    private val _eventStream: MutableSharedFlow<DialogInteraction<Event>> = MutableSharedFlow(0)
    fun eventStream(onPositive: (Event).() -> Unit, onNegative: (Event).() -> Unit): Flow<DialogInteraction<Event>> = _eventStream.onEach {
        when (it) {
            is DialogInteraction.Negative -> onNegative(it.event)
            is DialogInteraction.Positive -> onPositive(it.event)
        }
    }

    fun send(event: DialogInteraction<Event>) {
        viewModelScope.launch { _eventStream.emit(event) }
    }
}

fun <T> Fragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<T>> =
        createViewModelLazy(Interactor<T>()::class, { this.viewModelStore }, factoryProducer)

fun <T> DialogFragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<T>> =
        createViewModelLazy(Interactor<T>()::class, { requireParentFragment().viewModelStore }, factoryProducer)
