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

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.databinding.FragmentInfoEventBinding
import com.google.samples.apps.iosched.model.ConferenceWifiInfo
import com.google.samples.apps.iosched.shared.di.AssistantAppEnabledFlag
import com.google.samples.apps.iosched.shared.util.TimeUtils
import com.google.samples.apps.iosched.shared.util.requireActivity
import com.google.samples.apps.iosched.shared.util.viewModelProvider
import com.google.samples.apps.iosched.ui.MainNavigationController
import com.google.samples.apps.iosched.ui.MainActivity.Companion.TestType
import com.google.samples.apps.iosched.ui.messages.SnackbarMessageManager
import com.google.samples.apps.iosched.ui.setUpSnackbar
import com.google.samples.apps.iosched.util.doOnApplyWindowInsets
import com.google.samples.apps.iosched.util.finishTraceForTest
import com.google.samples.apps.iosched.widget.FadingSnackbar
import javax.inject.Inject

class EventFragment : MainNavigationController() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var snackbarMessageManager: SnackbarMessageManager

    @Inject
    @JvmField
    @AssistantAppEnabledFlag
    var assistantAppEnabled = false

    private lateinit var eventInfoViewModel: EventInfoViewModel

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        eventInfoViewModel = viewModelProvider(viewModelFactory)

        val binding = FragmentInfoEventBinding.inflate(inflater, container, false).apply {
            viewModel = eventInfoViewModel
            showAssistantApp = assistantAppEnabled
            lifecycleOwner = viewLifecycleOwner
        }

        // Pad the bottom of the content so that it is above the nav bar
        binding.content.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }

        val snackbarLayout = requireActivity().findViewById<FadingSnackbar>(R.id.snackbar)
        setUpSnackbar(eventInfoViewModel.snackBarMessage, snackbarLayout, snackbarMessageManager,
            context = container.context)

        eventInfoViewModel.openUrlEvent.observe(this, Observer {
            val url = it?.getContentIfNotHandled() ?: return@Observer
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        })

        return binding.root
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        finishTraceForTest(TestType.Feed_Info)
    }
}

@BindingAdapter("countdownVisibility")
fun countdownVisibility(countdown: View, ignored: Boolean?) {
    // TODO Remove this method since ignored is unused
    countdown.visibility = if (TimeUtils.conferenceHasStarted()) GONE else VISIBLE
}

@BindingAdapter("wifiInfo")
fun bindWifiInfo(textView: TextView, wifiInfo: ConferenceWifiInfo?) {
    textView.text = if (wifiInfo == null) null else {
        textView.resources.getString(
            R.string.wifi_network_and_password, wifiInfo.ssid, wifiInfo.password
        )
    }
}
