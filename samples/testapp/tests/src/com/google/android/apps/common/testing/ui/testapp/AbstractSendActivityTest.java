package com.google.android.apps.common.testing.ui.testapp;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.droiddriver.By;
import com.google.android.droiddriver.DroidDriver;

/**
 * Base class for testing SendActivity.
 */
// TODO: support Parameterized test runner?
// google3/javatests/com/google/android/apps/common/testing/ui/espresso/exampletest/ExampleTest.java
public abstract class AbstractSendActivityTest extends ActivityInstrumentationTestCase2<SendActivity> {

  private DroidDriver driver;

  public AbstractSendActivityTest() {
    super(SendActivity.class);
  }

  protected void setDriver(DroidDriver driver) {
    this.driver = driver;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    getActivity();
  }

  public void testClick() {
    driver.waitForElement(By.text("Send")).click();
    assertTrue(driver.waitForElement(By.text("Data from sender")).isVisible());
  }

  public void testClickXPath() {
    // Note '//' before Button: uiautomator filters out the LinearLayout node in
    // between.
    // We'll do the same filtering in DroidDriver, or provide our own viewer
    // tool.
    driver.waitForElement(By.xpath("//ScrollView//Button")).click();
    assertTrue(driver.waitForElement(By.xpath("//TextView[@text='Data from sender']")).isVisible());
  }
}
