/*
 * Copyright (C) 2013 UiDriver committers
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

package com.google.android.uidriver.util;

import android.os.SystemClock;

import com.google.android.uidriver.Matcher;
import com.google.android.uidriver.Poller;
import com.google.android.uidriver.UiDriver;
import com.google.android.uidriver.UiElement;
import com.google.android.uidriver.exceptions.ElementNotFoundException;
import com.google.android.uidriver.exceptions.TimeoutException;
import com.google.android.uidriver.exceptions.UnsatisfiedConditionException;
import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Default implementation of a {@link Poller}.
 */
public class DefaultPoller implements Poller {
  private final Collection<TimeoutListener> timeoutListeners = Lists.newLinkedList();
  private final Collection<PollingListener> pollingListeners = Lists.newLinkedList();
  private int timeoutMillis = 10000;
  private int intervalMillis = 500;

  @Override
  public int getIntervalMillis() {
    return intervalMillis;
  }

  @Override
  public void setIntervalMillis(int intervalMillis) {
    this.intervalMillis = intervalMillis;
  }

  @Override
  public int getTimeoutMillis() {
    return timeoutMillis;
  }

  @Override
  public void setTimeoutMillis(int timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }

  @Override
  public <T> T pollFor(UiDriver driver, Matcher matcher, ConditionChecker<T> checker) {
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      try {
        return checker.check(driver, matcher);
      } catch (UnsatisfiedConditionException e) {
        // fall through to poll
      }

      if (SystemClock.uptimeMillis() > end) {
        for (TimeoutListener timeoutListener : timeoutListeners) {
          timeoutListener.onTimeout(driver, matcher);
        }
        throw new TimeoutException(String.format(
            "Timed out after %d milliseconds waiting for element %s", timeoutMillis, matcher));
      }
      for (PollingListener pollingListener : pollingListeners) {
        pollingListener.onPolling(driver, matcher);
      }
      SystemClock.sleep(intervalMillis);
    }
  }

  @Override
  public ListenerRemover addListener(final TimeoutListener timeoutListener) {
    timeoutListeners.add(timeoutListener);
    return new ListenerRemover() {
      @Override
      public void remove() {
        timeoutListeners.remove(timeoutListener);
      }
    };
  }

  @Override
  public ListenerRemover addListener(final PollingListener pollingListener) {
    pollingListeners.add(pollingListener);
    return new ListenerRemover() {
      @Override
      public void remove() {
        pollingListeners.remove(pollingListener);
      }
    };
  }

  public static class ExistsChecker implements ConditionChecker<UiElement> {
    @Override
    public UiElement check(UiDriver driver, Matcher matcher) {
      try {
        return driver.getRootElement().findElement(matcher);
      } catch (ElementNotFoundException e) {
        throw new UnsatisfiedConditionException("");
      }
    }
  }

  public static class GoneChecker implements ConditionChecker<Void> {
    @Override
    public Void check(UiDriver driver, Matcher matcher) {
      try {
        driver.getRootElement().findElement(matcher);
        throw new UnsatisfiedConditionException("");
      } catch (ElementNotFoundException e) {
        return null;
      }
    }
  }
}
