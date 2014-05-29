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

package com.google.android.droiddriver.uiautomation.accessibility;

import android.app.Instrumentation;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.actions.InputInjector;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.uiautomation.base.BaseUiAutomationContext;
import com.google.android.droiddriver.uiautomation.base.UiAutomationInputInjector;

class AccessibilityContext extends BaseUiAutomationContext<AccessibilityElement> {
  AccessibilityContext(Instrumentation instrumentation, AccessibilityDriver driver) {
    super(instrumentation, driver);
  }

  @Override
  protected InputInjector newInputInjector() {
    return new UiAutomationInputInjector(this) {
      @Override
      public boolean injectInputEvent(InputEvent event) {
        if (event instanceof MotionEvent) {
          throw new DroidDriverException(
              "AccessibilityDriver forbids MotionEvent in order to detect accessibility issues");
        }
        return super.injectInputEvent(event);
      }
    };
  }

  @Override
  protected AccessibilityElement newUiElement(AccessibilityNodeInfo node,
      AccessibilityElement parent) {
    return new AccessibilityElement(this, node, parent);
  }
}
