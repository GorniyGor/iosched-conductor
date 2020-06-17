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

package com.google.samples.apps.iosched.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.di.DaggerController

/**
 * To be implemented by components that host top-level navigation destinations.
 */
interface NavigationHost {

    /** Called by MainNavigationFragment to setup it's toolbar with the navigation controller. */
    fun registerToolbarWithNavigation(toolbar: Toolbar)
}

/**
 * To be implemented by main navigation destinations shown by a [NavigationHost].
 */
interface NavigationDestination {

    /** Called by the host when the user interacts with it. */
    fun onUserInteraction() {}
}

/**
 * Controller representing a main navigation destination. This class handles wiring up the [Toolbar]
 * navigation icon if the controller is attached to a [NavigationHost].
 */
//TODO( may be reflection bug because there is defined one constructor for Java )
abstract class MainNavigationController(args: Bundle? = null) : DaggerController(args), NavigationDestination {

    protected val viewLifecycleOwner: LifecycleOwner = this
    protected var navigationHost: NavigationHost? = null

    protected abstract fun inflateView( inflater: LayoutInflater,  container: ViewGroup ): View
    protected open fun onViewBound(view: View, savedInstanceState: Bundle? = null) {}

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        if (activity is NavigationHost) navigationHost = activity as NavigationHost
        inject()
        val view = inflateView(inflater, container)
        onViewBound(view, savedViewState)
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        // If we have a toolbar and we are attached to a proper navigation host, set up the toolbar
        // navigation icon.
        navigationHost?.also { host ->
            view.findViewById<Toolbar>(R.id.toolbar)?.apply {
                host.registerToolbarWithNavigation(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHost = null
    }
}