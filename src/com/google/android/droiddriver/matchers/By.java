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

import com.google.android.droiddriver.UiElement;
import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

/**
 * Convenience methods to create commonly used matchers.
 */
public class By {

  /**
   * @param resourceId The resource id to match against
   * @return a matcher to find an element by resource id
   */
  public static final ByResourceId resourceId(String resourceId) {
    return new ByResourceId(resourceId);
  }

  /**
   * @param text The exact text to match against
   * @return a matcher to find an element by text
   */
  public static final ByText text(String text) {
    return new ByText(text);
  }

  /**
   * @param contentDescription The exact content description to match against
   * @return a matcher to find an element by content description
   */
  public static final ByContentDescription contentDescription(String contentDescription) {
    return new ByContentDescription(contentDescription);
  }

  /**
   * @param className The exact class name to match against
   * @return a matcher to find an element by class name
   */
  public static final ByClassName className(String className) {
    return new ByClassName(className);
  }

  /**
   * @param clazz The class whose name is matched against
   * @return a matcher to find an element by class name
   */
  public static final ByClassName className(Class<?> clazz) {
    return new ByClassName(clazz.getName());
  }

  /**
   * Matches by XPath. When applied on an non-root element, it will not evaluate
   * above the context element.
   *
   * @param xPath The xpath to use
   * @return a matcher which locates elements via XPath
   */
  // TODO: add UiElement.findElements
  @Beta
  public static final ByXPath xpath(String xPath) {
    return new ByXPath(xPath);
  }

  // Hamcrest style matcher aggregators
  /** @return a matcher that is the logical conjunction of given matchers */
  public static final Matcher allOf(final Matcher... matchers) {
    return new Matcher() {
      @Override
      public boolean matches(UiElement element) {
        for (Matcher matcher : matchers) {
          if (!matcher.matches(element)) {
            return false;
          }
        }
        return true;
      }

      @Override
      public String toString() {
        return "allOf(" + Joiner.on(",").join(matchers) + ")";
      }
    };
  }

  /** @return a matcher that is the logical disjunction of given matchers */
  public static final Matcher anyOf(final Matcher... matchers) {
    return new Matcher() {
      @Override
      public boolean matches(UiElement element) {
        for (Matcher matcher : matchers) {
          if (matcher.matches(element)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return "anyOf(" + Joiner.on(",").join(matchers) + ")";
      }
    };
  }

  private By() {}
}
