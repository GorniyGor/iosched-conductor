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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.google.samples.apps.iosched.R
import dagger.internal.Beta
import javax.inject.Scope

abstract class MainNavigationController : DaggerController(), NavigationDestination {

    protected var navigationHost: NavigationHost? = null

    protected abstract fun inflateView( inflater: LayoutInflater,  container: ViewGroup ): View
    protected open fun onViewBound(view: View, savedInstanceState: Bundle? = null) {}

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        if (activity is NavigationHost) {
            navigationHost = activity as NavigationHost
        }
        val view = inflateView(inflater, container)
        onViewCreated(view, savedViewState)
        return view
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewBound(view, savedInstanceState)
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

@Beta
abstract class DaggerController : LifecycleController() {

    @Scope
    annotation class ViewScope

}
