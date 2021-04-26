package com.example.new_dialog_architecture.ui.main.di

import com.example.new_dialog_architecture.ui.main.MainFragment
import com.example.new_dialog_architecture.ui.main.di.scopes.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [DialogInteractorModule::class])
abstract class FragmentBindingsModule() {

    @ContributesAndroidInjector()
    @FragmentScope
    abstract fun provideMain(): MainFragment

}
