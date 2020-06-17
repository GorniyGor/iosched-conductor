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

package com.google.samples.apps.iosched.tests.timing.espresso

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.tests.SetPreferencesRule
import com.google.samples.apps.iosched.tests.ui.MainActivityTestRule
import com.google.samples.apps.iosched.ui.MainActivity.Companion.TestType
import com.google.samples.apps.iosched.ui.SimpleIdlingResource
import com.google.samples.apps.iosched.ui.schedule.SessionViewHolder
import org.junit.After
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
class ScheduleScreenTest {

    @get:Rule
    var activityRule = MainActivityTestRule(R.id.navigation_schedule)

    // Sets the preferences so no welcome screens are shown
    @get:Rule
    var preferencesRule = SetPreferencesRule()

    private var idlingResources: Map<TestType, SimpleIdlingResource>? = null

    @Before
    fun before() {
        idlingResources = activityRule.activity.idlingResources
        idlingResources!!.forEach { IdlingRegistry.getInstance().register(it.value) }

    }

    @After
    fun after() {
        idlingResources?.let { it.forEach { IdlingRegistry.getInstance().unregister(it.value) } }
    }


    /**
     * Schedule -> Details
     */
    @Test
    fun clickOnFirstItem_detailsShown_v2() {
        for (i in 0..100) {
            onView(withId(R.id.recyclerview_schedule))
                .perform(RecyclerViewActions.actionOnItemAtPosition<SessionViewHolder>(0, click()))

            onView(withId(R.id.session_detail_title)).check(matches(isDisplayed()))

            //fix loop's problem (recyclerview_schedule on next iteration was not found)
            idlingResources!!.getValue(TestType._Service).setIdleState(false)
            pressBack()
            //---
        }
    }

    //problem - some traces was invalid "Trace 'anyname' is started but not stopped when it is destructed!"
    /*@get:Rule
    var repeatRule = RepeatRule()
    @Test
    @Repeat(times = COUNT)
    fun clickOnFirstItem_detailsShown_v1() {
        onView(withId(R.id.recyclerview_schedule))
            .perform(RecyclerViewActions.actionOnItemAtPosition<SessionViewHolder>(0, click()))

        onView(withId(R.id.session_detail_title)).check(matches(isDisplayed()))
        Thread.sleep(500)
    }*/
}
