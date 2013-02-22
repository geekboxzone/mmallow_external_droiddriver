/*
 * Copyright (C) 2013 UiDriver committers
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

package com.google.android.uidriver.uiautomation;

import com.google.android.uidriver.Events;

import android.app.UiAutomation;
import android.os.SystemClock;

/**
 * Helper methods to inject input interactions with the device.
 */
public class Interactions {

  public static void click(UiAutomation uiAutomation, int x, int y) {
    uiAutomation.injectInputEvent(Events.newTouchDownEvent(x, y), true /* sync */);
    //TODO: Make sleep configurable.
    SystemClock.sleep(250);
    uiAutomation.injectInputEvent(Events.newTouchUpEvent(x, y), true /* sync */);
  }
}
