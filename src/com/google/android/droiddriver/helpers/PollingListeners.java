package com.google.android.droiddriver.helpers;

import com.google.android.droiddriver.DroidDriver;
import com.google.android.droiddriver.Poller.PollingListener;
import com.google.android.droiddriver.UiElement;
import com.google.android.droiddriver.exceptions.ElementNotFoundException;
import com.google.android.droiddriver.finders.Finder;

/**
 * Static utility methods to create commonly used PollingListeners.
 */
public class PollingListeners {
  private static UiElement tryFind(DroidDriver driver, Finder finder) {
    try {
      return driver.find(finder);
    } catch (ElementNotFoundException enfe) {
      return null;
    }
  }

  /**
   * Returns a new {@code PollingListener} that will look for
   * {@code watchFinder}, then click {@code dismissFinder} to dismiss it.
   * <p>
   * Typically a {@code PollingListener} is used to dismiss "random" dialogs. If
   * you know the certain situation when a dialog is displayed, you should deal
   * with the dialog in the specific situation instead of using a
   * {@code PollingListener} because it is checked in all polling events, which
   * occur frequently.
   * </p>
   *
   * @param watchFinder Identifies the UI component, for example an AlertDialog
   * @param dismissFinder Identifies the UiElement to click on that will dismiss
   *        the UI component
   */
  public static PollingListener newDismissListener(final Finder watchFinder,
      final Finder dismissFinder) {
    return new PollingListener() {
      @Override
      public void onPolling(DroidDriver driver, Finder finder) {
        if (tryFind(driver, watchFinder) != null) {
          driver.find(dismissFinder).click();
        }
      }
    };
  }

  private PollingListeners() {}
}
