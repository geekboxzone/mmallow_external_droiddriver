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

import com.google.android.droiddriver.exceptions.UnsatisfiedConditionException;
import com.google.android.droiddriver.matchers.Matcher;

/**
 * Interface for polling mechanism.
 */
public interface Poller {
  /**
   * Interface for a callback to be invoked when {@link #pollFor} times out.
   */
  interface TimeoutListener {
    /**
     * Called when {@link #pollFor} times out.
     */
    void onTimeout(DroidDriver driver, Matcher matcher);
  }

  /**
   * Interface for a callback to be invoked when {@link #pollFor} polls.
   */
  interface PollingListener {
    /**
     * Called when {@link #pollFor} polls.
     */
    void onPolling(DroidDriver driver, Matcher matcher);
  }
  /**
   * Interface for removing a listener.
   */
  interface ListenerRemover {
    /**
     * Removes the associated listener.
     */
    void remove();
  }

  /**
   * Used by Poller to check conditions.
   */
  interface ConditionChecker<T> {
    /**
     * Checks condition that overriding methods provide.
     *
     * @throws UnsatisfiedConditionException If the condition is not met
     */
    T check(DroidDriver driver, Matcher matcher);
  }

  /**
   * Polls until {@code checker} does not throw
   * {@link UnsatisfiedConditionException}.
   *
   * @return An object of type T returned by {@code checker}
   */
  <T> T pollFor(DroidDriver driver, Matcher matcher, ConditionChecker<T> checker);

  /**
   * Adds a {@link TimeoutListener}.
   */
  ListenerRemover addListener(TimeoutListener timeoutListener);

  /**
   * Adds a {@link PollingListener}.
   */
  ListenerRemover addListener(PollingListener pollingListener);

  /**
   * Sets timeoutMillis.
   */
  void setTimeoutMillis(int timeoutMillis);

  /**
   * @return timeoutMillis
   */
  int getTimeoutMillis();

  /**
   * Sets intervalMillis.
   */
  void setIntervalMillis(int intervalMillis);

  /**
   * @return intervalMillis
   */
  int getIntervalMillis();
}
