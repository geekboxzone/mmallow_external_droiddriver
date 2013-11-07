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

package com.google.android.droiddriver.uiautomation;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.view.InputEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.actions.InputInjector;
import com.google.android.droiddriver.base.DroidDriverContext;
import com.google.android.droiddriver.finders.ByXPath;
import com.google.common.collect.MapMaker;

import java.util.Map;

class UiAutomationContext implements DroidDriverContext {
  private final Map<AccessibilityNodeInfo, UiAutomationElement> map = new MapMaker().weakKeys()
      .weakValues().makeMap();
  private final UiAutomation uiAutomation;
  private final Instrumentation instrumentation;
  private final UiAutomationDriver driver;
  private final InputInjector injector;

  UiAutomationContext(final Instrumentation instrumentation, UiAutomationDriver driver) {
    this.instrumentation = instrumentation;
    this.uiAutomation = instrumentation.getUiAutomation();
    this.driver = driver;
    this.injector = new InputInjector() {
      @Override
      public boolean injectInputEvent(InputEvent event) {
        return uiAutomation.injectInputEvent(event, true /* sync */);
      }
    };
  }

  @Override
  public Instrumentation getInstrumentation() {
    return instrumentation;
  }

  @Override
  public UiAutomationDriver getDriver() {
    return driver;
  }

  @Override
  public InputInjector getInjector() {
    return injector;
  }

  public UiAutomationElement getUiElement(AccessibilityNodeInfo node, UiAutomationElement parent) {
    UiAutomationElement element = map.get(node);
    if (element == null) {
      element = new UiAutomationElement(this, node, parent);
      map.put(node, element);
    }
    return element;
  }

  @Override
  public void clearData() {
    map.clear();
    ByXPath.clearData();
  }

  public UiAutomation getUiAutomation() {
    return uiAutomation;
  }
}
