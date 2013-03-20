package com.google.android.apps.common.testing.ui.testapp;

import com.google.android.droiddriver.uiautomation.UiAutomationDriver;

/**
 * Tests SendActivity.
 */
// google3/javatests/com/google/android/apps/common/testing/ui/espresso/exampletest/ExampleTest.java
public class UiAutomationSendActivityTest extends AbstractSendActivityTest {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setDriver(new UiAutomationDriver(getInstrumentation().getUiAutomation()));
  }
}
