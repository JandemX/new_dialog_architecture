package com.example.new_dialog_architecture.ui.main

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class CheckBoxDialogView : DialogView<MainDialogEvents, MultiCheckboxState> {
    @IgnoredOnParcel
    override val layoutId: Int = R.layout.some_dialog_content
    override fun newView(view: View, state: () -> MultiCheckboxState, update: (MultiCheckboxState) -> Unit) {
        val list: LinearLayout = view.findViewById(R.id.checkbox_list)
        Log.d("CheckboxDialogView", "newView: ${state().selected}")
        state().possible.forEach {
            val checkbox = CheckBox(list.context)
            checkbox.text = it
            checkbox.isChecked = state().selected.contains(it)
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val newList = if (isChecked) state().selected + listOf(it) else state().selected - listOf(it)
                update(state().copy(selected = newList))
            }
            list.addView(checkbox)
        }
    }

}
