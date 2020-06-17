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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.samples.apps.iosched.tests.timing.automator.FeedScreenAutomatorTest.Companion.BASIC_SAMPLE_PACKAGE
import com.google.samples.apps.iosched.tests.timing.automator.FeedScreenAutomatorTest.Companion.LAUNCH_TIMEOUT
import com.google.samples.apps.iosched.tests.timing.automator.FeedScreenAutomatorTest.Companion.waitAndClick
import com.google.samples.apps.iosched.tests.ui.ScheduleTest
import org.hamcrest.core.IsNull
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Basic Espresso tests for the schedule screen.
 */
@RunWith(AndroidJUnit4::class)
class ScheduleScreenAutomatorTest {

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
            Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT
        )

        // Go to Schedule screen
        device.findObject(By.res(BASIC_SAMPLE_PACKAGE, "toolbar"))
            .findObject(By.descContains("Open navigation drawer"))
            .click()

        device.waitAndClick(By.descContains("Schedule"))

        // Wait for Schedule screen to appear
        device.wait(Until.hasObject(By.text(ScheduleTest.FAKE_SESSION_ON_DAY1)), 100)
    }


    /**
     * Schedule -> Details
     */
    @Test
    fun clickOnFirstItem_detailsShown_v2() {
        for (i in 0..100) {
            device.findObject(By.text("Google Keynote")).click()

            device.wait<UiObject2>(
                Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "session_detail_title")),
                TimeUnit.SECONDS.toMillis(15)
            )

            device.pressBack()
            //This is interesting that Automator is needed in delay, but after backPress one isn't need in time for the screen appearance
        }
    }
}
