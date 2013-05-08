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

package com.google.android.droiddriver.actions;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.UiElement;

/**
 * Interface for performing action on a UiElement.
 */
public interface Action {
  /**
   * Performs the action.
   *
   * @param injector the injector to inject input events
   * @param element the Ui element to perform the action on
   * @return Whether the action is successful. Some actions throw exceptions in
   *         case of failure, when that behavior is more appropriate. For
   *         example, ClickAction.
   */
  boolean perform(InputInjector injector, UiElement element);
}
