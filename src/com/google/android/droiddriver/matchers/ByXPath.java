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
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class ByXPath implements Matcher {
  private final String xPathString;
  private final XPathExpression xPathExpression;

  protected ByXPath(String xPathString) {
    this.xPathString = Preconditions.checkNotNull(xPathString);
    try {
      xPathExpression = XPathFactory.newInstance().newXPath().compile(xPathString);
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
    return Objects.toStringHelper(this).addValue(xPathString).toString();
  }

  public XPathExpression getXPathExpression() {
    return xPathExpression;
  }

  public String getXPathString() {
    return xPathString;
  }

  /**
   * @return The tag name used to build UiElement DOM. It is preferable to use
   *         this to build XPath instead of String literals.
   */
  public static String tagName(String className) {
    return simpleClassName(className);
  }

  /**
   * @return The tag name used to build UiElement DOM. It is preferable to use
   *         this to build XPath instead of String literals.
   */
  public static String tagName(Class<?> clazz) {
    return tagName(clazz.getName());
  }

  private static String simpleClassName(String name) {
    // the nth anonymous class has a class name ending in "Outer$n"
    // and local inner classes have names ending in "Outer.$1Inner"
    name = name.replaceAll("\\$[0-9]+", "\\$");

    // we want the name of the inner class all by its lonesome
    int start = name.lastIndexOf('$');

    // if this isn't an inner class, just find the start of the
    // top level class name.
    if (start == -1) {
      start = name.lastIndexOf('.');
    }
    return name.substring(start + 1);
  }
}
