package com.example.new_dialog_architecture.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.bottomSheet
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.dialog
import com.example.new_dialog_architecture.arch.dialogInteractor
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

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var buttonDialogCheckbox: Button
    private lateinit var buttonBottomSheetCheckbox: Button
    private lateinit var buttonBottomSheetList: Button
    private lateinit var message: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonDialogCheckbox = view.findViewById(R.id.button_dialog_checkbox)
        buttonBottomSheetCheckbox = view.findViewById(R.id.button_bottomsheet_checkbox)
        buttonBottomSheetList = view.findViewById(R.id.button_bottomsheet_list)

        message = view.findViewById(R.id.message)

        buttonDialogCheckbox.setOnClickListener {
            buttonDialogCheckbox.text = "test"

            openDialog {
                dialog<MainDialogEvents.CheckboxDialogEvent, MultiCheckboxState>(CheckBoxDialogView()) {
                    initialState = MultiCheckboxState(List(5) { "checkbox$it" })
                    dialogTitle = "Simple Checkbox Dialog"
                    buttons {
                        positiveButtonText = "Positive"
                        onPositiveAction = {
                            MainDialogEvents.CheckboxDialogEvent
                        }
                    }
                }
            }
        }

        buttonBottomSheetCheckbox.setOnClickListener {
            openDialog {
                bottomSheet<MainDialogEvents.BottomSheetCheckboxEvent, MultiCheckboxState>(CheckBoxDialogView()) {
                    initialState = MultiCheckboxState(List(5) { "checkbox$it" })
                    buttons {
                        positiveButtonText = "Positive"
                        onPositiveAction = { state ->
                            MainDialogEvents.BottomSheetCheckboxEvent
                        }
                    }
                }
            }
        }

        buttonBottomSheetList.setOnClickListener {
            openDialog {
                bottomSheet<MainDialogEvents.ListDialogEvent, ItemListDialogView.ListDialogState>(ItemListDialogView()) {
                    initialState = ItemListDialogView.ListDialogState(List(5) { "listitem$it" })
                    buttons {
                        positiveButtonText = "Positive"
                        onPositiveAction = {
                            MainDialogEvents.ListDialogEvent
                        }
                        onNegativeAction = {
                            MainDialogEvents.ListDialogEvent
                        }
                    }
                    additional {
                        singleChoice = true
                        cancellable = true
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            collectDialogEvents()
        }
    }

    private suspend fun collectDialogEvents() {
        interactor.eventStream
                .onEach {
                    when (it) {
                        is Data -> {
                        }
                    }
                }.flowOn(Dispatchers.Main).collect()
    }


    private fun Fragment.openDialog(dialog: () -> DialogFragment) {
        childFragmentManager.beginTransaction().apply {
            dialog().show(this, "test")
        }
        childFragmentManager.executePendingTransactions()
    }
}

sealed class MainDialogEvents {
    object ListDialogEvent : MainDialogEvents()
    object CheckboxDialogEvent : MainDialogEvents()
    object BottomSheetCheckboxEvent : MainDialogEvents()
}


data class Data(val string: String) : MainDialogEvents()

data class CheckboxState(val possible: List<String>, val selected: String? = null)

data class MultiCheckboxState(val possible: List<String>, val selected: List<String> = emptyList())

