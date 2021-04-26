package com.example.new_dialog_architecture.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.bottomSheet
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.dialog
import com.example.new_dialog_architecture.arch.dialogInteractor
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
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
            childFragmentManager.beginTransaction().apply {
                dialog(CheckBoxDialogView()) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = CheckboxState(List(5) { "checkbox$it" }, "checkbox1")
                    positiveButtonText = "Positive"
                    onPositiveAction = { it }
                    singleChoice = true
                }.show(this, "test")
            }
            childFragmentManager.executePendingTransactions()
        }

        buttonBottomSheetCheckbox.setOnClickListener {
            childFragmentManager.beginTransaction().apply {
                bottomSheet(CheckBoxDialogView()) {
                    initialState = CheckboxState(List(5) { "checkbox$it" }, "checkbox1")
                }.show(this, "test")
            }
        }

        buttonBottomSheetList.setOnClickListener {
            childFragmentManager.beginTransaction().apply {
                bottomSheet(ItemListDialogView()) {
                    initialState = ListDialogState(List(5) { "listitem$it" })
                    onPositiveAction = {
                        it
                    }
                    singleChoice = true
                }.show(this, "test")
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            collectDialogEvents()
        }
    }

    private suspend fun collectDialogEvents() {
        interactor.eventStream(
                onPositive = {
                    when (this) {
                        is ListDialogState -> {
                            Toast.makeText(this@MainFragment.requireContext(), selected, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onNegative = {}
        ).flowOn(Dispatchers.Main).collect()
    }
}


sealed class MainDialogEvents
data class Data(val string: String) : MainDialogEvents()
data class CheckboxState(val possible: List<String>, val selected: String? = null) : MainDialogEvents()
data class ListDialogState(val possible: List<String> = emptyList(), val selected: String? = null) : MainDialogEvents()
