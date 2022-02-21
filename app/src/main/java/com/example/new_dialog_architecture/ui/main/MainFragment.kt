package com.example.new_dialog_architecture.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.WebviewActivity
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.bottomSheet
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.dialog
import com.example.new_dialog_architecture.arch.DialogBuilder.Companion.openDialog
import com.example.new_dialog_architecture.arch.dialogInteractor
import com.example.new_dialog_architecture.ui.main.MainDialogEvents.*
import com.example.new_dialog_architecture.ui.main.examples.CheckBoxDialogView
import com.example.new_dialog_architecture.ui.main.examples.ComposeDialogView
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

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

    private val number = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.main_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        viewmodel.bind(interactor)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interactor.eventStream.onEach { event ->
            when (event) {
                is BottomSheetCheckboxEvent.Positive -> {
                    openGoogle(requireActivity())
                }
                else -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

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

//            openDialog {
//                dialogCompose(DialogTestView()) {
//                    initialState =
//                        CheckBoxDialogView.MultiCheckboxState(possible = List(5) { "checkbox$it" },
//                            selected = List(3) { "checkbox$it" })
//                }
//            }

            openDialog {
                dialog(CheckBoxDialogView()) {
                    initialState = CheckBoxDialogView.MultiCheckboxState(List(5) { "checkbox$it" })
                    dialogTitle = "Simple Checkbox Dialog"
                    buttons {
                        positiveButtonText = "Positive"
                        negativeButtonText = "Negative"
                        onPositiveAction = {
                            println("test dialog")
                            DialogCheckboxEvent.Positive(it?.selected.toString())
                        }
                    }
                }
            }
        }

        buttonBottomSheetCheckbox.setOnClickListener {
            openDialog {
                bottomSheet(CheckBoxDialogView()) {
                    initialState = CheckBoxDialogView.MultiCheckboxState(
                        List(5) { "checkbox$it" },
                        action = {
                            Log.d("MainFragment", "action with " + it * Random.nextInt())
                        }
                    )

                    buttons {
                        positiveButtonText = "Positive"
                    }
                }
            }
        }

        buttonBottomSheetList.setOnClickListener {
            openDialog {
                bottomSheet(CheckBoxDialogView()) {
                    initialState = CheckBoxDialogView.MultiCheckboxState(
                        List(5) { "checkbox$it" },
                        action = {
                            it * 5
                        }
                    )
                    buttons {
                        positiveButtonText = "Positive"
                        onPositiveAction =
                            { state -> BottomSheetCheckboxEvent.Positive(state?.selected.toString()) }
                    }
                }
//                bottomSheet<BottomSheetListEvent, ItemListDialogView.ListDialogState>(
//                    ItemListDialogView()
//                ) {
//                    initialState = ItemListDialogView.ListDialogState(List(50) { "listitem$it" })
//                    buttons {
//                        onPositiveAction = { BottomSheetListEvent.Positive(it?.selected ?: "") }
//                        onNegativeAction = { BottomSheetListEvent.Negative }
//                    }
//                    additional {
//                        singleChoice = true
//                        cancellable = true
//                    }
//                }
            }
        }
    }


    @Parcelize
    class DialogTestView :
        ComposeDialogView<CheckBoxDialogView.MultiCheckboxState, MainDialogEvents> {

        @Composable
        override fun compose(
            stateFlow: MutableStateFlow<CheckBoxDialogView.MultiCheckboxState?>,
            interact: (MainDialogEvents) -> Unit
        ) {
            Column {
                stateFlow.value?.apply {
                    possible.forEach { element ->
                        Row {
                            val state = remember {
                                mutableStateOf(stateFlow.value?.selected ?: emptyList())
                            }
                            Text(text = element)
                            Checkbox(
                                checked = state.value.contains(element),
                                onCheckedChange = {
                                    if (it) {
                                        state.value = state.value + element
                                        stateFlow.value =
                                            stateFlow.value?.copy(selected = selected + element)
                                    } else {
                                        state.value = state.value - element
                                        stateFlow.value =
                                            stateFlow.value?.copy(selected = selected - element)
                                    }
                                })
                        }
                    }
                }

                Button(
                    onClick = {
                        interact(DialogCheckboxEvent.Positive(stateFlow.value?.selected.toString()))
                    }) {

                }
            }
        }
    }

    private fun openGoogle(context: Context) {
        val intent = Intent(context, WebviewActivity::class.java)
        startActivity(intent)
    }
}

sealed class MainDialogEvents {

    sealed class BottomSheetListEvent : MainDialogEvents() {
        data class Positive(val data: String) : BottomSheetListEvent()
        object Negative : BottomSheetListEvent()
    }

    sealed class CheckBoxStringEvent : MainDialogEvents() {
        abstract val data: String
    }

    sealed class DialogCheckboxEvent : MainDialogEvents() {
        data class Positive(val data: String) : DialogCheckboxEvent()
    }

    sealed class BottomSheetCheckboxEvent : MainDialogEvents() {
        data class Positive(val data: String) : BottomSheetCheckboxEvent()
    }
}
