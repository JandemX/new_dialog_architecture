package com.example.new_dialog_architecture.ui.main

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.new_dialog_architecture.R

interface DialogView<Event, State> {
    val layoutId: Int
    fun setView(view: View, dialog: StateDialog<Event, State>)
}

class CheckBoxDialogView : DialogView<MainDialogEvents, CheckboxState> {
    override val layoutId: Int = R.layout.some_dialog_content

    override fun setView(view: View, dialog: StateDialog<MainDialogEvents, CheckboxState>) {
        val list: LinearLayout = view.findViewById(R.id.checkbox_list)
        dialog.state?.possible?.forEach {
            val checkbox = CheckBox(list.context)
            checkbox.text = it
            checkbox.isChecked = dialog.state?.selected?.contains(it) ?: false
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val currentstate = dialog.state ?: CheckboxState(emptyList())
                val combinedSelected: List<String> = if (isChecked) currentstate.selected.plus(listOf(it)) else currentstate.selected.minus(listOf(it))
                dialog.updateState(currentstate.copy(selected = combinedSelected))
            }
            list.addView(checkbox)
        }
    }
}

class SimpleAlertDialogView : DialogView<MainDialogEvents, String> {

    override val layoutId: Int = R.layout.some_information_dialog_content
    override fun setView(view: View, dialog: StateDialog<MainDialogEvents, String>) {
        val text: TextView = view.findViewById(R.id.alert)
        text.text = "ALARM ALARM"
    }
}
