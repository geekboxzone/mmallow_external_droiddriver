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

package com.google.android.droiddriver.base;

import android.util.Log;

import com.google.android.droiddriver.Matcher;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.ClickAction;
import com.google.android.droiddriver.actions.ScrollDirection;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.actions.TypeAction;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.ElementNotVisibleException;
import com.google.android.droiddriver.util.Logs;

/**
 * Abstract implementation with common methods already implemented.
 */
public abstract class AbstractUiElement implements UiElement {

  @Override
  public void setText(String text) {
    checkVisible();
    // TODO: Define common actions as a const.
    perform(new TypeAction(text));
  }

  @Override
  public void click() {
    checkVisible();
    perform(new ClickAction());
  }

  @Override
  public void scroll(ScrollDirection direction) {
    checkVisible();
    perform(new SwipeAction(direction, false));
  }

  protected abstract int getChildCount();

  protected abstract UiElement getChild(int index);

  private void checkVisible() {
    if (!isVisible()) {
      throw new ElementNotVisibleException("Element is not visible on screen");
    }
  }

  @Override
  public UiElement findElement(Matcher matcher) {
    if (matcher.matches(this)) {
      Log.d(Logs.TAG, "Found match: " + toString());
      return this;
    }
    int childCount = getChildCount();
    Log.d(Logs.TAG, "Looping through number of children " + childCount);
    for (int i = 0; i < childCount; i++) {
      UiElement child = getChild(i);
      if (child == null) {
        Log.w(Logs.TAG, "Skip null child for " + toString());
        continue;
      }
      try {
        return child.findElement(matcher);
      } catch (ElementNotFoundException enfe) {
        // Do nothing. Continue searching.
      }
    }
    throw new ElementNotFoundException("Could not find any element matching " + matcher);
  }
}
