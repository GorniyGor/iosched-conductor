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

package com.google.samples.apps.iosched.tests.timing

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import kotlin.concurrent.thread

class AndroidBenchmarkRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)

//        if(sustainedPerformanceModeInUse) {
//            thread(name = "BenchSpinThread") {
//                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
//                while(true) {}
//            }
//        }
    }
}