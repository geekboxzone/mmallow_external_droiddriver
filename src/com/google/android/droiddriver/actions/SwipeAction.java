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

package com.google.android.droiddriver.actions;

import static com.google.android.droiddriver.scroll.Direction.PhysicalDirection.DOWN;
import static com.google.android.droiddriver.scroll.Direction.PhysicalDirection.LEFT;
import static com.google.android.droiddriver.scroll.Direction.PhysicalDirection.RIGHT;
import static com.google.android.droiddriver.scroll.Direction.PhysicalDirection.UP;

import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.view.ViewConfiguration;

import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ActionException;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;
import com.google.android.droiddriver.util.Events;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Ints;

/**
 * A {@link ScrollAction} that swipes the touch screen. Note the scroll
 * direction enum values specify where the content will move, instead of the
 * finger. This class includes some common instances that use the finger
 * direction in the names, hopefully to mitigate this confusion.
 */
public class SwipeAction extends ScrollAction {
  // The action is a fling if the ACTION_MOVE velocity is greater than
  // ViewConfiguration#getScaledMinimumFlingVelocity. The velocity is calculated
  // as <distance between ACTION_MOVE points> / ACTION_MOVE_INTERVAL
  // Note: ACTION_MOVE_INTERVAL is the minimum interval between injected events;
  // the actual interval typically is longer.
  private static final int ACTION_MOVE_INTERVAL = 5;
  // ViewConfiguration.MINIMUM_FLING_VELOCITY = 50, so if there is no scale, in
  // theory a swipe of 20 steps is a scroll instead of fling on devices that
  // have 20 * 50 * 5 = 5000 pixels in one direction. Make it 40 for safety.
  private static final int SCROLL_STEPS = 40;
  // TODO: Find the exact version-dependent fling steps. It is observed that 2
  // does not work on GINGERBREAD; we haven't tested all versions so <JELLY_BEAN
  // is used as a guess.
  private static final int FLING_STEPS = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ? 3
      : 2;

  /**
   * Common instances for convenience. The direction in names reflects the
   * direction of finger rather than the scroll direction.
   */
  private static final SwipeAction SWIPE_DOWN = new SwipeAction(UP, SCROLL_STEPS);
  private static final SwipeAction SWIPE_UP = new SwipeAction(DOWN, SCROLL_STEPS);
  private static final SwipeAction SWIPE_RIGHT = new SwipeAction(LEFT, SCROLL_STEPS);
  private static final SwipeAction SWIPE_LEFT = new SwipeAction(RIGHT, SCROLL_STEPS);

  private static final SwipeAction FLING_DOWN = new SwipeAction(UP, FLING_STEPS);
  private static final SwipeAction FLING_UP = new SwipeAction(DOWN, FLING_STEPS);
  private static final SwipeAction FLING_RIGHT = new SwipeAction(LEFT, FLING_STEPS);
  private static final SwipeAction FLING_LEFT = new SwipeAction(RIGHT, FLING_STEPS);

  /**
   * Gets canned common instances for scrolling. Note the scroll direction
   * specifies where the content will move, instead of the finger.
   */
  // TODO: We may use "smart" steps that depend on the size of the UiElement and
  // ViewConfiguration#getScaledMinimumFlingVelocity.
  public static SwipeAction toScroll(PhysicalDirection direction) {
    switch (direction) {
      case UP:
        return SWIPE_DOWN;
      case DOWN:
        return SWIPE_UP;
      case LEFT:
        return SWIPE_RIGHT;
      case RIGHT:
        return SWIPE_LEFT;
      default:
        throw new ActionException("Unknown scroll direction: " + direction);
    }
  }

  /**
   * Gets canned common instances for flinging. Note the scroll direction
   * specifies where the content will move, instead of the finger.
   * <p>
   * Note: This may not actually fling, depending on the size of the target
   * UiElement and the SDK version of the device. If it does not behave as
   * expected, you can use SwipeAction instances with custom steps.
   * </p>
   *
   * @see ViewConfiguration#getScaledMinimumFlingVelocity
   */
  // TODO: We may use "smart" steps that depend on the size of the UiElement and
  // ViewConfiguration#getScaledMinimumFlingVelocity.
  public static SwipeAction toFling(PhysicalDirection direction) {
    switch (direction) {
      case UP:
        return FLING_DOWN;
      case DOWN:
        return FLING_UP;
      case LEFT:
        return FLING_RIGHT;
      case RIGHT:
        return FLING_LEFT;
      default:
        throw new ActionException("Unknown scroll direction: " + direction);
    }
  }

  private final PhysicalDirection direction;
  private final boolean drag;
  private final int steps;

  /**
   * Defaults timeoutMillis to 1000 and no drag.
   */
  public SwipeAction(PhysicalDirection direction, int steps) {
    this(direction, steps, false, 1000L);
  }

  /**
   * Defaults timeoutMillis to 1000 and to swipe rather than flinging.
   */
  public SwipeAction(PhysicalDirection direction, boolean drag) {
    this(direction, SCROLL_STEPS, drag, 1000L);
  }

  /**
   * @param direction the scroll direction specifying where the content will
   *        move, instead of the finger.
   * @param steps minimum 2; (steps-1) is the number of {@code ACTION_MOVE} that
   *        will be injected between {@code ACTION_DOWN} and {@code ACTION_UP}.
   * @param drag whether this is a drag
   * @param timeoutMillis
   */
  public SwipeAction(PhysicalDirection direction, int steps, boolean drag, long timeoutMillis) {
    super(timeoutMillis);
    this.direction = direction;
    this.steps = Ints.max(2, steps);
    this.drag = drag;
  }

  @Override
  public boolean perform(InputInjector injector, UiElement element) {
    Rect elementRect = element.getVisibleBounds();

    int swipeAreaHeightAdjust = (int) (elementRect.height() * 0.1);
    int swipeAreaWidthAdjust = (int) (elementRect.width() * 0.1);
    int startX;
    int startY;
    int endX;
    int endY;

    switch (direction) {
      case DOWN:
        startX = elementRect.centerX();
        startY = elementRect.bottom - swipeAreaHeightAdjust;
        endX = elementRect.centerX();
        endY = elementRect.top + swipeAreaHeightAdjust;
        break;
      case UP:
        startX = elementRect.centerX();
        startY = elementRect.top + swipeAreaHeightAdjust;
        endX = elementRect.centerX();
        endY = elementRect.bottom - swipeAreaHeightAdjust;
        break;
      case LEFT:
        startX = elementRect.left + swipeAreaWidthAdjust;
        startY = elementRect.centerY();
        endX = elementRect.right - swipeAreaWidthAdjust;
        endY = elementRect.centerY();
        break;
      case RIGHT:
        startX = elementRect.right - swipeAreaWidthAdjust;
        startY = elementRect.centerY();
        endX = elementRect.left + swipeAreaHeightAdjust;
        endY = elementRect.centerY();
        break;
      default:
        throw new ActionException("Unknown scroll direction: " + direction);
    }

    double xStep = ((double) (endX - startX)) / steps;
    double yStep = ((double) (endY - startY)) / steps;

    // First touch starts exactly at the point requested
    long downTime = Events.touchDown(injector, startX, startY);
    SystemClock.sleep(ACTION_MOVE_INTERVAL);
    if (drag) {
      SystemClock.sleep((long) (ViewConfiguration.getLongPressTimeout() * 1.5f));
    }
    for (int i = 1; i < steps; i++) {
      Events.touchMove(injector, downTime, startX + (int) (xStep * i), startY + (int) (yStep * i));
      SystemClock.sleep(ACTION_MOVE_INTERVAL);
    }
    if (drag) {
      // Hold final position for a little bit to simulate drag.
      SystemClock.sleep(100);
    }
    Events.touchUp(injector, downTime, endX, endY);
    return true;
  }

  @Override
  public String toString() {
    ToStringHelper toStringHelper = Objects.toStringHelper(this);
    toStringHelper.addValue(direction);
    toStringHelper.add("steps", steps);
    if (drag) {
      toStringHelper.addValue("drag");
    }
    return toStringHelper.toString();
  }
}
