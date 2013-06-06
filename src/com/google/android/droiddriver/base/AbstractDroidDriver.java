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

package com.google.android.droiddriver.base;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.Poller;
import com.google.android.droiddriver.Poller.ConditionChecker;
import com.google.android.droiddriver.Poller.UnsatisfiedConditionException;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.TimeoutException;
import com.google.android.droiddriver.finders.ByXPath;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.util.DefaultPoller;
import com.google.android.droiddriver.util.Logs;

/**
 * Abstract implementation of DroidDriver that does the common actions, and
 * should not differ in implementations of {@link DroidDriver}.
 */
public abstract class AbstractDroidDriver implements DroidDriver {

  /**
   * A ConditionChecker that does not throw only if the matching
   * {@link UiElement} is gone.
   */
  private static final ConditionChecker<Void> GONE = new ConditionChecker<Void>() {
    @Override
    public Void check(DroidDriver driver, Finder finder) throws UnsatisfiedConditionException {
      if (driver.has(finder)) {
        throw new UnsatisfiedConditionException();
      }
      return null;
    }

    @Override
    public String toString() {
      return "to disappear";
    }
  };
  /**
   * A ConditionChecker that returns the matching {@link UiElement}.
   */
  private static final ConditionChecker<UiElement> EXISTS = new ConditionChecker<UiElement>() {
    @Override
    public UiElement check(DroidDriver driver, Finder finder)
        throws UnsatisfiedConditionException {
      try {
        return ((AbstractDroidDriver) driver).find(finder);
      } catch (ElementNotFoundException e) {
        throw new UnsatisfiedConditionException();
      }
    }

    @Override
    public String toString() {
      return "to appear";
    }
  };

  private Poller poller = new DefaultPoller();

  /**
   * For internal use.
   *
   * @param finder
   * @return the matching element without polling
   * @throws ElementNotFoundException
   */
  public UiElement find(Finder finder) {
    Logs.call(this, "find", finder);
    return finder.find(getRootElement());
  }

  @Override
  public boolean has(Finder finder) {
    try {
      find(finder);
      return true;
    } catch (ElementNotFoundException enfe) {
      return false;
    }
  }

  @Override
  public boolean has(Finder finder, long timeoutMillis) {
    try {
      checkExists(finder, timeoutMillis);
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  @Override
  public void checkExists(Finder finder) {
    checkExists(finder, getPoller().getTimeoutMillis());
  }

  @Override
  public void checkExists(Finder finder, long timeoutMillis) {
    Logs.call(this, "checkExists", finder, timeoutMillis);
    getPoller().pollFor(this, finder, EXISTS, timeoutMillis);
  }

  @Override
  public UiElement on(Finder finder) {
    Logs.call(this, "on", finder);
    return getPoller().pollFor(this, finder, EXISTS);
  }

  @Override
  public void checkGone(Finder finder) {
    checkGone(finder, getPoller().getTimeoutMillis());
  }

  @Override
  public void checkGone(Finder finder, long timeoutMillis) {
    Logs.call(this, "checkGone", finder, timeoutMillis);
    getPoller().pollFor(this, finder, GONE, timeoutMillis);
  }

  @Override
  public Poller getPoller() {
    return poller;
  }

  @Override
  public void setPoller(Poller poller) {
    this.poller = poller;
  }

  protected abstract AbstractUiElement getRootElement();

  @Override
  public boolean dumpUiElementTree(String path) {
    Logs.call(this, "dumpUiElementTree", path);
    return ByXPath.dumpDom(path, getRootElement());
  }
}
