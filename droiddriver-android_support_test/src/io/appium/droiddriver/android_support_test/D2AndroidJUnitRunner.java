/*
 * Copyright (C) 2015 DroidDriver committers
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

package io.appium.droiddriver.android_support_test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.test.internal.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.AndroidJUnitRunner;
import android.support.test.runner.lifecycle.Stage;
import android.util.Log;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.appium.droiddriver.exceptions.DroidDriverException;
import io.appium.droiddriver.exceptions.TimeoutException;
import io.appium.droiddriver.helpers.DroidDrivers;
import io.appium.droiddriver.util.ActivityUtils;
import io.appium.droiddriver.util.Logs;

/**
 * Integrates DroidDriver with AndroidJUnitRunner. <p> TODO: support DroidDriver test filter
 * annotations.
 */
public class D2AndroidJUnitRunner extends AndroidJUnitRunner {
  private static final Callable<Activity> GET_RUNNING_ACTIVITY = new Callable<Activity>() {
    @Override
    public Activity call() {
      Iterator<Activity> activityIterator = ActivityLifecycleMonitorRegistry.getInstance()
          .getActivitiesInStage(Stage.RESUMED).iterator();
      return activityIterator.hasNext() ? activityIterator.next() : null;
    }
  };

  /**
   * {@inheritDoc} <p> Sets the values for the convenience methods {@link
   * DroidDrivers#getInstrumentation()} and {@link DroidDrivers#getOptions()}.
   */
  @Override
  public void onCreate(Bundle arguments) {
    DroidDrivers.initInstrumentation(this, arguments);
    super.onCreate(arguments);
  }

  /**
   * {@inheritDoc} <p> Hooks {@link ActivityUtils#setRunningActivitySupplier} to {@link
   * ActivityLifecycleMonitorRegistry}.
   */
  @Override
  public void onStart() {
    ActivityUtils.setRunningActivitySupplier(new ActivityUtils.Supplier<Activity>() {
      @Override
      public Activity get() {
        try {
          // If this is called on main (UI) thread, don't call runOnMainSync
          if (Looper.myLooper() == Looper.getMainLooper()) {
            return GET_RUNNING_ACTIVITY.call();
          }

          return runOnMainSyncWithTimeout(GET_RUNNING_ACTIVITY);
        } catch (Exception e) {
          Logs.log(Log.WARN, e);
          return null;
        }
      }
    });
    super.onStart();
  }

  /**
   * Runs {@code callable} on the main thread on best-effort basis up to a time limit, which
   * defaults to {@code 10000L} and can be set as an <a href= "http://developer.android.com/tools/testing/testing_otheride.html#AMOptionsSyntax">
   * am instrument option</a> under the key {@code dd.runOnMainSyncTimeout}. <p>This is a safer
   * variation of {@link #runOnMainSync} because the latter may hang. But it is heavy because a new
   * thread is created for each call unless the am command line specifies {@code
   * dd.runOnMainSyncTimeout <=0} such as "-e dd.runOnMainSyncTimeout 0".</p>The {@code callable}
   * may never run, for example, in case that the main Looper has exited due to uncaught exception.
   */
  // TODO: move this to DroidDrivers
  // TODO: call runOnMainSync on a single worker thread?
  private <V> V runOnMainSyncWithTimeout(Callable<V> callable) {
    final RunOnMainSyncFutureTask<V> futureTask = new RunOnMainSyncFutureTask<>(callable);

    String timeoutMillisString = DroidDrivers.getOptions().getString("dd.runOnMainSyncTimeout");
    long timeoutMillis = timeoutMillisString == null? 10000L : Long.parseLong(timeoutMillisString);
    if (timeoutMillis <= 0L) {
      // Call runOnMainSync on current thread without time limit.
      futureTask.runOnMainSyncNoThrow();
    } else {
      new Thread() {
        @Override
        public void run() {
          futureTask.runOnMainSyncNoThrow();
        }
      }.start();
    }

    try {
      return futureTask.get(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (java.util.concurrent.TimeoutException e) {
      throw new TimeoutException("Timed out after " + timeoutMillis
          + " milliseconds waiting for Instrumentation.runOnMainSync", e);
    } catch (Throwable e) {
      throw new DroidDriverException(e);
    } finally {
      futureTask.cancel(false);
    }
  }

  private class RunOnMainSyncFutureTask<V> extends FutureTask<V> {
    public RunOnMainSyncFutureTask(Callable<V> callable) {
      super(callable);
    }

    public void runOnMainSyncNoThrow() {
      try {
        runOnMainSync(this);
      } catch (Throwable e) {
        setException(e);
      }
    }
  }
}
