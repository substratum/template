package substratum.theme.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.github.javiersantos.piracychecker.PiracyChecker
import com.github.javiersantos.piracychecker.PiracyCheckerUtils
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import substratum.theme.template.Constants.ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS
import substratum.theme.template.Constants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.Constants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.Constants.OTHER_THEME_SYSTEMS
import substratum.theme.template.Constants.SUBSTRATUM_FILTER_CHECK
import substratum.theme.template.Constants.THEME_READY_GOOGLE_APPS
import substratum.theme.template.Constants.THEME_READY_PACKAGES
import substratum.theme.template.ThemeFunctions.SUBSTRATUM_PACKAGE_NAME
import substratum.theme.template.ThemeFunctions.checkNetworkConnection
import substratum.theme.template.ThemeFunctions.checkSubstratumIntegrity
import substratum.theme.template.ThemeFunctions.getSelfSignature
import substratum.theme.template.ThemeFunctions.getSelfVerifiedIntentResponse
import substratum.theme.template.ThemeFunctions.getSelfVerifiedPirateTools
import substratum.theme.template.ThemeFunctions.getSelfVerifiedThemeEngines
import substratum.theme.template.ThemeFunctions.getSubstratumFromPlayStore
import substratum.theme.template.ThemeFunctions.getSubstratumUpdatedResponse
import substratum.theme.template.ThemeFunctions.hasOtherThemeSystem
import substratum.theme.template.ThemeFunctions.isCallingPackageAllowed
import substratum.theme.template.ThemeFunctions.isPackageInstalled
import java.io.File
import java.util.*

@Suppress("ConstantConditionIf") // This needs to be defined by the themer, so suppress!
class SubstratumLauncher : Activity() {

    private var substratumIntentData = "projekt.substratum.THEME"
    private var getKeysIntent = "projekt.substratum.GET_KEYS"
    private var receiveKeysIntent = "projekt.substratum.RECEIVE_KEYS"
    private var themeReadyScript = "/system/addon.d/80-ThemeReady.sh"
    private var tag = "SubstratumThemeReport"
    private var piracyChecker: PiracyChecker? = null

    private fun calibrateSystem(certified: Boolean, modeLaunch: String?) {
        if (getAPStatus() && !BuildConfig.DEBUG) {
            startAntiPiracyCheck(certified, modeLaunch)
        } else {
            quitSelf(certified, modeLaunch)
        }
    }

    private fun startAntiPiracyCheck(certified: Boolean, modeLaunch: String?) {
        if (piracyChecker != null) {
            piracyChecker!!.start()
        } else {
            if (getAPStatus() && getAPKSignatureProduction().isEmpty() && !BuildConfig.DEBUG) {
                Log.e(tag, PiracyCheckerUtils.getAPKSignature(this))
            }

            piracyChecker = PiracyChecker(this)
            if (getGooglePlayRequirement())
                piracyChecker!!.enableInstallerId(InstallerID.GOOGLE_PLAY)
            if (getAmazonAppStoreRequirement())
                piracyChecker!!.enableInstallerId(InstallerID.AMAZON_APP_STORE)

            piracyChecker!!.callback(object : PiracyCheckerCallback() {
                override fun allow() {
                    quitSelf(certified, modeLaunch)
                }

                override fun dontAllow(error: PiracyCheckerError, pirateApp: PirateApp?) {
                    val parse = String.format(
                            getString(R.string.toast_unlicensed),
                            getString(R.string.ThemeName))
                    Toast.makeText(this@SubstratumLauncher, parse, Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

            if (getBase64Key().isNotEmpty()) {
                piracyChecker!!.enableGooglePlayLicensing(getBase64Key())
            }
            if (getAPKSignatureProduction().isNotEmpty()) {
                piracyChecker!!.enableSigningCertificate(getAPKSignatureProduction())
            }
            piracyChecker!!.start()
        }
    }

    private fun quitSelf(certified: Boolean, modeLaunch: String?): Boolean {
        if (!hasOtherThemeSystem(this)) {
            if (!isPackageInstalled(applicationContext, SUBSTRATUM_PACKAGE_NAME)) {
                getSubstratumFromPlayStore(this)
                return false
            }
            if (ENFORCE_MINIMUM_SUBSTRATUM_VERSION
                    && !getSubstratumUpdatedResponse(applicationContext)) {
                val parse = String.format(
                        getString(R.string.outdated_substratum),
                        getString(R.string.ThemeName),
                        MINIMUM_SUBSTRATUM_VERSION.toString())
                Toast.makeText(this, parse, Toast.LENGTH_SHORT).show()
                return false
            }
        } else if (!ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS) {
            Toast.makeText(this, R.string.unauthorized_theme_client, Toast.LENGTH_LONG).show()
            finish()
            return false
        }

        var returnIntent = Intent()
        if (intent.action == getKeysIntent) {
            returnIntent = Intent(receiveKeysIntent)
        }

        val themeName = getString(R.string.ThemeName)
        val themeAuthor = getString(R.string.ThemeAuthor)
        val themePid = packageName
        returnIntent.putExtra("theme_name", themeName)
        returnIntent.putExtra("theme_author", themeAuthor)
        returnIntent.putExtra("theme_pid", themePid)
        returnIntent.putExtra("theme_mode", modeLaunch)

        val themeHash = getSelfSignature(applicationContext)
        val themeLaunchType = getSelfVerifiedThemeEngines(applicationContext)
        var themePiracyCheck = false
        if (getBlacklistedApplications())
            themePiracyCheck = getSelfVerifiedPirateTools(applicationContext)
        if (!themePiracyCheck || SUBSTRATUM_FILTER_CHECK && (!certified)) {
            Toast.makeText(this, R.string.unauthorized, Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        returnIntent.putExtra("theme_hash", themeHash)
        returnIntent.putExtra("theme_launch_type", themeLaunchType)
        returnIntent.putExtra("theme_debug", BuildConfig.DEBUG)
        returnIntent.putExtra("theme_piracy_check", themePiracyCheck)
        returnIntent.putExtra("encryption_key", BuildConfig.DECRYPTION_KEY)
        returnIntent.putExtra("iv_encrypt_key", BuildConfig.IV_KEY)

        if (intent.action == substratumIntentData) {
            setResult(getSelfVerifiedIntentResponse(applicationContext)!!, returnIntent)
        } else if (intent.action == getKeysIntent) {
            val callingPackage = intent.getStringExtra("calling_package_name")
            returnIntent.`package` = callingPackage
            returnIntent.action = receiveKeysIntent
            if (callingPackage != null) {
                if (isCallingPackageAllowed(callingPackage)) {
                    sendBroadcast(returnIntent)
                }
            }
        }
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We will ensure that our support is added where it belongs
        val intent = intent
        val action = intent.action
        var verified = false
        if (action == substratumIntentData) {
            verified = if (allowThirdPartySubstratumBuilds()) {
                true
            } else {
                checkSubstratumIntegrity(this)
            }
        } else {
            OTHER_THEME_SYSTEMS
                    .filter { action.startsWith(prefix = it, ignoreCase = true) }
                    .forEach { verified = true }
        }
        if (!verified) {
            Log.e(tag, "This theme does not support the launching theme system. ($action)")
            Toast.makeText(this, R.string.unauthorized_theme_client, Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Log.d(tag, "'$action' has been authorized to launch this theme.")
        }

        val certified = intent.getBooleanExtra("certified", false)
        val modeLaunch: String? = intent.getStringExtra("theme_mode")

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if (getInternetCheck()) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                if (THEME_READY_GOOGLE_APPS) {
                    detectThemeReady(certified, modeLaunch)
                } else {
                    calibrateSystem(certified, modeLaunch)
                }
            } else {
                checkConnection(certified, modeLaunch)
            }
        } else if (THEME_READY_GOOGLE_APPS) {
            detectThemeReady(certified, modeLaunch)
        } else {
            calibrateSystem(certified, modeLaunch)
        }
    }

    private fun checkConnection(certified: Boolean, modeLaunch: String?): Boolean {
        val isConnected = checkNetworkConnection()
        return if (!isConnected!!) {
            Toast.makeText(this, R.string.toast_internet, Toast.LENGTH_LONG).show()
            false
        } else {
            val editor = getPreferences(Context.MODE_PRIVATE).edit()
            editor.putInt("last_version", BuildConfig.VERSION_CODE).apply()
            if (THEME_READY_GOOGLE_APPS) {
                detectThemeReady(certified, modeLaunch)
            } else {
                calibrateSystem(certified, modeLaunch)
            }
            true
        }
    }

    private fun detectThemeReady(certified: Boolean, modeLaunch: String?) {
        val addon = File(themeReadyScript)
        if (addon.exists()) {
            val apps = ArrayList<String>()
            var updated = false
            val incomplete = false
            val packageManager = this.packageManager
            val appName = StringBuilder()

            for (packageName in THEME_READY_PACKAGES) {
                try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    if (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                        updated = true
                        apps.add(packageManager.getApplicationLabel(appInfo).toString())
                    }
                } catch (e: Exception) {
                    // Package not found
                }
            }

            for (i in apps.indices) {
                appName.append(apps[i])
                if (i <= apps.size - 3) {
                    appName.append(", ")
                } else if (i == apps.size - 2) {
                    appName.append(" ")
                            .append(getString(R.string.and))
                            .append(" ")
                }
            }

            if (!updated && !incomplete) {
                calibrateSystem(certified, modeLaunch)
            } else {
                val stringInt = R.string.theme_ready_updated
                val parse = String.format(getString(stringInt),
                        appName)

                AlertDialog.Builder(this, R.style.DialogStyle)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.ThemeName))
                        .setMessage(parse)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            calibrateSystem(certified, modeLaunch)
                        }
                        .setNegativeButton(R.string.no) { _, _ -> finish() }
                        .setOnCancelListener { finish() }
                        .show()
            }
        } else {
            AlertDialog.Builder(this, R.style.DialogStyle)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.ThemeName))
                    .setMessage(getString(R.string.theme_ready_not_detected))
                    .setPositiveButton(R.string.yes) { _, _ -> calibrateSystem(certified, modeLaunch) }
                    .setNegativeButton(R.string.no) { _, _ -> finish() }
                    .setOnCancelListener { finish() }
                    .show()
        }
    }

    // Load up the JNI library
    init {
        System.loadLibrary("LoadingProcess")
    }

    private external fun getAPStatus(): Boolean
    private external fun getInternetCheck(): Boolean
    private external fun getGooglePlayRequirement(): Boolean
    private external fun getAmazonAppStoreRequirement(): Boolean
    private external fun getBase64Key(): String
    private external fun getAPKSignatureProduction(): String
    private external fun getBlacklistedApplications(): Boolean
    private external fun allowThirdPartySubstratumBuilds(): Boolean
}