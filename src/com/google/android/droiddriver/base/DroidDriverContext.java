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

package com.google.android.droiddriver.base;

import android.app.Instrumentation;
import android.os.Looper;
import android.util.Log;

import com.google.android.droiddriver.actions.InputInjector;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.util.Logs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Internal helper for DroidDriver implementation.
 */
public abstract class DroidDriverContext {
  private final Instrumentation instrumentation;

  protected DroidDriverContext(Instrumentation instrumentation) {
    this.instrumentation = instrumentation;
  }

  public Instrumentation getInstrumentation() {
    return instrumentation;
  }

  public abstract BaseDroidDriver getDriver();

  public abstract InputInjector getInjector();

  /** Clears UiElement instances in the context */
  public abstract void clearData();

  /**
   * Tries to wait for an idle state on the main thread on best-effort basis up
   * to {@code timeoutMillis}. The main thread may not enter the idle state when
   * animation is playing, for example, the ProgressBar.
   */
  public boolean tryWaitForIdleSync(long timeoutMillis) {
    validateNotAppThread();
    FutureTask<?> futureTask = new FutureTask<Void>(new Runnable() {
      @Override
      public void run() {}
    }, null);
    instrumentation.waitForIdle(futureTask);

    try {
      futureTask.get(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new DroidDriverException(e);
    } catch (ExecutionException e) {
      throw new DroidDriverException(e);
    } catch (java.util.concurrent.TimeoutException e) {
      Logs.log(Log.DEBUG, String.format(
          "Timed out after %d milliseconds waiting for idle on main looper", timeoutMillis));
      return false;
    }
    return true;
  }

  private void validateNotAppThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw new DroidDriverException(
          "This method can not be called from the main application thread");
    }
  }
}
