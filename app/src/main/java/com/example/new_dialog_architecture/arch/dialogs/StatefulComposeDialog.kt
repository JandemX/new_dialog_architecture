package com.example.new_dialog_architecture.arch.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.*
import com.example.new_dialog_architecture.arch.SimpleInteractionDialogVM
import com.example.new_dialog_architecture.instanceId
import com.example.new_dialog_architecture.ui.main.examples.ComposeDialogView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
//
//class StatefulComposeDialog<Event: Any, State : Any> : StateDialog,
//    AppCompatDialogFragment() {
//
//    private val producer = object : ViewModelProvider.Factory {
//        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//            return SimpleInteractionDialogVM(initialState) as T
//        }
//    }
//
//    override val customTag: String by lazy { "customView.instanceId" }
//
//    private val state: State
//        get() = viewModel.stateSubject.value
//
//
//    private var initialState: State by argument()
//
//    private val viewModel: SimpleInteractionDialogVM<State> by viewModels(factoryProducer = { producer })
//    private lateinit var interactor: Interactor<Event>
//
//    private var composeContent: ComposeDialogView<State, Event> by argument()
//
//    private var onPositiveAction: ((State) -> Event)? by argument()
//    private var onNegativeAction: (() -> Event)? by argument()
//    private var dialogTitle: String by argument()
//    private var positiveButtonText: String by argument()
//    private var negativeButtonText: String by argument()
//    private var cancellable: Boolean by argument()
//    private var singleChoice: Boolean by argument()
//
//    override fun onAttach(context: Context) {
//        interactor = dialogInteractor<Event>(interactorKey).value
//        context
//            .applicationContext
//            .injector<StatefulBottomSheetDialog.StatefulBottomSheetInjector<Event, State>>().inject(this)
//        super.onAttach(context)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        if (singleChoice) {
//            lifecycleScope.launch {
//                viewModel.stateStream
//                    .drop(1)
//                    .onEach {
//                        onPositiveAction?.invoke(state)?.run {
//                            interact(this)
//                        }
//                    }.collect()
//            }
//        }
//
//        return ComposeView(requireContext()).apply {
//            setContent {
//                DialogScaffold {
//                    composeContent.compose(
//                        viewModel.stateSubject,
//                        interact = { interact(it) }
//                    )
//                }
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        with(view) {
////            positiveButton = findViewById(R.id.positive_button)
////            negativeButton = findViewById(R.id.negative_button)
////            title = findViewById(R.id.dialog_title)
////        }
////
////        title.text = dialogTitle
////
////        positiveButton.apply {
////            isVisible = !singleChoice
////            text = positiveButtonText
////            setOnClickListener {
////                onPositiveAction?.invoke(state)?.run {
////                    interact(this)
////                }
////                dismissAllowingStateLoss()
////            }
////        }
////
////        negativeButton.apply {
////            isVisible = !singleChoice && negativeButtonText.isNotBlank()
////            text = negativeButtonText
////            setOnClick    Listener {
////                onNegativeAction?.invoke()?.run {
////                    interact(this)
////                }
////                dismissAllowingStateLoss()
////            }
////        }
////
////        customView.bind(
////                view = view,
////                state = ::state,
////                update = ::updateState
////        )
//    }
//
//    override fun onCancel(dialog: DialogInterface) {
//        onNegativeAction?.invoke()?.run { interact(this) }
//        super.onCancel(dialog)
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        this.isCancelable = cancellable
//        return super.onCreateDialog(savedInstanceState)
//    }
//
//
//    @Composable
//    fun DialogScaffold(
//        customContent: @Composable () -> Unit
//    ) {
//        MaterialTheme(
//            colors = if (isSystemInDarkTheme()) {
//                darkColors()
//            } else lightColors(),
//        ) {
//            Surface(
//                Modifier.fillMaxHeight(),
//            ) {
//                customContent()
//            }
//        }
//
//
//    }
//
//    companion object {
//        fun <Event, State : Any> newInstance(dialogData: DialogComposeBuilder.DialogComposeData<Event, State>): StatefulComposeDialog<Event, State> =
//            StatefulComposeDialog<Event, State>().apply {
//                initialState = dialogData.initialState
//                composeContent = dialogData.composeView
//                onPositiveAction = dialogData.buttons.onPositiveAction
//                onNegativeAction = dialogData.buttons.onNegativeAction
//                dialogTitle = dialogData.dialogTitle
//                positiveButtonText = dialogData.buttons.positiveButtonText
//                negativeButtonText = dialogData.buttons.negativeButtonText
//                cancellable = dialogData.additional.cancellable
//                singleChoice = dialogData.additional.singleChoice
//            }
//    }
//}

