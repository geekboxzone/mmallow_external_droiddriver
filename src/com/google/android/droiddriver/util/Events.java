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

package com.google.android.droiddriver.util;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.exceptions.ActionException;

/**
 * Helper methods to create InputEvents.
 */
public class Events {
  /**
   * @return a touch down event at the specified coordinates
   */
  public static MotionEvent newTouchDownEvent(int x, int y) {
    long downTime = SystemClock.uptimeMillis();
    MotionEvent event = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 1);
    event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    return event;
  }

  /**
   * @return a touch up event at the specified coordinates
   */
  public static MotionEvent newTouchUpEvent(long downTime, int x, int y) {
    long eventTime = SystemClock.uptimeMillis();
    MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 1);
    event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    return event;
  }

  /**
   * @return a touch move event at the specified coordinates
   */
  public static MotionEvent newTouchMoveEvent(long downTime, int x, int y) {
    long eventTime = SystemClock.uptimeMillis();
    MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 1);
    event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    return event;
  }

  public static KeyEvent newKeyEvent(long downTime, int action, int keyCode) {
    KeyEvent event = new KeyEvent(downTime, downTime, action, keyCode, 0 /* repeat */);
    event.setSource(InputDevice.SOURCE_KEYBOARD);
    return event;
  }

  /**
   * Injects {@code event}, and recycles it. After calling this function you
   * must not ever touch the event again.
   */
  public static boolean tryInjectEvent(InputInjector injector, MotionEvent event) {
    boolean injected = injector.injectInputEvent(event);
    event.recycle();
    return injected;
  }

  /**
   * Calls {@link #tryInjectEvent}, and throws in case of failure.
   */
  public static void injectEvent(InputInjector injector, MotionEvent event) {
    if (!tryInjectEvent(injector, event)) {
      throw new ActionException("Failed to inject " + event);
    }
  }

  private Events() {}
}
