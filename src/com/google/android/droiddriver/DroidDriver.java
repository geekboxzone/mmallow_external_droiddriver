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
import com.google.android.droiddriver.finders.Finder;

public interface DroidDriver {
  /**
   * Returns whether a matching element exists without polling.
   */
  boolean has(Finder finder);

  /**
   * Returns whether a matching element exists within up to
   * {@code timeoutMillis}.
   */
  boolean has(Finder finder, long timeoutMillis);

  /**
   * Polls until a {@link UiElement} is found using the given finder, or the
   * default timeout is reached.
   *
   * @param finder The matching mechanism
   * @throws TimeoutException If matching element does not appear within the
   *         default timeout
   */
  void checkExists(Finder finder);

  /**
   * Polls until a {@link UiElement} is found using the given finder, or
   * {@code timeoutMillis} is reached.
   *
   * @param finder The matching mechanism
   * @param timeoutMillis The ad-hoc timeout for this method
   * @throws TimeoutException If matching element does not appear within
   *         {@code timeoutMillis}
   */
  void checkExists(Finder finder, long timeoutMillis);

  /**
   * Returns the first {@link UiElement} found using the given finder. This
   * method will poll until a match is found, or the default timeout is reached.
   * <p>
   * Users are advised not to store the returned instance for later use -- the
   * instance could be short-lived and become stale.
   *
   * @param finder The matching mechanism
   * @return The first matching element
   * @throws TimeoutException If no matching elements are found within the
   *         allowed time
   */
  UiElement on(Finder finder);

  /**
   * Returns the first {@link UiElement} found using the given finder without
   * polling. This method is useful in {@link Poller.PollingListener#onPolling}.
   * In other situations polling is desired, and {@link #on} is more
   * appropriate.
   *
   * @param finder The matching mechanism
   * @return The first matching element
   * @throws ElementNotFoundException If no matching elements are found
   */
  UiElement find(Finder finder);

  /**
   * Polls until the {@link UiElement} found using the given finder is gone, or
   * the default timeout is reached.
   *
   * @param finder The matching mechanism
   * @throws TimeoutException If matching element is not gone within the default
   *         timeout
   */
  void checkGone(Finder finder);

  /**
   * Polls until the {@link UiElement} found using the given finder is gone, or
   * {@code timeoutMillis} is reached.
   *
   * @param finder The matching mechanism
   * @param timeoutMillis The ad-hoc timeout for this method
   * @throws TimeoutException If matching element is not gone within
   *         {@code timeoutMillis}
   */
  void checkGone(Finder finder, long timeoutMillis);

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
