package com.example.new_dialog_architecture.arch

import android.app.Activity
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.new_dialog_architecture.arch.dialogs.StateFullBottomSheetDialog
import com.example.new_dialog_architecture.arch.dialogs.StateFullInteractionDialog

@DslMarker
annotation class DialogBuilderDsl

@DialogBuilderDsl
class DialogBuilder<Event, State : Any>(private val context: Context, private val contentView: DialogView<State>) {

    private val layoutId: Int = contentView.layoutId
    private var buttons: DialogButton<Event, State> = DialogButtonsBuilder<Event, State>().build()
    private var additional: Additional = AdditionalBuilder().build()
    lateinit var initialState: State
    var dialogTitle: String = ""

    fun buttons(block: DialogButtonsBuilder<Event, State>.() -> Unit) {
        buttons = DialogButtonsBuilder<Event, State>().apply(block).build()
    }

    fun additional(block: AdditionalBuilder.() -> Unit) {
        additional = AdditionalBuilder().apply(block).build()
    }

    fun build(): DialogData<Event, State> = DialogData(
            context,
            layoutId,
            initialState,
            dialogTitle,
            contentView,
            buttons,
            additional
    )

    @DialogBuilderDsl
    class AdditionalBuilder {
        var cancellable: Boolean = true
        var singleChoice: Boolean = false

        fun build(): Additional = Additional(cancellable, singleChoice)
    }

    @DialogBuilderDsl
    class DialogButtonsBuilder<Event, State> {
        var positiveButtonText: String = ""
        var negativeButtonText: String = ""
        var onPositiveAction: ((State) -> Event)? = null
        var onNegativeAction: (() -> Event)? = null

        fun build(): DialogButton<Event, State> = DialogButton(positiveButtonText, negativeButtonText, onPositiveAction, onNegativeAction)
    }

    data class DialogData<Event, State>(
            val context: Context,
            val layoutId: Int,
            val initialState: State,
            val dialogTitle: String,
            val contentView: DialogView<State>,
            val buttons: DialogButton<Event, State>,
            val additional: Additional
    )

    data class DialogButton<Event, State>(
            val positiveButtonText: String,
            val negativeButtonText: String,
            val onPositiveAction: ((State) -> Event)?,
            val onNegativeAction: (() -> Event)?
    )

    data class Additional(
            val cancellable: Boolean,
            val singleChoice: Boolean,
    )

    companion object {
        private fun <Event, State : Any> DialogData<Event, State>.createDialog() =
                StateFullInteractionDialog.newInstance(this)

        private fun <Event, State : Any> DialogData<Event, State>.createBottomSheet() =
                StateFullBottomSheetDialog.newInstance(this)

        fun <Event, State : Any> Fragment.dialog(view: DialogView<State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment =
                DialogBuilder<Event, State>(requireContext(), view).apply(block).build().createDialog()

        fun <Event, State : Any> Fragment.bottomSheet(view: DialogView<State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment =
                DialogBuilder<Event, State>(requireContext(), view).apply(block).build().createBottomSheet()

        fun <Event, State : Any> Activity.dialog(view: DialogView<State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment =
                DialogBuilder<Event, State>(this.applicationContext, view).apply(block).build().createDialog()

        fun <Event, State : Any> Activity.bottomSheet(view: DialogView<State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment =
                DialogBuilder<Event, State>(this.applicationContext, view).apply(block).build().createBottomSheet()
    }
}

