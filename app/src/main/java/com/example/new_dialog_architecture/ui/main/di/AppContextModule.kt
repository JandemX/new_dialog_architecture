package com.example.new_dialog_architecture.ui.main.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppContextModule {

    @Binds
    @Singleton
    internal abstract fun bindAppContext(application: Application): Context
}
