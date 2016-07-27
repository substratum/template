package substratum.theme.template;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
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

    // THEMERS: Control whether Anti-Piracy should be activated while testing
    public static Boolean ENABLE_ANTI_PIRACY = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION!!!!!!!
        Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this));
        // COMMENT OUT THE ABOVE LINE ONCE YOU OBTAINED YOUR APK SIGNATURE USING TWO DASHES --> //
        // Build your signed APK using your signature, and run the app once. Check your logcat!
        // You will need to do this only when submitting to Play.
        // Copy what you get into APK_SIGNATURE_PRODUCTION below (SubstratumAntiPiracyLog).

        if (ENABLE_ANTI_PIRACY) {
            new PiracyChecker(this)
                    // To disable certain piracy features, just remove the whole line, or comment it
                    // out so that it doesn't trigger antipiracy.
                    .enableInstallerId(InstallerID.GOOGLE_PLAY)
                    //.enableInstallerId(InstallerID.AMAZON_APP_STORE)

                    // In order to retrieve your BASE64 license key your app must be uploaded to the
                    // Play Developer Console. Then access to your app -> Services and APIs.
                    .enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
                    .enableSigningCertificate("APK_SIGNATURE_PRODUCTION")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            // If Substratum is found, then launch it with specific parameters
                            Intent launchIntent = new Intent("projekt.substratum");
                            if (launchIntent != null) {
                                // Substratum is found, launch it directly into InformationActivity
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
                                startActivity(intent);
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
                            }
                        }

                        @Override
                        public void dontAllow(PiracyCheckerError error) {
                            String parse = String.format(getString(R.string.toast_unlicensed),
                                    getString(R.string.ThemeName));
                            Toast toast = Toast.makeText(getApplicationContext(), parse,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    })
                    .start();
        } else {
            // If Substratum is found, then launch it with specific parameters
            Intent launchIntent = new Intent("projekt.substratum");
            if (launchIntent != null) {
                // Substratum is found, launch it directly into InformationActivity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(ComponentName.unflattenFromString(
                        "projekt.substratum/projekt.substratum.InformationActivity"));

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("theme_name", getString(R.string.ThemeName));
                intent.putExtra("theme_pid", getApplicationContext()
                        .getPackageName());
                startActivity(intent);
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
            }
        }

    }
}