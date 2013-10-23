package com.google.android.apps.common.testing.ui.testapp.sendactivity;

import com.google.android.apps.common.testing.ui.testapp.AbstractSendActivityTest;
import com.google.android.droiddriver.helpers.DroidDrivers;

// Optional annotation to show filtering tests by build version.
@com.google.android.droiddriver.runner.UseUiAutomation
public class UseUiAutomation extends AbstractSendActivityTest {
  @Override
  protected void classSetUp() {
    DroidDrivers.init(DroidDrivers.newUiAutomationDriver(getInstrumentation()));
  }
}
