package com.example.new_dialog_architecture.arch

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class Interactor<Event> : ViewModel() {
    private val _eventStream: MutableSharedFlow<Event> = MutableSharedFlow(0)
    val eventStream: Flow<Event> = _eventStream.asSharedFlow()

    fun send(event: Event) {
        viewModelScope.launch { _eventStream.emit(event) }
    }
}

inline fun <reified T : Any> Fragment.dialogInteractor(
    noinline factoryProducer: (() -> ViewModelProvider.Factory) = { DialogInteractorFactory<T>() }
): Lazy<Interactor<T>> {
    return DialogInteractorLazy(
        Interactor<T>()::class,
        T::class.simpleName
            ?: throw IllegalArgumentException("StatefulBottomSheetDialog Interactor key cannot be null"),
        { this.viewModelStore },
        factoryProducer
    )
}

fun <T : Any> DialogFragment.dialogInteractor(
    interactorEventKey: String,
    factoryProducer: (() -> ViewModelProvider.Factory) = { DialogInteractorFactory<T>() }
): Lazy<Interactor<T>> {
    return DialogInteractorLazy(
        Interactor<T>()::class,
        interactorEventKey,
        { requireParentFragment().viewModelStore },
        factoryProducer
    )
}
