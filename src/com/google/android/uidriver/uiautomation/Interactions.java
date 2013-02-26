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

import android.app.UiAutomation;
import android.os.SystemClock;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 * Helper methods to inject input interactions with the device.
 */
public class Interactions {

  private static final KeyCharacterMap KEY_CHAR_MAP =
      KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

  public static void click(UiAutomation uiAutomation, int x, int y) {
    uiAutomation.injectInputEvent(Events.newTouchDownEvent(x, y), true /* sync */);
    //TODO: Make sleep configurable.
    SystemClock.sleep(250);
    uiAutomation.injectInputEvent(Events.newTouchUpEvent(x, y), true /* sync */);
  }

  public static void sendText(UiAutomation uiAutomation, String text) {
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
}
