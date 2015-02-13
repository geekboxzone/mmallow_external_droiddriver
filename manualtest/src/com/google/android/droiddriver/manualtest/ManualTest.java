package com.google.android.droiddriver.manualtest;

import android.app.Activity;

import com.google.android.droiddriver.actions.TextAction;
import com.google.android.droiddriver.finders.By;
import com.google.android.droiddriver.finders.Finder;
import com.google.android.droiddriver.helpers.BaseDroidDriverTest;
import com.google.android.droiddriver.helpers.DroidDrivers;

/**
 * This is for manually testing DroidDriver. It is not meant for continuous
 * testing. Instead it is used for debugging failures. It assumes the device is
 * in a condition that is ready to reproduce a failure. For example,
 * {@link #testSetTextForPassword} assumes the password_edit field is displayed
 * on screen and has input focus.
 * <p>
 * Run it as (optionally with -e debug true)
 *
 * <pre>
 * adb shell am instrument -w com.google.android.droiddriver.manualtest/com.google.android.droiddriver.runner.TestRunner
 * </pre>
 */
public class ManualTest extends BaseDroidDriverTest<Activity> {
  public ManualTest() {
    super(Activity.class);
  }

  public void testSetTextForPassword() {
    Finder password_edit = By.resourceId("com.google.android.gsf.login:id/password_edit");
    driver.on(password_edit).perform(
        new TextAction("A fake password that is not empty and needs to be cleared by setText"));
    String password = "1";
    driver.on(password_edit).setText(password);
    // This won't work because password_edit does not reveal text to
    // Accessibility service. But you can see the length changed on screen.
    // assertEquals(password, driver.on(password_edit).getText());
    assertEquals(null, driver.on(password_edit).getText());
  }

  @Override
  protected final void classSetUp() {
    DroidDrivers.init(DroidDrivers.newDriver(getInstrumentation()));
  }
}
