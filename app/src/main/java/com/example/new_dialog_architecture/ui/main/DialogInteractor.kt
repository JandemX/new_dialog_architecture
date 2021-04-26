package com.example.new_dialog_architecture.ui.main

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class Interactor<T> : ViewModel() {
    private val _eventStream: MutableSharedFlow<T> = MutableSharedFlow(0)
    val eventStream: SharedFlow<T> = _eventStream.asSharedFlow()

    fun send(event: T) {
        viewModelScope.launch { _eventStream.emit(event) }
    }
}

fun <T> Fragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<DialogInteractorEvent<T>>> =
        createViewModelLazy(Interactor<DialogInteractorEvent<T>>()::class, { this.viewModelStore }, factoryProducer)

fun <T> DialogFragment.dialogInteractor(factoryProducer: (() -> ViewModelProvider.Factory)? = null): Lazy<Interactor<T>> =
        createViewModelLazy(Interactor<T>()::class, { requireParentFragment().viewModelStore }, factoryProducer)
