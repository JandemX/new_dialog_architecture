package com.example.new_dialog_architecture.arch

import kotlinx.android.parcel.Parcelize

interface StateDialog<Event, State : Any> {
    val state: State?
    fun updateState(newState: State)
    fun interact(event: DialogInteraction<Event>)
}

sealed class DialogInteraction<Event> {
    data class Positive<Event>(val event: Event) : DialogInteraction<Event>()
    class Negative<Event>(val event: Event) : DialogInteraction<Event>()
}
