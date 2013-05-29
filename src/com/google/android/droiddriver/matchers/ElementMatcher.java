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

package com.google.android.droiddriver.matchers;

import android.util.Log;

import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.util.Logs;

/**
 * Find matching UiElement by operations on an instance.
 */
public abstract class ElementMatcher implements Matcher {
  /**
   * Returns true if the {@code element} matches the implementing matcher. The
   * implementing matcher should return quickly.
   *
   * @param element The element to validate against
   * @return true if the element matches
   */
  public abstract boolean matches(UiElement element);

  /**
   * {@inheritDoc}
   *
   * <p>
   * It is recommended that this method return the description of the matcher,
   * for example, "ByAttribute{text equals OK}".
   */
  @Override
  public abstract String toString();

  public UiElement find(UiElement context) {
    if (matches(context)) {
      Log.d(Logs.TAG, "Found match: " + context);
      return context;
    }
    int childCount = context.getChildCount();
    for (int i = 0; i < childCount; i++) {
      UiElement child = context.getChild(i);
      if (child == null) {
        Log.w(Logs.TAG, "Skip null child for " + context);
        continue;
      }
      if (!child.isVisible()) {
        Logs.println(Log.VERBOSE, "Skip invisible child: ", child);
        continue;
      }
      try {
        return find(child);
      } catch (ElementNotFoundException enfe) {
        // Do nothing. Continue searching.
      }
    }
    throw new ElementNotFoundException(this);
  }
}
