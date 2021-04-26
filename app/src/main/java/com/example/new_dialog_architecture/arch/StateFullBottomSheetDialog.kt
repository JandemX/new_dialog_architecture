package com.example.new_dialog_architecture.arch

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StateFullBottomSheetDialog<Event, State> : StateDialog<Event, State>, BottomSheetDialogFragment() {

    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(initialState) as T
        }
    }

    override val state: State?
        get() = viewModel.stateSubject.value

    override fun updateState(newState: State) {
        viewModel.stateSubject.compareAndSet(state, newState)
    }

    override fun interact(event: DialogInteraction<Event>) {
        interactor.send(event)
        dismissAllowingStateLoss()
    }

    private val viewModel: SimpleInteractionDialogVM<State> by viewModels(factoryProducer = { producer })
    private val interactor: Interactor<Event> by dialogInteractor()
    private var initialState: State? = null

    private lateinit var title: TextView
    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private var layout: Int by argument()
    private var customView: (View, StateDialog<Event, State>) -> Unit by argument()
    private var onPositiveAction: (State?) -> Event? by argument()
    private var onNegativeAction: () -> Unit by argument()
    private var dialogTitle: String by argument()
    private var positiveButtonText: String by argument()
    private var negativeButtonText: String by argument()

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
                            onPositiveAction(it)?.run {
                                interact(DialogInteraction.Positive(this))
                            }
                        }.collect()
            }
        }

        return mainView
    }

    override fun onDismiss(dialog: DialogInterface) {
        onNegativeAction()
        super.onDismiss(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            positiveButton = findViewById(R.id.positive_button)
            title = findViewById(R.id.dialog_title)
        }
        positiveButton.isVisible = false
        title.isVisible = false
        customView(view, this)
    }

    companion object {
        fun <Event, State> newInstance(
                layoutid: Int,
                initialState: State?,
                customView: (View, StateDialog<Event, State>) -> Unit,
                onPositiveAction: (State?) -> Event?,
                onNegativeAction: () -> Unit,
                title: String,
                positiveText: String,
                negativeText: String,
                singleChoice: Boolean,
        ): StateFullBottomSheetDialog<Event, State> = StateFullBottomSheetDialog<Event, State>().apply {
            layout = layoutid
            this.initialState = initialState
            this.customView = customView
            this.onPositiveAction = onPositiveAction
            this.onNegativeAction = onNegativeAction
            this.dialogTitle = title
            this.positiveButtonText = positiveText
            this.negativeButtonText = negativeText
            this.singleChoice = singleChoice
        }
    }
}
