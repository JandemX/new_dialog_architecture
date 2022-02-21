package com.example.new_dialog_architecture.arch

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.new_dialog_architecture.arch.dialogs.StateDialog
import com.example.new_dialog_architecture.arch.dialogs.StatefulBottomSheetDialog
import com.example.new_dialog_architecture.arch.dialogs.StatefulInteractionDialog
import kotlin.reflect.KClass

@DslMarker
annotation class DialogBuilderDsl

@DialogBuilderDsl
class DialogBuilder<Event, State : Any>(
    private val context: Context,
    private val contentView: DialogView<State, Event>
) {

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
        var onShow: () -> Unit = { }

        fun build(): Additional = Additional(cancellable, singleChoice, onShow)
    }

    @DialogBuilderDsl
    class DialogButtonsBuilder<Event, State> {
        var positiveButtonText: String = ""
        var negativeButtonText: String = ""
        var onPositiveAction: ((State?) -> Event)? = null
        var onNegativeAction: (() -> Event)? = null

        fun build(): DialogButton<Event, State> =
            DialogButton(positiveButtonText, negativeButtonText, onPositiveAction, onNegativeAction)
    }

    data class DialogData<Event, State>(
        val context: Context,
        val layoutId: Int,
        val initialState: State,
        val dialogTitle: String,
        val contentView: DialogView<State, Event>,
        val buttons: DialogButton<Event, State>,
        val additional: Additional
    )

    data class DialogButton<Event, State>(
        val positiveButtonText: String,
        val negativeButtonText: String,
        val onPositiveAction: ((State?) -> Event)?,
        val onNegativeAction: (() -> Event)?
    )

    data class Additional(
        val cancellable: Boolean,
        val singleChoice: Boolean,
        val onShow: () -> Unit
    )

    companion object {
        @PublishedApi
        internal fun <Event : Any, State : Any> DialogData<Event, State>.createDialog(eventClazz: KClass<Event>) =
            StatefulInteractionDialog.newInstance(eventClazz, this)

        @PublishedApi
        internal fun <Event : Any, State : Any> DialogData<Event, State>.createBottomSheet(clazz: KClass<Event>) =
            StatefulBottomSheetDialog.newInstance(clazz = clazz, this)

        internal inline fun <reified Event : Any, State : Any> Context.dialog(
            view: DialogView<State, Event>,
            block: DialogBuilder<Event, State>.() -> Unit
        ): DialogFragment =
            DialogBuilder<Event, State>(this, view).apply(block).build().createDialog(Event::class)

        inline fun <reified Event : Any, State : Any> Fragment.dialog(
            view: DialogView<State, Event>,
            block: DialogBuilder<Event, State>.() -> Unit
        ): DialogFragment =
            DialogBuilder<Event, State>(requireContext(), view).apply(block).build()
                .createDialog(Event::class)

        fun Fragment.openDialog(dialog: Fragment.() -> DialogFragment) {
            with(dialog()) {
                val tag = (this as? StateDialog)?.customTag ?: ""
                val transaction = this@openDialog.childFragmentManager.beginTransaction()
                show(transaction, tag)
                this@openDialog.childFragmentManager.executePendingTransactions()
            }
        }

        inline fun <reified Event : Any, State : Any> Context.bottomSheet(
            view: DialogView<State, Event>,
            block: DialogBuilder<Event, State>.() -> Unit
        ): DialogFragment =
            DialogBuilder<Event, State>(this, view).apply(block).build()
                .createBottomSheet(Event::class)

        inline fun <reified Event : Any, State : Any> Fragment.bottomSheet(
            view: DialogView<State, Event>,
            block: DialogBuilder<Event, State>.() -> Unit
        ): DialogFragment =
            DialogBuilder<Event, State>(requireContext(), view).apply(block).build()
                .createBottomSheet(Event::class)
    }
}
