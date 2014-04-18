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

package com.google.android.droiddriver.finders;

import android.text.TextUtils;

import com.google.android.droiddriver.UiElement;

/**
 * Static utility methods pertaining to {@code Predicate} instances.
 */
public final class Predicates {
  private Predicates() {}

  private static final Predicate<Object> ANY = new Predicate<Object>() {
    @Override
    public boolean apply(Object o) {
      return true;
    }

    @Override
    public String toString() {
      return "any";
    }
  };

  /**
   * Returns a predicate that always evaluates to {@code true}.
   */
  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> any() {
    return (Predicate<T>) ANY;
  }

  /**
   * Returns a predicate that evaluates to {@code true} if each of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a false
   * predicate is found.
   */
  public static <T> Predicate<T> allOf(
      @SuppressWarnings("unchecked") final Predicate<? super T>... components) {
    return new Predicate<T>() {
      @Override
      public boolean apply(T input) {
        for (Predicate<? super T> each : components) {
          if (!each.apply(input)) {
            return false;
          }
        }
        return true;
      }

      @Override
      public String toString() {
        return "allOf(" + TextUtils.join(", ", components) + ")";
      }
    };
  }

  /**
   * Returns a predicate that evaluates to {@code true} if any one of its
   * components evaluates to {@code true}. The components are evaluated in
   * order, and evaluation will be "short-circuited" as soon as a true predicate
   * is found.
   */
  public static <T> Predicate<T> anyOf(
      @SuppressWarnings("unchecked") final Predicate<? super T>... components) {
    return new Predicate<T>() {
      @Override
      public boolean apply(T input) {
        for (Predicate<? super T> each : components) {
          if (each.apply(input)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return "anyOf(" + TextUtils.join(", ", components) + ")";
      }
    };
  }

  public static Predicate<UiElement> withParent(final Predicate<? super UiElement> parentPredicate) {
    return new Predicate<UiElement>() {
      @Override
      public boolean apply(UiElement element) {
        UiElement parent = element.getParent();
        return parent != null && parentPredicate.apply(parent);
      }

      @Override
      public String toString() {
        return "withParent(" + parentPredicate + ")";
      }
    };
  }

  public static Predicate<UiElement> withAncestor(
      final Predicate<? super UiElement> ancestorPredicate) {
    return new Predicate<UiElement>() {
      @Override
      public boolean apply(UiElement element) {
        UiElement parent = element.getParent();
        while (parent != null) {
          if (ancestorPredicate.apply(parent)) {
            return true;
          }
          parent = parent.getParent();
        }
        return false;
      }

      @Override
      public String toString() {
        return "withAncestor(" + ancestorPredicate + ")";
      }
    };
  }

  public static Predicate<UiElement> withSibling(final Predicate<? super UiElement> siblingPredicate) {
    return new Predicate<UiElement>() {
      @Override
      public boolean apply(UiElement element) {
        UiElement parent = element.getParent();
        if (parent == null) {
          return false;
        }
        for (UiElement sibling : parent.getChildren(UiElement.VISIBLE)) {
          if (sibling != element && siblingPredicate.apply(sibling)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return "withSibling(" + siblingPredicate + ")";
      }
    };
  }

  public static Predicate<UiElement> withChild(final Predicate<? super UiElement> childPredicate) {
    return new Predicate<UiElement>() {
      @Override
      public boolean apply(UiElement element) {
        for (UiElement child : element.getChildren(UiElement.VISIBLE)) {
          if (childPredicate.apply(child)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return "withChild(" + childPredicate + ")";
      }
    };
  }
}
