package com.example.new_dialog_architecture.di

import com.example.new_dialog_architecture.MainActivity
import com.example.new_dialog_architecture.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBindingsModule::class])
    @ActivityScope
    abstract fun contributeMainActivity(): MainActivity
}
