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

import static com.google.android.droiddriver.scroll.Direction.LogicalDirection.BACKWARD;

import android.util.Log;
import android.view.KeyEvent;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.Poller;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.SingleKeyAction;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.TimeoutException;
import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Direction.Axis;
import com.google.android.droiddriver.scroll.Direction.DirectionConverter;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;
import com.google.android.droiddriver.util.Logs;

/**
 * A {@link Scroller} that looks for the desired item in the currently shown
 * content of the scrollable container, otherwise scrolls the container one step
 * at a time and look again, until we cannot scroll any more. A
 * {@link SentinelStrategy} is used to determine whether more scrolling is
 * possible.
 * <p>
 * This implementation may not work well with InstrumentationDriver if
 * {@link UiElement#getChildren} returns children in an order different from the
 * Accessibility API. In this case you may try
 * {@link AbstractSentinelStrategy#SECOND_LAST_CHILD_GETTER}.
 * </p>
 * TODO: A {@link Scroller} that directly jumps to the item if an
 * InstrumentationDriver is used.
 */
public class SentinelScroller implements Scroller {

  private static final SingleKeyAction MOVE_HOME = new SingleKeyAction(KeyEvent.KEYCODE_MOVE_HOME,
      1000L, false);

  private final int maxScrolls;
  private final long perScrollTimeoutMillis;
  private final Axis axis;
  private final SentinelStrategy sentinelStrategy;
  private final boolean startFromBeginning;

  /**
   * @param maxScrolls the maximum number of scrolls. It should be large enough
   *        to allow any reasonable list size
   * @param perScrollTimeoutMillis the timeout in millis that we poll for the
   *        item after each scroll
   * @param axis the axis this scroller can scroll
   * @param startFromBeginning if {@code true},
   *        {@link #scrollTo(DroidDriver, Finder, Finder)} starts from the
   *        beginning and scrolls forward, instead of starting from the current
   *        location and scrolling in both directions. It may not always work,
   *        but when it works, it is faster.
   */
  public SentinelScroller(int maxScrolls, long perScrollTimeoutMillis, Axis axis,
      SentinelStrategy sentinelStrategy, boolean startFromBeginning) {
    this.maxScrolls = maxScrolls;
    this.perScrollTimeoutMillis = perScrollTimeoutMillis;
    this.axis = axis;
    this.sentinelStrategy = sentinelStrategy;
    this.startFromBeginning = startFromBeginning;
  }

  /**
   * Constructs with default not startFromBegining.
   */
  public SentinelScroller(int maxScrolls, long perScrollTimeoutMillis, Axis axis,
      SentinelStrategy sentinelStrategy) {
    this(maxScrolls, perScrollTimeoutMillis, axis, sentinelStrategy, false);
  }

  /**
   * Constructs with default 100 maxScrolls, 1 second for
   * perScrollTimeoutMillis, vertical axis, not startFromBegining.
   */
  public SentinelScroller(SentinelStrategy sentinelStrategy) {
    this(100, 1000L, Axis.VERTICAL, sentinelStrategy, false);
  }

  // if scrollBack is true, scrolls back to starting location if not found, so
  // that we can start search in the other direction w/o polling on pages we
  // have tried.
  protected UiElement scrollTo(DroidDriver driver, Finder containerFinder, Finder itemFinder,
      PhysicalDirection direction, boolean scrollBack) {
    Logs.call(this, "scrollTo", driver, containerFinder, itemFinder, direction, scrollBack);
    // Enforce itemFinder is relative to containerFinder.
    // Combine with containerFinder to make itemFinder absolute.
    itemFinder = By.chain(containerFinder, itemFinder);

    int i = 0;
    for (; i <= maxScrolls; i++) {
      try {
        return driver.getPoller()
            .pollFor(driver, itemFinder, Poller.EXISTS, perScrollTimeoutMillis);
      } catch (TimeoutException e) {
        if (i < maxScrolls && !sentinelStrategy.scroll(driver, containerFinder, direction)) {
          break;
        }
      }
    }

    ElementNotFoundException exception = new ElementNotFoundException(itemFinder);
    if (i == maxScrolls) {
      // This is often a program error -- maxScrolls is a safety net; we should
      // have either found itemFinder, or stopped to scroll b/c of reaching the
      // end. If maxScrolls is reasonably large, sentinelStrategy must be wrong.
      Logs.logfmt(Log.WARN, exception, "Scrolled %s %d times; sentinelStrategy=%s",
          containerFinder, maxScrolls, sentinelStrategy);
    }

    if (scrollBack) {
      for (; i > 1; i--) {
        driver.on(containerFinder).scroll(direction.reverse());
      }
    }
    throw exception;
  }

  @Override
  public UiElement scrollTo(DroidDriver driver, Finder containerFinder, Finder itemFinder,
      PhysicalDirection direction) {
    return scrollTo(driver, containerFinder, itemFinder, direction, false);
  }

  @Override
  public UiElement scrollTo(DroidDriver driver, Finder containerFinder, Finder itemFinder) {
    Logs.call(this, "scrollTo", driver, containerFinder, itemFinder);
    DirectionConverter converter = sentinelStrategy.getDirectionConverter();
    PhysicalDirection backwardDirection = converter.toPhysicalDirection(axis, BACKWARD);

    if (startFromBeginning) {
      // First try w/o scrolling
      try {
        return driver.getPoller().pollFor(driver, By.chain(containerFinder, itemFinder),
            Poller.EXISTS, perScrollTimeoutMillis);
      } catch (TimeoutException unused) {
        // fall through to scroll to find
      }

      UiElement container = driver.on(containerFinder);
      if (container.isFocused()) {
        // Use KeyEvent to go to beginning
        container.perform(MOVE_HOME);
        Logs.log(Log.DEBUG, "MOVE_HOME used");
      } else {
        // Fling to beginning
        container.perform(SwipeAction.toFling(backwardDirection));
      }
    } else {
      // search backward
      try {
        return scrollTo(driver, containerFinder, itemFinder, backwardDirection, true);
      } catch (ElementNotFoundException e) {
        // try another direction
      }
    }

    // search forward
    return scrollTo(driver, containerFinder, itemFinder, backwardDirection.reverse(), false);
  }
}
