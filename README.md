# Substratum Theme Template

The official theme template from the team, for every prospective/current themer who wishes to join the Substratum game!

There are three types of Substratum as of this date (17/6/17)
  - Substratum OMS
  - Substratum Samsung
  - Substratum Legacy (RRO)

To get started, first of all, FORK this project on GitHub and open it up with Android Studio (We recommend to keep yourself up to date with the latest Google has to offer)

## Step 0: DISABLE INSTANT RUN ON YOUR PROJECT!

For more information: http://stackoverflow.com/a/35169716

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
```
  * Look at the "project panel" in android studio (the one on the left). 
  * In the top right corner you will see a little "gear icon". 
  * Click on it.
  * in the dialog which opens click on "Compact Empty Middle Packages".
  * Select the "substratum" folder (in JAVA/com/ and rightclick it. 
  * Choose "refactor" and then "rename".
  * A warning window will pop up. Just click on "Rename package".
  * Enter the desired first part of your package name(should be same as that in applicationId of build.gradle) and click refactor.
  * At the bottom of the screen a refactoring preview will appear. Just click on "DO REFACTOR" at the left corner. <br/> <br/>
  * Do the same with the theme and template folders, name it same as the name in applicationId of build.gradle.
```

For more information: http://stackoverflow.com/a/29092698

## Step 2: Theme Properties
You can control your theme properties such as whether yout theme supports OMS, RRO or both inside
the theme configuration file: [theme_configurations.xml](app/src/main/res/values/theme_configurations.xml)

Now configure it to your liking:
```
<!-- Theme name and author name -->
<string name="ThemeName">Substratum Theme Template</string>
<string name="ThemeAuthor">[projekt.] development team</string>
<!-- Your email for bug reports -->
<string name="ThemeAuthorEmail"></string>
<!-- Do you support Stock and Theme Ready Gapps? -> all -->
<!-- Do you support Theme Ready but not Stock Gapps? -> ready -->
<!-- Do you support Stock but not Theme Ready Gapps? -> stock -->
<string name="ThemeReadyRequirement">all</string>
<!-- Theme wallpaper source (must be raw) -->
<string name="ThemeWallpaperSource">http://pastebin.com/raw/TG4mFdXz</string>
<!-- Do your theme support substratum legacy? -->
<!-- If you choose not to support legacy, then change line 12 in AndroidManifest.xml -->
<!-- from 'false' to 'true' to hide your theme on the Play Store from legacy ROM-->
<bool name="ThemeSupportLegacy">true</bool>
<!-- Do your theme support substratum samsung? -->
<bool name="ThemeSupportSamsung">false</bool>
```
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
assets/bootanimation/MarvelDC.zip
```

### Fonts
To install fonts, you will need to have a fonts folder, just like it's listed [here](app/src/main/assets/fonts).

You are allowed to add multiple fonts and name them differently so they show up individually on the spinner dropdown in the app.

If you are looking to load up DOWNLOADED fonts from the internet, then download the ZIP from your source, extract it and navigate INTO the folder, until you find the folder "fonts" in "/system/fonts", and ZIP the whole contents of the folder up to form your new fonts pack.

This also supports fonts.xml within the font's ZIP file, so if you have any knowledge of this, you can add it in.

Example:
```
assets/fonts/SegoeUIWP8.zip
assets/fonts/ohds5.zip
```
Font ZIP root structure:
```
SegoeUIWP8.zip/font1.ttf
SegoeUIWP8.zip/font2.ttf
SegoeUIWP8.zip/font3.ttf
```

### Overlays
If you had any experience with overlays on CMTE, it is rather the same format, except we added our own twists for enhanced theming capabilities for Substratum users.

If you would like to theme an application, first you need their package identifier. Let's say you wanted to theme Substratum (projekt.substratum), you will create a folder within "assets/overlays" and name it the package name and start following the app's resource structure, e.g.
```
assets/overlays/projekt.substratum/res/values/colors.xml
```
https://docs.google.com/document/d/1H8tC1hjhADPOZdjebJEeC3FYZid9gZ7F56OA-27PxfU/edit?usp=sharing

## Step 4: Finalize your theme!
To get your theme to show up on Substratum, all you need to ensure that your hero image relates to your theme. The hero image DETERMINES the color of the automatic actionbar and nav bar colors based on the (0,0)'th pixel on your image. Your theme hero image is located [here](app/src/main/res/drawable-xxhdpi/heroimage.png).

If you have a completely white image, your actionbar and nav bar will change to that color, and the text colors will determine whether the black text is more visible on that color, or a white color.

## Step 5: Include a built in changelog for your users!

If you take a look at the aforementioned theme_configurations.xml, you will see a <string-array> with name "ThemeChangelog" where you can add your own changelog line by line. Please do not include line breaks unless you really have to. Substratum does this automagically! You can have as many lines as you want, so don't worry about the default 5 lines in the template!

## Step 6: Safeguard your theme! Don't let the pirates win!

### If you want to enable the Substratum theme for other Theme Managers (e.g. Slim)
In app build.gradle, change the [SUPPORTS_THIRD_PARTY_SYSTEMS](app/build.gradle#L16) on line 16 of app build.gradle from false to true.

### If you don't want to activate AntiPiracy
Then you can stop reading and get your theme published! Good luck!

### Getting started with AntiPiracy

If you are ready to get AntiPiracy set up, all you need to look at is [ThemersConstant.kt](app/src/main/java/substratum/theme/template/ThemerConstants.kt)

First change the [PIRACY_CHECK](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L5) value on line 5 from false to true.

And you need compile your theme as a SIGNED production APK from Android Studio (Build -> Compile Signed APK). Then launch the signed apk on your device and your log will spit out an error log under the name "SubstratumAntiPiracyLog", and you want to copy and paste that into [APK_SIGNATURE_PRODUCTION](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L8) on line 8.

Then you would need to go to Play Developer Console. Then access to your app -> Services and APIs, generate a new API key for your app and then paste it into [BASE_64_LICENSE_KEY](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L7) on line 7.

Third, if you would like to enable intensive mode antipiracy (App package blacklist), add as many package names as you want under [BLACKLISTED_APPLICATIONS](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L27) on line 27. Then enable [ENABLE_BLACKLISTED_APPLICATIONS](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L26) on line 26.

Finally, if you would like to change where it checks for piracy such as Amazon App Store Enforcement or Play Store Enforcement, you have options listed in [ThemerConstants.kt](app/src/main/java/substratum/theme/template/ThemerConstants.kt#L9) for you to simply change true and false!

**DO NOT SHARE YOUR THEMERCONSTANTS.KT FILE IF YOU OPEN SOURCE YOUR THEME AND WANT TO KEEP ANTIPIRACY!**

### Encrypted Assets
As of template version 10.0.0, the theme assets now can be encrypted! All you need to do is switch the [SHOULD_ENCRYPT_ASSETS](app/build.gradle#L13) in app build.gradle line 13.

But if you ever lost your source files for your theme, unfortunately, it is all gone. Thankfully, BitBucket and GitLab support private repos for free (GitHub is paid, or free with student discount)...so we recommend to keep all your files stored there!

BitBucket: https://bitbucket.org/<br>
GitLab: https://about.gitlab.com/

### Now what?
Nothing. Now you're set to publish your theme!
