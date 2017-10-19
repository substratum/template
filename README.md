# Substratum Theme Template

The official theme template from the team, for every prospective/current themer who wishes to join the Substratum game!

There are six types of Substratum as of this date (18/10/17)
  - Substratum OMS (3/7) (Marshmallow) [Marshmallow has been deprecated since 510, but it is still available]
  - Substratum OMS (7) (Nougat)
  - Substratum Dynamic Overlays (Stock Oreo) [Will require a rooted device, or Andromeda installed]
  - Substratum Dynamic Overlays (Oreo)
  - Substratum Samsung (RRO-OMS fusion) [Will require a device with the samsung plugin]
  - Substratum Legacy (RRO) [Rooted]

To get started, first of all, FORK and CLONE this project on GitHub and open it up with Android Studio (We recommend to keep yourself up to date with the latest Google has to offer)

## Step 0: PRECONFIGURATIONS!
Install the latest Android Studio Preview: https://developer.android.com/studio/preview/index.html

Install NDK, CMAKE and LLDB from the SDK Manager: ![Alt text](https://developer.android.com/studio/images/projects/ndk-install_2-2_2x.png)

Disable Instant Run: http://stackoverflow.com/a/35169716

Disable activity launch on theme: 
  - Click on the above dropdown menu in Android Studio that says `app` (by default there should be an icon of andy with an x on it)
  - Click on `Edit configurations...`
  - Locate "Launch Options" and instead of Default Activity, select Nothing
  - This will now push your theme APK to your device without having to worry about build errors

## Step 1: Package Naming
The FIRST thing you need to change is the package identifier (the name the app identifies as) to something more meaningful to you. Open up [build.gradle](app/build.gradle) and look for this line
```
applicationId "substratum.theme.template"
```
Change this to anything you want, for instance:
```
applicationId "com.yourname.themename"
```

Change Package Name in the project structure: (optional)
 http://stackoverflow.com/a/29092698
 
NOTE: If you change your project structure name, the AndroidManifest AND the build.gradle package names must match, or else encrypted assets will break.

## Step 2: Theme Properties
You can control your theme properties such as whether your theme supports OMS, RRO or both inside
the theme configuration file: [theme_configurations.xml](app/src/main/res/values/theme_configurations.xml)

For wallpapers, make sure you use DIRECT LINKS for everything you see in the pastebin template. We recommend using pastebin, 
but you could use Google Drive (for the XML file). Pictures must be located in a public area so that people can download them freely.

## Step 3: Start theming!
### Audio
To install system sound effects, you will need to have an audio folder, just like it's listed [here](app/src/main/assets/audio).

You are allowed to add multiple audio files and name them differently so they show up indivually on the spinner dropdown in the app.

Example:
```
assets/audio/StarWarsLockUnlock.zip
```
Audio ZIP root structure:
```
StarWarsLockUnlock.zip/ui/Lock.ogg(or mp3)
StarWarsLockUnlock.zip/ui/Unlock.ogg(or mp3)
StarWarsLockUnlock.zip/ui/effect_tick.ogg(or mp3)
StarWarsLockUnlock.zip/alarms/alarm.ogg(or mp3)
StarWarsLockUnlock.zip/ringtones/ringtone.ogg(or mp3)
StarWarsLockUnlock.zip/notifications/notification.ogg(or mp3)
```

### Boot Animations
To install boot animations, you will need to have a bootanimations folder, just like it's listed [here](app/src/main/assets/bootanimation).

You are allowed to add multiple bootanimations and name them differently so they show up individually on the spinner dropdown in the app.

Example:
```
assets/bootanimation/Sakura.zip
```

### Shutdown Animations
To install shutdown animations, you will need to have a shutdownanimation folder, just like it's listed [here](app/src/main/assets/shutdownanimation).

Just like bootanimations, but only enabled on Oreo based devices and above!

Example:
```
assets/shutdownanimation/Sakura.zip
```

### Fonts
To install fonts, you will need to have a fonts folder, just like it's listed [here](app/src/main/assets/fonts).

You are allowed to add multiple fonts and name them differently so they show up individually on the spinner dropdown in the app.

If you are looking to load up DOWNLOADED fonts from the internet, then download the ZIP from your source, extract it and navigate INTO the folder, until you find the folder "fonts" in "/system/fonts", and ZIP the whole contents of the folder up to form your new fonts pack.

This also supports fonts.xml within the font's ZIP file, so if you have any knowledge of this, you can add it in.

Example:
```
assets/fonts/Ubuntu.zip
```
Font ZIP root structure:
```
Ubuntu.zip/font1.ttf
Ubuntu.zip/font2.ttf
Ubuntu.zip/font3.ttf
```

### Overlays
If you had any experience with overlays on CMTE, it is rather the same format, except we added our own twists for enhanced theming capabilities for Substratum users.

If you would like to theme an application, first you need their package identifier. Let's say you wanted to theme Substratum (projekt.substratum), you will create a folder within "assets/overlays" and name it the package name and start following the app's resource structure, e.g.
```
assets/overlays/projekt.substratum/res/values/colors.xml
```

### Overlay Type Options Guide

https://docs.google.com/document/d/1H8tC1hjhADPOZdjebJEeC3FYZid9gZ7F56OA-27PxfU/edit?usp=sharing

## Step 4: Finalize your theme!
To get your theme to show up on Substratum, all you need to ensure that your hero image relates to your theme. The hero image DETERMINES the color of the automatic actionbar and nav bar colors based on the (0,0)'th pixel on your image. Your theme hero image is located [here](app/src/main/res/drawable-xxhdpi/heroimage.png).

If you have a completely white image, your actionbar and nav bar will change to that color, and the text colors will determine whether the black text is more visible on that color, or a white color.

## Step 5: Include a built in changelog for your users!

If you take a look at the aforementioned theme_configurations.xml, you will see a <string-array> with name "ThemeChangelog" where you can add your own changelog line by line. Please do not include line breaks unless you really have to. Substratum does this automagically! You can have as many lines as you want, so don't worry about the default 5 lines in the template!

## Step 6: Safeguard your theme! Don't let the pirates win!

### If you want to enable the Substratum theme for other Theme Managers (e.g. Slim)
In app build.gradle, change the [SUPPORTS_THIRD_PARTY_SYSTEMS](app/build.gradle#L19) on line 19 of your app's build.gradle from false to true.

### If you don't want to activate AntiPiracy
Then you can stop reading and get your theme published! Good luck!

### Getting started with AntiPiracy

If you are ready to get AntiPiracy set up, all you need to look at is [LoadingProcess.c](app/src/main/jni/LoadingProcess.c)!

First change the [getAPStatus](app/src/main/jni/LoadingProcess.c#L12) value on line 12 from `JNI_FALSE` to `JNI_TRUE`.

Compile your theme as a SIGNED release APK from Android Studio (Build -> Generate Signed APK). Then launch the signed apk on your device and your log will spit out an error log under the name "SubstratumThemeReport", and you want to copy and paste that into [APK_SIGNATURE_PRODUCTION](app/src/main/jni/LoadingProcess.c#L25) on line 25, then count the amount of characters you have that you pasted, and replace `000` with the number you got from your counter (http://www.lettercount.com/). This will ensure there is no easy one shot kill crack for your theme!

Then you would need to go to Play Developer Console. Then access to your app -> Services and APIs, generate a new API key for your app and then paste it into [getBase64Key](app/src/main/java/substratum/theme/template/Constants.kt#L10) on line 10.

Third, if you would like to enable intensive mode antipiracy (App package blacklist), add as many package names as you want under [BLACKLISTED_APPLICATIONS](app/src/main/java/substratum/theme/template/Constants.kt#L31) on line 31. Then enable [getBlacklistedApplications](app/src/main/jni/LoadingProcess.c#L79) on line 79.

Finally, if you would like to change where it checks for piracy such as Amazon App Store Enforcement or Play Store Enforcement, you have options listed in [Constants.kt](app/src/main/jni/LoadingProcess.c#L57-L70) for you to simply change from `JNI_TRUE` and `JNI_FALSE`!

**Under no circumstances should you share your LoadingProcess.c file, unless specifically asked by an [official substratum developer!](https://github.com/substratum/documentation#team-info-and-responsibilities)**

### Encrypted Assets
As of template version 11.0.0, all theme assets are duplicated are encrypted within the APK by default, not your original assets!

Always use a version control tool listed below to host your private themes!

BitBucket: https://bitbucket.org/
GitLab: https://about.gitlab.com/

### Enforcing security
As of template version 11.0.0, themes have an additional check on the build of substratum your users should be running.

What this means is that themes can ensure their themes ONLY function with our full release cycle with debug and Play Store releases.

If you would like to enable this feature, all you have to do is to flip `JNI_TRUE` to `JNI_FALSE` [here](app/src/main/jni/LoadingProcess.c#L94)!

However, this may affect people who build their own versions of Substratum. If you want your theme to work on unofficial versions of Substratum, all you have to do is to flip `JNI_FALSE` to `JNI_TRUE` [here](app/src/main/jni/LoadingProcess.c#L94)!

### Now what?
Nothing. Now you're set to publish your theme!

## Written with ❤ from themers and developers alike!
