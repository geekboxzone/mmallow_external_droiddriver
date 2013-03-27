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

package com.google.android.droiddriver.runner;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.test.suitebuilder.TestMethod;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.google.android.droiddriver.util.Logs;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Adds activity watcher to InstrumentationTestRunner.
 */
public class TestRunner extends InstrumentationTestRunner {
  private final Set<Activity> activities = Sets.newIdentityHashSet();
  private final AndroidTestRunner androidTestRunner = new AndroidTestRunner();

  /**
   * Returns an {@link AndroidTestRunner} that is shared by this and super, such
   * that we can add custom {@link TestListener}s.
   */
  @Override
  protected AndroidTestRunner getAndroidTestRunner() {
    return androidTestRunner;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Adds a {@link TestListener} that finishes all created activities.
   */
  @Override
  public void onStart() {
    getAndroidTestRunner().addTestListener(new TestListener() {
      @Override
      public void endTest(Test test) {
        runOnMainSync(new Runnable() {
          @Override
          public void run() {
            Iterator<Activity> iterator = activities.iterator();
            while (iterator.hasNext()) {
              Activity activity = iterator.next();
              iterator.remove();
              if (!activity.isFinishing()) {
                try {
                  Log.i(Logs.TAG, "Stopping activity: " + activity);
                  activity.finish();
                } catch (RuntimeException e) {
                  Log.e(Logs.TAG, "Failed to stop activity", e);
                }
              }
            }
          }
        });
      }

      @Override
      public void addError(Test arg0, Throwable arg1) {}

      @Override
      public void addFailure(Test arg0, AssertionFailedError arg1) {}

      @Override
      public void startTest(Test arg0) {}
    });
    super.onStart();
  }

  // Overrides InstrumentationTestRunner
  List<Predicate<TestMethod>> getBuilderRequirements() {
    List<Predicate<TestMethod>> requirements = Lists.newArrayList();
    requirements.add(new Predicate<TestMethod>() {
      @Override
      public boolean apply(TestMethod arg0) {
        MinSdkVersion annotation = arg0.getAnnotation(MinSdkVersion.class);
        if (annotation == null) {
          annotation = arg0.getEnclosingClass().getAnnotation(MinSdkVersion.class);
        }
        boolean result = annotation == null || annotation.value() <= Build.VERSION.SDK_INT;
        if (!result) {
          String msg =
              String.format("filtered %s#%s: MinSdkVersion=%d", arg0.getEnclosingClassname(),
                  arg0.getName(), annotation.value());
          Log.i(Logs.TAG, msg);
        }
        return result;
      }
    });
    return requirements;
  }

  @Override
  public void callActivityOnDestroy(Activity activity) {
    super.callActivityOnDestroy(activity);
    activities.remove(activity);
  }

  @Override
  public void callActivityOnCreate(Activity activity, Bundle bundle) {
    super.callActivityOnCreate(activity, bundle);
    activities.add(activity);
  }
}
