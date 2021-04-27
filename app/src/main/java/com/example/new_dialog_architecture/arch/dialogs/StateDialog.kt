package com.example.new_dialog_architecture.arch.dialogs

import com.example.new_dialog_architecture.arch.DialogInteraction

interface StateDialog<Event, State : Any> {
    val state: State?
    fun updateState(newState: State)
    fun interact(event: DialogInteraction<Event>)
}

