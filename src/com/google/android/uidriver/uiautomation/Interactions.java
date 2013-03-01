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
import com.google.android.uidriver.exceptions.UiDriverException;
import com.google.common.base.Preconditions;

import android.app.UiAutomation;
import android.os.SystemClock;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Helper methods to inject input interactions with the device.
 */
public class Interactions {

  private static final KeyCharacterMap KEY_CHAR_MAP =
      KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

  private final UiAutomation uiAutomation;

  public Interactions(UiAutomation uiAutomation) {
    this.uiAutomation = uiAutomation;
  }

  public void click(int x, int y) {
    MotionEvent downEvent = Events.newTouchDownEvent(x, y);
    uiAutomation.injectInputEvent(downEvent, true /* sync */);
    //TODO: Make sleep configurable.
    SystemClock.sleep(100);
    uiAutomation.injectInputEvent(
        Events.newTouchUpEvent(downEvent.getDownTime(), x, y), true /* sync */);
  }

  public void sendText(String text) {
    KeyEvent[] events = KEY_CHAR_MAP.getEvents(text.toCharArray());

    if (events != null) {
      for (KeyEvent event : events) {
        // We have to change the time of an event before injecting it because
        // all KeyEvents returned by KeyCharacterMap.getEvents() have the same
        // time stamp and the system rejects too old events. Hence, it is
        // possible for an event to become stale before it is injected if it
        // takes too long to inject the preceding ones.
        KeyEvent modifiedEvent = KeyEvent.changeTimeRepeat(event, SystemClock.uptimeMillis(), 0);
        uiAutomation.injectInputEvent(modifiedEvent, true /* sync */);
      }
    } else {
      throw new UiDriverException("The given text is not supported: " + text);
    }
  }

  /**
   * Handle swipes/drags in any direction.
   */
  public void swipe(int startX, int startY, int endX, int endY, int steps, boolean drag) {
    Preconditions.checkArgument(startX >= 0);
    Preconditions.checkArgument(startY >= 0);
    Preconditions.checkArgument(endX >= 0);
    Preconditions.checkArgument(endY >= 0);
    Preconditions.checkArgument(steps > 0);
    double xStep = ((double)(endX - startX)) / steps;
    double yStep = ((double)(endY - startY)) / steps;

    // first touch starts exactly at the point requested
    MotionEvent downEvent = Events.newTouchDownEvent(startX, startY);
    uiAutomation.injectInputEvent(downEvent, true /* sync */);
    if (drag) {
      SystemClock.sleep(ViewConfiguration.getLongPressTimeout());
    }
    for (int i = 1; i < steps; i++) {
      uiAutomation.injectInputEvent(Events.newTouchMoveEvent(downEvent.getDownTime(),
          startX + (int)(xStep * i), startY + (int)(yStep * i)), true /* sync */);
      SystemClock.sleep(5);
    }
    if (drag) {
      //TODO: Make sleep configurable.
      // Hold final position for a little bit to simulate drag.
      SystemClock.sleep(100);
    }
    uiAutomation.injectInputEvent(
        Events.newTouchUpEvent(downEvent.getDownTime(), endX, endY), true /* sync */);
  }
}
