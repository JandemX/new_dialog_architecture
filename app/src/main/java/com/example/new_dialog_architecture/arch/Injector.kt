package com.example.new_dialog_architecture.arch

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

interface Injector<T> {
    fun inject(target: T)
}

/**
 * Usage:
 * - [Application] : [InjectorHolder.Provider], getting it from AppComponent : [InjectorHolder.Provider]
 * - add YourInjector to object graph using
 *    @Binds
 *    @IntoMap
 *    @ClassKey(YourInjector::class)
 */
@Singleton
class InjectorHolder @Inject constructor(
    private val injectors: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Injector<*>>
) {
    @Suppress("UNCHECKED_CAST")
    fun <I : Injector<*>> getInjector(clazz: Class<I>): I = injectors[clazz] as I

    interface Provider {
        val injectorHolder: InjectorHolder
    }
}

/**
 * Requires [Application] class to implement [InjectorHolder.Provider] interface
 */
inline fun <reified T : Injector<*>> Context.injector(): T =
    (applicationContext as InjectorHolder.Provider).injectorHolder.getInjector(T::class.java)

inline fun <T, reified I : Injector<T>> Context.inject(target: T) {
    (applicationContext as InjectorHolder.Provider).injectorHolder.getInjector(I::class.java).inject(target)
}
