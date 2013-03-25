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

import android.os.SystemClock;
import android.view.KeyEvent;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.util.Events;

/**
 * An general action to press any of the soft keys on the device.
 */
public class PressKeyAction implements Action {
  private final int keyCode;

  public PressKeyAction(int keyCode) {
    this.keyCode = keyCode;
  }

  @Override
  public boolean perform(InputInjector injector, UiElement element) {
    final long downTime = SystemClock.uptimeMillis();
    KeyEvent downEvent = Events.newKeyEvent(downTime, KeyEvent.ACTION_DOWN, keyCode);
    KeyEvent upEvent = Events.newKeyEvent(downTime, KeyEvent.ACTION_UP, keyCode);

    return injector.injectInputEvent(downEvent) && injector.injectInputEvent(upEvent);
  }
}
