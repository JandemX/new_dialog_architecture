package com.example.new_dialog_architecture.ui.main

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.new_dialog_architecture.arch.Interactor
import com.example.new_dialog_architecture.arch.SimpleInteractionDialogVM
import com.example.new_dialog_architecture.arch.dialogInteractor

abstract class StateDialog<Event, State> : DialogFragment() {

    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(initialState) as T
        }
    }

    protected val viewModel: SimpleInteractionDialogVM<State> by viewModels(factoryProducer = { producer })
    protected val interactor: Interactor<DialogInteractorEvent<Event>> by dialogInteractor()
    protected var initialState: State? = null

    val state: State?
        get() = viewModel.stateSubject.value

    fun updateState(newState: State) = viewModel.stateSubject.compareAndSet(state, newState)

}

sealed class DialogInteractorEvent<Event> {
    data class Positive<Event>(val data: Event) : DialogInteractorEvent<Event>()
    data class Negative<Event>(val data: Event) : DialogInteractorEvent<Event>()
}
