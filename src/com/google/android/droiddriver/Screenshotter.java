/*
 * Copyright (C) 2013 DroidDriver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.droiddriver;

/**
 * Interface for taking screenshot.
 *
 * @see UiAutomationDriver
 */
public interface Screenshotter {
  /**
   * Takes a screenshot of current window and stores it as JPEG.
   *
   * @param path the path of file to save screenshot; ".jpg" is appended
   * @param quality quality of the PNG compression; range: 0-100
   * @return true if screen shot is created successfully
   */
  boolean takeScreenshot(String path, int quality);
}
