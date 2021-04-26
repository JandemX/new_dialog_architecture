package com.example.new_dialog_architecture.ui.main

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import kotlinx.parcelize.Parcelize

@Parcelize
class CheckBoxDialogView : DialogView<MainDialogEvents, CheckboxState> {
    override val layoutId: Int = R.layout.some_dialog_content

    override fun newView(view: View, state: CheckboxState, update: (CheckboxState) -> Unit) {
        val list: LinearLayout = view.findViewById(R.id.checkbox_list)
        state.possible.forEach {
            val checkbox = CheckBox(list.context)
            checkbox.text = it
            checkbox.isChecked = state.selected?.contains(it) ?: false
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                update(state.copy(selected = it))
            }
            list.addView(checkbox)
        }
    }
}
