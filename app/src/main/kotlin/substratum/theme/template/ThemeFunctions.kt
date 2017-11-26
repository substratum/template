package substratum.theme.template

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.os.RemoteException
import android.widget.Toast
import substratum.theme.template.Constants.BLACKLISTED_APPLICATIONS
import substratum.theme.template.Constants.ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS
import substratum.theme.template.Constants.MINIMUM_SUBSTRATUM_VERSION
import substratum.theme.template.Constants.OTHER_THEME_SYSTEMS


@Suppress("ConstantConditionIf") // This needs to be defined by the themer, so suppress!
object ThemeFunctions {

    val SUBSTRATUM_PACKAGE_NAME = "projekt.substratum"

    fun isPackageInstalled(context: Context, package_name: String): Boolean {
        return try {
            val pm = context.packageManager
            val ai = context.packageManager.getApplicationInfo(package_name, 0)
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES)
            ai.enabled
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun checkSubstratumIntegrity(context: Context,
                                         packageName: String?): Boolean {
        return try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            if (pi.signatures != null
                    && pi.signatures.size == 1
                    && ((SIGNATURES[0] == pi.signatures[0]) ||
                    (SIGNATURES[1] == pi.signatures[0]))) {
                return true
            }
            false
        } catch (e: RemoteException) {
            false
        }
    }

    fun getSubstratumFromPlayStore(activity: Activity) {
        val playURL = "https://play.google.com/store/apps/details?id=projekt.substratum"
        val i = Intent(Intent.ACTION_VIEW)
        Toast.makeText(
                activity,
                activity.getString(R.string.toast_substratum),
                Toast.LENGTH_SHORT
        ).show()
        i.data = Uri.parse(playURL)
        activity.startActivity(i)
        activity.finishAffinity()
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
        return if (ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS) {
            getSelfSignature(context)
        } else {
            getSubstratumSignature(context)
        }
    }

    fun getSelfVerifiedPirateTools(context: Context): Boolean {
        BLACKLISTED_APPLICATIONS
                .filter { isPackageInstalled(context, it) }
                .forEach { return true }
        return false
    }

    fun checkSubstratumIntegrity(context: Context): Boolean {
        SIGNATURES
                .filter { checkSubstratumIntegrity(context, SUBSTRATUM_PACKAGE_NAME) }
                .forEach { return true }
        return false
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

    // Enforce a way to get official support from the team, by ensuring that only
    private val SUBSTRATUM_SIGNATURE = Signature(""
            + "308202eb308201d3a003020102020411c02f2f300d06092a864886f70d01010b050030263124302206"
            + "03550403131b5375627374726174756d20446576656c6f706d656e74205465616d301e170d31363037"
            + "30333032333335385a170d3431303632373032333335385a3026312430220603550403131b53756273"
            + "74726174756d20446576656c6f706d656e74205465616d30820122300d06092a864886f70d01010105"
            + "000382010f003082010a02820101008855626336f645a335aa5d40938f15db911556385f72f72b5f8b"
            + "ad01339aaf82ae2d30302d3f2bba26126e8da8e76a834e9da200cdf66d1d5977c90a4e4172ce455704"
            + "a22bbe4a01b08478673b37d23c34c8ade3ec040a704da8570d0a17fce3c7397ea63ebcde3a2a3c7c5f"
            + "983a163e4cd5a1fc80c735808d014df54120e2e5708874739e22e5a22d50e1c454b2ae310b480825ab"
            + "3d877f675d6ac1293222602a53080f94e4a7f0692b627905f69d4f0bb1dfd647e281cc0695e0733fa3"
            + "efc57d88706d4426c4969aff7a177ac2d9634401913bb20a93b6efe60e790e06dad3493776c2c0878c"
            + "e82caababa183b494120edde3d823333efd464c8aea1f51f330203010001a321301f301d0603551d0e"
            + "04160414203ec8b075d1c9eb9d600100281c3924a831a46c300d06092a864886f70d01010b05000382"
            + "01010042d4bd26d535ce2bf0375446615ef5bf25973f61ecf955bdb543e4b6e6b5d026fdcab09fec09"
            + "c747fb26633c221df8e3d3d0fe39ce30ca0a31547e9ec693a0f2d83e26d231386ff45f8e4fd5c06095"
            + "8681f9d3bd6db5e940b1e4a0b424f5c463c79c5748a14a3a38da4dd7a5499dcc14a70ba82a50be5fe0"
            + "82890c89a27e56067d2eae952e0bcba4d6beb5359520845f1fdb7df99868786055555187ba46c69ee6"
            + "7fa2d2c79e74a364a8b3544997dc29cc625395e2f45bf8bdb2c9d8df0d5af1a59a58ad08b32cdbec38"
            + "19fa49201bb5b5aadeee8f2f096ac029055713b77054e8af07cd61fe97f7365d0aa92d570be98acb89"
            + "41b8a2b0053b54f18bfde092eb")

    // Also allow our CI builds
    private val SUBSTRATUM_CI_SIGNATURE = Signature(""
            + "308201dd30820146020101300d06092a864886f70d010105050030373116301406035504030c0d416e"
            + "64726f69642044656275673110300e060355040a0c07416e64726f6964310b30090603550406130255"
            + "53301e170d3137303232333036303730325a170d3437303231363036303730325a3037311630140603"
            + "5504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b300906"
            + "035504061302555330819f300d06092a864886f70d010101050003818d00308189028181008aa6cf56"
            + "e3ba4d0921da3baf527529205efbe440e1f351c40603afa5e6966e6a6ef2def780c8be80d189dc6101"
            + "935e6f8340e61dc699cfd34d50e37d69bf66fbb58619d0ebf66f22db5dbe240b6087719aa3ceb1c68f"
            + "3fa277b8846f1326763634687cc286b0760e51d1b791689fa2d948ae5f31cb8e807e00bd1eb72788b2"
            + "330203010001300d06092a864886f70d0101050500038181007b2b7e432bff612367fbb6fdf8ed0ad1"
            + "a19b969e4c4ddd8837d71ae2ec0c35f52fe7c8129ccdcdc41325f0bcbc90c38a0ad6fc0c604a737209"
            + "17d37421955c47f9104ea56ad05031b90c748b94831969a266fa7c55bc083e20899a13089402be49a5"
            + "edc769811adc2b0496a8a066924af9eeb33f8d57d625a5fa150f7bc18e55")

    // Whitelisted signatures
    private val SIGNATURES = arrayOf(
            SUBSTRATUM_SIGNATURE,
            SUBSTRATUM_CI_SIGNATURE
    )
}