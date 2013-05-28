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

import android.util.Log;

import com.google.common.base.Joiner;

/**
 * Internal helper for logging.
 */
public class Logs {
  public static final String TAG = "DroidDriver";
  public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

  public static void call(Object self, String method, Object... args) {
    if (DEBUG) {
      Log.d(
          TAG,
          String.format("Invoking %s.%s(%s)", self.getClass().getSimpleName(), method,
              Joiner.on(",").join(args)));
    }
  }

  public static void println(int priority, Object... msgs) {
    if (Log.isLoggable(TAG, priority)) {
      Log.println(priority, TAG, Joiner.on(" ").join(msgs));
    }
  }

  private Logs() {}
}
