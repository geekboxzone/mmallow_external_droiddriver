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

  /** Shorthand for {@link #attribute}{@code (attribute, OBJECT_EQUALS, expected)} */
  public static <T> ByAttribute<T> attribute(Attribute attribute, T expected) {
    return attribute(attribute, OBJECT_EQUALS, expected);
  }

  /** Shorthand for {@link #attribute}{@code (attribute, true)} */
  public static ByAttribute<Boolean> is(Attribute attribute) {
    return attribute(attribute, true);
  }

  /** Shorthand for {@link #attribute}{@code (attribute, false)} */
  public static ByAttribute<Boolean> not(Attribute attribute) {
    return attribute(attribute, false);
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
    return is(Attribute.SELECTED);
  }

  /**
   * Matches by XPath. When applied on an non-root element, it will not evaluate
   * above the context element.
   * <p>
   * XPath is the domain-specific-language for navigating a node tree. It is
   * ideal if the UiElement to match has a complex relationship with surrounding
   * nodes. For simple cases, consider {@link #withParent} or
   * {@link #withAncestor}. For complex cases like below, XPath is superior:
   *
   * <pre>
   * {@code
   * <View><!-- a custom view to group a cluster of items -->
   *   <LinearLayout>
   *     <TextView text='Albums'/>
   *     <TextView text='4 MORE'/>
   *   </LinearLayout>
   *   <RelativeLayout>
   *     <TextView text='Forever'/>
   *     <ImageView/>
   *   </RelativeLayout>
   * </View><!-- end of Albums cluster -->
   * <!-- imagine there are other clusters for Artists and Songs -->
   * }
   * </pre>
   *
   * If we need to locate the RelativeLayout containing the album "Forever"
   * instead of a song or an artist named "Forever", this XPath works:
   *
   * <pre>
   * {@code //*[LinearLayout/*[@text='Albums']]/RelativeLayout[*[@text='Forever']]}
   * </pre>
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
  /**
   * Evaluates given {@matchers} in short-circuit fashion in the
   * order they are passed. Costly matchers (for example those returned by with*
   * methods that navigate the node tree) should be passed after cheap matchers
   * (for example the ByAttribute matchers).
   *
   * @return a matcher that is the logical conjunction of given matchers
   */
  public static final ElementMatcher allOf(final ElementMatcher... matchers) {
    return new ElementMatcher() {
      @Override
      public boolean matches(UiElement element) {
        for (ElementMatcher matcher : matchers) {
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

  /**
   * Evaluates given {@matchers} in short-circuit fashion in the
   * order they are passed. Costly matchers (for example those returned by with*
   * methods that navigate the node tree) should be passed after cheap matchers
   * (for example the ByAttribute matchers).
   *
   * @return a matcher that is the logical disjunction of given matchers
   */
  public static final ElementMatcher anyOf(final ElementMatcher... matchers) {
    return new ElementMatcher() {
      @Override
      public boolean matches(UiElement element) {
        for (ElementMatcher matcher : matchers) {
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

  /**
   * Matches a UiElement whose parent matches the given parentMatcher. For
   * complex cases, consider {@link #xpath}.
   */
  public static final ElementMatcher withParent(final ElementMatcher parentMatcher) {
    checkNotNull(parentMatcher);
    return new ElementMatcher() {
      @Override
      public boolean matches(UiElement element) {
        UiElement parent = element.getParent();
        return parent != null && parentMatcher.matches(parent);
      }

      @Override
      public String toString() {
        return "withParent(" + parentMatcher + ")";
      }
    };
  }

  /**
   * Matches a UiElement whose ancestor matches the given ancestorMatcher. For
   * complex cases, consider {@link #xpath}.
   */
  public static final ElementMatcher withAncestor(final ElementMatcher ancestorMatcher) {
    checkNotNull(ancestorMatcher);
    return new ElementMatcher() {
      @Override
      public boolean matches(UiElement element) {
        UiElement parent = element.getParent();
        while (parent != null) {
          if (ancestorMatcher.matches(parent)) {
            return true;
          }
          parent = parent.getParent();
        }
        return false;
      }

      @Override
      public String toString() {
        return "withAncestor(" + ancestorMatcher + ")";
      }
    };
  }

  /**
   * Matches a UiElement which has a sibling matching the given siblingMatcher.
   * For complex cases, consider {@link #xpath}.
   */
  public static final ElementMatcher withSibling(final ElementMatcher siblingMatcher) {
    checkNotNull(siblingMatcher);
    return new ElementMatcher() {
      @Override
      public boolean matches(UiElement element) {
        UiElement parent = element.getParent();
        if (parent == null) {
          return false;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
          if (siblingMatcher.matches(parent.getChild(i))) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return "withSibling(" + siblingMatcher + ")";
      }
    };
  }

  private By() {}
}
