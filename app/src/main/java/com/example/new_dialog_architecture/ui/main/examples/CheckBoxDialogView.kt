package com.example.new_dialog_architecture.ui.main.examples

import android.os.Parcelable
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class CheckBoxDialogView : DialogView<CheckBoxDialogView.MultiCheckboxState> {

    @Parcelize
    data class MultiCheckboxState(val possible: List<String>, val selected: List<String> = emptyList()) : Parcelable

    @IgnoredOnParcel
    override val layoutId: Int = R.layout.some_dialog_content
    override fun bind(view: View, state: () -> MultiCheckboxState, update: (MultiCheckboxState) -> Unit) {
        val list: LinearLayout = view.findViewById(R.id.checkbox_list)
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
