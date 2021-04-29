package com.example.new_dialog_architecture.arch

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class Interactor<Event> : ViewModel() {
    private val _eventStream: MutableSharedFlow<Event> = MutableSharedFlow(0)
    val eventStream: Flow<Event> = _eventStream.asSharedFlow()

    fun send(event: Event) {
        viewModelScope.launch { _eventStream.emit(event) }
    }
}

sealed class DialogInteraction<Event> {
    data class Positive<Event>(val event: Event) : DialogInteraction<Event>()
    class Negative<Event>(val event: Event) : DialogInteraction<Event>()
}

fun <T> Fragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<T>> =
        createViewModelLazy(Interactor<T>()::class, { this.viewModelStore }, factoryProducer)

fun <T> DialogFragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<T>> =
        createViewModelLazy(Interactor<T>()::class, { requireParentFragment().viewModelStore }, factoryProducer)
