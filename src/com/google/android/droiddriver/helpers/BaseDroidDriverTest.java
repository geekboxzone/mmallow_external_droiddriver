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

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.util.FileUtils;
import com.google.android.droiddriver.util.Logs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Base class for tests using DroidDriver that handles uncaught exceptions, for
 * example OOME, and takes screenshot on failure. It is NOT required, but
 * provides handy utility methods.
 */
public abstract class BaseDroidDriverTest<T extends Activity> extends
    D2ActivityInstrumentationTestCase2<T> {
  private static boolean classSetUpDone = false;
  // In case of device-wide fatal errors, e.g. OOME, the remaining tests will
  // fail and the messages will not help, so skip them.
  protected static boolean skipRemainingTests = false;
  // Prevent crash by uncaught exception.
  private static volatile Throwable uncaughtException;
  static {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable ex) {
        uncaughtException = ex;
      }
    });
  }

  protected DroidDriver driver;

  protected BaseDroidDriverTest(Class<T> activityClass) {
    super(activityClass);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    if (!classSetUpDone) {
      classSetUp();
      classSetUpDone = true;
    }
    driver = DroidDrivers.get();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    driver = null;
  }

  protected Context getTargetContext() {
    return getInstrumentation().getTargetContext();
  }

  /**
   * Initializes test fixture once for all tests extending this class. Typically
   * you call {@link DroidDrivers#init} with an appropriate instance. If an
   * InstrumentationDriver is used, this is a good place to call
   * {@link com.google.android.droiddriver.instrumentation.ViewElement#overrideClassName}
   */
  protected abstract void classSetUp();

  /**
   * Takes a screenshot on failure.
   */
  @SuppressWarnings("finally")
  protected void onFailure(Throwable failure) throws Throwable {
    // Give uncaughtException (thrown by app instead of tests) high priority
    if (uncaughtException != null) {
      failure = uncaughtException;
      uncaughtException = null;
      skipRemainingTests = true;
    }

    try {
      if (failure instanceof UnrecoverableFailure) {
        skipRemainingTests = true;
      }
      if (failure instanceof OutOfMemoryError) {
        dumpHprof();
      } else {
        String baseFileName = getBaseFileName();
        driver.dumpUiElementTree(baseFileName + ".xml");
        driver.getUiDevice().takeScreenshot(baseFileName + ".png");
      }
    } catch (Throwable e) {
      // This method is for troubleshooting. Do not throw new error; we'll
      // throw the original failure.
      Logs.log(Log.WARN, e);
      if (e instanceof OutOfMemoryError) {
        dumpHprof();
      }
    } finally {
      throw failure;
    }
  }

  /**
   * Gets the base filename for troubleshooting files. For example, a screenshot
   * is saved in the file "basename".png.
   */
  protected String getBaseFileName() {
    return "dd/" + getClass().getSimpleName() + "." + getName();
  }

  protected void dumpHprof() throws IOException, FileNotFoundException {
    skipRemainingTests = true;
    String path = FileUtils.getAbsoluteFile(getBaseFileName() + ".hprof").getPath();
    // create an empty readable file
    FileUtils.open(path).close();
    Debug.dumpHprofData(path);
  }

  /**
   * Fixes JUnit3: always call tearDown even when setUp throws. Also calls
   * {@link #onFailure}.
   */
  @Override
  public void runBare() throws Throwable {
    if (skipRemainingTests) {
      return;
    }
    Throwable exception = null;
    try {
      setUp();
      runTest();
    } catch (Throwable runException) {
      exception = runException;
      // ActivityInstrumentationTestCase2.tearDown() finishes activity
      // created by getActivity(), so call this before tearDown().
      onFailure(exception);
    } finally {
      try {
        tearDown();
      } catch (Throwable tearDownException) {
        if (exception == null) {
          exception = tearDownException;
        }
      }
    }
    if (exception != null) {
      throw exception;
    }
    if (uncaughtException != null) {
      onFailure(uncaughtException);
    }
  }
}
