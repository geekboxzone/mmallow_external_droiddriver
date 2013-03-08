LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# We only want this apk build for tests.
LOCAL_MODULE_TAGS := tests

LOCAL_STATIC_JAVA_LIBRARIES += uidriver

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := uidriver.samples.testapp.tests

LOCAL_INSTRUMENTATION_FOR := uidriver.samples.testapp

LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)
