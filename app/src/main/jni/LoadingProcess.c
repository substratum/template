#include <jni.h>
#include "GeneratedInformation.h"

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
/*
 * Piracy Check
 *
 * Change this value to JNI_TRUE if you would like to enable anti piracy
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAPStatus(JNIEnv *env) {
    return ENABLE_ANTIPIRACY;
}

/*
 * Base 64 License Key
 *
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBase64Key(JNIEnv *env) {
    return (*env)->NewStringUTF(env, BASE64_LICENSE_KEY);
}

/*
 * APK Signature Production
 *
 * Insert your release APK's signature in the quotations below!
 * All release production signature prefixes have the length of 28!
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAPKSignatureProduction(JNIEnv *env) {
    return (*env)->NewStringUTF(env, APK_SIGNATURE);
}

/*
 * Enforce Internet Check
 *
 * Change this value to JNI_TRUE if you would like to enable internet check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getInternetCheck(JNIEnv *env) {
    return INTERNET_CHECK;
}

/*
 * Enforce Google Play Install
 *
 * Change this value to JNI_TRUE if you would like to enable this check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getGooglePlayRequirement(JNIEnv *env) {
    return ENFORCE_GOOGLE_PLAY_INSTALL;
}

/*
 * Enforce Amazon App Store Install
 *
 * Change this value to JNI_TRUE if you would like to enable this check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAmazonAppStoreRequirement(JNIEnv *env) {
    return ENFORCE_AMAZON_APP_INSTALL;
}

/*
 * Enable check for Blacklisted APKs
 *
 * Change this value to JNI_TRUE if you would like to enable blacklist check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBlacklistedApplications(JNIEnv *env) {
    return ENFORCE_BLACKLISTED_APKS_CHECK;
}

/*
 * Allow Third Party Substratum Builds
 *
 * Change this value to JNI_FALSE if you would like to ban your theme to work on external, non-team
 * built compilations of substratum
 *
 * WARNING: Having this enabled may or may not cause a bunch of issues due to the system not built
 *          and distributed by an official team member. You will take charge of handling bug reports
 *          if there are specific bugs unreproducible on the main stream of APKs.
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_allowThirdPartySubstratumBuilds(JNIEnv *env) {
    return ALLOW_THIRD_PARTY_SUBSTRATUM_BUILD;
}

/*
 * Enable Samsung theming
 *
 * Change Line 94 to JNI_FALSE for official Samsung support!
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getSamsungSupport(JNIEnv *env) {
    return Java_substratum_theme_template_SubstratumLauncher_allowThirdPartySubstratumBuilds(env);
}

#pragma clang diagnostic pop