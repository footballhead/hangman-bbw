# Hamgman!

Hangman Android app, made during BlackBerry Hackathon 2015

  * Find it on BlackBerry World: https://appworld.blackberry.com/webstore/content/59957346/
  * Latest APK: https://drive.google.com/open?id=0B3j7CNvMoPZac2Z4ZzBFSjJ6WWM
  * Video demonstration: https://www.youtube.com/watch?v=BU1kOHdRTHc

## Building with Android Studio

This repo works with Android Studio 3.0 provided you use the "Import Project (Gradle, Eclipse ADT, etc.)" button on the welcome screen.

## Signing

The signing part of the build assumes that `.keystore/keystore.properties` exists with the following structure:

```
storeFile keystore.jks
keyAlias key_alias
keyPassword key_password
storePassword store_password
```

`storeFile` is relative to `.keystore`, so this example assumes `.keystore/keystore.jks` exists.