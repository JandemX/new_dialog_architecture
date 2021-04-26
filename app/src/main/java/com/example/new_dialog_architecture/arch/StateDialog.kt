package com.example.new_dialog_architecture.arch

interface StateDialog<Event, State> {
    val state: State?
    fun updateState(newState: State)
    fun interact(event: DialogInteraction<Event>)
}

sealed class DialogInteraction<Event> {
    data class Positive<Event>(val event: Event) : DialogInteraction<Event>()
    class Negative<Event>(val event: Event) : DialogInteraction<Event>()
}
