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

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.actions.accessibility.AccessibilityUiElementActor;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.uiautomation.base.BaseUiAutomationElement;

/**
 * A UiElement that gets attributes via the Accessibility API and is acted upon
 * via the Accessibility API.
 */
class AccessibilityElement extends BaseUiAutomationElement<AccessibilityElement> {
  AccessibilityElement(AccessibilityContext context, AccessibilityNodeInfo node,
      AccessibilityElement parent) {
    super(context, node, parent, AccessibilityUiElementActor.INSTANCE);
  }

  @Override
  public boolean perform(Action action) {
    checkAccessible();
    return super.perform(action);
  }

  private void checkAccessible() {
    if (getParent() != null // don't check root
        && TextUtils.isEmpty(this.getContentDescription()) && TextUtils.isEmpty(this.getText())) {
      throw new DroidDriverException(
          "Accessibility issue: either content description or text must be set for actionable"
              + " user interface controls");
    }
  }
}
