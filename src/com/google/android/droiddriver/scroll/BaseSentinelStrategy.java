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

import android.util.Log;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.scroll.Direction.DirectionConverter;
import com.google.android.droiddriver.scroll.Direction.LogicalDirection;
import com.google.android.droiddriver.scroll.Direction.PhysicalDirection;
import com.google.android.droiddriver.util.Logs;

/**
 * Base class for {@link SentinelStrategy}.
 */
public abstract class BaseSentinelStrategy implements SentinelStrategy {

  // Make sure sentinel exists in container
  private static class SentinelFinder implements Finder {
    private final Getter getter;

    public SentinelFinder(Getter getter) {
      this.getter = getter;
    }

    @Override
    public UiElement find(UiElement container) {
      UiElement sentinel = getter.getSentinel(container);
      if (sentinel == null) {
        throw new ElementNotFoundException(this);
      }
      Logs.log(Log.INFO, "Found match: " + sentinel);
      return sentinel;
    }

    @Override
    public String toString() {
      return String.format("SentinelFinder{%s}", getter);
    }
  }

  private final Getter backwardGetter;
  private final Getter forwardGetter;
  private final DirectionConverter directionConverter;
  private final SentinelFinder backwardSentinelFinder;
  private final SentinelFinder forwardSentinelFinder;

  protected BaseSentinelStrategy(Getter backwardGetter, Getter forwardGetter,
      DirectionConverter directionConverter) {
    this.backwardGetter = backwardGetter;
    this.forwardGetter = forwardGetter;
    this.directionConverter = directionConverter;
    this.backwardSentinelFinder = new SentinelFinder(backwardGetter);
    this.forwardSentinelFinder = new SentinelFinder(forwardGetter);
  }

  protected UiElement getSentinel(DroidDriver driver, Finder containerFinder,
      PhysicalDirection direction) {
    Logs.call(this, "getSentinel", driver, containerFinder, direction);
    Finder sentinelFinder;
    LogicalDirection logicalDirection = directionConverter.toLogicalDirection(direction);
    if (logicalDirection == LogicalDirection.BACKWARD) {
      sentinelFinder = By.chain(containerFinder, backwardSentinelFinder);
    } else {
      sentinelFinder = By.chain(containerFinder, forwardSentinelFinder);
    }
    return driver.on(sentinelFinder);
  }

  @Override
  public final DirectionConverter getDirectionConverter() {
    return directionConverter;
  }

  @Override
  public void beginScrolling(DroidDriver driver, Finder containerFinder, Finder itemFinder,
      PhysicalDirection direction) {}

  @Override
  public void endScrolling(DroidDriver driver, Finder containerFinder, Finder itemFinder,
      PhysicalDirection direction) {}

  @Override
  public String toString() {
    return String.format("{backwardGetter=%s, forwardGetter=%s}", backwardGetter, forwardGetter);
  }

  @Override
  public void doScroll(UiElement container, PhysicalDirection direction) {
    container.scroll(direction);
  }
}
