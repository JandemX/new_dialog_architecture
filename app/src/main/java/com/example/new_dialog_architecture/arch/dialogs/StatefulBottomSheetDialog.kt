package com.example.new_dialog_architecture.arch.dialogs

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.new_dialog_architecture.R
import com.example.new_dialog_architecture.arch.*
import com.example.new_dialog_architecture.getMyVM
import com.example.new_dialog_architecture.instanceId
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Suppress("DEPRECATION")
internal class StatefulBottomSheetDialog<Event : Any, State : Any> : StateDialog,
    BottomSheetDialogFragment() {

    // internal lateinit var dialogOpenHelper: DialogOpenHelper

    override val customTag: String by lazy { dialogData.contentView.instanceId }

    private val state
        get() = viewModel.stateStream.value

    private lateinit var viewModel: SimpleInteractionDialogVM<State, Event>
    private lateinit var interactor: Interactor<Event>

    private lateinit var title: TextView
    private lateinit var negativeButton: ImageView
    private lateinit var container: CoordinatorLayout

    private var interactorKey: String by argument()

    private lateinit var dialogData: DialogBuilder.DialogData<Event, State>

    private val producer = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SimpleInteractionDialogVM(dialogData) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog_Rounded)
        println("viewmodel instance id: " + viewModel.instanceId)
    }

    override fun onAttach(context: Context) {
        interactor = dialogInteractor<Event>(interactorKey).value
        viewModel = ViewModelProvider(requireActivity(), producer).getMyVM(tag ?: "")

        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        scaffold.run {
            val mainView = inflater.inflate(R.layout.bottomsheet_scaffold, container, false)
            val contentView = inflater.inflate(layoutId, container, false)
            val mainviewContent = mainView.findViewById<FrameLayout>(R.id.my_content)
            mainviewContent.addView(contentView)

            if (additional.singleChoice) {
                viewModel.stateStream
                    .drop(1)
                    .onEach {
                        buttons.onPositiveAction?.invoke(it)
                    }.launchIn(lifecycleScope)
            }

            mainView
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scaffold.run {
            with(view) {
                negativeButton = findViewById(R.id.close_sheet_button)
                container = findViewById(R.id.bottom_sheet_container)
                title = findViewById(R.id.dialog_title)
            }

            title.apply {
                text = dialogTitle
                isVisible = dialogTitle.isNotBlank()
            }

            negativeButton.setOnClickListener {
                dismissAllowingStateLoss()
            }

            contentView.bind(
                view = view,
                state = ::state,
                update = ::updateState,
                sendEvent = ::interact
            )

            dialog?.setOnShowListener {
                additional.onShow.invoke()
                val dialogParent = container.parent as View
                val behavior = BottomSheetBehavior.from(dialogParent)
                behavior.peekHeight = container.height
                dialogParent.requestLayout()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        // dialogOpenHelper.dialogIsGone()
        scaffold.buttons.onNegativeAction?.invoke()?.run(::interact)
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        scaffold.buttons.onNegativeAction?.invoke()?.run(::interact)
        super.onCancel(dialog)
    }

    private val scaffold
        get() = viewModel.dialogData

    private fun updateState(newState: State) {
        viewModel.updateState(newState)
    }

    private fun interact(event: Event, closeDialog: Boolean = false) {
        interactor.send(event)
        if (closeDialog) dismissAllowingStateLoss()
    }

    companion object {
        internal fun <Event : Any, State : Any> newInstance(
            clazz: KClass<Event>,
            dialogData: DialogBuilder.DialogData<Event, State>
        ): StatefulBottomSheetDialog<Event, State> =
            StatefulBottomSheetDialog<Event, State>().apply {
                interactorKey = clazz.simpleName
                    ?: throw IllegalArgumentException("StatefulBottomSheetDialog Interactor key cannot be null")
                this.dialogData = dialogData
            }
    }
}
