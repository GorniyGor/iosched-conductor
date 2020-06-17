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

import android.content.Context
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.internal.NavigationMenuItemView
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.tests.SetPreferencesRule
import com.google.samples.apps.iosched.tests.SyncTaskExecutorRule
import com.google.samples.apps.iosched.tests.ui.MainActivityTestRule
import com.google.samples.apps.iosched.tests.ui.ScheduleTest
import com.google.samples.apps.iosched.ui.MainActivity
import com.google.samples.apps.iosched.ui.MainActivity.Companion.TestType
import com.google.samples.apps.iosched.ui.SimpleIdlingResource
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
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
class FeedScreenTest {

    @get:Rule
    var activityRule = MainActivityTestRule(R.id.navigation_feed)

    // The rule to enable test repetition.
    @get:Rule
    var repeatRule = RepeatRule()

    // Sets the preferences so no welcome screens are shown
    @get:Rule
    var preferencesRule = SetPreferencesRule()

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources
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
     * Feed -> Schedule
     */
    @Test
    fun clickOnBurger_clickOnSchedule_sessionOnFirstDayShown() {
        for (i in 0..100) {
            onView(allOf(instanceOf(ImageView::class.java), withParent(withId(R.id.toolbar)))).perform(click())
            onView(allOf(instanceOf(NavigationMenuItemView::class.java), withContentDescription(R.string.title_schedule))).perform(click())

            onView(ViewMatchers.withText(ScheduleTest.FAKE_SESSION_ON_DAY1)).check(matches(isDisplayed()))

            supportBackPress()
            pressBack()
        }
    }

    /**
     * Feed -> Info
     */
    @Test
    fun clickOnBurger_clickOnInfo_basicInfoViewDisplayed() {
        for (i in 0..100) {
            onView(allOf(instanceOf(ImageView::class.java), withParent(withId(R.id.toolbar)))).perform(click())
            onView(allOf(instanceOf(NavigationMenuItemView::class.java), withContentDescription(R.string.title_info))).perform(click())

            onView(ViewMatchers.withText(resources.getString(R.string.event_types_header))).check(matches(isDisplayed()))

            supportBackPress()
            pressBack()
        }
    }

    /**
     * Activity -> Feed
     *
     * check Controller init
     */
    //TODO(check with and without) | log "I/ActivityTaskManager: Displayed ... ms" (time) is written for case "without" only for first iteration

    // By Custom trace: with vs without = 41 vs 58 ms [on COUNT = 10, exclude a first measure]
    @Test
    @Repeat(times = 100+1)
    fun feedShown() {
        onView(ViewMatchers.withText(R.string.feed_announcement_title)).check(matches(isDisplayed()))
        /*pressBack()*/ //maybe there is need in close app.
        // really sometimes pressBack not have been worked out, so it used not recommended our it needs in code addition
    }

    fun supportBackPress() {
        idlingResources!!.getValue(TestType._Service).setIdleState(false)
    }
}
