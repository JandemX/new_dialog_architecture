package com.example.new_dialog_architecture.arch.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogBuilder
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.arch.Interactor
import com.example.new_dialog_architecture.arch.SimpleInteractionDialogVM
import com.example.new_dialog_architecture.arch.argument
import com.example.new_dialog_architecture.arch.dialogInteractor
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StateFullBottomSheetDialog<Event, State : Any> : StateDialog<Event, State>, BottomSheetDialogFragment() {

    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(initialState) as T
        }
    }

    override val state: State
        get() = viewModel.stateSubject.value ?: initialState

    override fun updateState(newState: State) {
        viewModel.stateSubject.compareAndSet(state, newState)
    }

    override fun interact(event: Event) {
        interactor.send(event)
        dismissAllowingStateLoss()
    }

    private var customView: DialogView<State> by argument()

    private val viewModel: SimpleInteractionDialogVM<State> by viewModels(factoryProducer = { producer })
    private val interactor: Interactor<Event> by dialogInteractor()

    private lateinit var initialState: State
    private lateinit var title: TextView
    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private var layout: Int by argument()
    private var onPositiveAction: ((State) -> Event)? by argument()
    private var onNegativeAction: (() -> Event)? by argument()
    private var dialogTitle: String by argument()
    private var positiveButtonText: String by argument()
    private var negativeButtonText: String by argument()

    private var singleChoice: Boolean by argument()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.dialog_scaffold, container, false)
        val contentView = inflater.inflate(layout, container, false)
        val mainviewContent = mainView.findViewById<FrameLayout>(R.id.my_content)
        mainviewContent.addView(contentView)

        if (singleChoice) {
            lifecycleScope.launch {
                viewModel.stateStream
                        .drop(1)
                        .onEach {
                            onPositiveAction?.invoke(state)?.run(::interact)
                        }.collect()
            }
        }

        return mainView
    }

    override fun onDismiss(dialog: DialogInterface) {
        onNegativeAction?.invoke()?.run(::interact)
        super.onDismiss(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            positiveButton = findViewById(R.id.positive_button)
            title = findViewById(R.id.dialog_title)
        }
        positiveButton.isVisible = false
        title.isVisible = false
        customView.bind(
                view = view,
                state = ::state,
                update = ::updateState
        )
    }

    companion object {
        fun <Event, State : Any> newInstance(dialogData: DialogBuilder.DialogData<Event, State>): StateFullBottomSheetDialog<Event, State> =
                StateFullBottomSheetDialog<Event, State>().apply {
                    layout = dialogData.layoutId
                    initialState = dialogData.initialState
                    customView = dialogData.contentView
                    onPositiveAction = dialogData.buttons.onPositiveAction
                    onNegativeAction = dialogData.buttons.onNegativeAction
                    dialogTitle = dialogData.dialogTitle
                    positiveButtonText = dialogData.buttons.positiveButtonText
                    negativeButtonText = dialogData.buttons.negativeButtonText
                    singleChoice = dialogData.additional.singleChoice
                }
    }
}
