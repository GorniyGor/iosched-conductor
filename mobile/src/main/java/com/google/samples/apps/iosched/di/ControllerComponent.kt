package com.google.samples.apps.iosched.di

import com.google.samples.apps.iosched.ui.DaggerController.ViewScope
import com.google.samples.apps.iosched.ui.feed.FeedFragment
import com.google.samples.apps.iosched.ui.feed.FeedModule
import com.google.samples.apps.iosched.ui.schedule.ScheduleFragment
import com.google.samples.apps.iosched.ui.schedule.ScheduleModule
import com.google.samples.apps.iosched.ui.schedule.filters.ScheduleFilterFragment
import dagger.Subcomponent

@ViewScope
@Subcomponent(modules = [ScheduleModule::class, FeedModule::class])
interface ControllerComponent {
    fun inject(controller: ScheduleFragment)
    fun inject(controller: ScheduleFilterFragment)
    fun inject(controller: FeedFragment)
}
