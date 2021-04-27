package com.example.new_dialog_architecture.arch

import android.os.Parcelable
import android.view.View

interface DialogView<Event, State> : Parcelable {
    val layoutId: Int
    fun setView(view: View, state: () -> State, update: (State) -> Unit)
}


