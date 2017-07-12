package substratum.theme.template.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import substratum.theme.template.ThemerConstants.BLACKLISTED_APPLICATIONS
import substratum.theme.template.ThemerConstants.ENABLE_BLACKLISTED_APPLICATIONS
import substratum.theme.template.ThemerConstants.ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS
import substratum.theme.template.ThemerConstants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.ThemerConstants.OTHER_THEME_SYSTEMS

object SystemInformation {

    val SUBSTRATUM_PACKAGE_NAME = "projekt.substratum"

    fun checkNetworkConnection(): Boolean? {
        var isConnected = false
        try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 www.google.com")
            val returnVal = process.waitFor()
            isConnected = returnVal == 0
        } catch (e: Exception) {
            // Suppress error
        }

        return isConnected
    }

    fun isPackageInstalled(context: Context, package_name: String): Boolean {
        try {
            val pm = context.packageManager
            val ai = context.packageManager.getApplicationInfo(package_name, 0)
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES)
            return ai.enabled
        } catch (e: Exception) {
            return false
        }

    }

    fun hasOtherThemeSystem(context: Context): Boolean {
        try {
            val pm = context.packageManager
            for (s: String in OTHER_THEME_SYSTEMS) {
                val ai = pm.getApplicationInfo(s, 0)
                pm.getPackageInfo(s, PackageManager.GET_ACTIVITIES)
                return ai.enabled
            }
        } catch (e: Exception) {
        }
        return false
    }

    fun getSubstratumUpdatedResponse(context: Context): Boolean {
        try {
            val packageInfo = context.applicationContext.packageManager
                    .getPackageInfo(SUBSTRATUM_PACKAGE_NAME, 0)
            if (packageInfo.versionCode >= MINIMUM_SUBSTRATUM_VERSION) {
                return true
            }
        } catch (e: Exception) {
            // Suppress warning
        }

        return false
    }

    fun getSelfVerifiedIntentResponse(context: Context): Int? {
        if (ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS) {
            return getSelfSignature(context)
        } else {
            return getSubstratumSignature(context)
        }
    }

    fun getSelfVerifiedPirateTools(context: Context): Boolean {
        if (ENABLE_BLACKLISTED_APPLICATIONS) {
            BLACKLISTED_APPLICATIONS
                    .filter { isPackageInstalled(context, it) }
                    .forEach { return false }
        }
        return true
    }

    fun getSelfVerifiedThemeEngines(context: Context): Boolean? {
        val isPermitted: Boolean? = OTHER_THEME_SYSTEMS.any { isPackageInstalled(context, it) }
        if (ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS) {
            return isPermitted
        } else if (isPackageInstalled(context, SUBSTRATUM_PACKAGE_NAME)) {
            return (!isPermitted!!)
        }
        return false
    }

    fun isCallingPackageAllowed(packageId: String): Boolean {
        if (packageId == SUBSTRATUM_PACKAGE_NAME) return true
        if (ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS) {
            OTHER_THEME_SYSTEMS.filter { packageId == it }.forEach { return true }
        }
        return false
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun getSubstratumSignature(context: Context): Int {
        val sigs: Array<Signature>
        try {
            sigs = context.packageManager.getPackageInfo(
                    SUBSTRATUM_PACKAGE_NAME,
                    PackageManager.GET_SIGNATURES
            ).signatures
            return sigs[0].hashCode()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun getSelfSignature(context: Context): Int {
        val sigs: Array<Signature>
        try {
            sigs = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
            ).signatures
            return sigs[0].hashCode()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }
}
