#
# Description:
#   Tests for DroidDriver test framework

licenses(["notice"])  # Apache License 2.0, Google-owned

filegroup(
    name = "opensource_filegroup",
    srcs = glob(["src/**/*.java"]) + ["AndroidManifest.xml"],
    visibility = ["//third_party/java_src/android_libs/droiddriver/opensource:__pkg__"],
)

android_resources(
    name = "resources",
    custom_package = "com.google.android.apps.common.testing.ui.testapp.tests",
    inline_constants = 0,
    manifest = "AndroidManifest.xml",
    resources = glob(["res/**"]),
    resources_dir = "res",
)

android_library(
    name = "resource_compiletime",
    srcs = [":resources"],
    neverlink = 1,
)

# Common android test library, containing:
#   project under test library and resources compile-time targets, and
#   test project dependencies.
android_library(
    name = "test_lib",
    srcs = [":resources"],
    resources = ":resources",
    deps = [
        "//third_party/java/android_libs/droiddriver",
        "//third_party/java_src/android_libs/droiddriver/samples/testapp:test_lib",
    ],
)

android_test(
    name = "LocalAttachedTests",
    size = "medium",
    srcs = glob(
        ["src/**/*.java"],
    ),
    args = [
        "--device_broker_type=LOCAL_ADB_SERVER",
    ],
    resources = ":resources",
    shard_count = 1,
    tags = [
        "local",
        "manual",
        "notap",
    ],
    # unused - actually we used whatever we're attached to.
    target_devices = ["//tools/android/emulated_devices/generic_phone:android_7_arm"],
    deps = [
        ":test_lib",
        "//third_party/java_src/android_libs/droiddriver/samples/testapp",
    ],
)
