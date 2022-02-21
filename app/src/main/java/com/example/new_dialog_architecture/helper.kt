package com.example.new_dialog_architecture

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val Any?.instanceId: String
    get() =
        if (this == null) "null" else "${javaClass.simpleName}@0x${
            System.identityHashCode(this).toString(16)
        }"


@MainThread
inline fun <reified VM : ViewModel> ViewModelProvider.getMyVM(key: String): VM =
    get(key, VM::class.java)
