package substratum.theme.template

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import substratum.theme.template.Constants.ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS
import substratum.theme.template.Constants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.Constants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.Constants.OTHER_THEME_SYSTEMS
import substratum.theme.template.Constants.SHOW_DIALOG_REPEATEDLY
import substratum.theme.template.Constants.SHOW_LAUNCH_DIALOG
import substratum.theme.template.Constants.SUBSTRATUM_FILTER_CHECK
import substratum.theme.template.ThemeFunctions.SUBSTRATUM_PACKAGE_NAME
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

@Suppress("ConstantConditionIf") // This needs to be defined by the themer, so suppress!
class SubstratumLauncher : Activity() {

    private var substratumIntentData = "projekt.substratum.THEME"
    private var getKeysIntent = "projekt.substratum.GET_KEYS"
    private var receiveKeysIntent = "projekt.substratum.RECEIVE_KEYS"
    private var tag = "SubstratumThemeReport"
    private var piracyChecker: PiracyChecker? = null

    private fun calibrateSystem(certified: Boolean, modeLaunch: String?) {
        if (!BuildConfig.DEBUG) {
            startAntiPiracyCheck(certified, modeLaunch)
        } else {
            quitSelf(certified, modeLaunch)
        }
    }

    private fun startAntiPiracyCheck(certified: Boolean, modeLaunch: String?) {
        if (piracyChecker != null) {
            piracyChecker!!.start()
        } else {
            if (getAPKSignatureProduction().isEmpty() && !BuildConfig.DEBUG) {
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
        if (themePiracyCheck or (SUBSTRATUM_FILTER_CHECK && !certified)) {
            Toast.makeText(this, R.string.unauthorized, Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        returnIntent.putExtra("theme_hash", themeHash)
        returnIntent.putExtra("theme_launch_type", themeLaunchType)
        returnIntent.putExtra("theme_debug", BuildConfig.DEBUG)
        returnIntent.putExtra("theme_piracy_check", themePiracyCheck)
        returnIntent.putExtra("encryption_key", getDecryptionKey())
        returnIntent.putExtra("iv_encrypt_key", getIVKey())

        val callingPackage = intent.getStringExtra("calling_package_name")
        if (callingPackage == null) {
            val parse = String.format(
                    getString(R.string.outdated_substratum),
                    getString(R.string.ThemeName),
                    915)
            Toast.makeText(this, parse, Toast.LENGTH_SHORT).show()
            finish()
            return false
        }
        if (!isCallingPackageAllowed(callingPackage)) {
            return false
        } else {
            returnIntent.`package` = callingPackage
        }

        if (intent.action == substratumIntentData) {
            setResult(getSelfVerifiedIntentResponse(applicationContext)!!, returnIntent)
        } else if (intent.action == getKeysIntent) {
            returnIntent.action = receiveKeysIntent
            sendBroadcast(returnIntent)
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
        val certified = intent.getBooleanExtra("certified", false)
        val modeLaunch: String? = intent.getStringExtra("theme_mode")

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if ((action == substratumIntentData) or (action == getKeysIntent)) {
            verified = when {
                allowThirdPartySubstratumBuilds() -> true
                else -> checkSubstratumIntegrity(this)
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

        if (SHOW_LAUNCH_DIALOG) run {
            if (SHOW_DIALOG_REPEATEDLY) {
                showDialog(certified, modeLaunch)
                sharedPref.edit().remove("dialog_showed").apply()
            } else if (!sharedPref.getBoolean("dialog_showed", false)) {
                showDialog(certified, modeLaunch)
                sharedPref.edit().putBoolean("dialog_showed", true).apply()
            } else {
                if (getInternetCheck()) {
                    if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                        calibrateSystem(certified, modeLaunch)
                    } else {
                        checkConnection(certified, modeLaunch)
                    }
                } else {
                    calibrateSystem(certified, modeLaunch)
                }
            }
        } else if (getInternetCheck()) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                calibrateSystem(certified, modeLaunch)
            } else {
                checkConnection(certified, modeLaunch)
            }
        } else {
            calibrateSystem(certified, modeLaunch)
        }
    }

    private fun checkConnection(certified: Boolean, modeLaunch: String?) {
        val editor = getPreferences(Context.MODE_PRIVATE).edit()
        editor.putInt("last_version", BuildConfig.VERSION_CODE).apply()
        calibrateSystem(certified, modeLaunch)
    }

    private fun showDialog(certified: Boolean, modeLaunch: String?) {
        val dialog = AlertDialog.Builder(this, R.style.DialogStyle)
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.launch_dialog_title)
                .setMessage(R.string.launch_dialog_content)
                .setPositiveButton(R.string.launch_dialog_positive) { _, _ ->
                    val sharedPref = getPreferences(Context.MODE_PRIVATE)
                    if (getInternetCheck()) {
                        if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                            calibrateSystem(certified, modeLaunch)
                        } else {
                            checkConnection(certified, modeLaunch)
                        }
                    } else {
                        calibrateSystem(certified, modeLaunch)
                    }
                }
        if (getString(R.string.launch_dialog_negative).isNotEmpty()) {
            if (getString(R.string.launch_dialog_negative_url).isNotEmpty()) {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.launch_dialog_negative_url))))
                    finish()
                }
            } else {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ -> finish() }
            }
        }
        dialog.show()
    }

    // Load up the JNI library
    init {
        System.loadLibrary("LoadingProcess")
    }

    private external fun getInternetCheck(): Boolean
    private external fun getGooglePlayRequirement(): Boolean
    private external fun getAmazonAppStoreRequirement(): Boolean
    private external fun getBase64Key(): String
    private external fun getAPKSignatureProduction(): String
    private external fun getBlacklistedApplications(): Boolean
    private external fun allowThirdPartySubstratumBuilds(): Boolean
    private external fun getDecryptionKey(): ByteArray
    private external fun getIVKey(): ByteArray
}