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

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.base.AbstractUiElement;
import com.google.android.droiddriver.util.TextUtils;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A UiElement that is backed by a View.
 */
public class ViewElement extends AbstractUiElement {
  private final InstrumentationContext context;
  private final View view;

  public ViewElement(InstrumentationContext context, View view) {
    this.context = Preconditions.checkNotNull(context);
    this.view = Preconditions.checkNotNull(view);
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
    return action.perform(context.getInjector(), this);
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

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("view", view).toString();
  }

  @Override
  protected int getChildCount() {
    if (!(view instanceof ViewGroup)) {
      return 0;
    }
    return ((ViewGroup) view).getChildCount();
  }

  @Override
  protected ViewElement getChild(int index) {
    if (!(view instanceof ViewGroup)) {
      return null;
    }
    View child = ((ViewGroup) view).getChildAt(index);
    return child == null ? null : context.getUiElement(child);
  }
}
