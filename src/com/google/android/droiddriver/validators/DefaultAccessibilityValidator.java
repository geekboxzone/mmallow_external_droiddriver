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

package com.google.android.droiddriver.validators;

import android.text.TextUtils;

import com.google.android.droiddriver.UiElement;

/**
 * Validates accessibility.
 */
// TODO: Treats various types of UiElement as TalkBack does.
public class DefaultAccessibilityValidator implements Validator {
  @Override
  public boolean isValid(UiElement element) {
    return element.getParent() != null // don't check root
        && TextUtils.isEmpty(element.getContentDescription())
        && TextUtils.isEmpty(element.getText());
  }
}
