package com.example.new_dialog_architecture.ui.main.examples

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

interface ComposeDialogView<State, Event> : Parcelable {
    @Composable
    fun compose(stateFlow: MutableStateFlow<State?>, interact: (Event) -> Unit)
}
