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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.android.droiddriver.UiElement;
import com.google.common.base.Preconditions;

/**
 * A matcher that matches element by attributes.
 */
public class ByAttribute<T> implements Matcher {
  private final Attribute attribute;
  private final MatchStrategy<T> strategy;
  private final T expected;

  /**
   * @param attribute the attribute to match against
   * @param strategy the matching strategy, for instance, equals or matches
   *        regular expression
   * @param expected the expected attribute value
   * @return a new ByAttribute matcher
   */
  public static <A> ByAttribute<A> newMatcher(Attribute attribute, MatchStrategy<A> strategy,
      A expected) {
    return new ByAttribute<A>(attribute, strategy, expected);
  }

  protected ByAttribute(Attribute attribute, MatchStrategy<T> strategy, T expected) {
    this.attribute = Preconditions.checkNotNull(attribute);
    this.strategy = Preconditions.checkNotNull(strategy);
    this.expected = checkNotNull(expected);
  }

  @Override
  public boolean matches(UiElement element) {
    T value = attribute.getValue(element);
    return value != null && strategy.match(expected, value);
  }

  @Override
  public String toString() {
    return String.format("ByAttribute{%s %s %s}", attribute, strategy, expected);
  }
}
