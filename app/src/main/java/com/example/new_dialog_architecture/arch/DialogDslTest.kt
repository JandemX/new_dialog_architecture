package com.example.new_dialog_architecture.arch

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

data class DialogBuilder<Event, State>(
        val alertContext: Context,
        val layoutId: Int,
        val initialState: State,
        val setCustomView: (View, StateDialog<Event, State>) -> Unit,
        val onPositiveAction: (State?) -> Event?,
        val onNegativeAction: () -> Unit,
        val dialogTitle: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
) {
    constructor(builder: Builder<Event, State>) : this(
            builder.alertContext,
            builder.layoutId,
            builder.initialState!!,
            builder.contentView,
            builder.onPositiveAction,
            builder.onNegativeAction,
            builder.dialogTitle,
            builder.positiveButtonText,
            builder.negativeButtonText,
    )

    fun build() = StateFullInteractionDialog.newInstance(layoutId, initialState, setCustomView, onPositiveAction, onNegativeAction, dialogTitle, positiveButtonText, negativeButtonText)

    companion object {
        fun <Event, State> Fragment.dialog(view: DialogView<Event, State>, block: Builder<Event, State>.() -> Unit): DialogFragment {
            return Builder(this.requireContext(), view).apply { block() }.build()
        }
    }

    class Builder<Event, State>(context: Context, private val view: DialogView<Event, State>) {
        val alertContext: Context = context
        val layoutId: Int = view.layoutId
        val contentView: (View, StateDialog<Event, State>) -> Unit = view::setView

        var initialState: State? = null

        var dialogTitle: String = ""
        var positiveButtonText: String = ""
        var negativeButtonText: String = ""

        var onPositiveAction: (State?) -> Event? = { null }
        var onNegativeAction: () -> Unit = {}

        fun build() = DialogBuilder(this).build()
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

