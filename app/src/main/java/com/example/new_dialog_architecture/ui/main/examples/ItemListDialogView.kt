package com.example.new_dialog_architecture.ui.main.examples

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.ui.main.MainDialogEvents

class ItemListDialogView : DialogView<ItemListDialogView.ListDialogState, MainDialogEvents> {

    data class ListDialogState(
        val possible: List<String> = emptyList(),
        val selected: String? = null
    )

    override val layoutId: Int = R.layout.dialog_list_content
    override fun bind(
        view: View,
        state: () -> ListDialogState,
        update: (ListDialogState) -> Unit,
        sendEvent: (MainDialogEvents) -> Unit
    ) {
        val recycler = view.findViewById<RecyclerView>(R.id.simple_recycler)
        state().possible.run {
            recycler.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = SomeAdapter(this@run) {
                    update(state().copy(selected = it))
                }
            }
        }
    }

    override fun onUpdate(state: ListDialogState) {

    }
}

