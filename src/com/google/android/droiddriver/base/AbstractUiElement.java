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
import com.google.android.droiddriver.matchers.Matcher;
import com.google.android.droiddriver.util.Logs;

import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Abstract implementation with common methods already implemented.
 */
public abstract class AbstractUiElement implements UiElement {
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
    return matcher.find(this);
  }

  @Override
  public boolean dumpDom(String nodeDescription) {
    // logcat has a per-entry limit (4076b), so write to a file
    FileOutputStream fos = null;
    try {
      File domFile = File.createTempFile("dom", ".xml");
      domFile.setReadable(true /* readable */, false/* ownerOnly */);
      fos = new FileOutputStream(domFile);
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(new DOMSource(getDomNode()), new StreamResult(fos));
      Log.i(Logs.TAG, "Wrote dom for " + nodeDescription + " to " + domFile.getCanonicalPath());
    } catch (Exception e) {
      Log.e(Logs.TAG, "Fail to transform node", e);
      return false;
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (Exception e) {
          // ignore
        }
      }
    }
    return true;
  }

  /**
   * Used internally in {@link ByXPath}.
   */
  public Element getDomNode() {
    if (domNode == null) {
      domNode = ByXPath.buildDomNode(this);
    }
    return domNode;
  }
}
