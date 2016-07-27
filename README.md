# SubstratumThemeTemplate

The official Substratum Theme Template from the team, for every prospective/current themer who wishes to join the Substratum game!

There are two types of Substratum as of this date (27/7/16)
  - Substratum OMS
  - Substratum Legacy (RRO)

To get started, first of all, FORK this project on GitHub and open it up with Android Studio (I use Android Studio 2.1.2 and Android Studio 2.2 Preview Canary, although these versions don't matter, you should keep yourself up to date with the latest Google has to offer)

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

## Step 2: AndroidManifest Metadatas
Now you have to pick whether your theme supports OMS or RRO, or both.

Open this file: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/AndroidManifest.xml

Now configure it to your liking:
```
<meta-data
    android:name="Substratum_Legacy"
    android:value="true"/>
<meta-data
    android:name="Substratum_Theme"
    android:value="@string/ThemeName"/>
<meta-data
    android:name="Substratum_Author"
    android:value="@string/ThemeAuthor"/>
```
These files link back to the strings.xml inside the res/values folder, here: 
https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/res/values/strings.xml
```
<string name="ThemeName">My New Theme</string>
<string name="ThemeAuthor">Nicholas Chum</string>
```

## Step 3: Start theming!
### Audio
To install system sound effects, you will need to have an audio folder, just like it's listed here: https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/audio

You are allowed to add multiple bootanimations and name them differently so they show up differently on the spinner dropdown in the app.

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

You are allowed to add multiple bootanimations and name them differently so they show up differently on the spinner dropdown in the app.

Example:
```
assets/bootanimation/Sakura.zip
assets/bootanimation/MarvelDC.zip
```

### Fonts
To install fonts, you will need to have a fonts folder, just like it's listed here: https://github.com/TeamSubstratum/SubstratumThemeTemplate/tree/master/app/src/main/assets/fonts

You are allowed to add multiple fonts and name them differently so they show up differently on the spinner dropdown in the app.

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

## Step 5: Safeguard your theme! Don't let the pirates win!
[BETA] Has not been merged yet to the Substratum repo so the antipiracy isn't essentially doing much except telling the user it isn't licensed.

### DURING THEME DEBUGGING ON YOUR OWN PHONE
While debugging your application, you should be setting this to false so that it doesn't keep triggering antipiracy when you are working: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/SubstratumLauncher.java#L24

### If you don't want to activate AntiPiracy
Set this value to false and the antipiracy check will report back true to Substratum every time:
https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/SubstratumLauncher.java#L24

### Getting started with AntiPiracy

If you are ready to get AntiPiracy set up, you must first compile your theme as a SIGNED production APK from Android Studio (Build -> Compile Signed APK). Then launch the signed apk on your device and your log will spit out an error log under the name "SubstratumAntiPiracyLog", and you want to copy and paste that into Line 47: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/SubstratumLauncher.java#L47

Then you would need to go to Play Developer Console. Then access to your app -> Services and APIs, generate a new API key for your app and then paste it in BASE_64_LICENSE_KEY on line 46: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/SubstratumLauncher.java#L46

Finally, if you would like to change where it checks for piracy, you should just comment out the .enable lines such as if you would not like to have Amazon App Store piracy checking, just disable it below this line: https://github.com/TeamSubstratum/SubstratumThemeTemplate/blob/master/app/src/main/java/substratum/theme/template/SubstratumLauncher.java#L41

# DO NOT SHARE YOUR SUBSTRATUMLAUNCHER.JAVA FILE IF YOU OPEN SOURCE YOUR THEME AND WANT TO KEEP PIRACY!
