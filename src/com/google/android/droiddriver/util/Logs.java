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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Internal helper for logging.
 */
public class Logs {
  public static final String TAG = "DroidDriver";

  // support TYPE as well?
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Loggable {
    int priority() default Log.DEBUG;
  }

  /**
   * An {@link InvocationHandler} that logs invocations of {@link Loggable}
   * methods.
   */
  public static class LoggingWrapper implements InvocationHandler {
    private final Object wrapped;

    public LoggingWrapper(Object wrapped) {
      this.wrapped = Preconditions.checkNotNull(wrapped);
      assert !(wrapped instanceof LoggingWrapper); // prevent recursion
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.isAnnotationPresent(Loggable.class)) {
        Log.println(
            method.getAnnotation(Loggable.class).priority(),
            TAG,
            String.format("Invoking %s.%s(%s)", method.getDeclaringClass().getName(),
                method.toString(), Joiner.on(",").join(args)));
      }
      try {
        return method.invoke(wrapped, args);
        // TODO: log return value as well
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      }
    }
  }

  /**
   * Wraps {@code obj} in a {@link LoggingWrapper}.
   */
  public static <T> T wrap(Class<T> cls, T obj) {
    return Reflection.newProxy(cls, new LoggingWrapper(obj));
  }

  private Logs() {}
}
