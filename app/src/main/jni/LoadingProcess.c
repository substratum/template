#include <jni.h>
#include <LoadingProcess.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

// Make sure to fill in those values if you enable anti piracy
char APK_SIGNATURE_PRODUCTION[] = ""; // Signature string value from release key
char BASE_64_LICENSE_KEY[] = ""; // Base64 license key from Google Play Console

// You can change those values if you enable anti piracy
jboolean ENABLE_APP_BLACKLIST_CHECK = JNI_FALSE; // You can see the blacklisted app in Constants.kt line 31
jboolean ENABLE_INTERNET_CHECK = JNI_FALSE;
jboolean REQUIRE_INSTALL_FROM_PLAY_STORE = JNI_FALSE;
jboolean REQUIRE_INSTALL_FROM_AMAZON_STORE = JNI_FALSE;

// Change this value to JNI_FALSE to block users from using your theme with third party substratum build
jboolean ALLOW_THIRD_PARTY_SUBSTRATUM_BUILD = JNI_TRUE;

/*
 * APK Signature Production
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAPKSignatureProduction(JNIEnv *env) {
    return (*env)->NewStringUTF(env, APK_SIGNATURE_PRODUCTION);
}

/*
 * Base 64 License Key
 */
JNIEXPORT jstring JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBase64Key(JNIEnv *env) {
    return (*env)->NewStringUTF(env, BASE_64_LICENSE_KEY);
}

/*
 * Enforce Internet Check
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getInternetCheck(JNIEnv *env) {
    return ENABLE_INTERNET_CHECK;
}

/*
 * Enforce Google Play Install
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getGooglePlayRequirement(JNIEnv *env) {
    return REQUIRE_INSTALL_FROM_PLAY_STORE;
}

/*
 * Enforce Amazon App Store Install
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getAmazonAppStoreRequirement(JNIEnv *env) {
    return REQUIRE_INSTALL_FROM_AMAZON_STORE;
}

/*
 * Enable check for Blacklisted APKs
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_getBlacklistedApplications(JNIEnv *env) {
    return ENABLE_APP_BLACKLIST_CHECK;
}

/*
 * Allow Third Party Substratum Builds
 *
 * WARNING: Having this enabled may or may not cause a bunch of issues due to the system not built
 *          and distributed by an official team member. You will take charge of handling bug reports
 *          if there are specific bugs unreproducible on the main stream of APKs.
 */
JNIEXPORT jboolean JNICALL
Java_substratum_theme_template_SubstratumLauncher_allowThirdPartySubstratumBuilds(JNIEnv *env) {
    return ALLOW_THIRD_PARTY_SUBSTRATUM_BUILD;
}

JNIEXPORT jbyteArray JNICALL
Java_substratum_theme_template_SubstratumLauncher_getDecryptionKey(JNIEnv *env) {
    jbyteArray ret = (*env)->NewByteArray(env, 16);
    (*env)->SetByteArrayRegion(env, ret, 0, 16, DECRYPTION_KEY);
    return ret;
}

JNIEXPORT jbyteArray JNICALL
Java_substratum_theme_template_SubstratumLauncher_getIVKey(JNIEnv *env) {
    jbyteArray ret = (*env)->NewByteArray(env, 16);
    (*env)->SetByteArrayRegion(env, ret, 0, 16, IV_KEY);
    return ret;
}

#pragma clang diagnostic pop