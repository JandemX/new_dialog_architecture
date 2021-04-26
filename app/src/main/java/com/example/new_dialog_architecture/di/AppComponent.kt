package com.example.new_dialog_architecture.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [ActivityModule::class, AndroidInjectionModule::class])
interface AppComponent : AndroidInjector<Controller> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun provideApplication(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(appController: Controller)
}
