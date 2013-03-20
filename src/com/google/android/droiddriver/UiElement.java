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

package com.google.android.droiddriver;

import android.graphics.Rect;

import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.actions.ScrollDirection;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.ElementNotVisibleException;

/**
 * Represents an UI element within an Android App.
 *
 * <p>
 * UI elements are generally views.
 */
public interface UiElement {
  /**
   * Finds the first {@link UiElement} that matches the given matcher,
   * traversing from this element. If the elements tree may change, use
   * {@link DroidDriver#waitForElement(Matcher)}.
   *
   * @param matcher The matching mechanism
   * @return The first matching element on the current context
   * @throws ElementNotFoundException If no matching elements are found
   */
  UiElement findElement(Matcher matcher);

  /**
   * Gets the text of this element.
   */
  String getText();

  /**
   * Sets the text of this element.
   *
   * @param text The text to enter.
   * @throws ElementNotVisibleException when the element is not visible
   */
  // TODO: Should this clear the text before setting?
  void setText(String text);

  /**
   * Gets the content description of this element.
   */
  String getContentDescription();

  /**
   * Gets the class name of the underlying view.
   */
  String getClassName();

  /**
   * Executes the given action.
   *
   * @param action The action to execute
   * @return true if the action is successful
   */
  boolean perform(Action action);

  /**
   * Clicks this element. The click will be at the center of the visible
   * element.
   *
   * @throws ElementNotVisibleException when the element is not visible
   */
  void click();

  /**
   * @returns whether or not this element is visible on the device's display.
   */
  boolean isVisible();

  /**
   * Gets the UiElement bounds in screen coordinates. The coordinates may not be
   * visible on screen.
   */
  Rect getRect();

  /**
   * Scrolls in the given direction. Scrolling down means swiping upwards.
   */
  void scroll(ScrollDirection direction);
}
