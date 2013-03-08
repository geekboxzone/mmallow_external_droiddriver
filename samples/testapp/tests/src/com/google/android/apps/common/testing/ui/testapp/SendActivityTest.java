package com.google.android.apps.common.testing.ui.testapp;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.uidriver.By;
import com.google.android.uidriver.UiDriver;
import com.google.android.uidriver.uiautomation.UiAutomationDriver;

/**
 * Simple activity used for validating intent sending and UI behavior.
 */
public class SendActivityTest extends ActivityInstrumentationTestCase2<SendActivity> {
  private UiDriver driver;

  public SendActivityTest() {
    super(SendActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    getActivity();
    driver = new UiAutomationDriver(getInstrumentation().getUiAutomation());
  }

  public void testClick() {
    driver.waitForElement(By.text("Send")).click();
    // figure out why fail
    // assertTrue(driver.waitForElement(By.text("Data from sender")).isVisible());
  }
}
