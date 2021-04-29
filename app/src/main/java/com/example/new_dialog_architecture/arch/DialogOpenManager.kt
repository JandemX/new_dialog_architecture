package com.example.new_dialog_architecture.arch

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@FlowPreview
class DialogOpenManager(private val scope: CoroutineScope) {

    private val _state: MutableStateFlow<DialogFragment?> = MutableStateFlow(null)

    private lateinit var fragmentManager: FragmentManager

    @OptIn(ExperimentalTime::class)
    fun bind(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        scope.launch {
            _state.filterNotNull().sample(100.milliseconds).onEach {
                this@DialogOpenManager.fragmentManager.beginTransaction().apply {
                    it.show(this, "test")
                }
            }.collect()
        }
    }

    fun openDialog(dialogToBe: ()->DialogFragment) {
        _state.value = dialogToBe()
    }
}
