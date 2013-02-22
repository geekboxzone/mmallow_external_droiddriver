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

package com.google.android.uidriver;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * Helper methods to create InputEvents.
 */
public class Events {
  public static MotionEvent newTouchDownEvent(int x, int y) {
    long mDownTime = SystemClock.uptimeMillis();
    MotionEvent event = MotionEvent.obtain(mDownTime, mDownTime, MotionEvent.ACTION_DOWN, x, y, 1);
    event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    return event;
  }

  public static MotionEvent newTouchUpEvent(int x, int y) {
    long mDownTime = SystemClock.uptimeMillis();
    MotionEvent event = MotionEvent.obtain(mDownTime, mDownTime, MotionEvent.ACTION_UP, x, y, 1);
    event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    return event;
  }

  private Events() {}
}
