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

public interface Matcher {
  /**
   * Returns true if the UiElement matches the implementing matcher. The
   * implementing matcher should return quickly.
   *
   * @param element The element to validate against
   * @return true if the element matches
   */
  boolean matches(UiElement element);
}
