package substratum.theme.template;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author Nicholas Chum (nicholaschum)
 */

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean is_updating = false;
        try {
            Context myContext = getApplicationContext().createPackageContext
                    ("projekt.substratum", Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences testPrefs = myContext.getSharedPreferences
                    ("substratum_state", Context.MODE_WORLD_READABLE);
            is_updating = testPrefs.getBoolean("is_updating", true);
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
            System.exit(0);
        } else {
            // Hide the app icon automatically if the theme isn't updating, we don't need the icon
            // anymore!
            PackageManager p = getPackageManager();
            p.setComponentEnabledSetting(
                    getComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            Intent intent = new Intent(this, SubstratumLauncher.class);
            startActivity(intent);
            this.finish();
            System.exit(0);
        }
        finish();
    }
}