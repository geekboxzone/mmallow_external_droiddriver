/*
 * Copyright (C) 2013 DroidDriver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.droiddriver.helpers;

import android.app.Instrumentation;
import android.os.Build;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.instrumentation.InstrumentationDriver;
import com.google.android.droiddriver.uiautomation.UiAutomationDriver;

/**
 * Static utility methods pertaining to {@link DroidDriver} instances.
 */
public class DroidDrivers {
  private static DroidDriver driver;

  /**
   * Gets the singleton driver. Throws if {@link #init} has not been called.
   */
  public static DroidDriver get() {
    if (DroidDrivers.driver == null) {
      throw new DroidDriverException("init() has not been called");
    }
    return DroidDrivers.driver;
  }

  /**
   * Initializes the singleton driver. The singleton driver is NOT required, but
   * it is handy and using a singleton driver can avoid memory leak if you have
   * many instances around (for example, one in every test -- JUnit framework
   * keeps the test instances in memory after running them).
   */
  public static void init(DroidDriver driver) {
    if (DroidDrivers.driver != null) {
      throw new DroidDriverException("init() can only be called once");
    }
    DroidDrivers.driver = driver;
  }

  /**
   * Returns whether the running target (device or emulator) has
   * {@link android.app.UiAutomation} API, which is introduced in SDK API 18
   * (JELLY_BEAN_MR2).
   */
  public static boolean hasUiAutomation() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
  }

  /**
   * Returns a new UiAutomationDriver if {@link android.app.UiAutomation} is
   * available; otherwise a new InstrumentationDriver.
   */
  public static DroidDriver newDriver(Instrumentation instrumentation) {
    if (hasUiAutomation()) {
      return newUiAutomationDriver(instrumentation);
    }
    return newInstrumentationDriver(instrumentation);
  }

  /** Returns a new InstrumentationDriver */
  public static InstrumentationDriver newInstrumentationDriver(Instrumentation instrumentation) {
    return new InstrumentationDriver(instrumentation);
  }

  /** Returns a new UiAutomationDriver */
  public static UiAutomationDriver newUiAutomationDriver(Instrumentation instrumentation) {
    if (!hasUiAutomation()) {
      throw new DroidDriverException(
          "http://developer.android.com/reference/android/app/UiAutomation.html" +
          " is not available below API 18");
    }
    if (instrumentation.getUiAutomation() == null) {
      throw new DroidDriverException(
          "uiAutomation==null: did you forget to set '-w' flag for 'am instrument'?");
    }
    return new UiAutomationDriver(instrumentation);
  }
}
