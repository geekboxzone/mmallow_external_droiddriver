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
import com.google.android.droiddriver.matchers.ByXPath;
import com.google.android.droiddriver.matchers.Matcher;
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
    public Void check(DroidDriver driver, Matcher matcher) throws UnsatisfiedConditionException {
      if (driver.has(matcher)) {
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
    public UiElement check(DroidDriver driver, Matcher matcher)
        throws UnsatisfiedConditionException {
      try {
        return matcher.find(((AbstractDroidDriver) driver).getRootElement());
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

  @Override
  public boolean has(Matcher matcher) {
    Logs.call(this, "has", matcher);
    try {
      matcher.find(getRootElement());
      return true;
    } catch (ElementNotFoundException enfe) {
      return false;
    }

  }

  @Override
  public UiElement on(Matcher matcher) {
    Logs.call(this, "on", matcher);
    return getPoller().pollFor(this, matcher, EXISTS);
  }

  @Override
  public void checkGone(Matcher matcher) {
    Logs.call(this, "checkGone", matcher);
    getPoller().pollFor(this, matcher, GONE);
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
