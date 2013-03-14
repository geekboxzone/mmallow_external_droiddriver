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
import com.google.android.droiddriver.Matcher;
import com.google.android.droiddriver.Poller;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.util.ConditionCheckers;
import com.google.android.droiddriver.util.DefaultPoller;

/**
 * Abstract implementation of DroidDriver that does the common actions, and should not
 * differ in implementations of {@link DroidDriver}.
 */
public abstract class AbstractDroidDriver implements DroidDriver {

  private Poller poller;

  public AbstractDroidDriver() {
    this.poller = new DefaultPoller();
  }

  @Override
  public UiElement waitForElement(Matcher matcher) {
    return getPoller().pollFor(this, matcher, ConditionCheckers.EXISTS_CHECKER);
  }

  @Override
  public void waitUntilGone(Matcher matcher) {
    getPoller().pollFor(this, matcher, ConditionCheckers.GONE_CHECKER);
  }

  @Override
  public Poller getPoller() {
    return poller;
  }

  @Override
  public void setPoller(Poller poller) {
    this.poller = poller;
  }
}
