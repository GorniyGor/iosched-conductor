/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.widget.conductor

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.plusAssign
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.R.styleable
import com.google.samples.apps.iosched.widget.conductor.ConductorNavigator.Destination

class ConductorNavHost(
    private val activity: Activity,
    private val view: ViewGroup,
    private val savedInstanceState: Bundle?) : NavHost {
    val router: Router

    private val navigationController =
        NavController(view.context).apply {
            //TODO( somehow in Fragment realisation is used view.parent )
            /*val rootView = if (view.parent != null) view.parent as View else view*/
            router = Conductor.attachRouter(activity, view, savedInstanceState)
            Navigation.setViewNavController(view, this)
            navigatorProvider += ConductorNavigator(
                router)
            setGraph(R.navigation.nav_graph)
        }

    override fun getNavController(): NavController = navigationController
}

@Navigator.Name("conductor_controller")
class ConductorNavigator(private val router: Router): Navigator<Destination>() {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        val controller = ControllerFactory.instantiate(
            router.activity!!.classLoader, destination.getClassName(), args
        )
        router.pushController(RouterTransaction.with(controller))
        return destination
    }

    override fun createDestination(): Destination = Destination(
        this)

    override fun popBackStack(): Boolean {
        return router.handleBack()
    }

    @NavDestination.ClassType(Controller::class)
    class Destination: NavDestination {
        constructor(navigatorProvider: NavigatorProvider) :
            this(navigatorProvider.getNavigator(
                ConductorNavigator::class.java))
        constructor(navigator: Navigator<out Destination?>) : super(navigator)

        private var mClassName: String? = null

        @CallSuper override fun onInflate(
            context: Context,
            attrs: AttributeSet
        ) {
            super.onInflate(context, attrs)
            val a = context.resources.obtainAttributes(attrs,
                styleable.ConductorNavigator)
            val className = a.getString(styleable.ConductorNavigator_android_name)
            className?.let { setClassName(it) }
            a.recycle()
        }

        /**
         * Set the Fragment class name associated with this destination
         * @param className The class name of the Fragment to show when you navigate to this
         * destination
         * @return this [Destination]
         * @see .instantiateFragment
         */
        private fun setClassName(className: String): Destination {
            mClassName = className
            return this
        }

        /**
         * Gets the Controller's class name associated with this destination
         *
         * @throws IllegalStateException when no Controller class was set.
         * @see ControllerFactory.instantiate
         */
        fun getClassName(): String {
            checkNotNull(mClassName) { "Controller class was not set" }
            return mClassName!!
        }
    }
}