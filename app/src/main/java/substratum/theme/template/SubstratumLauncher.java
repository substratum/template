package substratum.theme.template;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SubstratumLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            intent.putExtra("theme_name", getString(R.string.theme_name));
            intent.putExtra("theme_pid", getApplicationContext().getPackageName());
            startActivity(intent);
        } else {
            String playURL =
                    "https://play.google.com/store/apps/details?id=projekt.substratum&hl=en";
            Intent i = new Intent(Intent.ACTION_VIEW);
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string
                            .toast_substratum),
                    Toast.LENGTH_SHORT);
            toast.show();
            i.setData(Uri.parse(playURL));
            startActivity(i);
        }
        this.finish();
    }
}