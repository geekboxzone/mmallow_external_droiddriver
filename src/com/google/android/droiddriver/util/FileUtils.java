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

package com.google.android.droiddriver.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Static helper methods for manipulating files.
 */
public class FileUtils {
  public static BufferedOutputStream open(String path) throws FileNotFoundException {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(System.getProperty("java.io.tmpdir"), path);
    }
    file.setReadable(true /* readable */, false/* ownerOnly */);
    Logs.log(Log.INFO, "opening file " + file.getAbsolutePath());
    return new BufferedOutputStream(new FileOutputStream(file));
  }
}
