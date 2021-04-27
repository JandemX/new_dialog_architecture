package com.example.new_dialog_architecture.arch

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.new_dialog_architecture.arch.dialogs.StateFullBottomSheetDialog
import com.example.new_dialog_architecture.arch.dialogs.StateFullInteractionDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

data class DialogBuilder<Event, State : Parcelable>(
        val alertContext: Context,
        val layoutId: Int,
        val initialState: State,
        val setCustomView: DialogView<Event, State>,
        val onPositiveAction: ((State) -> Event)?,
        val onNegativeAction: () -> Unit,
        val dialogTitle: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
        val cancellable: Boolean,
        val singleChoice: Boolean,
) {
    constructor(configuration: Configuration<Event, State>) : this(
            configuration.alertContext,
            configuration.layoutId,
            configuration.initialState,
            configuration.contentView,
            configuration.onPositiveAction,
            configuration.onNegativeAction,
            configuration.dialogTitle,
            configuration.positiveButtonText,
            configuration.negativeButtonText,
            configuration.cancellable,
            configuration.singleChoice
    )

    fun build() = StateFullInteractionDialog.newInstance(
            layoutId,
            initialState,
            setCustomView,
            onPositiveAction,
            onNegativeAction,
            dialogTitle,
            positiveButtonText,
            negativeButtonText,
            cancellable,
            singleChoice
    )

    fun buildBottomSheet() = StateFullBottomSheetDialog.newInstance(
            layoutId,
            initialState,
            setCustomView,
            onPositiveAction,
            onNegativeAction,
            dialogTitle,
            positiveButtonText,
            negativeButtonText,
            singleChoice
    )

    class Configuration<Event, State : Parcelable>(context: Context, view: DialogView<Event, State>) {

        operator fun invoke(): DialogBuilder<Event, State> = DialogBuilder(this)

        val alertContext: Context = context
        val layoutId: Int = view.layoutId

        var contentView: DialogView<Event, State> = view

        lateinit var initialState: State

        var dialogTitle: String = ""
        var positiveButtonText: String = ""
        var negativeButtonText: String = ""

        var onPositiveAction: ((State) -> Event)? = null
        var onNegativeAction: () -> Unit = {}

        var cancellable: Boolean = true
        var singleChoice: Boolean = false

    }

    companion object {
        fun <Event, State : Parcelable> Fragment.dialog(view: DialogView<Event, State>, block: Configuration<Event, State>.() -> Unit): DialogFragment {
            return Configuration(this.requireContext(), view).apply { block() }.invoke().build()
        }

        fun <Event, State : Parcelable> Fragment.bottomSheet(view: DialogView<Event, State>, block: Configuration<Event, State>.() -> Unit): BottomSheetDialogFragment {
            return Configuration(this.requireContext(), view).apply { block() }.invoke().buildBottomSheet()
        }
    }
}


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

