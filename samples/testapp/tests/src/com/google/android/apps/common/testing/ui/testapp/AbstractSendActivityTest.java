package com.google.android.apps.common.testing.ui.testapp;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.DroidDriver;

/**
 * Base class for testing SendActivity.
 */
// google3/javatests/com/google/android/apps/common/testing/ui/espresso/exampletest/ExampleTest.java
public abstract class AbstractSendActivityTest extends ActivityInstrumentationTestCase2<SendActivity> {

  private DroidDriver driver;

  public AbstractSendActivityTest() {
    super(SendActivity.class);
  }

  protected abstract DroidDriver getDriver();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    if (driver == null) {
      driver = getDriver();
    }
    getActivity();
  }

  public void testClick() {
    driver.on(By.text("Send")).click();
    assertTrue(driver.on(By.text("Data from sender")).isVisible());
  }

  public void testClickXPath() {
    // Note '//' before Button: uiautomator filters out the LinearLayout node in
    // between.
    // We'll do the same filtering in DroidDriver, or provide our own viewer
    // tool.
    driver.on(By.xpath("//ScrollView//Button")).click();
    assertTrue(driver.on(By.xpath("//TextView[@text='Data from sender']")).isVisible());
  }
}
