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

package com.google.android.droiddriver.base;

import android.util.Log;

import com.google.android.droiddriver.By.ByXPath;
import com.google.android.droiddriver.Matcher;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.actions.ClickAction;
import com.google.android.droiddriver.actions.ScrollDirection;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.actions.TypeAction;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.ElementNotVisibleException;
import com.google.android.droiddriver.util.Logs;
import com.google.android.droiddriver.util.Logs.LogDesired;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

/**
 * Abstract implementation with common methods already implemented.
 */
public abstract class AbstractUiElement implements UiElement {

  private static final String UI_ELEMENT = "UiElement";
  // document needs to be static so that when buildDomNode is called recursively
  // on children they are in the same document to be appended to this domNode.
  private static Document document;
  private Element domNode;

  @LogDesired
  @Override
  public abstract boolean perform(Action action);

  @Override
  public void setText(String text) {
    checkVisible();
    // TODO: Define common actions as a const.
    perform(new TypeAction(text));
  }

  @Override
  public void click() {
    checkVisible();
    perform(new ClickAction());
  }

  @Override
  public void scroll(ScrollDirection direction) {
    checkVisible();
    perform(new SwipeAction(direction, false));
  }

  protected abstract int getChildCount();

  protected abstract AbstractUiElement getChild(int index);

  private void checkVisible() {
    if (!isVisible()) {
      throw new ElementNotVisibleException("Element is not visible on screen");
    }
  }

  @LogDesired
  @Override
  public UiElement findElement(Matcher matcher) {
    return Logs.wrap(UiElement.class, findUnwrappedElement(matcher));
  }

  protected AbstractUiElement findUnwrappedElement(Matcher matcher) {
    // Special case for XPath here to avoid polluting UiElement interface
    if (matcher instanceof ByXPath) {
      return findByXPath((ByXPath) matcher);
    }

    if (matcher.matches(this)) {
      Log.d(Logs.TAG, "Found match: " + toString());
      return this;
    }
    int childCount = getChildCount();
    Log.d(Logs.TAG, "Looping through number of children " + childCount);
    for (int i = 0; i < childCount; i++) {
      AbstractUiElement child = getChild(i);
      if (child == null) {
        Log.w(Logs.TAG, "Skip null child for " + toString());
        continue;
      }
      try {
        return child.findUnwrappedElement(matcher);
      } catch (ElementNotFoundException enfe) {
        // Do nothing. Continue searching.
      }
    }
    throw new ElementNotFoundException(matcherFailMessage(matcher));
  }

  private static String matcherFailMessage(Matcher matcher) {
    return "Could not find any element matching " + matcher;
  }

  private AbstractUiElement findByXPath(ByXPath byXPath) {
    try {
      getDocument().appendChild(getDomNode());
      Element foundNode =
          (Element) byXPath.getXPathExpression().evaluate(getDomNode(), XPathConstants.NODE);
      if (foundNode == null) {
        throw new XPathExpressionException("XPath evaluation returns null");
      }
      return (AbstractUiElement) foundNode.getUserData(UI_ELEMENT);
    } catch (XPathExpressionException e) {
      logDomNode();
      throw new ElementNotFoundException(matcherFailMessage(byXPath), e);
    } finally {
      try {
        getDocument().removeChild(getDomNode());
      } catch (DOMException ignored) {
        document = null; // getDocument will create new
      }
    }
  }

  private void logDomNode() {
    if (Logs.DEBUG) {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(getDomNode()), new StreamResult(baos));
        Log.d(Logs.TAG, baos.toString());
      } catch (Exception e) {
        Log.d(Logs.TAG, "fail to transform node", e);
      }
    }
  }

  private Element getDomNode() {
    if (domNode == null) {
      domNode = buildDomNode();
    }
    return domNode;
  }

  // TODO: build the whole tree from root instead of subtree from this element
  // to support searching above context element?
  // TODO: uiautomatorviewer filters out many "insignificant" views. If we want
  // to let users make use of it, we need to do the same filtering
  @LogDesired
  private Element buildDomNode() {
    String className = getClassName();
    if (className == null) {
      className = "UNKNOWN";
    }
    Element element = getDocument().createElement(simpleClassName(className));
    element.setUserData(UI_ELEMENT, this, null /* UserDataHandler */);
    // TODO: add all attrs
    setAttribute(element, "class", className);
    setAttribute(element, "content-desc", getContentDescription());
    setAttribute(element, "text", getText());

    // TODO: visitor pattern
    int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      AbstractUiElement child = getChild(i);
      if (child == null) {
        Log.w(Logs.TAG, "Skip null child for " + toString());
        continue;
      }

      element.appendChild(child.getDomNode());
    }
    return element;
  }

  private static void setAttribute(Element element, String name, String value) {
    if (value != null) {
      element.setAttribute(name, value);
    }
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

  private static Document getDocument() {
    if (document == null) {
      try {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      } catch (ParserConfigurationException e) {
        throw new DroidDriverException(e);
      }
    }
    return document;
  }
}
