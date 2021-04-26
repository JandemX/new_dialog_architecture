package com.example.new_dialog_architecture.ui.main.di

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class Controller: DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out Controller> =
            DaggerAppComponent.builder().provideApplication(this).build()
}
