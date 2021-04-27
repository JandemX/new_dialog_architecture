package com.example.new_dialog_architecture.arch

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.new_dialog_architecture.arch.dialogs.StateFullInteractionDialog
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@DslMarker
annotation class DialogBuilderDsl

@DialogBuilderDsl
class DialogBuilder<Event, State : Any>(private val context: Context, private val contentView: DialogView<Event, State>) {

    private val layoutId: Int = contentView.layoutId
    private var buttons: DialogButton<Event, State> = DialogButtonsBuilder<Event, State>().build()
    private var additional: Additional = AdditionalBuilder().build()

    var dialogTitle: String = ""

    lateinit var initialState: State

    fun buttons(block: DialogButtonsBuilder<Event, State>.() -> Unit) {
        buttons = DialogButtonsBuilder<Event, State>().apply(block).build()
    }

    fun additional(block: AdditionalBuilder.() -> Unit) {
        additional = AdditionalBuilder().apply(block).build()
    }

    fun build(): Dialog<Event, State> = Dialog(
            context,
            layoutId,
            initialState,
            dialogTitle,
            contentView,
            buttons,
            additional
    )


    companion object {
        fun <Event, State : Any> Fragment.dialog(view: DialogView<Event, State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment {
            return with(DialogBuilder(requireContext(), view).apply(block).build()) {
                StateFullInteractionDialog.newInstance(
                        layoutid = layoutId,
                        initialState = initialState,
                        customView = this.contentView,
                        onPositiveAction = buttons.onPositiveAction,
                        onNegativeAction = buttons.onNegativeAction,
                        title = dialogTitle,
                        positiveText = buttons.positiveButtonText,
                        negativeText = buttons.negativeButtonText,
                        cancellable = additional.cancellable,
                        singleChoice = additional.singleChoice
                )
            }
        }

        fun <Event, State : Any> Fragment.bottomSheet(view: DialogView<Event, State>, block: DialogBuilder<Event, State>.() -> Unit): DialogFragment {
            return with(DialogBuilder(requireContext(), view).apply(block).build()) {
                StateFullInteractionDialog.newInstance(
                        layoutid = layoutId,
                        initialState = initialState,
                        customView = this.contentView,
                        onPositiveAction = buttons.onPositiveAction,
                        onNegativeAction = buttons.onNegativeAction,
                        title = dialogTitle,
                        positiveText = buttons.positiveButtonText,
                        negativeText = buttons.negativeButtonText,
                        cancellable = additional.cancellable,
                        singleChoice = additional.singleChoice
                )
            }
        }
    }

}

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
    var onNegativeAction: (() -> Unit) = {}

    fun build(): DialogButton<Event, State> = DialogButton(positiveButtonText, negativeButtonText, onPositiveAction, onNegativeAction)
}

data class Dialog<Event, State>(
        val context: Context,
        val layoutId: Int,
        val initialState: State,
        val dialogTitle: String,
        val contentView: DialogView<Event, State>,
        val buttons: DialogButton<Event, State>,
        val additional: Additional
)

data class DialogButton<Event, State>(
        val positiveButtonText: String,
        val negativeButtonText: String,
        val onPositiveAction: ((State) -> Event)?,
        val onNegativeAction: () -> Unit
)

data class Additional(
        val cancellable: Boolean,
        val singleChoice: Boolean,
)


@Suppress("unused")
fun <T> Fragment.argument() = object : ReadWriteProperty<Fragment, T> {
    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also {
            thisRef.arguments = it
        }
        args.putAll(bundleOf(property.name to value))
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
            thisRef.arguments?.get(property.name) as T
}

fun <T> consume(prop: KMutableProperty0<T?>) = prop.get().also { prop.set(null) }

