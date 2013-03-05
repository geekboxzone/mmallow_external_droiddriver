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

import com.google.android.uidriver.exceptions.ElementNotVisibleException;
import com.google.android.uidriver.util.Logs.Loggable;

/**
 * Represents an UI element within an Android App.
 *
 * <p>UI elements are generally views.
 */
public interface UiElement extends SearchContext {
  /**
   * Gets the text of this element.
   */
  @Loggable
  String getText();

  /**
   * Sets the text of this element.
   * @param text The text to enter.
   * @throws ElementNotVisibleException when the element is not visible
   */
  // TODO: Should this clear the text before setting?
  @Loggable
  void setText(String text);

  /**
   * Gets the content description of this element.
   */
  @Loggable
  String getContentDescription();

  /**
   * Gets the class name of the underlying view.
   */
  @Loggable
  String getClassName();

  /**
   * Clicks this element.  The click will be at the center of the visible element.
   * @throws ElementNotVisibleException when the element is not visible
   */
  @Loggable
  void click();

  /**
   * @returns whether or not this element is visible on the device's display.
   */
  @Loggable
  boolean isVisible();

  /**
   * Scrolls in the given direction.  Scrolling down means swiping upwards.
   * @returns true if we can continue to scroll, otherwise false if we are at the beginning or
   *          end of a scrollable view or the element is not scrollable.
   */
  @Loggable
  boolean scroll(ScrollDirection direction);
}
