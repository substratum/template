package substratum.theme.template;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.PiracyCheckerUtils;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;

/**
 * @author Nicholas Chum (nicholaschum)
 */

public class SubstratumLauncher extends Activity {

    // < STATIC THEMER CRUISE CONTROL >
    // On Android Studio, please open the bottom window tab before continuing: TODO
    // You MUST complete ALL 5 steps!
    //
    // TODO: Themers, this is your FIRST step
    // UNIVERSAL SWITCH: Control whether Anti-Piracy should be activated while testing
    private static final boolean ENABLE_ANTI_PIRACY = false;
    // In order to retrieve your BASE64 license key your app must be uploaded to
    // Play Developer Console. Then access to your app -> Services and APIs.
    // You will need to replace "" with the code you obtained from the Play Developer Console.
    // If ENABLE_ANTI_PIRACY is false, you may skip this
    // TODO: Themers, this is your SECOND step
    private static final String BASE_64_LICENSE_KEY = "";
    // Build your signed APK using your signature, and run the app once in Substratum
    // (open your theme). Check your logcat!
    // You will need to do this only when submitting to Play. Locate "SubstratumAntiPiracyLog" and
    // head down to line 51. You will need to replace "" with the code you obtained from your
    // logcat.
    // If ENABLE_ANTI_PIRACY is false, you may skip this
    // TODO: Themers, this is your THIRD step
    private static final String APK_SIGNATURE_PRODUCTION = "";
    //
    // END OF STATIC THEMER CRUISE CONTROL

    private void startAntiPiracyCheck() {
        // TODO: Themers, this is your FOURTH step
        Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this));
        // COMMENT OUT THE ABOVE LINE ONCE YOU OBTAINED YOUR APK SIGNATURE USING
        // TWO DASHES (LIKE THIS EXACT LINE)

        PiracyChecker piracyChecker = new PiracyChecker(this)

                // TODO: Themers, this is your FINAL step
                // To disable certain piracy features, comment it out so that it doesn't
                // trigger anti-piracy.
                .enableInstallerId(InstallerID.GOOGLE_PLAY)
                //.enableInstallerId(InstallerID.AMAZON_APP_STORE)
                // END OF THEMER TOUCHABLE OPTIONS
                // TO RETAIN INTEGRITY, PLEASE DO NOT MODIFY ANY OTHER LINES

                .callback(new PiracyCheckerCallback() {
                    @Override
                    public void allow() {
                        beginSubstratumLaunch();
                    }

                    @Override
                    public void dontAllow(PiracyCheckerError error) {
                        String parse = String.format(getString(R.string.toast_unlicensed),
                                getString(R.string.ThemeName));
                        Toast toast = Toast.makeText(SubstratumLauncher.this, parse,
                                Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });

        if (BASE_64_LICENSE_KEY.length() > 0) {
            piracyChecker.enableGooglePlayLicensing(BASE_64_LICENSE_KEY);
        }
        if (APK_SIGNATURE_PRODUCTION.length() > 0) {
            piracyChecker.enableSigningCertificate(APK_SIGNATURE_PRODUCTION);
        }
        piracyChecker.start();
    }

    /**
     * Other variables/methods; do not modify
     */

    private static final String SUBSTRATUM_PACKAGE_NAME = "projekt.substratum";

    private boolean isPackageInstalled(String package_name) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPackageEnabled(String package_name) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(package_name, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void beginSubstratumLaunch() {
        // If Substratum is found, then launch it with specific parameters
        if (isPackageInstalled(SUBSTRATUM_PACKAGE_NAME)) {
            if (!isPackageEnabled(SUBSTRATUM_PACKAGE_NAME)) {
                Toast toast = Toast.makeText(this, getString(R.string.toast_substratum_frozen),
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // Substratum is found, launch it directly
            launchSubstratum();
        } else {
            getSubstratumFromPlayStore();
        }
    }

    private void getSubstratumFromPlayStore() {
        String playURL =
                "https://play.google.com/store/apps/details?" +
                        "id=projekt.substratum&hl=en";
        Intent i = new Intent(Intent.ACTION_VIEW);
        Toast toast = Toast.makeText(this, getString(R.string.toast_substratum),
                Toast.LENGTH_SHORT);
        toast.show();
        i.setData(Uri.parse(playURL));
        startActivity(i);
        finish();
    }

    private void launchSubstratum() {
        Intent currentIntent = getIntent();
        String theme_mode = currentIntent.getStringExtra("theme_mode");
        if (theme_mode == null) theme_mode = "";
        final boolean theme_legacy = currentIntent.getBooleanExtra("theme_legacy", false);
        final boolean refresh_mode = currentIntent.getBooleanExtra("refresh_mode", false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(ComponentName.unflattenFromString(
                "projekt.substratum/projekt.substratum.InformationActivity"));
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("theme_name", getString(R.string.ThemeName));
        intent.putExtra("theme_pid", getPackageName());
        intent.putExtra("theme_legacy", theme_legacy);
        intent.putExtra("theme_mode", theme_mode);
        intent.putExtra("refresh_mode", refresh_mode);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ENABLE_ANTI_PIRACY && !BuildConfig.DEBUG) {
            startAntiPiracyCheck();
        } else {
            beginSubstratumLaunch();
        }
    }
}