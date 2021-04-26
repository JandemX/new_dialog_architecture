package com.example.new_dialog_architecture.ui.main

import android.view.View
import android.widget.TextView
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.arch.StateDialog

class SimpleAlertDialogView : DialogView<MainDialogEvents, String> {

    override val layoutId: Int = R.layout.some_information_dialog_content
    override fun setView(view: View, dialog: StateDialog<MainDialogEvents, String>) {
        val text: TextView = view.findViewById(R.id.alert)
        text.text = "ALARM ALARM"
    }
}
