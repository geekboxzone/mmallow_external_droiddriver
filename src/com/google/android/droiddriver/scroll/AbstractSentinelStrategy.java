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

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Direction.LogicalDirection;
import com.google.android.droiddriver.scroll.Direction.PhysicalToLogicalConverter;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.List;

/**
 * Base {@link SentinelStrategy} for common code.
 */
public abstract class AbstractSentinelStrategy implements SentinelStrategy {

  /**
   * Gets sentinel based on {@link Predicate}.
   */
  public static abstract class GetStrategy {
    protected final Predicate<? super UiElement> predicate;
    protected final String description;

    protected GetStrategy(Predicate<? super UiElement> predicate, String description) {
      this.predicate = predicate;
      this.description = description;
    }

    /**
     * Gets the sentinel, which must be an immediate child of {@code parent} --
     * not a descendant. Note this could be null if {@code parent} has not
     * finished updating.
     */
    public UiElement getSentinel(UiElement parent) {
      return getSentinel(parent.getChildren(predicate));
    }

    protected abstract UiElement getSentinel(List<? extends UiElement> children);

    @Override
    public String toString() {
      return description;
    }
  }

  /**
   * Decorates an existing {@link GetStrategy} by adding another
   * {@link Predicate}.
   */
  public static class MorePredicateGetStrategy extends GetStrategy {
    private final GetStrategy original;

    public MorePredicateGetStrategy(GetStrategy original,
        Predicate<? super UiElement> extraPredicate, String extraDescription) {
      super(Predicates.and(original.predicate, extraPredicate), extraDescription
          + original.description);
      this.original = original;
    }

    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return original.getSentinel(children);
    }
  }

  /**
   * Returns the first child as the sentinel.
   */
  public static final GetStrategy FIRST_CHILD_GETTER = new GetStrategy(Predicates.alwaysTrue(),
      "FIRST_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.isEmpty() ? null : children.get(0);
    }
  };

  /**
   * Returns the last child as the sentinel.
   */
  public static final GetStrategy LAST_CHILD_GETTER = new GetStrategy(Predicates.alwaysTrue(),
      "LAST_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.isEmpty() ? null : children.get(children.size() - 1);
    }
  };

  /**
   * Returns the second child as the sentinel. Useful when the activity shows a
   * fixed first child.
   */
  public static final GetStrategy SECOND_CHILD_GETTER = new GetStrategy(Predicates.alwaysTrue(),
      "SECOND_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.size() <= 1 ? null : children.get(1);
    }
  };

  private static class SentinelFinder implements Finder {
    private final GetStrategy getStrategy;

    public SentinelFinder(GetStrategy getStrategy) {
      this.getStrategy = getStrategy;
    }

    @Override
    public UiElement find(UiElement parent) {
      UiElement sentinel = getStrategy.getSentinel(parent);
      if (sentinel == null) {
        throw new ElementNotFoundException(this);
      }
      return sentinel;
    }

    @Override
    public String toString() {
      return String.format("SentinelFinder{%s}", getStrategy);
    }
  }

  protected final GetStrategy backwardGetStrategy;
  protected final GetStrategy forwardGetStrategy;
  protected final PhysicalToLogicalConverter physicalToLogicalConverter;
  private final SentinelFinder backwardSentinelFinder;
  private final SentinelFinder forwardSentinelFinder;

  public AbstractSentinelStrategy(GetStrategy backwardGetStrategy, GetStrategy forwardGetStrategy,
      PhysicalToLogicalConverter physicalToLogicalConverter) {
    this.backwardGetStrategy = backwardGetStrategy;
    this.forwardGetStrategy = forwardGetStrategy;
    this.physicalToLogicalConverter = physicalToLogicalConverter;
    this.backwardSentinelFinder = new SentinelFinder(backwardGetStrategy);
    this.forwardSentinelFinder = new SentinelFinder(forwardGetStrategy);
  }

  protected UiElement getSentinel(DroidDriver driver, Finder parentFinder,
      PhysicalDirection direction) {
    // Make sure sentinel exists in parent
    Finder chainFinder;
    LogicalDirection logicalDirection = physicalToLogicalConverter.toLogicalDirection(direction);
    if (logicalDirection == LogicalDirection.BACKWARD) {
      chainFinder = By.chain(parentFinder, backwardSentinelFinder);
    } else {
      chainFinder = By.chain(parentFinder, forwardSentinelFinder);
    }
    return driver.on(chainFinder);
  }

  @Override
  public String toString() {
    return String.format("{backwardGetStrategy=%s, forwardGetStrategy=%s}", backwardGetStrategy,
        forwardGetStrategy);
  }
}
