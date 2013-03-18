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

package com.google.android.droiddriver;

import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Convenience methods to create commonly used matchers.
 */
public class By {

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
   * @return a matcher to find an element by content description
   */
  public static final ByClassName className(String className) {
    return new ByClassName(className);
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

  public static class ByText implements Matcher {
    private final String text;

    public ByText(String text) {
      this.text = Preconditions.checkNotNull(text);
    }

    @Override
    public boolean matches(UiElement element) {
      return text.equals(element.getText());
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this).addValue(text).toString();
    }
  }

  public static class ByContentDescription implements Matcher {
    private final String contentDescription;

    public ByContentDescription(String contentDescription) {
      this.contentDescription = Preconditions.checkNotNull(contentDescription);
    }

    @Override
    public boolean matches(UiElement element) {
      return contentDescription.equals(element.getContentDescription());
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this).addValue(contentDescription).toString();
    }
  }

  public static class ByClassName implements Matcher {
    private final String className;

    public ByClassName(String className) {
      this.className = Preconditions.checkNotNull(className);
    }

    @Override
    public boolean matches(UiElement element) {
      return className.equals(element.getClassName());
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this).addValue(className).toString();
    }
  }

  public static class ByXPath implements Matcher {
    private final String xPath;
    private final XPathExpression xPathExpression;

    public ByXPath(String xPath) {
      this.xPath = Preconditions.checkNotNull(xPath);
      try {
        xPathExpression = XPathFactory.newInstance().newXPath().compile(xPath);
      } catch (XPathExpressionException e) {
        throw new DroidDriverException(e);
      }
    }

    @Override
    public boolean matches(UiElement element) {
      throw new DroidDriverException("ByXPath.matches() should not be invoked; cyclic calling "
          + toString());
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this).addValue(xPath).toString();
    }

    public XPathExpression getXPathExpression() {
      return xPathExpression;
    }
  }

  private By() {}
}
