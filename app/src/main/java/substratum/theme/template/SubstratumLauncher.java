package substratum.theme.template;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    public static Boolean ENABLE_ANTI_PIRACY = false;
    // In order to retrieve your BASE64 license key your app must be uploaded to
    // Play Developer Console. Then access to your app -> Services and APIs.
    // You will need to replace "" with the code you obtained from the Play Developer Console.
    // NOTE: THIS STEP IS SKIPPABLE, BUT YOU WILL NEED TO COMMENT OUT LINE 77!
    // TODO: Themers, this is your SECOND step
    private static String BASE_64_LICENSE_KEY = "";
    // Build your signed APK using your signature, and run the app once. Check your logcat!
    // You will need to do this only when submitting to Play. Locate "SubstratumAntiPiracyLog" and
    // head down to line 66. You will need to replace "" with the code you obtained from your
    // logcat.
    // NOTE: THIS STEP IS SKIPPABLE, BUT YOU WILL NEED TO COMMENT OUT LINE 78!
    // TODO: Themers, this is your THIRD step
    private static String APK_SIGNATURE_PRODUCTION = "";
    //
    // END OF STATIC THEMER CRUISE CONTROL


    private String theme_mode = "";  // SUBSTRATUM INTERNAL USE: DO NOT TOUCH

    private static boolean isPackageInstalled(Context context, String package_name) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void startAntiPiracyCheck(final Boolean theme_legacy, final Boolean refresh_mode) {
        // TODO: Themers, this is your FOURTH step
        Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this));
        // COMMENT OUT THE ABOVE LINE ONCE YOU OBTAINED YOUR APK SIGNATURE USING
        // TWO DASHES --> //

        new PiracyChecker(this)

                // TODO: Themers, this is your FINAL step
                // To disable certain piracy features, comment it out so that it doesn't
                // trigger anti-piracy.
                .enableInstallerId(InstallerID.GOOGLE_PLAY)
                //.enableInstallerId(InstallerID.AMAZON_APP_STORE)
                .enableGooglePlayLicensing(BASE_64_LICENSE_KEY)
                .enableSigningCertificate(APK_SIGNATURE_PRODUCTION)
                // END OF THEMER TOUCHABLE OPTIONS
                // TO RETAIN INTEGRITY, PLEASE DO NOT MODIFY ANY OTHER LINES

                .callback(new PiracyCheckerCallback() {
                    @Override
                    public void allow() {
                        // If Substratum is found, then launch it with specific parameters
                        Intent launchIntent = new Intent("projekt.substratum");
                        if (isPackageInstalled(getApplicationContext(),
                                "projekt.substratum") && launchIntent != null) {
                            // Substratum is found, launch it directly
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setComponent(ComponentName.unflattenFromString(
                                    "projekt.substratum/projekt.substratum" +
                                            ".InformationActivity"));
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("theme_name", getString(R.string.ThemeName));
                            intent.putExtra("theme_pid", getApplicationContext()
                                    .getPackageName());
                            intent.putExtra("theme_legacy", theme_legacy);
                            intent.putExtra("theme_mode", theme_mode);
                            intent.putExtra("refresh_mode", refresh_mode);
                            PackageManager p = getPackageManager();
                            ComponentName componentName = new ComponentName(
                                    getApplicationContext(), LauncherActivity.class);
                            p.setComponentEnabledSetting(
                                    componentName,
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                    PackageManager.DONT_KILL_APP);
                            startActivity(intent);
                            finish();
                        } else {
                            String playURL =
                                    "https://play.google.com/store/apps/details?" +
                                            "id=projekt.substratum&hl=en";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getString(R.string
                                            .toast_substratum),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            i.setData(Uri.parse(playURL));
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void dontAllow(PiracyCheckerError error) {
                        PackageManager p = getPackageManager();
                        p.setComponentEnabledSetting(
                                getComponentName(),
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                        String parse = String.format(getString(R.string.toast_unlicensed),
                                getString(R.string.ThemeName));
                        Toast toast = Toast.makeText(getApplicationContext(), parse,
                                Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                })
                .start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean is_updating = false;
        Boolean is_substratum_installed = isPackageInstalled(getApplicationContext(),
                "projekt.substratum");

        if (is_substratum_installed) {
            try {
                Context mContext = getApplicationContext().createPackageContext(
                        "projekt.substratum", Context.CONTEXT_IGNORE_SECURITY);
                SharedPreferences mPrefs = mContext.getSharedPreferences("substratum_state",
                        Activity.MODE_PRIVATE);
                is_updating = mPrefs.getBoolean("is_updating", false);
            } catch (Exception e) {
                // Exception
            }

            if (is_updating) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string
                                .toast_updating),
                        Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        System.exit(0);
                    }
                };
                handler.postDelayed(runnable, 2000);
            } else {
                Intent currentIntent = getIntent();
                theme_mode = currentIntent.getStringExtra("theme_mode");
                final Boolean theme_legacy = currentIntent.getBooleanExtra("theme_legacy", false);
                final Boolean refresh_mode = currentIntent.getBooleanExtra("refresh_mode", false);
                if (theme_mode == null) {
                    theme_mode = "";
                }
                if (ENABLE_ANTI_PIRACY) {
                    startAntiPiracyCheck(theme_legacy, refresh_mode);
                } else {
                    // If Substratum is found, then launch it with specific parameters
                    Intent launchIntent = new Intent("projekt.substratum");
                    if (launchIntent != null) {
                        // Substratum is found, launch it directly
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(ComponentName.unflattenFromString(
                                "projekt.substratum/projekt.substratum" +
                                        ".InformationActivity"));
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("theme_name", getString(R.string.ThemeName));
                        intent.putExtra("theme_pid", getApplicationContext()
                                .getPackageName());
                        intent.putExtra("theme_legacy", theme_legacy);
                        intent.putExtra("theme_mode", theme_mode);
                        intent.putExtra("refresh_mode", refresh_mode);
                        PackageManager p = getPackageManager();
                        ComponentName componentName = new ComponentName(this,
                                LauncherActivity.class);
                        p.setComponentEnabledSetting(
                                componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        } else {
            String playURL =
                    "https://play.google.com/store/apps/details?" +
                            "id=projekt.substratum&hl=en";
            Intent i = new Intent(Intent.ACTION_VIEW);
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string
                            .toast_substratum),
                    Toast.LENGTH_SHORT);
            toast.show();
            i.setData(Uri.parse(playURL));
            startActivity(i);
            finish();
        }
    }
}