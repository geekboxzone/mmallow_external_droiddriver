package com.google.android.apps.common.testing.ui.testapp;

import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.finders.XPaths;
import com.google.android.droiddriver.helpers.BaseDroidDriverTest;

/**
 * Base class for testing SendActivity.
 */
public abstract class AbstractSendActivityTest extends BaseDroidDriverTest<SendActivity> {
  public AbstractSendActivityTest() {
    super(SendActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    getActivity();
  }

  public void testClick() {
    driver.on(By.text(getTargetContext().getString(R.string.button_send))).click();
    assertTrue(driver.on(By.text(getDisplayTitle())).isVisible());
  }

  public void testClickXPath() {
    driver.on(By.xpath("//ScrollView//Button")).click();
    assertTrue(driver.on(By.xpath("//TextView" + XPaths.text(getDisplayTitle()))).isVisible());
  }

  private String getDisplayTitle() {
    return getTargetContext().getString(R.string.display_title);
  }
}
