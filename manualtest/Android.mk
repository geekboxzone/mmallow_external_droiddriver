LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := ManualDD

LOCAL_MODULE_TAGS := tests

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
    droiddriver

LOCAL_INSTRUMENTATION_FOR := ManualDD

LOCAL_SDK_VERSION := 19

include $(BUILD_PACKAGE)

