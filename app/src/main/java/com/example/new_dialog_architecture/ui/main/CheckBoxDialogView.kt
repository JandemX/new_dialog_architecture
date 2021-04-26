package com.example.new_dialog_architecture.ui.main

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.arch.StateDialog

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
                dialog.updateState(currentstate.copy(selected = it))
            }
            list.addView(checkbox)
        }
    }
}
