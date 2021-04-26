package com.example.new_dialog_architecture.arch


// abstract makes no sense. convert to interface and move all the protected stuff into the interactionDialog.
interface StateDialog<Event, State> {
    val state: State?
    fun updateState(newState: State)
    fun interact(event: DialogInteractorEvent<Event>)
}

sealed class DialogInteractorEvent<Event> {
    data class Positive<Event>(val data: Event) : DialogInteractorEvent<Event>()
    data class Negative<Event>(val data: Event) : DialogInteractorEvent<Event>()
}
