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
package com.google.android.droiddriver.matchers;

import com.google.android.droiddriver.UiElement;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class ByText implements Matcher {
  private final String text;

  protected ByText(String text) {
    this.text = Preconditions.checkNotNull(text);
  }

  @Override
  public boolean matches(UiElement element) {
    return text.equals(element.getText());
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).addValue(text).toString();
  }
}
