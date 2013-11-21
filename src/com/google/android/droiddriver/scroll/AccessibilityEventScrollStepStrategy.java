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
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Direction.Axis;
import com.google.android.droiddriver.scroll.Direction.DirectionConverter;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;
import com.google.android.droiddriver.util.Logs;

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
  /**
   * Stores the data if we reached end at the last
   * {@link AccessibilityEventScrollStepStrategy#scroll}. If the data match when
   * a new scroll is requested, we can return immediately.
   */
  private static class EndData {
    private Finder containerFinderAtEnd;
    private PhysicalDirection directionAtEnd;

    public boolean match(Finder containerFinder, PhysicalDirection direction) {
      return containerFinderAtEnd == containerFinder && directionAtEnd == direction;
    }

    public void set(Finder containerFinder, PhysicalDirection direction) {
      containerFinderAtEnd = containerFinder;
      directionAtEnd = direction;
    }

    public void reset() {
      set(null, null);
    }
  }

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
  private final EndData atEndData = new EndData();

  public AccessibilityEventScrollStepStrategy(UiAutomation uiAutomation,
      long scrollEventTimeoutMillis, DirectionConverter converter) {
    this.uiAutomation = uiAutomation;
    this.scrollEventTimeoutMillis = scrollEventTimeoutMillis;
    this.directionConverter = converter;
  }

  @Override
  public boolean scroll(DroidDriver driver, Finder containerFinder,
      final PhysicalDirection direction) {
    // Check if we've reached end after last scroll.
    if (atEndData.match(containerFinder, direction)) {
      return false;
    }

    final UiElement container = driver.on(containerFinder);
    try {
      AccessibilityEvent event = uiAutomation.executeAndWaitForEvent(new Runnable() {
        @Override
        public void run() {
          SwipeAction.toScroll(direction).perform(container.getInjector(), container);
        }
      }, SCROLL_EVENT_FILTER, scrollEventTimeoutMillis);

      if (detectEnd(direction.axis(), event)) {
        atEndData.set(containerFinder, direction);
        Logs.log(Log.DEBUG, "reached scroll end");
      }
    } catch (TimeoutException e) {
      // If no TYPE_VIEW_SCROLLED event, no more scrolling is possible
      return false;
    }
    return true;
  }

  // Copied from UiAutomator.
  // AdapterViews have indices we can use to check for the beginning.
  private static boolean detectEnd(Axis axis, AccessibilityEvent event) {
    boolean foundEnd = false;
    if (event.getFromIndex() != -1 && event.getToIndex() != -1 && event.getItemCount() != -1) {
      foundEnd = event.getFromIndex() == 0 || (event.getItemCount() - 1) == event.getToIndex();
    } else if (event.getScrollX() != -1 && event.getScrollY() != -1) {
      if (axis == Axis.VERTICAL) {
        foundEnd = event.getScrollY() == 0 || event.getScrollY() == event.getMaxScrollY();
      } else if (axis == Axis.HORIZONTAL) {
        foundEnd = event.getScrollX() == 0 || event.getScrollX() == event.getMaxScrollX();
      }
    }
    event.recycle();
    return foundEnd;
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

  @Override
  public void beginScrolling(Finder containerFinder, Finder itemFinder, PhysicalDirection direction) {
    atEndData.reset();
  }

  @Override
  public void endScrolling(Finder containerFinder, Finder itemFinder, PhysicalDirection direction) {}
}
