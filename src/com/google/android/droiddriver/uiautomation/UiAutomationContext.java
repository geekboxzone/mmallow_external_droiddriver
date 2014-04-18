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
import com.google.android.droiddriver.exceptions.UnrecoverableException;
import com.google.android.droiddriver.finders.ByXPath;

import java.util.Map;
import java.util.WeakHashMap;

class UiAutomationContext extends DroidDriverContext {
  private final Map<AccessibilityNodeInfo, UiAutomationElement> map =
      new WeakHashMap<AccessibilityNodeInfo, UiAutomationElement>();
  private final UiAutomation uiAutomation;
  private final InputInjector injector;
  private final UiAutomationDriver driver;

  UiAutomationContext(final Instrumentation instrumentation, UiAutomationDriver driver) {
    super(instrumentation);
    this.uiAutomation = instrumentation.getUiAutomation();
    this.driver = driver;
    this.injector = new InputInjector() {
      @Override
      public boolean injectInputEvent(final InputEvent event) {
        return callUiAutomation(new UiAutomationCallable<Boolean>() {
          @Override
          public Boolean call(UiAutomation uiAutomation) {
            return uiAutomation.injectInputEvent(event, true /* sync */);
          }
        });
      }
    };
  }

  @Override
  public UiAutomationDriver getDriver() {
    return driver;
  }

  @Override
  public InputInjector getInjector() {
    return injector;
  }

  UiAutomationElement getUiElement(AccessibilityNodeInfo node, UiAutomationElement parent) {
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

  interface UiAutomationCallable<T> {
    T call(UiAutomation uiAutomation);
  }

  /*
   * Wraps calls to UiAutomation API. Currently supports fail-fast if
   * UiAutomation throws IllegalStateException, which occurs when the connection
   * to UiAutomation service is lost.
   */
  <T> T callUiAutomation(UiAutomationCallable<T> uiAutomationCallable) {
    try {
      return uiAutomationCallable.call(uiAutomation);
    } catch (IllegalStateException e) {
      throw new UnrecoverableException(e);
    }
  }
}
