# SubstratumThemeTemplate

The official Substratum Theme Template from the team, for every prospective/current themer who wishes to join the Substratum game!

There are two types of Substratum as of this date (27/7/16)
  - Substratum OMS
  - Substratum Legacy (RRO)

To get started, first of all, FORK this project on GitHub and open it up with Android Studio (I use Android Studio 2.1.2 and Android Studio 2.2 Preview Canary, although these versions don't matter, you should keep yourself up to date with the latest Google has to offer)

## Step 0: DISABLE INSTANT RUN ON YOUR PROJECT!

For more information: http://stackoverflow.com/a/35169716

## Step 1: Package Naming
The FIRST thing you need to change is the package identifier (the name the app identifies as) to something more meaningful to you:

https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/build.gradle
```
applicationId "substratum.theme.template"
```
Change this to:
```
applicationId "com.yourname.themename"
```

* **Change Package Name in the project structure: (optional)**
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

## Step 2: AndroidManifest Metadatas
Now you have to pick whether your theme supports OMS or RRO, or both.

Open this file: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/AndroidManifest.xml

Now configure it to your liking:
```
<meta-data
    android:name="Substratum_Legacy"
    android:value="true"/>
<meta-data
    android:name="Substratum_Name"
    android:value="@string/ThemeName"/>
<meta-data
    android:name="Substratum_Author"
    android:value="@string/ThemeAuthor"/>
<meta-data
    android:name="Substratum_Email"
    android:value=""/> <!-- Insert your email for bug reports -->
<!-- Do you support Stock and Theme Ready Gapps? -> all -->
<!-- Do you support Theme Ready but not Stock Gapps? -> ready -->
<!-- Do you support Stock but not Theme Ready Gapps? -> stock -->
<meta-data
    android:name="Substratum_ThemeReady"
    android:value="all|ready|stock"/> <!-- Only pick one! -->
<meta-data
    android:name="Substratum_Wallpapers"
    android:value="http://pastebin.com/raw/3scTbGep"/> <!-- Must be raw -->
```
These files link back to the strings.xml inside the res/values folder, here: 
https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/res/values/strings.xml

For wallpapers, make sure you use DIRECT LINKS for everything you see in the pastebin template. I would recommend using pastebin, 
but you could use Google Drive (for the XML file). Pictures must be located in a public area so that people can download them freely.
```
<string name="ThemeName">My New Theme</string>
<string name="ThemeAuthor">Nicholas Chum</string>
```

## Step 3: Start theming!
### Audio
To install system sound effects, you will need to have an audio folder, just like it's listed here: https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/audio

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
To install boot animations, you will need to have a bootanimations folder, just like it's listed here: https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/bootanimation

You are allowed to add multiple bootanimations and name them differently so they show up individually on the spinner dropdown in the app.

Example:
```
assets/bootanimation/Sakura.zip
assets/bootanimation/MarvelDC.zip
```

### Fonts
To install fonts, you will need to have a fonts folder, just like it's listed here: https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/fonts

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

![Alt text](http://i.imgur.com/wXNQjc0.jpg)

#### Type 1 Overlays
type1 can be a/b/c, as type1(a/b/c).xml at the ROOT of your overlay, and it does the Arcus way of moving it to res/values/type1(a/b/c).xml to replace Colors, Styles, Dimensions, Bools, Integers, Strings, etc...

An example of how to set up a Type 1 overlay:
https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/assets/overlays/android/type1a_PINK.xml

![Alt text](http://i.imgur.com/sobtTyK.png)
To adjust these preview colors, ensure that your desired preview color (any name) is at the top of type1(a/b/c)_VARIANT-NAME.xml. This way you get the previews. For example in the link above this picture, the type1a example will display holo_blue_light's #C2185B. This value will work with ?android:attr attributes as well as @android:color/ colors, based on the current framework applied.

#### Type 2 Overlays
type2 is a FOLDER, and they must start with /assets/overlays/overlay_name/type2_FOLDERNAME/ and this is a base extension (means it is NOT dependent on framework). You can have nav bars here, headers, special icons, etc. Type2's show up as the FOURTH SPINNER, if all type1 spinners have been used.

An example of how to set up a Type 2 overlay:
https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/overlays/android/type2_Gryffindor

#### Type 3 Overlays
type3 is also a FOLDER, but these only show up BASED ON FRAMEWORK. So if you wanted to have a clear theme bundled in with your dark theme, just create a assets/overlays/android/type3_FOLDERNAME which houses ANYTHING a type2 can house, except you can add special files like ^attrs_private.xml. If done correctly, a base spinner will show up at the top of the overlay manager (in theme information and compiling it will cause ANY overlay with a type3_FOLDERNAME spinner inside the overlay to be built automatically.

An example of how to set up a Type 3 overlay from Framework, then another app
https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/overlays/android/type3_Translucent%20Theme
https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/overlays/com.android.settings/type3_TranslucentTheme

#### Renaming Type Spinners
If you would like to rename the spinner names, you just need to create a file with NO EXTENSION on the root of the overlay folder:

For example, if I wanted to change the type1a spinner's name for a specific overlay, I would put it like this:
https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/assets/overlays/android/type1a

## Step 4: Finalize your theme!
To get your theme to show up on Substratum, all you need to ensure that your hero image relates to your theme. The hero image DETERMINES the color of the automatic actionbar and nav bar colors based on the (0,0)'th pixel on your image.

https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/res/drawable-xxhdpi/heroimage.png

If you have a completely white image, your actionbar and nav bar will change to that color, and the text colors will determine whether the black text is more visible on that color, or a white color.

## Step 5: Include a built in changelog for your users!

If you take a look at theme_strings.xml, you will see a <string-array> with name "ThemeChangelog" where you can add your own changelog line by line. Please do not include line breaks unless you really have to. Substratum does this automagically! You can have as many lines as you want, so don't worry about the default 5 lines in the template!

## Step 6: Safeguard your theme! Don't let the pirates win!

### If you don't want to activate AntiPiracy
Then do NOT change ThemerConstants.java's line 5 from false to true! Simple as that!

### Getting started with AntiPiracy

If you are ready to get AntiPiracy set up, you must first compile your theme as a SIGNED production APK from Android Studio (Build -> Compile Signed APK). Then launch the signed apk on your device and your log will spit out an error log under the name "SubstratumAntiPiracyLog", and you want to copy and paste that into Line 18 (APK_SIGNATURE_PRODUCTION): https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/ThemerConstants.java#L18

Then you would need to go to Play Developer Console. Then access to your app -> Services and APIs, generate a new API key for your app and then paste it in BASE_64_LICENSE_KEY on line 17: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/ThemerConstants.java#L17

Third, if you would like to enable intensive mode antipiracy (App package blacklist), open up ThemerConstants.java and add as many package names as you want under BLACKLISTED_APPLICATIONS.

Finally, if you would like to change where it checks for piracy such as Amazon App Store Enforcement or Play Store Enforcement, you have options listed in ThemerConstants.java for you to simply change true and false!

# DO NOT SHARE YOUR THEMERCONSTANTS.JAVA FILE IF YOU OPEN SOURCE YOUR THEME AND WANT TO KEEP ANTIPIRACY!
