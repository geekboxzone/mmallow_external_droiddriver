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

package com.google.android.uidriver.actions;

import com.google.android.uidriver.Events;
import com.google.android.uidriver.InputInjector;
import com.google.android.uidriver.UiElement;
import com.google.android.uidriver.exceptions.ActionException;

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * An action that does a click on an UiElement.
 */
public class ClickAction implements Action {

  @Override
  public boolean perform(InputInjector injector, UiElement element) {
    Rect elementRect = element.getRect();
    MotionEvent downEvent = Events.newTouchDownEvent(elementRect.centerX(), elementRect.centerY());
    if (!injector.injectInputEvent(downEvent)) {
        throw new ActionException("Failed to inject down event");
    }
    SystemClock.sleep(100);
    return injector.injectInputEvent(Events.newTouchUpEvent(downEvent.getDownTime(),
        elementRect.centerX(), elementRect.centerY()));
  }
}
