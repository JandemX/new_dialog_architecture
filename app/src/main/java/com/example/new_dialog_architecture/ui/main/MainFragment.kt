package com.example.new_dialog_architecture.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.bottomSheet
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.dialog
import com.example.new_dialog_architecture.arch.dialogInteractor
import com.example.new_dialog_architecture.ui.main.MainDialogEvents.BottomSheetCheckboxEvent
import com.example.new_dialog_architecture.ui.main.MainDialogEvents.BottomSheetListEvent
import com.example.new_dialog_architecture.ui.main.MainDialogEvents.DialogCheckboxEvent
import com.example.new_dialog_architecture.ui.main.examples.CheckBoxDialogView
import com.example.new_dialog_architecture.ui.main.examples.ItemListDialogView
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainFragment : DaggerFragment() {

    private val interactor by dialogInteractor<MainDialogEvents>()
    private val viewmodel: MainViewModel by viewModels()

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var buttonDialogCheckbox: Button
    private lateinit var buttonBottomSheetCheckbox: Button
    private lateinit var buttonBottomSheetList: Button
    private lateinit var message: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.main_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        viewmodel.bind(interactor)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonDialogCheckbox = view.findViewById(R.id.button_dialog_checkbox)
        buttonBottomSheetCheckbox = view.findViewById(R.id.button_bottomsheet_checkbox)
        buttonBottomSheetList = view.findViewById(R.id.button_bottomsheet_list)

        message = view.findViewById(R.id.message)


        lifecycleScope.launch {
            viewmodel.state.onEach {
                message.text = it
            }.flowOn(Dispatchers.Main).collect()
        }

        buttonDialogCheckbox.setOnClickListener {
            buttonDialogCheckbox.text = "test"

            openDialog {
                dialog<DialogCheckboxEvent, CheckBoxDialogView.MultiCheckboxState>(CheckBoxDialogView()) {
                    initialState = CheckBoxDialogView.MultiCheckboxState(List(5) { "checkbox$it" })
                    dialogTitle = "Simple Checkbox Dialog"
                    buttons {
                        positiveButtonText = "Positive"
                        negativeButtonText = "Negative"
                        onPositiveAction = { DialogCheckboxEvent.Positive(it.selected.toString()) }
                        onNegativeAction = { DialogCheckboxEvent.Negative }
                    }
                }
            }
        }

        buttonBottomSheetCheckbox.setOnClickListener {
            openDialog {
                bottomSheet<BottomSheetCheckboxEvent, CheckBoxDialogView.MultiCheckboxState>(CheckBoxDialogView()) {
                    initialState = CheckBoxDialogView.MultiCheckboxState(List(5) { "checkbox$it" })
                    buttons {
                        positiveButtonText = "Positive"
                        onPositiveAction = { state -> BottomSheetCheckboxEvent.Positive(state.selected.toString()) }
                        onNegativeAction = { BottomSheetCheckboxEvent.Negative }
                    }
                }
            }
        }

        buttonBottomSheetList.setOnClickListener {
            openDialog {
                bottomSheet<BottomSheetListEvent, ItemListDialogView.ListDialogState>(ItemListDialogView()) {
                    initialState = ItemListDialogView.ListDialogState(List(50) { "listitem$it" })
                    buttons {
                        onPositiveAction = { BottomSheetListEvent.Positive(it.selected ?: "") }
                        onNegativeAction = { BottomSheetListEvent.Negative }
                    }
                    additional {
                        singleChoice = true
                        cancellable = true
                    }
                }
            }
        }
    }

    private fun Fragment.openDialog(dialog: () -> DialogFragment) {
        childFragmentManager.beginTransaction().apply {
            dialog().show(this, "test, need to find a solution for this") // TODO: find out for what we could use this tag
        }
        childFragmentManager.executePendingTransactions()
    }
}

sealed class MainDialogEvents {

    sealed class BottomSheetListEvent : MainDialogEvents() {
        data class Positive(val data: String) : BottomSheetListEvent()
        object Negative : BottomSheetListEvent()
    }

    sealed class DialogCheckboxEvent : MainDialogEvents() {
        data class Positive(val data: String) : DialogCheckboxEvent()
        object Negative : DialogCheckboxEvent()
    }

    sealed class BottomSheetCheckboxEvent : MainDialogEvents() {
        data class Positive(val data: String) : BottomSheetCheckboxEvent()
        object Negative : BottomSheetCheckboxEvent()
    }
}
