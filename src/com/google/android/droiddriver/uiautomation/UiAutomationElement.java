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

import static com.google.android.droiddriver.util.TextUtils.charSequenceToString;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.base.AbstractUiElement;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A UiElement that is backed by the UiAutomation object.
 */
public class UiAutomationElement extends AbstractUiElement {

  private final UiAutomationContext context;
  private final AccessibilityNodeInfo node;

  public UiAutomationElement(UiAutomationContext context, AccessibilityNodeInfo node) {
    this.context = Preconditions.checkNotNull(context);
    this.node = Preconditions.checkNotNull(node);
  }

  @Override
  public String getText() {
    return charSequenceToString(node.getText());
  }

  @Override
  public String getContentDescription() {
    return charSequenceToString(node.getContentDescription());
  }

  @Override
  public String getClassName() {
    return charSequenceToString(node.getClassName());
  }

  @Override
  public String getResourceId() {
    return charSequenceToString(node.getViewIdResourceName());
  }

  @Override
  public String getPackageName() {
    return charSequenceToString(node.getPackageName());
  }

  @Override
  public InputInjector getInjector() {
    return context.getInjector();
  }

  @Override
  public boolean isVisible() {
    return node.isVisibleToUser();
  }

  @Override
  public boolean isCheckable() {
    return node.isCheckable();
  }

  @Override
  public boolean isChecked() {
    return node.isChecked();
  }

  @Override
  public boolean isClickable() {
    return node.isClickable();
  }

  @Override
  public boolean isEnabled() {
    return node.isEnabled();
  }

  @Override
  public boolean isFocusable() {
    return node.isFocusable();
  }

  @Override
  public boolean isFocused() {
    return node.isFocused();
  }

  @Override
  public boolean isScrollable() {
    return node.isScrollable();
  }

  @Override
  public boolean isLongClickable() {
    return node.isLongClickable();
  }

  @Override
  public boolean isPassword() {
    return node.isPassword();
  }

  @Override
  public boolean isSelected() {
    return node.isSelected();
  }

  @Override
  public Rect getRect() {
    Rect rect = new Rect();
    node.getBoundsInScreen(rect);
    return rect;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("node", node).toString();
  }

  @Override
  protected int getChildCount() {
    return node.getChildCount();
  }

  @Override
  protected UiAutomationElement getChild(int index) {
    AccessibilityNodeInfo child = node.getChild(index);
    return child == null ? null : context.getUiElement(child);
  }
}
