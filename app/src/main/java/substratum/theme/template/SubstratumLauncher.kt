package substratum.theme.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
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
import substratum.theme.template.ThemerConstants.APK_SIGNATURE_PRODUCTION
import substratum.theme.template.ThemerConstants.BASE_64_LICENSE_KEY
import substratum.theme.template.ThemerConstants.ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS
import substratum.theme.template.ThemerConstants.ENFORCE_AMAZON_APP_STORE_INSTALL
import substratum.theme.template.ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL
import substratum.theme.template.ThemerConstants.ENFORCE_INTERNET_CHECK
import substratum.theme.template.ThemerConstants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.ThemerConstants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.ThemerConstants.PIRACY_CHECK
import substratum.theme.template.ThemerConstants.SUBSTRATUM_FILTER_CHECK
import substratum.theme.template.ThemerConstants.THEME_READY_GOOGLE_APPS
import substratum.theme.template.internal.SystemInformation.SUBSTRATUM_PACKAGE_NAME
import substratum.theme.template.internal.SystemInformation.checkNetworkConnection
import substratum.theme.template.internal.SystemInformation.getSelfSignature
import substratum.theme.template.internal.SystemInformation.getSelfVerifiedIntentResponse
import substratum.theme.template.internal.SystemInformation.getSelfVerifiedPirateTools
import substratum.theme.template.internal.SystemInformation.getSelfVerifiedThemeEngines
import substratum.theme.template.internal.SystemInformation.getSubstratumUpdatedResponse
import substratum.theme.template.internal.SystemInformation.hasOtherThemeSystem
import substratum.theme.template.internal.SystemInformation.isCallingPackageAllowed
import substratum.theme.template.internal.SystemInformation.isPackageInstalled
import substratum.theme.template.internal.TBOConstants.THEME_READY_PACKAGES
import java.io.File
import java.util.*

class SubstratumLauncher : Activity() {

    private var mVerified: Boolean? = false
    private var piracyChecker: PiracyChecker? = null
    private var mModeLaunch: String? = ""

    private fun calibrateSystem() {
        if (PIRACY_CHECK && !BuildConfig.DEBUG) {
            startAntiPiracyCheck()
        } else {
            quitSelf()
        }
    }

    private fun startAntiPiracyCheck() {
        if (piracyChecker != null) {
            piracyChecker!!.start()
        } else {
            if (PIRACY_CHECK && APK_SIGNATURE_PRODUCTION.isEmpty() && !BuildConfig.DEBUG) {
                Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this))
            }

            piracyChecker = PiracyChecker(this)
            if (ENFORCE_GOOGLE_PLAY_INSTALL)
                piracyChecker!!.enableInstallerId(InstallerID.GOOGLE_PLAY)
            if (ENFORCE_AMAZON_APP_STORE_INSTALL)
                piracyChecker!!.enableInstallerId(InstallerID.AMAZON_APP_STORE)

            piracyChecker!!.callback(object : PiracyCheckerCallback() {
                override fun allow() {
                    quitSelf()
                }

                override fun dontAllow(error: PiracyCheckerError, pirateApp: PirateApp?) {
                    val parse = String.format(
                            getString(R.string.toast_unlicensed),
                            getString(R.string.ThemeName))
                    Toast.makeText(this@SubstratumLauncher, parse, Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

            if (BASE_64_LICENSE_KEY.isNotEmpty()) {
                piracyChecker!!.enableGooglePlayLicensing(BASE_64_LICENSE_KEY)
            }
            if (APK_SIGNATURE_PRODUCTION.isNotEmpty()) {
                piracyChecker!!.enableSigningCertificate(APK_SIGNATURE_PRODUCTION)
            }
            piracyChecker!!.start()
        }
    }

    private fun getSubstratumFromPlayStore() {
        val playURL = "https://play.google.com/store/apps/details?id=projekt.substratum"
        val i = Intent(Intent.ACTION_VIEW)
        Toast.makeText(this, getString(R.string.toast_substratum), Toast.LENGTH_SHORT).show()
        i.data = Uri.parse(playURL)
        startActivity(i)
        finish()
    }

    private fun quitSelf(): Boolean {
        if (!hasOtherThemeSystem(this)) {
            if (!isPackageInstalled(applicationContext, SUBSTRATUM_PACKAGE_NAME)) {
                getSubstratumFromPlayStore()
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
        if (intent.action == "projekt.substratum.GET_KEYS") {
            returnIntent = Intent("projekt.substratum.RECEIVE_KEYS")
        }

        val theme_name = getString(R.string.ThemeName)
        val theme_author = getString(R.string.ThemeAuthor)
        val theme_pid = packageName
        val theme_mode = mModeLaunch
        returnIntent.putExtra("theme_name", theme_name)
        returnIntent.putExtra("theme_author", theme_author)
        returnIntent.putExtra("theme_pid", theme_pid)
        returnIntent.putExtra("theme_mode", theme_mode)

        val theme_hash = getSelfSignature(applicationContext)
        val theme_launch_type = getSelfVerifiedThemeEngines(applicationContext)
        val theme_piracy_check = getSelfVerifiedPirateTools(applicationContext)
        if (!theme_piracy_check || SUBSTRATUM_FILTER_CHECK && (!mVerified!!)) {
            Toast.makeText(this, R.string.unauthorized, Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        returnIntent.putExtra("theme_hash", theme_hash)
        returnIntent.putExtra("theme_launch_type", theme_launch_type)
        returnIntent.putExtra("theme_debug", BuildConfig.DEBUG)
        returnIntent.putExtra("theme_piracy_check", theme_piracy_check)
        returnIntent.putExtra("encryption_key", BuildConfig.DECRYPTION_KEY)
        returnIntent.putExtra("iv_encrypt_key", BuildConfig.IV_KEY)

        if (intent.action == "projekt.substratum.THEME") {
            setResult(getSelfVerifiedIntentResponse(applicationContext)!!, returnIntent)
        } else if (intent.action == "projekt.substratum.GET_KEYS") {
            val callingPackage = intent.getStringExtra("calling_package_name")
            returnIntent.`package` = callingPackage
            returnIntent.action = "projekt.substratum.RECEIVE_KEYS"
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

        val intent = intent
        mVerified = intent.getBooleanExtra("certified", false)
        mModeLaunch = intent.getStringExtra("theme_mode")

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if (ENFORCE_INTERNET_CHECK) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                if (THEME_READY_GOOGLE_APPS) {
                    detectThemeReady()
                } else {
                    calibrateSystem()
                }
            } else {
                checkConnection()
            }
        } else if (THEME_READY_GOOGLE_APPS) {
            detectThemeReady()
        } else {
            calibrateSystem()
        }
    }

    private fun checkConnection(): Boolean {
        val isConnected = checkNetworkConnection()
        if (!isConnected!!) {
            Toast.makeText(this, R.string.toast_internet, Toast.LENGTH_LONG).show()
            return false
        } else {
            val editor = getPreferences(Context.MODE_PRIVATE).edit()
            editor.putInt("last_version", BuildConfig.VERSION_CODE).apply()
            if (THEME_READY_GOOGLE_APPS) {
                detectThemeReady()
            } else {
                calibrateSystem()
            }
            return true
        }
    }

    private fun detectThemeReady() {
        val addon = File("/system/addon.d/80-ThemeReady.sh")
        if (addon.exists()) {
            val apps = ArrayList<String>()
            var updated = false
            var incomplete = false
            val packageManager = this.packageManager
            val app_name = StringBuilder()

            if (!incomplete) {
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
            }

            for (i in apps.indices) {
                app_name.append(apps[i])
                if (i <= apps.size - 3) {
                    app_name.append(", ")
                } else if (i == apps.size - 2) {
                    app_name.append(" ").append(getString(R.string.and)).append(" ")
                }
            }

            if (!updated && !incomplete) {
                calibrateSystem()
            } else {
                val stringInt = if (incomplete)
                    R.string.theme_ready_incomplete
                else
                    R.string.theme_ready_updated
                val parse = String.format(getString(stringInt),
                        app_name)

                AlertDialog.Builder(this, R.style.DialogStyle)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.ThemeName))
                        .setMessage(parse)
                        .setPositiveButton(R.string.yes) { _, _ -> calibrateSystem() }
                        .setNegativeButton(R.string.no) { _, _ -> finish() }
                        .setOnCancelListener { finish() }
                        .show()
            }
        } else {
            AlertDialog.Builder(this, R.style.DialogStyle)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.ThemeName))
                    .setMessage(getString(R.string.theme_ready_not_detected))
                    .setPositiveButton(R.string.yes) { _, _ -> calibrateSystem() }
                    .setNegativeButton(R.string.no) { _, _ -> finish() }
                    .setOnCancelListener { finish() }
                    .show()
        }
    }
}
