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
import com.google.android.uidriver.UiDriver;
import com.google.android.uidriver.UiElement;
import com.google.android.uidriver.exceptions.ElementNotFoundException;
import com.google.android.uidriver.exceptions.TimeoutException;
import com.google.common.base.Preconditions;

import android.app.UiAutomation;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Implementation of a UiDriver that is driven via the accessibility layer.
 */
public class UiAutomationDriver implements UiDriver {

  private final UiAutomation uiAutomation;

  public UiAutomationDriver(UiAutomation uiAutomation) {
    this.uiAutomation = Preconditions.checkNotNull(uiAutomation);
  }

  @Override
  public UiElement waitForElement(Matcher matcher) {
    // TODO: Make this configurable
    final int timeoutMillis = 10000;
    final int intervalMillis = 500;
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      UiElement root = UiAutomationDrivers.newUiAutomationElement(uiAutomation, getRootNode());
      try {
        return root.findElement(matcher);
      } catch (ElementNotFoundException e) {
        // Do nothing.
      }

      if (SystemClock.uptimeMillis() > end) {
        throw new TimeoutException(String.format(
            "Timed out after %d milliseconds waiting for element %s", timeoutMillis, matcher));
      }
      SystemClock.sleep(intervalMillis);
    }
  }

  private AccessibilityNodeInfo getRootNode() {
    for (int i = 0; i < 3; i++) {
      AccessibilityNodeInfo root = uiAutomation.getRootInActiveWindow();
      if (root != null) {
        return root;
      }
      SystemClock.sleep(250);
    }
    throw new ElementNotFoundException("Could not find root node!");
  }
}
