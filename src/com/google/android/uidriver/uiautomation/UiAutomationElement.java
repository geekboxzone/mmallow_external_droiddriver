/*
 * Copyright (C) 2013 UiDriver committers
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

package com.google.android.uidriver.uiautomation;

import com.google.android.uidriver.Matcher;
import com.google.android.uidriver.UiElement;
import com.google.android.uidriver.exceptions.ElementNotFoundException;
import com.google.android.uidriver.exceptions.TimeoutException;
import com.google.common.base.Preconditions;

import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * A UiElement that is backed by the UiAutomation object.
 */
public class UiAutomationElement implements UiElement {

  private static final String TAG = UiAutomationDriver.class.getSimpleName();

  private final UiAutomation uiAutomation;
  private final AccessibilityNodeInfo node;

  public UiAutomationElement(UiAutomation uiAutomation, AccessibilityNodeInfo node) {
    this.uiAutomation = Preconditions.checkNotNull(uiAutomation);
    this.node = Preconditions.checkNotNull(node);
  }

  @Override
  public String getText() {
    return node.getText() == null ? null : node.getText().toString();
  }

  @Override
  public void setText(String text) {
    Interactions.sendText(uiAutomation, text);
  }

  @Override
  public String getContentDescription() {
    return node.getContentDescription() == null ? null : node.getContentDescription().toString();
  }

  @Override
  public UiElement findElement(Matcher matcher) {
    int childCount = node.getChildCount();
    Log.d(TAG, "Looping through number of childs " + childCount);
    for (int i = 0; i < childCount; i++) {
      UiElement element = new UiAutomationElement(uiAutomation, node.getChild(i));
      if (matcher.matches(element)) {
        Log.d(TAG, "Found match: " + node.getChild(i));
        return element;
      } else {
        //TODO: Remove since this is spammy, or put behind some debug if block.
        Log.d(TAG, "Not found match: " + node.getChild(i));
        try {
          UiElement foundElement = element.findElement(matcher);
          return foundElement;
        } catch (ElementNotFoundException enfe) {
          // Do nothing.  Continue searching.
        }
      }
    }
    throw new ElementNotFoundException(
        "Could not find any matching element for selector: " + matcher);
  }

  @Override
  public UiElement waitForElement(Matcher matcher) {
    //TODO: Make this configurable
    final int timeoutMillis = 10000;
    final int intervalMillis = 500;
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      try {
        return findElement(matcher);
      } catch (ElementNotFoundException e) {
        // Do nothing.
      }

      if (SystemClock.uptimeMillis() > end) {
        throw new TimeoutException(
            String.format("Timed out after %d milliseconds waiting for element %s",
                timeoutMillis, matcher));
      }
      SystemClock.sleep(intervalMillis);
    }
  }


  @Override
  public void click() {
    // TODO(thanhle): need to find visible bounds.

    Rect nodeRect = new Rect();
    node.getBoundsInScreen(nodeRect);
    Interactions.click(uiAutomation, nodeRect.centerX(), nodeRect.centerY());
  }

  @Override
  public boolean isVisible() {
    return node.isVisibleToUser();
  }
}
