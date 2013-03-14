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

import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.base.AbstractDroidDriver;

import android.app.Instrumentation;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a UiDriver that is driven via instrumentation.
 */
public class InstrumentationDriver extends AbstractDroidDriver {

  private final Instrumentation instrumentation;

  public InstrumentationDriver(Instrumentation instrumentation) {
    this.instrumentation = instrumentation;
  }

  @Override
  public UiElement getRootElement() {
    View[] views = RootFinder.getRootViews();
    // Need to create a fake root to be able to traverse all the possible views.
    RootViewGroup root = new RootViewGroup(instrumentation.getTargetContext());
    for (View view : views) {
      root.addView(view);
    }
    return new ViewElement(instrumentation, root);
  }

  private static class RootViewGroup extends ViewGroup {
    List<View> children = new ArrayList<View>();

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

}
