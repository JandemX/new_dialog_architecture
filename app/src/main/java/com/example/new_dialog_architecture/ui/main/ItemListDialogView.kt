package com.example.new_dialog_architecture.ui.main

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.arch.StateDialog

class ItemListDialogView : DialogView<MainDialogEvents, ListDialogState> {
    override val layoutId: Int = R.layout.dialog_list_content

    override fun setView(view: View, dialog: StateDialog<MainDialogEvents, ListDialogState>) {
        val recycler = view.findViewById<RecyclerView>(R.id.simple_recycler)
        dialog.state?.possible?.run {
            recycler.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = SomeAdapter(this@run) {
                    dialog.updateState(dialog.state!!.copy(selected = it))
                }
            }
        }
    }
}

