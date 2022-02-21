package com.example.new_dialog_architecture.arch

import android.view.View

interface DialogView<State, Event> {

    val layoutId: Int
    fun bind(view: View, state: () -> State, update: (State) -> Unit, sendEvent: (Event) -> Unit)
    fun onUpdate(state: State)
}

