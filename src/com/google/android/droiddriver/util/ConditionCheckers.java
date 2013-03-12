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

package com.google.android.droiddriver.util;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.Matcher;
import com.google.android.droiddriver.Poller.ConditionChecker;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.exceptions.UnsatisfiedConditionException;

/**
 * Helpers for {@link ConditionChecker}.
 */
public class ConditionCheckers {

  /**
   * A ConditionChecker that returns the matching {@link UiElement}.
   */
  public static final ConditionChecker<UiElement> EXISTS_CHECKER =
      new ConditionChecker<UiElement>() {
        @Override
        public UiElement check(DroidDriver driver, Matcher matcher) {
          try {
            return driver.getRootElement().findElement(matcher);
          } catch (ElementNotFoundException e) {
            throw new UnsatisfiedConditionException("");
          }
        }
      };
  /**
   * A ConditionChecker that does not throw only if the matching
   * {@link UiElement} is gone.
   */
  public static final ConditionChecker<Void> GONE_CHECKER = new ConditionChecker<Void>() {
    @Override
    public Void check(DroidDriver driver, Matcher matcher) {
      try {
        driver.getRootElement().findElement(matcher);
        throw new UnsatisfiedConditionException("");
      } catch (ElementNotFoundException e) {
        return null;
      }
    }
  };

  private ConditionCheckers() {}
}
