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
package com.google.android.droiddriver.scroll;

import android.app.UiAutomation;
import android.app.UiAutomation.AccessibilityEventFilter;
import android.view.accessibility.AccessibilityEvent;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Direction.DirectionConverter;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;

import java.util.concurrent.TimeoutException;

/**
 * A {@link ScrollStepStrategy} that determines whether more scrolling is
 * possible by checking the {@link AccessibilityEvent} returned by
 * {@link android.app.UiAutomation}.
 * <p>
 * This implementation behaves just like the <a href=
 * "http://developer.android.com/tools/help/uiautomator/UiScrollable.html"
 * >UiScrollable</a> class. It may not work in all cases. For instance,
 * sometimes {@link android.support.v4.widget.DrawerLayout} does not send
 * correct {@link AccessibilityEvent}s after scrolling.
 * </p>
 */
public class AccessibilityEventScrollStepStrategy implements ScrollStepStrategy {
  private static final AccessibilityEventFilter SCROLL_EVENT_FILTER =
      new AccessibilityEventFilter() {
        @Override
        public boolean accept(AccessibilityEvent arg0) {
          return (arg0.getEventType() & AccessibilityEvent.TYPE_VIEW_SCROLLED) != 0;
        }
      };

  private final UiAutomation uiAutomation;
  private final long scrollEventTimeoutMillis;
  private final DirectionConverter directionConverter;

  public AccessibilityEventScrollStepStrategy(UiAutomation uiAutomation,
      long scrollEventTimeoutMillis, DirectionConverter converter) {
    this.uiAutomation = uiAutomation;
    this.scrollEventTimeoutMillis = scrollEventTimeoutMillis;
    this.directionConverter = converter;
  }

  @Override
  public boolean scroll(DroidDriver driver, Finder containerFinder,
      final PhysicalDirection direction) {
    final UiElement container = driver.on(containerFinder);
    try {
      uiAutomation.executeAndWaitForEvent(new Runnable() {
        @Override
        public void run() {
          SwipeAction.toScroll(direction).perform(container.getInjector(), container);
        }
      }, SCROLL_EVENT_FILTER, scrollEventTimeoutMillis);
    } catch (TimeoutException e) {
      // If no TYPE_VIEW_SCROLLED event, no more scrolling is possible
      return false;
    }
    return true;
  }

  @Override
  public final DirectionConverter getDirectionConverter() {
    return directionConverter;
  }

  @Override
  public String toString() {
    return String.format("AccessibilityEventScrollStepStrategy{scrollEventTimeoutMillis=%d}",
        scrollEventTimeoutMillis);
  }
}
