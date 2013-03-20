package com.google.android.apps.common.testing.ui.testapp;

import com.google.android.droiddriver.instrumentation.InstrumentationDriver;

/**
 * Tests SendActivity.
 */
// google3/javatests/com/google/android/apps/common/testing/ui/espresso/exampletest/ExampleTest.java
public class InstrumentationSendActivityTest extends AbstractSendActivityTest {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setDriver(new InstrumentationDriver(getInstrumentation()));
  }
}
