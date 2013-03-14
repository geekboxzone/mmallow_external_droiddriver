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

package com.google.android.droiddriver.instrumentation;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.Matcher;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.base.AbstractUiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.util.Logs;
import com.google.android.droiddriver.util.TextUtils;

import android.app.Instrumentation;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A UiElement that is backed by a View.
 */
public class ViewElement extends AbstractUiElement {

  private final Instrumentation instrumentation;
  private final View view;
  private final InputInjector injector;

  public ViewElement(Instrumentation instrumentation, View view) {
    this.instrumentation = instrumentation;
    this.view = view;
    this.injector = new InstrumentationInputInjector(instrumentation);
  }

  @Override
  public UiElement findElement(Matcher matcher) {
    if (!(view instanceof ViewGroup)) {
      throw new ElementNotFoundException("Could not find any matching element for selector: "
          + matcher);
    }
    ViewGroup viewGroup = (ViewGroup) view;
    int childCount = viewGroup.getChildCount();
    Log.d(Logs.TAG, "Looping through number of childs " + childCount);
    for (int i = 0; i < childCount; i++) {
      View childView = viewGroup.getChildAt(i);
      UiElement element = new ViewElement(instrumentation, childView);
      Log.d(Logs.TAG, "Child text " + element.getText());
      if (matcher.matches(element)) {
        Log.d(Logs.TAG, "Found match: " + childView);
        return element;
      } else {
        try {
          return element.findElement(matcher);
        } catch (ElementNotFoundException enfe) {
          // Do nothing. Continue searching.
        }
      }
    }
    throw new ElementNotFoundException("Could not find any matching element for selector: "
        + matcher);
  }

  @Override
  public String getText() {
    if (!(view instanceof TextView)) {
      return null;
    }
    return TextUtils.charSequenceToString(((TextView) view).getText());
  }

  @Override
  public String getContentDescription() {
    return TextUtils.charSequenceToString(view.getContentDescription());
  }

  @Override
  public String getClassName() {
    return view.getClass().getCanonicalName();
  }

  @Override
  public boolean perform(Action action) {
    return action.perform(injector, this);
  }

  @Override
  public boolean isVisible() {
    return view.getGlobalVisibleRect(new Rect());
  }

  @Override
  public Rect getRect() {
    Rect rect = new Rect();
    int[] xy = new int[2];
    view.getLocationOnScreen(xy);
    rect.set(xy[0], xy[1], xy[0] + view.getWidth(), xy[1] + view.getHeight());
    return rect;
  }
}
