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
  public UiElement findElement(Matcher matcher) {
    UiElement element = UiAutomationDrivers.newUiAutomationElement(uiAutomation, getRootNode());
    return element.findElement(matcher);
  }

  @Override
  public UiElement waitForElement(Matcher matcher) {
    UiElement element = UiAutomationDrivers.newUiAutomationElement(uiAutomation, getRootNode());
    return element.waitForElement(matcher);
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
