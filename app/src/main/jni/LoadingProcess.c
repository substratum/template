#include <jni.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
/*
 * Piracy Check
 *
 * Change this value to JNI_TRUE if you would like to enable anti piracy
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAPStatus(JNIEnv *env) {
    return JNI_FALSE;
}

/*
 * Base 64 License Key
 *
 * Insert your base 64 license key obtained from Play Store in the quotations below!
 * ATTENTION: On line 23, do you see the "000"? You MUST count the amount of characters of your key
 * and input the proper number! This is so that you get your own specialized set of variables!
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBase64Key(JNIEnv *env) {
    // TODO: This must be done!
    char BASE_64_LICENSE_KEY[000] = "";
    return (*env)->NewStringUTF(env, BASE_64_LICENSE_KEY);
}

/*
 * APK Signature Production
 *
 * Insert your release APK's signature in the quotations below!
 * ATTENTION: On line 34, do you see the "28"? If you're disabling antipiracy, change that to 0!
 * All release production signature prefixes have the length of 28!
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAPKSignatureProduction(JNIEnv *env) {
    char APK_SIGNATURE_PRODUCTION[28] = "";
    return (*env)->NewStringUTF(env, APK_SIGNATURE_PRODUCTION);
}

/*
 * Enforce Internet Check
 *
 * Change this value to JNI_TRUE if you would like to enable internet check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getInternetCheck(JNIEnv *env) {
    return JNI_FALSE;
}

/*
 * Enforce Google Play Install
 *
 * Change this value to JNI_TRUE if you would like to enable this check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getGooglePlayRequirement(JNIEnv *env) {
    return JNI_FALSE;
}

/*
 * Enforce Amazon App Store Install
 *
 * Change this value to JNI_TRUE if you would like to enable this check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAmazonAppStoreRequirement(JNIEnv *env) {
    return JNI_FALSE;
}

/*
 * Enable check for Blacklisted APKs
 *
 * Change this value to JNI_TRUE if you would like to enable blacklist check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBlacklistedApplications(JNIEnv *env) {
    return JNI_FALSE;
}

#pragma clang diagnostic pop