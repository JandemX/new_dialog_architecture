package com.example.new_dialog_architecture.arch.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.DialogView
import com.example.new_dialog_architecture.arch.Interactor
import com.example.new_dialog_architecture.arch.SimpleInteractionDialogVM
import com.example.new_dialog_architecture.arch.argument
import com.example.new_dialog_architecture.arch.dialogInteractor
import com.example.new_dialog_architecture.arch.dialogs.DialogInteraction.Positive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StateFullInteractionDialog<Event, State : Any> : StateDialog<Event, State>, AppCompatDialogFragment() {

    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(initialState) as T
        }
    }

    override fun interact(event: DialogInteraction<Event>) {
        interactor.send(event)
        dismissAllowingStateLoss()
    }

    override val state: State
        get() = viewModel.stateSubject.value ?: initialState

    override fun updateState(newState: State) {
        viewModel.stateSubject.compareAndSet(state, newState)
    }

    private val viewModel: SimpleInteractionDialogVM<State> by viewModels(factoryProducer = { producer })
    private val interactor: Interactor<Event> by dialogInteractor()
    private lateinit var initialState: State


    private lateinit var title: TextView
    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private var layout: Int by argument()

    private var customView: DialogView<Event, State> by argument()

    private var onPositiveAction: ((State) -> Event)? by argument()
    private var onNegativeAction: () -> Unit by argument()
    private var dialogTitle: String by argument()
    private var positiveButtonText: String by argument()
    private var negativeButtonText: String by argument()
    private var cancellable: Boolean by argument()
    private var singleChoice: Boolean by argument()

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
                            onPositiveAction?.invoke(state)?.run {
                                interact(Positive(this))
                            }
                        }.collect()
            }
        }

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            positiveButton = findViewById(R.id.positive_button)
            title = findViewById(R.id.dialog_title)
        }

        customView.setView(
                view = view,
                state = ::state,
                update = ::updateState
        )
        positiveButton.apply {
            isVisible = !singleChoice
            text = positiveButtonText
            setOnClickListener {
                onPositiveAction?.invoke(state)?.run {
                    interact(Positive(this))
                }
                dismissAllowingStateLoss()
            }
        }
        title.text = dialogTitle
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = window?.windowManager?.defaultDisplay?.width
        width?.run { dialog?.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = cancellable
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        fun <Event, State : Any> newInstance(
                layoutid: Int,
                initialState: State,
                customView: DialogView<Event, State>,
                onPositiveAction: ((State) -> Event)?,
                onNegativeAction: () -> Unit,
                title: String,
                positiveText: String,
                negativeText: String,
                cancellable: Boolean,
                singleChoice: Boolean,
        ): StateFullInteractionDialog<Event, State> = StateFullInteractionDialog<Event, State>().apply {
            layout = layoutid
            this.initialState = initialState
            this.customView = customView
            this.onPositiveAction = onPositiveAction
            this.onNegativeAction = onNegativeAction
            this.dialogTitle = title
            this.positiveButtonText = positiveText
            this.negativeButtonText = negativeText
            this.cancellable = cancellable
            this.singleChoice = singleChoice

        }
    }
}



