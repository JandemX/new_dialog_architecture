package com.example.new_dialog_architecture.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SimpleInteractionDialogVM<State, Event>(val dialogData: DialogBuilder.DialogData<Event, State>) :
    ViewModel() {
    private val stateSubject: MutableStateFlow<State> = MutableStateFlow(dialogData.initialState)
    val stateStream: StateFlow<State> = stateSubject.asStateFlow()

    fun updateState(updatedState: State) {
        stateSubject.compareAndSet(stateStream.value, updatedState)
    }
}
