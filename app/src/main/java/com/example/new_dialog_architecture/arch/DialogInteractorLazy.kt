package com.example.new_dialog_architecture.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlin.reflect.KClass

class DialogInteractorLazy<I : ViewModel>(
    private val viewModelClass: KClass<I>,
    private val key: String,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<I> {

    private var cached: I? = null

    override val value: I
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(key, viewModelClass.java)
                    .also {
                        cached = it
                    }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}
