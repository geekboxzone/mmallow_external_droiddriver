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

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.actions.ClickAction;
import com.google.android.droiddriver.actions.ScrollDirection;
import com.google.android.droiddriver.actions.SwipeAction;
import com.google.android.droiddriver.actions.TypeAction;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.ElementNotVisibleException;
import com.google.android.droiddriver.matchers.Attribute;
import com.google.android.droiddriver.matchers.ByXPath;
import com.google.android.droiddriver.matchers.ElementMatcher;
import com.google.android.droiddriver.matchers.Matcher;
import com.google.android.droiddriver.matchers.XPaths;
import com.google.android.droiddriver.util.Logs;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;

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

  @Override
  public <T> T get(Attribute attribute) {
    return attribute.getValue(this);
  }

  @Override
  public boolean perform(Action action) {
    Logs.call(this, "perform", action);
    checkVisible();
    return action.perform(getInjector(), this);
  }

  @Override
  public void setText(String text) {
    // TODO: Define common actions as a const.
    perform(new TypeAction(text));
    if (Logs.DEBUG) {
      String actual = getText();
      if (!text.equals(actual)) {
        throw new DroidDriverException(String.format(
            "setText failed: expected=\"%s\", actual=\"%s\"", text, actual));
      }
    }
  }

  @Override
  public void click() {
    perform(ClickAction.SINGLE);
  }

  @Override
  public void longClick() {
    perform(ClickAction.LONG);
  }

  @Override
  public void doubleClick() {
    perform(ClickAction.DOUBLE);
  }

  @Override
  public void scroll(ScrollDirection direction) {
    perform(new SwipeAction(direction, false));
  }

  @Override
  public abstract AbstractUiElement getChild(int index);

  protected abstract InputInjector getInjector();

  private void checkVisible() {
    if (!isVisible()) {
      throw new ElementNotVisibleException("Element is not visible on screen");
    }
  }

  @Override
  public boolean hasElement(Matcher matcher) {
    try {
      findElement(matcher);
      return true;
    } catch (ElementNotFoundException enfe) {
      return false;
    }
  }

  @Override
  public UiElement findElement(Matcher matcher) {
    Logs.call(this, "findElement", matcher);
    if (matcher instanceof ByXPath) {
      return findByXPath((ByXPath) matcher);
    }
    if (matcher instanceof ElementMatcher) {
      return findByElement((ElementMatcher) matcher);
    }
    throw new DroidDriverException("Unsupported Matcher type: " + matcher.getClass());
  }

  private UiElement findByElement(ElementMatcher matcher) {
    if (matcher.matches(this)) {
      Log.d(Logs.TAG, "Found match: " + toString());
      return this;
    }
    int childCount = getChildCount();
    Log.d(Logs.TAG, "Looping through number of children " + childCount);
    for (int i = 0; i < childCount; i++) {
      UiElement child = getChild(i);
      if (child == null) {
        Log.w(Logs.TAG, "Skip null child for " + toString());
        continue;
      }
      try {
        return child.findElement(matcher);
      } catch (ElementNotFoundException enfe) {
        // Do nothing. Continue searching.
      }
    }
    throw new ElementNotFoundException(matcherFailMessage(matcher));
  }

  private static String matcherFailMessage(Matcher matcher) {
    return "Could not find any element matching " + matcher;
  }

  private UiElement findByXPath(ByXPath byXPath) {
    try {
      getDocument().appendChild(getDomNode());
      Element foundNode =
          (Element) byXPath.getXPathExpression().evaluate(getDomNode(), XPathConstants.NODE);
      if (foundNode == null) {
        throw new XPathExpressionException("XPath evaluation returns null");
      }
      return (UiElement) foundNode.getUserData(UI_ELEMENT);
    } catch (XPathExpressionException e) {
      logDomNode(byXPath);
      throw new ElementNotFoundException(matcherFailMessage(byXPath), e);
    } finally {
      try {
        getDocument().removeChild(getDomNode());
      } catch (DOMException ignored) {
        document = null; // getDocument will create new
      }
    }
  }

  private void logDomNode(ByXPath byXPath) {
    if (Logs.DEBUG) {
      // logcat has a limit (4076b), so write to a file
      FileOutputStream fos = null;
      try {
        File domFile = File.createTempFile("dom", ".xml");
        domFile.setReadable(true /* readable */, false/* ownerOnly */);
        fos = new FileOutputStream(domFile);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(getDomNode()), new StreamResult(fos));
        Log.d(Logs.TAG, "Wrote dom for " + byXPath + " to " + domFile.getCanonicalPath());
      } catch (Exception e) {
        Log.d(Logs.TAG, "Fail to transform node", e);
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (Exception e) {
            // ignore
          }
        }
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
  private Element buildDomNode() {
    Logs.call(this, "buildDomNode");
    String className = getClassName();
    if (className == null) {
      className = "UNKNOWN";
    }
    Element element = getDocument().createElement(XPaths.tag(className));
    element.setUserData(UI_ELEMENT, this, null /* UserDataHandler */);

    setAttribute(element, Attribute.CLASS, className);
    setAttribute(element, Attribute.RESOURCE_ID, getResourceId());
    setAttribute(element, Attribute.PACKAGE, getPackageName());
    setAttribute(element, Attribute.CONTENT_DESC, getContentDescription());
    setAttribute(element, Attribute.TEXT, getText());
    setAttribute(element, Attribute.CHECKABLE, isCheckable());
    setAttribute(element, Attribute.CHECKED, isChecked());
    setAttribute(element, Attribute.CLICKABLE, isClickable());
    setAttribute(element, Attribute.ENABLED, isEnabled());
    setAttribute(element, Attribute.FOCUSABLE, isFocusable());
    setAttribute(element, Attribute.FOCUSED, isFocused());
    setAttribute(element, Attribute.SCROLLABLE, isScrollable());
    setAttribute(element, Attribute.LONG_CLICKABLE, isLongClickable());
    setAttribute(element, Attribute.PASSWORD, isPassword());
    setAttribute(element, Attribute.SELECTED, isSelected());

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

  private static void setAttribute(Element element, Attribute attr, String value) {
    if (value != null) {
      element.setAttribute(attr.getName(), value);
    }
  }

  // add attribute only if it's true
  private static void setAttribute(Element element, Attribute attr, boolean value) {
    if (value) {
      element.setAttribute(attr.getName(), "");
    }
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
