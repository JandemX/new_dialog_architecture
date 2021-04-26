package com.example.new_dialog_architecture.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.bottomSheet
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.dialog
import com.example.new_dialog_architecture.arch.DialogInteractorEvent
import com.example.new_dialog_architecture.arch.dialogInteractor
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

    private lateinit var button: Button
    private lateinit var message: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button = view.findViewById(R.id.button)
        message = view.findViewById(R.id.message)

        button.setOnClickListener {
            button.text = "test"
            childFragmentManager.beginTransaction().apply {

                dialog(SimpleAlertDialogView()) {
                    dialogTitle = "Alert dialog"
                    initialState = ""
                    positiveButtonText = "Positive"
                    onPositiveAction = {
                        it?.let { Data("its a me ") }
                    }
                }

                bottomSheet(SimpleAlertDialogView()) {
                    dialogTitle = "Alert dialog"
                    initialState = ""
                    positiveButtonText = "Positive"
                    onPositiveAction = {
                        it?.let { Data("its a me ") }
                    }
                }

                bottomSheet(CheckBoxDialogView()) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = CheckboxState(List(5) { "checkbox$it" })
                    positiveButtonText = "positive"
                    onPositiveAction = { it }
                }.show(this, "test")
            }
            childFragmentManager.executePendingTransactions()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            collectDialogEvents()
        }
    }

    private suspend fun collectDialogEvents() {
        interactor.eventStream.onEach {
            when (it) {
                is DialogInteractorEvent.Negative -> {
                    when (it.data) {
                        is CheckboxState -> {

                        }
                        is Data -> {

                        }
                    }
                }
                is DialogInteractorEvent.Positive -> {
                    when (it.data) {
                        is CheckboxState -> {
                            button.text = it.data.selected.toString()
                        }
                        is Data -> {
                            button.text = it.data.string
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.Main).collect()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}


sealed class MainDialogEvents
data class Data(val string: String) : MainDialogEvents()
data class CheckboxState(val possible: List<String>, val selected: List<String> = emptyList()) : MainDialogEvents()
