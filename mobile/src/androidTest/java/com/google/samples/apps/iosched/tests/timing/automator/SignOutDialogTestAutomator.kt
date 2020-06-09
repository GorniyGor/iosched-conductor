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
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.samples.apps.iosched.tests.timing.espresso.FeedScreenTest.Companion.COUNT
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
class SignOutDialogTestAutomator {

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources


    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage = launcherPackageName
        Assert.assertThat(launcherPackage, IsNull.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT)

        val context = InstrumentationRegistry.getTargetContext()
        val intent = context.packageManager
            .getLaunchIntentForPackage(
                BASIC_SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)    // Clear out any previous instances

        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(
            BASIC_SAMPLE_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT)
    }

    @Test
    @Throws(Throwable::class)
    fun testRunCalculationTraditionalLayouts() {
        for(i in 0..COUNT) {
            runCalculation()
            device.pressBack()
        }
    }

    private fun runCalculation() {
        device.findObject(By.res(
            BASIC_SAMPLE_PACKAGE, "action_profile")).click()
        device.wait<UiObject2>(Until.findObject(By.res(
            BASIC_SAMPLE_PACKAGE,
            "username_email_container")), TimeUnit.SECONDS.toMillis(15))
    }

    private val launcherPackageName: String
        get() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)

            val pm = InstrumentationRegistry.getContext().packageManager
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo.activityInfo.packageName
        }

    companion object {
        private val BASIC_SAMPLE_PACKAGE = "com.google.samples.apps.iosched"
        private val LAUNCH_TIMEOUT = 5000L
    }
}
