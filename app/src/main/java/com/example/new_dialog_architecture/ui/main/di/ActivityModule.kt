package com.example.new_dialog_architecture.ui.main.di

import com.example.new_dialog_architecture.MainActivity
import com.example.new_dialog_architecture.ui.main.di.scopes.ActivityScope
import com.example.new_dialog_architecture.ui.main.di.scopes.TestScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBindingsModule::class])
    @ActivityScope
    abstract fun contributeMainActivity(): MainActivity
}
