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

package com.google.android.droiddriver;

import android.app.Instrumentation;
import android.os.Build;

import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.instrumentation.InstrumentationDriver;
import com.google.android.droiddriver.uiautomation.UiAutomationDriver;
import com.google.common.base.Preconditions;

/**
 * Builds DroidDriver instances.
 */
public class DroidDriverBuilder {
  public enum Implementation {
    ANY, INSTRUMENTATION, UI_AUTOMATION
  }

  private final Instrumentation instrumentation;
  private Implementation implementation;

  public DroidDriverBuilder(Instrumentation instrumentation) {
    this.instrumentation = Preconditions.checkNotNull(instrumentation);
  }

  public DroidDriver build() {
    if (implementation == null) {
      implementation = Implementation.ANY;
    }
    switch (implementation) {
      case INSTRUMENTATION:
        return new InstrumentationDriver(instrumentation);
      case UI_AUTOMATION:
        return new UiAutomationDriver(instrumentation.getUiAutomation());
      case ANY:
        if (Build.VERSION.SDK_INT >= 18) {
          return new UiAutomationDriver(instrumentation.getUiAutomation());
        }
        return new InstrumentationDriver(instrumentation);
    }
    // should never reach here
    throw new DroidDriverException("Cannot build DroidDriver");
  }

  public DroidDriverBuilder use(Implementation implementation) {
    Preconditions.checkState(this.implementation == null,
        "Cannot set implementation more than once");
    Preconditions.checkArgument(implementation != Implementation.UI_AUTOMATION
        || Build.VERSION.SDK_INT >= 18, "UI_AUTOMATION is not available below API 18");
    this.implementation = Preconditions.checkNotNull(implementation);
    return this;
  }
}
