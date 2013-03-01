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
import com.google.android.uidriver.UiDriver;
import com.google.android.uidriver.exceptions.UiDriverException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import android.app.UiAutomation;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * Helper methods to inject input interactions with the device.
 */
public class Interactions implements OnAccessibilityEventListener {

  private static final KeyCharacterMap KEY_CHAR_MAP =
      KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

  private final List<AccessibilityEvent> events = Lists.newArrayList();
  private final UiAutomation uiAutomation;

  public Interactions(UiAutomation uiAutomation) {
    this.uiAutomation = uiAutomation;
    uiAutomation.setOnAccessibilityEventListener(this);
  }

  public void click(int x, int y) {
    Preconditions.checkArgument(x >= 0);
    Preconditions.checkArgument(y >= 0);
    events.clear();
    MotionEvent downEvent = Events.newTouchDownEvent(x, y);
    uiAutomation.injectInputEvent(downEvent, true /* sync */);
    //TODO: Make sleep configurable.
    SystemClock.sleep(100);
    uiAutomation.injectInputEvent(
        Events.newTouchUpEvent(downEvent.getDownTime(), x, y), true /* sync */);
  }

  public void sendText(String text) {
    Preconditions.checkNotNull(text);
    events.clear();
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
  public boolean swipe(int startX, int startY, int endX, int endY, int steps, boolean drag) {
    Preconditions.checkArgument(startX >= 0);
    Preconditions.checkArgument(startY >= 0);
    Preconditions.checkArgument(endX >= 0);
    Preconditions.checkArgument(endY >= 0);
    Preconditions.checkArgument(steps > 0);
    double xStep = ((double)(endX - startX)) / steps;
    double yStep = ((double)(endY - startY)) / steps;
    events.clear();

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
    return !isAtScrollEnd(startX, startY, endX, endY);
  }

  /**
   * @return true if we are at the end of a scrollable view.
   */
  private boolean isAtScrollEnd(int startX, int startY, int endX, int endY) {
    AccessibilityEvent event = getLastMatchingEvent(AccessibilityEvent.TYPE_VIEW_SCROLLED);

    if (event == null) {
      // End of scroll since no new scroll events received
      return true;
    }

    // AdapterViews have indices we can use to check for the beginning.
    boolean foundEnd = false;
    if (event.getFromIndex() != -1 && event.getToIndex() != -1 && event.getItemCount() != -1) {
      foundEnd = event.getFromIndex() == 0 || (event.getItemCount() - 1) == event.getToIndex();
    } else if (event.getScrollX() != -1 && event.getScrollY() != -1) {
      // Determine if we are scrolling vertically or horizontally.
      if (startX == endX) {
        // Vertical
        foundEnd = event.getScrollY() == 0 || event.getScrollY() == event.getMaxScrollY();
      } else if (startY == endY) {
        // Horizontal
        foundEnd = event.getScrollX() == 0 || event.getScrollX() == event.getMaxScrollX();
      }
    }
    return foundEnd;
  }

  private AccessibilityEvent getLastMatchingEvent(int type) {
    for (int i = events.size() - 1; i >= 0; i--) {
      AccessibilityEvent event = events.get(i);
      if ((event.getEventType() & type) != 0) {
        return event;
      }
    }
    return null;
  }

  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
    Log.d(UiDriver.class.getSimpleName(), "Received event: " + event);
    events.add(event);
  }
}
