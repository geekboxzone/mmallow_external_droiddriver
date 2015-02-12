# Releasing to JCenter

Add the following credentials to `local.properties` Your API key can be [found here](https://bintray.com/user/edit/tab/apikey).

```
bintray.user=USERNAME
bintray.key=APIKEY
```

Creating a release on jcenter is done by invoking the binaryUpload task. Note that you'll need to be a member of the appium organization on jcenter before publishing. Existing members are able to invite new ones.

Update the version number in `build.gradle` by modifying the value of `ddVersion`. Official releases should be made only after removing the `-SNAPSHOT` suffix. If the same version number is used as an existing release of droiddriver then jcenter will reject the upload.

`gradle clean bintrayUpload`
