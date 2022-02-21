package com.example.new_dialog_architecture.arch
//
//import android.content.Context
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment
//import com.example.new_dialog_architecture.arch.dialogs.StatefulComposeDialog
//import com.example.new_dialog_architecture.ui.main.examples.ComposeDialogView
//
//@DslMarker
//annotation class DialogComposeBuilderDsl
//
//@DialogBuilderDsl
//class DialogComposeBuilder<Event, State : Any>(private val context: Context, private val contentView: ComposeDialogView<State, Event>) {
//
//    private var buttons: DialogComposeButton<Event, State> = DialogButtonsBuilder<Event, State>().build()
//    private var additional: AdditionalCompose = AdditionalBuilder().build()
//    lateinit var initialState: State
//    var dialogTitle: String = ""
//
//    fun buttons(block: DialogButtonsBuilder<Event, State>.() -> Unit) {
//        buttons = DialogButtonsBuilder<Event, State>().apply(block).build()
//    }
//
//    fun additional(block: AdditionalBuilder.() -> Unit) {
//        additional = AdditionalBuilder().apply(block).build()
//    }
//
//    fun build(): DialogComposeData<Event, State> = DialogComposeData(
//            contentView,
//            context,
//            initialState,
//            dialogTitle,
//            buttons,
//            additional
//    )
//
//    @DialogBuilderDsl
//    class AdditionalBuilder {
//        var cancellable: Boolean = true
//        var singleChoice: Boolean = false
//
//        fun build(): AdditionalCompose = AdditionalCompose(cancellable, singleChoice)
//    }
//
//    @DialogBuilderDsl
//    class DialogButtonsBuilder<Event, State> {
//        var positiveButtonText: String = ""
//        var negativeButtonText: String = ""
//        var onPositiveAction: ((State) -> Event)? = null
//        var onNegativeAction: (() -> Event)? = null
//
//        fun build(): DialogComposeButton<Event, State> = DialogComposeButton(positiveButtonText, negativeButtonText, onPositiveAction, onNegativeAction)
//    }
//
//    data class DialogComposeData<Event, State>(
//            val composeView: ComposeDialogView<State, Event>,
//            val context: Context,
//            val initialState: State,
//            val dialogTitle: String,
//            val buttons: DialogComposeButton<Event, State>,
//            val additional: AdditionalCompose)
//
//    data class DialogComposeButton<Event, State>(
//            val positiveButtonText: String,
//            val negativeButtonText: String,
//            val onPositiveAction: ((State) -> Event)?,
//            val onNegativeAction: (() -> Event)?
//    )
//
//    data class AdditionalCompose(
//            val cancellable: Boolean,
//            val singleChoice: Boolean,
//    )
//
//    companion object {
//        private fun <Event, State : Any> DialogComposeData<Event, State>.createDialogCompose() =
//                StatefulComposeDialog.newInstance(this)
//
//
//        fun <Event, State : Any> Fragment.dialogCompose(view: ComposeDialogView<State, Event>, block: DialogComposeBuilder<Event, State>.() -> Unit): DialogFragment =
//                DialogComposeBuilder<Event, State>(requireContext(), view).apply(block).build().createDialogCompose()
//
//    }
//}
//
