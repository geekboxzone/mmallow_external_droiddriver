package com.google.android.apps.common.testing.ui.testapp.sendactivity;

import com.google.android.apps.common.testing.ui.testapp.AbstractSendActivityTest;
import com.google.android.droiddriver.helpers.DroidDrivers;

public class Default extends AbstractSendActivityTest {
  @Override
  protected void classSetUp() {
    DroidDrivers.init(DroidDrivers.newDriver(getInstrumentation()));
  }
}
