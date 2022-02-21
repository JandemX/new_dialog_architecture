package com.example.new_dialog_architecture.arch.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.*
import com.example.new_dialog_architecture.getMyVM
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

internal class StatefulInteractionDialog<Event : Any, State : Any> : StateDialog,
    AppCompatDialogFragment() {

    private lateinit var dialogData: DialogBuilder.DialogData<Event, State>
    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(dialogData) as T
        }
    }

    private lateinit var viewModel: SimpleInteractionDialogVM<State, Event>

    // internal lateinit var dialogOpenHelper: DialogOpenHelper
    override fun onAttach(context: Context) {
        interactor = dialogInteractor<Event>(interactorKey).value
        viewModel = ViewModelProvider(requireActivity(), producer).getMyVM(tag ?: "")

        super.onAttach(context)
    }

    override val customTag: String by lazy { "dialogData.contentView.instanceId " }

    private fun interact(event: Event, closeDialog: Boolean = false) {
        interactor.send(event)
        if (closeDialog) {
            dismissAllowingStateLoss()
        }
    }

    private val state
        get() = viewModel.stateStream.value

    private fun updateState(newState: State) {
        // if we select the already selected option in a SingleChoiceDialog,
        // compareAndSet will not trigger any update because it has the same state, and therefore
        // we need to dismiss it in that case
        if (scaffold.additional.singleChoice) {
            dismissAllowingStateLoss()
        }
        viewModel.updateState(newState)
    }

    private lateinit var interactor: Interactor<Event>

    private lateinit var title: TextView
    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button
    private lateinit var divider: View

    private var interactorKey: String by argument()

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        scaffold.run {
            val mainView = inflater.inflate(R.layout.dialog_scaffold, container, false)
            val contentView = inflater.inflate(layoutId, container, false)
            val mainviewContent = mainView.findViewById<FrameLayout>(R.id.my_content)
            mainviewContent.addView(contentView)

            if (additional.singleChoice) {
                viewModel.stateStream
                    .drop(1)
                    .onEach {
                        buttons.onPositiveAction?.invoke(state)?.run {
                            interact(this, true)
                        }
                    }.launchIn(lifecycleScope)
            }

            return mainView
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scaffold.run {
            with(view) {
                positiveButton = findViewById(R.id.positive_button)
                negativeButton = findViewById(R.id.negative_button)
                title = findViewById(R.id.dialog_title)
                divider = findViewById(R.id.divider)
            }

            view.updateDialogHeight(buttons.negativeButtonText)

            title.apply {
                text = dialogTitle
                isVisible = dialogTitle.isNotBlank()
            }

            positiveButton.apply {
                isVisible = !additional.singleChoice
                text = buttons.positiveButtonText
                setOnClickListener {
                    buttons.onPositiveAction?.invoke(state)?.run {
                        interact(this, true)
                    }
                    dismissAllowingStateLoss()
                }
            }

            negativeButton.apply {
                isVisible = !additional.singleChoice && buttons.negativeButtonText.isNotBlank()
                text = buttons.negativeButtonText
                setOnClickListener {
                    buttons.onNegativeAction?.invoke()?.run {
                        interact(this, true)
                    }
                }
            }

            contentView.bind(
                view = view,
                state = ::state,
                update = ::updateState,
                sendEvent = ::interact
            )
        }
    }

    private fun View.updateDialogHeight(negativeButtonText: String) {
        val content = findViewById<ConstraintLayout>(R.id.dialog_scaffold_content)
        val maxScreenHeight = dialog?.window?.windowManager?.defaultDisplay?.height?.times(0.80)
        content.post {
            if (maxScreenHeight != null && maxScreenHeight < content.height) {
                content.layoutParams =
                    FrameLayout.LayoutParams(content.width, maxScreenHeight.toInt())
                divider.isVisible = negativeButtonText.isNotEmpty()
            } else {
                content.layoutParams = FrameLayout.LayoutParams(content.width, content.height)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        // dialogOpenHelper.dialogIsGone()
        super.onDismiss(dialog)
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = window?.windowManager?.defaultDisplay?.width
        width?.run { window.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT) }
    }

    override fun onCancel(dialog: DialogInterface) {
        scaffold.buttons.onNegativeAction?.invoke()?.run(::interact)
        super.onCancel(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.isCancelable = scaffold.additional.cancellable

        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                scaffold.additional.onShow()
            }
        }
    }

    private val scaffold
        get() = viewModel.dialogData

    companion object {
        internal fun <Event : Any, State : Any> newInstance(
            eventClazz: KClass<Event>,
            dialogData: DialogBuilder.DialogData<Event, State>
        ) =
            StatefulInteractionDialog<Event, State>().apply {
                interactorKey = eventClazz.simpleName
                    ?: throw IllegalArgumentException("StatefullInteractionDialog Interactor key cannot be null")
                this.dialogData = dialogData
            }
    }

    internal class StatefulDialogInjector<Event : Any, State : Any>(
        // private val dialogOpenHelper: Provider<DialogOpenHelper>
    ) : Injector<StatefulInteractionDialog<Event, State>> {
        override fun inject(target: StatefulInteractionDialog<Event, State>) {
            // target.dialogOpenHelper = dialogOpenHelper.get()
        }
    }
}
