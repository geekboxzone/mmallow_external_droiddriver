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

package com.google.android.droiddriver.instrumentation;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.google.android.droiddriver.base.BaseDroidDriver;
import com.google.android.droiddriver.exceptions.DroidDriverException;
import com.google.android.droiddriver.exceptions.TimeoutException;
import com.google.android.droiddriver.util.ActivityUtils;
import com.google.android.droiddriver.util.Logs;

/**
 * Implementation of DroidDriver that is driven via instrumentation.
 */
public class InstrumentationDriver extends BaseDroidDriver {
  private final InstrumentationContext context;
  private final InstrumentationUiDevice uiDevice;

  public InstrumentationDriver(Instrumentation instrumentation) {
    this.context = new InstrumentationContext(instrumentation, this);
    uiDevice = new InstrumentationUiDevice(context);
  }

  @Override
  protected ViewElement getNewRootElement() {
    return context.getUiElement(findRootView(), null /* parent */);
  }

  @Override
  protected InstrumentationContext getContext() {
    return context;
  }

  private static class FindRootViewRunnable implements Runnable {
    View rootView;
    Throwable exception;

    @Override
    public void run() {
      try {
        View[] views = RootFinder.getRootViews();
        if (views.length > 1) {
          Logs.log(Log.VERBOSE, "views.length=" + views.length);
          for (View view : views) {
            if (view.hasWindowFocus()) {
              rootView = view;
              return;
            }
          }
        }
        // Fall back to DecorView.
        rootView = ActivityUtils.getRunningActivity().getWindow().getDecorView();
      } catch (Throwable e) {
        exception = e;
        Logs.log(Log.ERROR, e);
      }
    }
  }

  private View findRootView() {
    waitForRunningActivity();
    FindRootViewRunnable findRootViewRunnable = new FindRootViewRunnable();
    context.runOnMainSync(findRootViewRunnable);
    if (findRootViewRunnable.exception != null) {
      throw new DroidDriverException(findRootViewRunnable.exception);
    }
    return findRootViewRunnable.rootView;
  }

  private void waitForRunningActivity() {
    long timeoutMillis = getPoller().getTimeoutMillis();
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      if (ActivityUtils.getRunningActivity() != null) {
        return;
      }
      long remainingMillis = end - SystemClock.uptimeMillis();
      if (remainingMillis < 0) {
        throw new TimeoutException(String.format(
            "Timed out after %d milliseconds waiting for foreground activity", timeoutMillis));
      }
      SystemClock.sleep(Math.min(250, remainingMillis));
    }
  }

  private static class ScreenshotRunnable implements Runnable {
    private final View rootView;
    Bitmap screenshot;

    private ScreenshotRunnable(View rootView) {
      this.rootView = rootView;
    }

    @Override
    public void run() {
      try {
        rootView.destroyDrawingCache();
        rootView.buildDrawingCache(false);
        Bitmap drawingCache = rootView.getDrawingCache();
        int[] xy = new int[2];
        rootView.getLocationOnScreen(xy);
        if (xy[0] == 0 && xy[1] == 0) {
          screenshot = Bitmap.createBitmap(drawingCache);
        } else {
          Canvas canvas = new Canvas();
          Rect rect = new Rect(0, 0, drawingCache.getWidth(), drawingCache.getHeight());
          rect.offset(xy[0], xy[1]);
          screenshot =
              Bitmap.createBitmap(rect.width() + xy[0], rect.height() + xy[1], Config.ARGB_8888);
          canvas.setBitmap(screenshot);
          canvas.drawBitmap(drawingCache, null, new RectF(rect), null);
          canvas.setBitmap(null);
        }
        rootView.destroyDrawingCache();
      } catch (Throwable e) {
        Logs.log(Log.ERROR, e);
      }
    }
  }

  Bitmap takeScreenshot() {
    ScreenshotRunnable screenshotRunnable = new ScreenshotRunnable(findRootView());
    context.runOnMainSync(screenshotRunnable);
    return screenshotRunnable.screenshot;
  }

  @Override
  public InstrumentationUiDevice getUiDevice() {
    return uiDevice;
  }
}
