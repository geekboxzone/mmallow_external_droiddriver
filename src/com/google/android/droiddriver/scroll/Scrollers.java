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

package com.google.android.droiddriver.scroll;

import android.app.UiAutomation;

import com.google.android.droiddriver.scroll.Direction.DirectionConverter;

/**
 * Static utility methods pertaining to {@link Scroller} instances.
 */
public class Scrollers {
  /**
   * Returns a new default Scroller that works in simple cases. In complex cases
   * you may try a {@link StepBasedScroller} with a custom
   * {@link ScrollStepStrategy}:
   * <ul>
   * <li>If the Scroller is used with InstrumentationDriver,
   * StaticSentinelStrategy may work and it's the simplest.</li>
   * <li>Otherwise, DynamicSentinelStrategy should work in all cases, including
   * the case of dynamic list, which shows more items when scrolling beyond the
   * end. On the other hand, it's complex and needs more configuration.</li>
   * </ul>
   */
  public static Scroller newScroller(UiAutomation uiAutomation) {
    if (uiAutomation != null) {
      return new StepBasedScroller(new AccessibilityEventScrollStepStrategy(uiAutomation, 1000L,
          DirectionConverter.STANDARD_CONVERTER));
    }
    // TODO: A {@link Scroller} that directly jumps to the view if an
    // InstrumentationDriver is used.
    return new StepBasedScroller(StaticSentinelStrategy.DEFAULT);
  }
}
