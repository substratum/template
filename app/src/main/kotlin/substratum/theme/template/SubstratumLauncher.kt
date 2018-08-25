@file:Suppress("ConstantConditionIf")

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
import substratum.theme.template.AdvancedConstants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.AdvancedConstants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.AdvancedConstants.ORGANIZATION_THEME_SYSTEMS
import substratum.theme.template.AdvancedConstants.OTHER_THEME_SYSTEMS
import substratum.theme.template.AdvancedConstants.SHOW_DIALOG_REPEATEDLY
import substratum.theme.template.AdvancedConstants.SHOW_LAUNCH_DIALOG
import substratum.theme.template.AdvancedConstants.SUBSTRATUM_FILTER_CHECK
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

/**
 * NOTE TO THEMERS
 *
 * This class is a TEMPLATE of how you should be launching themes. As long as you keep the structure
 * of launching themes the same, you can avoid easy script crackers by changing how
 * functions/methods are coded, as well as boolean variable placement.
 *
 * The more you play with this the harder it would be to decompile and crack!
 */

class SubstratumLauncher : Activity() {

    private var substratumIntentData = "projekt.substratum.THEME"
    private var getKeysIntent = "projekt.substratum.GET_KEYS"
    private var receiveKeysIntent = "projekt.substratum.RECEIVE_KEYS"
    private var tag = "SubstratumThemeReport"
    private var piracyChecker: PiracyChecker? = null

    private fun calibrateSystem(certified: Boolean) {
        if (!BuildConfig.DEBUG) { // Themers may want to change this for release builds!
            startAntiPiracyCheck(certified)
        } else {
            quitSelf(certified)
        }
    }

    private fun startAntiPiracyCheck(certified: Boolean) {
        if (piracyChecker != null) {
            piracyChecker!!.start()
        } else {
            if (BuildConfig.BASE_64_LICENSE_KEY.isEmpty() && !BuildConfig.DEBUG) {
                Log.e(tag, PiracyCheckerUtils.getAPKSignature(this))
            }

            piracyChecker = PiracyChecker(this)
            if (BuildConfig.ENFORCE_GOOGLE_PLAY_INSTALL)
                piracyChecker!!.enableInstallerId(InstallerID.GOOGLE_PLAY)
            if (BuildConfig.ENFORCE_AMAZON_APP_STORE_INSTALL)
                piracyChecker!!.enableInstallerId(InstallerID.AMAZON_APP_STORE)

            piracyChecker!!.callback(object : PiracyCheckerCallback() {
                override fun allow() {
                    quitSelf(certified)
                }

                override fun dontAllow(error: PiracyCheckerError, pirateApp: PirateApp?) {
                    val parse = String.format(
                            getString(R.string.toast_unlicensed),
                            getString(R.string.ThemeName))
                    Toast.makeText(this@SubstratumLauncher, parse, Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

            if (BuildConfig.BASE_64_LICENSE_KEY.isNotEmpty()) {
                piracyChecker!!.enableGooglePlayLicensing(BuildConfig.BASE_64_LICENSE_KEY)
            }
            if (BuildConfig.APK_SIGNATURE_PRODUCTION.isNotEmpty()) {
                piracyChecker!!.enableSigningCertificate(BuildConfig.APK_SIGNATURE_PRODUCTION)
            }
            piracyChecker!!.start()
        }
    }

    private fun quitSelf(certified: Boolean): Boolean {
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
        } else if (!BuildConfig.SUPPORTS_THIRD_PARTY_SYSTEMS) {
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

        val themeLaunchType = getSelfVerifiedThemeEngines(applicationContext)
        val themeHash = getSelfSignature(applicationContext)
        var themePiracyCheck = false
        if (BuildConfig.ENABLE_APP_BLACKLIST_CHECK)
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
        returnIntent.putExtra("encryption_key", BuildConfig.DECRYPTION_KEY)
        returnIntent.putExtra("iv_encrypt_key", BuildConfig.IV_KEY)

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

        // Reject all other apps trying to hijack the theme first
        val caller = callingActivity?.packageName
        var callerVerified = false

        val themeSystems: MutableList<String> = mutableListOf()
        themeSystems.addAll(ORGANIZATION_THEME_SYSTEMS)
        themeSystems.addAll(OTHER_THEME_SYSTEMS)
        if (caller != null) {
            themeSystems
                    .filter { caller.startsWith(prefix = it, ignoreCase = true) }
                    .forEach { callerVerified = true }
        }
        if (!callerVerified) {
            Log.e(tag, "This theme does not support the launching theme system. [HIJACK] ($caller)")
            val hijackString =
                    String.format(getString(R.string.unauthorized_theme_client_hijack), caller)
            Toast.makeText(this, hijackString, Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Log.d(tag, "'$caller' has been authorized to launch this theme. (Phase 1)")
        }

        // We will ensure that our support is added where it belongs
        val intent = intent
        val action = intent.action
        var verified = false
        val certified = intent.getBooleanExtra("certified", false)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if ((action == substratumIntentData) or (action == getKeysIntent)) {
            verified = when {
                BuildConfig.ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS -> true
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
            Log.d(tag, "'$action' has been authorized to launch this theme. (Phase 2)")
        }

        if (SHOW_LAUNCH_DIALOG) run {
            if (SHOW_DIALOG_REPEATEDLY) {
                showDialog(certified)
                sharedPref.edit().remove("dialog_showed").apply()
            } else if (!sharedPref.getBoolean("dialog_showed", false)) {
                showDialog(certified)
                sharedPref.edit().putBoolean("dialog_showed", true).apply()
            } else {
                if (BuildConfig.ENFORCE_INTERNET_CHECK) {
                    if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                        calibrateSystem(certified)
                    } else {
                        checkConnection(certified)
                    }
                } else {
                    calibrateSystem(certified)
                }
            }
        } else if (BuildConfig.ENFORCE_INTERNET_CHECK) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                calibrateSystem(certified)
            } else {
                checkConnection(certified)
            }
        } else {
            calibrateSystem(certified)
        }
    }

    private fun checkConnection(certified: Boolean) {
        val editor = getPreferences(Context.MODE_PRIVATE).edit()
        editor.putInt("last_version", BuildConfig.VERSION_CODE).apply()
        calibrateSystem(certified)
    }

    private fun showDialog(certified: Boolean) {
        val dialog = AlertDialog.Builder(this, R.style.DialogStyle)
                .setCancelable(true)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.launch_dialog_title)
                .setMessage(R.string.launch_dialog_content)
                .setPositiveButton(R.string.launch_dialog_positive) { _, _ ->
                    val sharedPref = getPreferences(Context.MODE_PRIVATE)
                    if (BuildConfig.ENFORCE_INTERNET_CHECK) {
                        if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                            calibrateSystem(certified)
                        } else {
                            checkConnection(certified)
                        }
                    } else {
                        calibrateSystem(certified)
                    }
                }
        if (getString(R.string.launch_dialog_negative).isNotEmpty()) {
            if (getString(R.string.launch_dialog_negative_url).isNotEmpty()) {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.launch_dialog_negative_url))))
                    finish()
                }
            } else {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ -> finish() }
            }
        }
        dialog.show()
    }
}
