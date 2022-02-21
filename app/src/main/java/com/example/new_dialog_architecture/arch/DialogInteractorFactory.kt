package com.example.new_dialog_architecture.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DialogInteractorFactory<Event : Any> : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Interactor<Event>() as T
    }
}
