package com.example.new_dialog_architecture.ui.main.examples

import android.view.View
import android.widget.TextView
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.ui.main.MainDialogEvents
import kotlinx.parcelize.Parcelize

@Parcelize
class SimpleAlertDialogView : DialogView<MainDialogEvents, String> {

    override val layoutId: Int = R.layout.some_information_dialog_content

    override fun bind(view: View, state: () -> String, update: (String) -> Unit) {
        val text: TextView = view.findViewById(R.id.alert)
        text.text = "ALARM ALARM"
    }

}
