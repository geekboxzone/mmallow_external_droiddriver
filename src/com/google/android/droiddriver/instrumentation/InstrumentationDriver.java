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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.droiddriver.base.AbstractDroidDriver;
import com.google.android.droiddriver.exceptions.TimeoutException;
import com.google.android.droiddriver.util.ActivityUtils;
import com.google.android.droiddriver.util.Logs;
import com.google.common.primitives.Longs;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a UiDriver that is driven via instrumentation.
 */
public class InstrumentationDriver extends AbstractDroidDriver {

  private final InstrumentationContext context;

  public InstrumentationDriver(Instrumentation instrumentation) {
    this.context = new InstrumentationContext(instrumentation);
  }

  @Override
  public ViewElement getRootElement() {
    Activity runningActivity = getRunningActivity();

    View[] views = RootFinder.getRootViews();
    if (views.length > 1) {
      // We are assuming that there is only one root view.
      Logs.log(Log.WARN, "There are more than one root views.");
    }

    // Note(twickham): This is no longer needed (will be deleted soon).
    // Need to create a fake root to be able to traverse all the possible views.
    /*
     * RootViewGroup root = new
     * RootViewGroup(context.getInstrumentation().getTargetContext()); for (View
     * view : views) { root.addView(view); }
     */

    // We assume getWindow().getDecorView() on the currently resumed activity is
    // the sole root.
    View root = runningActivity.getWindow().getDecorView();
    return context.getUiElement(root);
  }

  private Activity getRunningActivity() {
    long timeoutMillis = getPoller().getTimeoutMillis();
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      context.getInstrumentation().waitForIdleSync();
      Activity runningActivity = ActivityUtils.getRunningActivity();
      if (runningActivity != null) {
        return runningActivity;
      }
      long remainingMillis = end - SystemClock.uptimeMillis();
      if (remainingMillis < 0) {
        throw new TimeoutException(String.format(
            "Timed out after %d milliseconds waiting for foreground activity", timeoutMillis));
      }
      SystemClock.sleep(Longs.min(250, remainingMillis));
    }
  }

  // Note(twickham): This class is no longer in use (will be deleted soon).
  private static class RootViewGroup extends ViewGroup {
    private final List<View> children = new ArrayList<View>();

    public RootViewGroup(Context context) {
      super(context);
    }

    @Override
    public void addView(View view) {
      children.add(view);
    }

    @Override
    public int getChildCount() {
      return children.size();
    }

    @Override
    public View getChildAt(int index) {
      return children.get(index);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
      // Do nothing.
    }
  }

  private static class ScreenshotRunnable implements Runnable {
    private final View rootView;
    private Bitmap screenshot;

    private ScreenshotRunnable(View rootView) {
      this.rootView = rootView;
    }

    @Override
    public void run() {
      rootView.destroyDrawingCache();
      rootView.buildDrawingCache(false);
      screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
      rootView.destroyDrawingCache();
    }
  }

  @Override
  protected Bitmap takeScreenshot() {
    ScreenshotRunnable screenshotRunnable = new ScreenshotRunnable(getRootElement().getView());
    context.getInstrumentation().runOnMainSync(screenshotRunnable);
    return screenshotRunnable.screenshot;
  }
}
