package com.example.new_dialog_architecture.arch

import android.view.View

interface DialogView<Event, State> {
    val layoutId: Int
    fun setView(view: View, dialog: StateDialog<Event, State>)
}


