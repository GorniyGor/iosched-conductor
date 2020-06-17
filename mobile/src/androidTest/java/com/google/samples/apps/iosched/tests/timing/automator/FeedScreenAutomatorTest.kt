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

package com.google.samples.apps.iosched.tests.timing.automator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.android.material.internal.NavigationMenuItemView
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.tests.timing.espresso.FeedScreenTest.Companion.COUNT
import com.google.samples.apps.iosched.tests.ui.MainActivityTestRule
import com.google.samples.apps.iosched.tests.ui.ScheduleTest
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import repeat.Repeat
import repeat.RepeatRule

/**
 * Basic Espresso tests for the schedule screen.
 */
@RunWith(AndroidJUnit4::class)
class FeedScreenAutomatorTest {

    // The rule to enable test repetition.
    @get:Rule
    var repeatRule = RepeatRule()

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources

    private lateinit var device: UiDevice

    private val launcherPackageName: String
        get() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)

            val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo.activityInfo.packageName
        }

    val EVENT_TYPES = resources.getString(R.string.event_types_header)
    val ANNOUNCEMENT = resources.getString(R.string.feed_announcement_title)

    @Before
    fun before() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage = launcherPackageName
        Assert.assertThat(launcherPackage, IsNull.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            BASIC_SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)    // Clear out any previous instances

        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }

    /**
     * Feed -> Schedule
     */
    @Test
    fun clickOnBurger_clickOnSchedule_sessionOnFirstDayShown() {
        for (i in 0..COUNT) {
            // Go to Schedule screen
            device.findObject(By.res(BASIC_SAMPLE_PACKAGE, "toolbar"))
                .findObject(By.descContains("Open navigation drawer"))
                .click()

            device.waitAndClick(By.descContains("Schedule"))

            // Wait for Schedule screen to appear
            device.wait(Until.hasObject(By.text(ScheduleTest.FAKE_SESSION_ON_DAY1)), 100)
            device.pressBack()
        }
    }

    /**
     * Feed -> Info
     */
    @Test
    fun clickOnBurger_clickOnInfo_basicInfoViewDisplayed() {
        for (i in 0..100) {
            // Go to Schedule screen
            device.findObject(By.res(BASIC_SAMPLE_PACKAGE, "toolbar"))
                .findObject(By.descContains("Open navigation drawer"))
                .click()

            device.waitAndClick(By.descContains("Info"))

            // Wait for Schedule screen to appear
            device.wait(Until.hasObject(By.text(EVENT_TYPES)), 100)
            device.pressBack()
        }
    }

    /**
     * Activity -> Feed
     *
     * check Controller init
     */

    // By Custom trace: with vs without = 41 vs 58 ms [on emulator, on COUNT = 10, exclude a first measure]
    @Test
    @Repeat(times = 101)
    fun feedShown() {
        device.wait(Until.hasObject(By.text(ANNOUNCEMENT)), 100) // Because into fun before() our screen had been started
        /*pressBack()*/ //maybe there is need in close app.
        // really sometimes pressBack not have been worked out, so it used not recommended our it needs in code addition
    }

    companion object {
        val BASIC_SAMPLE_PACKAGE = "com.google.samples.apps.iosched"
        val LAUNCH_TIMEOUT = 5000L
        fun UiDevice.waitAndClick(selector: BySelector) {
            wait(Until.hasObject(selector), 500)
            findObject(selector).click()
        }
    }
}
