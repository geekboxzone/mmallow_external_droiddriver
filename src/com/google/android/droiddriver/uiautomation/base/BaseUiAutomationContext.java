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

package com.google.android.droiddriver.uiautomation.base;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.actions.InputInjector;
import com.google.android.droiddriver.base.DroidDriverContext;
import com.google.android.droiddriver.exceptions.UnrecoverableException;
import com.google.android.droiddriver.finders.ByXPath;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class BaseUiAutomationContext<E extends BaseUiAutomationElement<E>> extends
    DroidDriverContext {
  private final UiAutomation uiAutomation;
  private final BaseUiAutomationDriver<E> driver;
  private final InputInjector injector;
  private final Map<AccessibilityNodeInfo, E> map;

  protected BaseUiAutomationContext(Instrumentation instrumentation,
      BaseUiAutomationDriver<E> driver) {
    super(instrumentation);
    this.uiAutomation = instrumentation.getUiAutomation();
    this.driver = driver;
    this.map = new WeakHashMap<AccessibilityNodeInfo, E>();
    injector = newInputInjector();
  }

  /**
   * Subclasses can override to return a different InputInjector, for example,
   * forbidding MotionEvent in order to detect accessibility issues.
   */
  protected InputInjector newInputInjector() {
    return new UiAutomationInputInjector(this);
  }

  /**
   * Returns a new UiElement of type {@code E}.
   */
  protected abstract E newUiElement(AccessibilityNodeInfo node, E parent);

  @Override
  public BaseUiAutomationDriver<E> getDriver() {
    return driver;
  }

  @Override
  public InputInjector getInjector() {
    return injector;
  }

  E getUiElement(AccessibilityNodeInfo node, E parent) {
    E element = map.get(node);
    if (element == null) {
      element = newUiElement(node, parent);
      map.put(node, element);
    }
    return element;
  }

  @Override
  public void clearData() {
    map.clear();
    ByXPath.clearData();
  }

  /**
   * Wraps calls to UiAutomation API. Currently supports fail-fast if
   * UiAutomation throws IllegalStateException, which occurs when the connection
   * to UiAutomation service is lost.
   */
  public <T> T callUiAutomation(UiAutomationCallable<T> uiAutomationCallable) {
    try {
      return uiAutomationCallable.call(uiAutomation);
    } catch (IllegalStateException e) {
      throw new UnrecoverableException(e);
    }
  }
}
