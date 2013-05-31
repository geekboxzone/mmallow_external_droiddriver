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

package com.google.android.droiddriver;

import com.google.android.droiddriver.exceptions.TimeoutException;
import com.google.android.droiddriver.matchers.Matcher;

public interface DroidDriver {
  /**
   * Returns whether a matching element exists without polling.
   */
  boolean has(Matcher matcher);

  /**
   * Returns the first {@link UiElement} that matches the given matcher. This
   * method will poll until a match is found, or the timeout is reached.
   *
   * @param matcher The matching mechanism
   * @return The first matching element
   * @throws TimeoutException If no matching elements are found within the
   *         allowed time
   */
  UiElement on(Matcher matcher);


  /**
   * Polls until the {@link UiElement} that matches the given matcher is gone.
   * This method will poll until matching element is gone, or the timeout is
   * reached.
   *
   * @param matcher The matching mechanism
   * @throws TimeoutException If matching element is not gone within the allowed
   *         time
   */
  void checkGone(Matcher matcher);

  /**
   * Returns the {@link Poller}.
   */
  Poller getPoller();

  /**
   * Sets the {@link Poller}.
   */
  void setPoller(Poller poller);

  /**
   * Dumps the UiElement tree to a file to help debug.
   *
   * @param path the path of file to save the tree
   * @return whether the dumping succeeded
   */
  boolean dumpUiElementTree(String path);
}
