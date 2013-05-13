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
import com.google.common.base.Objects;

/**
 * Convenience methods to create commonly used matchers.
 */
public class By {
  /** Matches by {@link Object#equals}. */
  public static final MatchStrategy<Object> OBJECT_EQUALS = new MatchStrategy<Object>() {
    @Override
    public boolean match(Object expected, Object actual) {
      return Objects.equal(actual, expected);
    }

    @Override
    public String toString() {
      return "equals";
    }
  };
  /** Matches by {@link String#matches}. */
  public static final MatchStrategy<String> STRING_MATCHES = new MatchStrategy<String>() {
    @Override
    public boolean match(String expected, String actual) {
      return actual != null && actual.matches(expected);
    }

    @Override
    public String toString() {
      return "matches pattern";
    }
  };

  /**
   * Creates a new ByAttribute matcher. Frequently-used matchers have shorthands
   * below, for example, {@link #text}, {@link #textRegex}. Users can create
   * custom matchers using this method.
   *
   * @param attribute the attribute to match against
   * @param strategy the matching strategy, for instance, equals or matches
   *        regular expression
   * @param expected the expected attribute value
   * @return a new ByAttribute matcher
   */
  public static <T> ByAttribute<T> attribute(Attribute attribute,
      MatchStrategy<? super T> strategy, T expected) {
    return new ByAttribute<T>(attribute, strategy, expected);
  }

  /**
   * @param resourceId The resource id to match against
   * @return a matcher to find an element by resource id
   */
  public static final ByAttribute<String> resourceId(String resourceId) {
    return attribute(Attribute.RESOURCE_ID, OBJECT_EQUALS, resourceId);
  }

  /**
   * @param text The exact text to match against
   * @return a matcher to find an element by text
   */
  public static final ByAttribute<String> text(String text) {
    return attribute(Attribute.TEXT, OBJECT_EQUALS, text);
  }

  /**
   * @param regex The regular expression pattern to match against
   * @return a matcher to find an element by text pattern
   */
  public static final ByAttribute<String> textRegex(String regex) {
    return attribute(Attribute.TEXT, STRING_MATCHES, regex);
  }

  /**
   * @param contentDescription The exact content description to match against
   * @return a matcher to find an element by content description
   */
  public static final ByAttribute<String> contentDescription(String contentDescription) {
    return attribute(Attribute.CONTENT_DESC, OBJECT_EQUALS, contentDescription);
  }

  /**
   * @param className The exact class name to match against
   * @return a matcher to find an element by class name
   */
  public static final ByAttribute<String> className(String className) {
    return attribute(Attribute.CLASS, OBJECT_EQUALS, className);
  }

  /**
   * @param clazz The class whose name is matched against
   * @return a matcher to find an element by class name
   */
  public static final ByAttribute<String> className(Class<?> clazz) {
    return className(clazz.getName());
  }

  /**
   * @return a matcher to find an element that is selected
   */
  public static final ByAttribute<Boolean> selected() {
    return attribute(Attribute.SELECTED, OBJECT_EQUALS, true);
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
