package com.google.samples.apps.iosched.di

import android.app.Activity
import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.internal.Beta
import dagger.internal.Preconditions
import leakcanary.AppWatcher
import javax.inject.Inject

@Beta
abstract class DaggerController(args: Bundle? = null) : LifecycleController(args), HasAndroidInjector {

    private var hasExited = false

    override fun onDestroy() {
        super.onDestroy()
        if (hasExited) {
            AppWatcher.objectWatcher.watch(this, "watch on a Controller")
        }
    }

    override fun onChangeEnded(
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        super.onChangeEnded(changeHandler, changeType)
        hasExited = !changeType.isEnter
        if (isDestroyed) {
            AppWatcher.objectWatcher.watch(this, "watch on a Controller")
        }
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any?>

    fun inject() = inject(this, findHasAndroidInjectorForController(this))

    override fun androidInjector(): AndroidInjector<Any?>? {
        return androidInjector
    }

    companion object {
        private fun inject(target: Any, hasAndroidInjector: HasAndroidInjector) {
            val androidInjector = hasAndroidInjector.androidInjector()
            Preconditions.checkNotNull(androidInjector, "%s.androidInjector() returned null",
                hasAndroidInjector.javaClass)
            androidInjector.inject(target)
        }

        private fun findHasAndroidInjectorForController(controller: Controller): HasAndroidInjector {
            var parentController: Controller? = controller
            while (parentController?.parentController.also { parentController = it } != null) {
                if (parentController is HasAndroidInjector) {
                    return parentController as HasAndroidInjector
                }
            }
            val activity: Activity? = controller.activity
            if (activity is HasAndroidInjector) {
                return activity
            }
            if (activity?.application is HasAndroidInjector) {
                return activity.application as HasAndroidInjector
            }
            throw IllegalArgumentException(
                String.format("No injector was found for %s", controller.javaClass.canonicalName)
            )
        }
    }
}