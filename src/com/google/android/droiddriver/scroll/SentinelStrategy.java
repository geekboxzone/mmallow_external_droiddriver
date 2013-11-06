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

import com.google.android.droiddriver.UiElement;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.List;

/**
 * Interface for determining whether scrolling is possible based on a sentinel.
 */
public interface SentinelStrategy extends ScrollStepStrategy {

  /**
   * Gets sentinel based on {@link Predicate}.
   */
  public static abstract class Getter {
    protected final Predicate<? super UiElement> predicate;
    protected final String description;

    protected Getter(Predicate<? super UiElement> predicate, String description) {
      this.predicate = predicate;
      this.description = description;
    }

    /**
     * Gets the sentinel, which must be an immediate child of {@code container}
     * -- not a descendant. Note this could be null if {@code container} has not
     * finished updating.
     */
    public UiElement getSentinel(UiElement container) {
      return getSentinel(container.getChildren(predicate));
    }

    protected abstract UiElement getSentinel(List<? extends UiElement> children);

    @Override
    public String toString() {
      return description;
    }
  }

  /**
   * Decorates an existing {@link Getter} by adding another {@link Predicate}.
   */
  public static class MorePredicateGetter extends Getter {
    private final Getter original;

    public MorePredicateGetter(Getter original, Predicate<? super UiElement> extraPredicate,
        String extraDescription) {
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
  public static final Getter FIRST_CHILD_GETTER =
      new Getter(Predicates.alwaysTrue(), "FIRST_CHILD") {
        @Override
        protected UiElement getSentinel(List<? extends UiElement> children) {
          return children.isEmpty() ? null : children.get(0);
        }
      };
  /**
   * Returns the last child as the sentinel.
   */
  public static final Getter LAST_CHILD_GETTER = new Getter(Predicates.alwaysTrue(), "LAST_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.isEmpty() ? null : children.get(children.size() - 1);
    }
  };
  /**
   * Returns the second last child as the sentinel. Useful when the activity
   * always shows the last child as an anchor (for example a footer).
   * <p>
   * Sometimes uiautomatorviewer may not show the anchor as the last child, due
   * to the reordering by layout described in {@link UiElement#getChildren}.
   * This is not a problem with UiAutomationDriver because it sees the same as
   * uiautomatorviewer does, but could be a problem with InstrumentationDriver.
   * </p>
   */
  public static final Getter SECOND_LAST_CHILD_GETTER = new Getter(Predicates.alwaysTrue(),
      "SECOND_LAST_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.size() < 2 ? null : children.get(children.size() - 2);
    }
  };
  /**
   * Returns the second child as the sentinel. Useful when the activity shows a
   * fixed first child.
   */
  public static final Getter SECOND_CHILD_GETTER = new Getter(Predicates.alwaysTrue(),
      "SECOND_CHILD") {
    @Override
    protected UiElement getSentinel(List<? extends UiElement> children) {
      return children.size() <= 1 ? null : children.get(1);
    }
  };
}
