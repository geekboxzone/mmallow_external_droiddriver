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

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.google.android.droiddriver.InputInjector;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ActionException;
import com.google.android.droiddriver.util.Events;

/**
 * An action that does clicks on an UiElement.
 */
public enum ClickAction implements Action {
  SINGLE {
    /** @throws ActionException */
    @Override
    public boolean perform(InputInjector injector, UiElement element) {
      Rect elementRect = element.getRect();
      long downTime = sendDown(injector, elementRect);
      sendUp(injector, elementRect, downTime);
      return true;
    }
  },
  LONG {
    /** @throws ActionException */
    @Override
    public boolean perform(InputInjector injector, UiElement element) {
      Rect elementRect = element.getRect();
      long downTime = sendDown(injector, elementRect);
      // see android.test.TouchUtils - *1.5 to make sure it's long press
      SystemClock.sleep((long) (ViewConfiguration.getLongPressTimeout() * 1.5));
      sendUp(injector, elementRect, downTime);
      return true;
    }
  },
  DOUBLE {
    /** @throws ActionException */
    @Override
    public boolean perform(InputInjector injector, UiElement element) {
      SINGLE.perform(injector, element);
      SINGLE.perform(injector, element);
      return true;
    }
  };

  private static long sendDown(InputInjector injector, Rect elementRect) {
    MotionEvent downEvent = Events.newTouchDownEvent(elementRect.centerX(), elementRect.centerY());
    long downTime = downEvent.getDownTime();
    Events.injectEvent(injector, downEvent);
    return downTime;
  }

  private static void sendUp(InputInjector injector, Rect elementRect, long downTime) {
    MotionEvent upEvent =
        Events.newTouchUpEvent(downTime, elementRect.centerX(), elementRect.centerY());
    Events.injectEvent(injector, upEvent);
  }
}
