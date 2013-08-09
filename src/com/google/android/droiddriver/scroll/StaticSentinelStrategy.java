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

import android.graphics.Rect;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.instrumentation.InstrumentationDriver;
import com.google.android.droiddriver.scroll.Direction.PhysicalToLogicalConverter;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;

/**
 * Determines whether scrolling is possible by checking whether the last child
 * in the logical scroll direction is fully visible. This assumes the count of
 * children is static, and {@link UiElement#getChildren} includes all children
 * no matter if it is visible. Currently {@link InstrumentationDriver} behaves
 * this way.
 * <p>
 * This does not work if a child is larger than the physical size of the parent.
 */
public class StaticSentinelStrategy extends AbstractSentinelStrategy {
  /**
   * Defaults to FIRST_CHILD_GETTER for backward scrolling, LAST_CHILD_GETTER
   * for forward scrolling, and the standard {@link PhysicalToLogicalConverter}.
   */
  public StaticSentinelStrategy() {
    super(FIRST_CHILD_GETTER, LAST_CHILD_GETTER, PhysicalToLogicalConverter.STANDARD_CONVERTER);
  }

  public StaticSentinelStrategy(GetStrategy backwardGetStrategy, GetStrategy forwardGetStrategy,
      PhysicalToLogicalConverter physicalToLogicalConverter) {
    super(backwardGetStrategy, forwardGetStrategy, physicalToLogicalConverter);
  }

  @Override
  public boolean scroll(DroidDriver driver, Finder parentFinder, PhysicalDirection direction) {
    UiElement sentinel = getSentinel(driver, parentFinder, direction);
    UiElement parent = sentinel.getParent();
    // If the last child in the logical scroll direction is fully visible, no
    // more scrolling is possible
    Rect visibleBounds = parent.getVisibleBounds();
    if (visibleBounds.contains(sentinel.getBounds())) {
      return false;
    }

    parent.scroll(direction);
    return true;
  }
}
