package com.example.new_dialog_architecture.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.ui.main.DialogInteractorEvent
import com.example.new_dialog_architecture.ui.main.StateDialog

class StateFullInteractionDialog<Event, State> : StateDialog<Event, State>() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(R.layout.dialog_scaffold, container, false)
        val contentView = inflater.inflate(layout, container, false)
        val mainviewContent = mainView.findViewById<FrameLayout>(R.id.my_content)
        mainviewContent.addView(contentView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            positiveButton = findViewById(R.id.positive_button)
            title = findViewById(R.id.dialog_title)
        }

        customView(view, this)
        positiveButton.apply {
            text = positiveButtonText
            setOnClickListener {
                onPositiveAction(viewModel.stateStream.value)?.run {
                    interactor.send(DialogInteractorEvent.Positive(this))
                }
                dismissAllowingStateLoss()
            }
        }
        title.text = dialogTitle
    }

    private fun ViewGroup.replaceView(now: View, toBe: View) {
        val index = indexOfChild(now)
        removeView(now)
        removeView(toBe)
        addView(toBe, index)
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
                negativeText: String
        ): StateFullInteractionDialog<Event, State> = StateFullInteractionDialog<Event, State>().apply {
            layout = layoutid
            this.initialState = initialState
            this.customView = customView
            this.onPositiveAction = onPositiveAction
            this.onNegativeAction = onNegativeAction
            this.dialogTitle = title
            this.positiveButtonText = positiveText
            this.negativeButtonText = negativeText
        }
    }
}



