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

import android.app.Service;
import android.os.PowerManager;
import android.view.KeyEvent;

import com.google.android.droiddriver.UiDevice;
import com.google.android.droiddriver.actions.Action;
import com.google.android.droiddriver.actions.PressKeyAction;

/**
 * Base implementation of {@link UiDevice}.
 */
public class BaseUiDevice implements UiDevice {
  // power off may not trigger new events
  private static final PressKeyAction POWER_OFF = new PressKeyAction(KeyEvent.KEYCODE_POWER, 0,
      false);
  // power on should always trigger new events
  private static final PressKeyAction POWER_ON = new PressKeyAction(KeyEvent.KEYCODE_POWER, 1000L,
      false);

  private final AbstractContext abstractContext;

  public BaseUiDevice(AbstractContext abstractContext) {
    this.abstractContext = abstractContext;
  }

  @Override
  public boolean isScreenOn() {
    PowerManager pm =
        (PowerManager) abstractContext.instrumentation.getTargetContext().getSystemService(
            Service.POWER_SERVICE);
    return pm.isScreenOn();
  }

  @Override
  public void wakeUp() {
    if (!isScreenOn()) {
      perform(POWER_ON);
    }
  }

  @Override
  public void sleep() {
    if (isScreenOn()) {
      perform(POWER_OFF);
    }
  }

  @Override
  public boolean perform(Action action) {
    return abstractContext.driver.getRootElement().perform(action);
  }
}
