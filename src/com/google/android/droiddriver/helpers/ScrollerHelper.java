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

package com.google.android.droiddriver.helpers;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Scroller;

/**
 * Helper for Scroller.
 */
public class ScrollerHelper {
  private final DroidDriver driver;
  private final Finder containerFinder;
  private final Scroller scroller;

  public ScrollerHelper(Scroller scroller, DroidDriver driver, Finder containerFinder) {
    this.scroller = scroller;
    this.driver = driver;
    this.containerFinder = containerFinder;
  }

  public UiElement scrollTo(Finder itemFinder) {
    return scroller.scrollTo(driver, containerFinder, itemFinder);
  }

  public boolean canScrollTo(Finder itemFinder) {
    try {
      scrollTo(itemFinder);
      return true;
    } catch (ElementNotFoundException e) {
      return false;
    }
  }
}
