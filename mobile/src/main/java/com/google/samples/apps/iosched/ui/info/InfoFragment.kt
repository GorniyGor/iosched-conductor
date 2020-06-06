/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.iosched.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.databinding.FragmentInfoBinding
import com.google.samples.apps.iosched.shared.analytics.AnalyticsHelper
import com.google.samples.apps.iosched.shared.util.activityViewModelProvider
import com.google.samples.apps.iosched.shared.util.requireActivity
import com.google.samples.apps.iosched.ui.MainNavigationController
import com.google.samples.apps.iosched.ui.signin.setupProfileMenuItem
import com.google.samples.apps.iosched.util.doOnApplyWindowInsets
import com.google.samples.apps.iosched.widget.conductor.RouterPagerAdapter
import javax.inject.Inject

class InfoFragment : MainNavigationController() {

    @Inject lateinit var analyticsHelper: AnalyticsHelper

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentInfoBinding

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        binding.viewpager.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
        binding.run {
            toolbar.setupProfileMenuItem(
                activityViewModelProvider(viewModelFactory), this@InfoFragment
            )

            viewpager.offscreenPageLimit = INFO_PAGES.size
            viewpager.adapter = InfoAdapter(this@InfoFragment)
            tabs.setupWithViewPager(binding.viewpager)

            // Analytics. Manually fire once for the loaded tab, then fire on tab change.
            trackInfoScreenView(0)
            viewpager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    trackInfoScreenView(position)
                }
            })
        }
    }

    private fun trackInfoScreenView(position: Int) {
        val pageName = resources?.getString(INFO_TITLES[position])
        analyticsHelper.sendScreenView("Info - $pageName", requireActivity())
    }

    /**
     * Adapter that builds a page for each info screen.
     */
    inner class InfoAdapter(host: Controller) : RouterPagerAdapter(host) {

        override fun getCount() = INFO_PAGES.size

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val page: Controller = INFO_PAGES[position]()
                router.setRoot(RouterTransaction.with(page))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return resources?.getString(INFO_TITLES[position]) ?: ""
        }
    }

    companion object {

        private val INFO_TITLES = arrayOf(
            R.string.event_title,
            R.string.travel_title,
            R.string.faq_title
        )
        private val INFO_PAGES = arrayOf(
            { EventFragment() },
            { TravelFragment() },
            { FaqFragment() }
        // TODO: Track the InfoPage performance b/130335745
        )
    }
}
