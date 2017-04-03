package substratum.theme.template;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.PiracyCheckerUtils;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import java.io.File;
import java.util.ArrayList;

import projekt.substrate.SubstratumLoader;

import static substratum.theme.template.ThemerConstants.APK_SIGNATURE_PRODUCTION;
import static substratum.theme.template.ThemerConstants.BASE_64_LICENSE_KEY;
import static substratum.theme.template.ThemerConstants.BLACKLISTED_APPLICATIONS;
import static substratum.theme.template.ThemerConstants.ENABLE_BLACKLISTED_APPLICATIONS;
import static substratum.theme.template.ThemerConstants.ENFORCE_AMAZON_APP_STORE_INSTALL;
import static substratum.theme.template.ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL;
import static substratum.theme.template.ThemerConstants.ENFORCE_INTERNET_CHECK;
import static substratum.theme.template.ThemerConstants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION;
import static substratum.theme.template.ThemerConstants.MINIMUM_SUBSTRATUM_VERSION;
import static substratum.theme.template.ThemerConstants.PIRACY_CHECK;
import static substratum.theme.template.ThemerConstants.THEME_READY_GOOGLE_APPS;

public class SubstratumLauncher extends Activity {

    private static final String SUBSTRATUM_PACKAGE_NAME = "projekt.substratum";

    private void startAntiPiracyCheck() {
        if (PIRACY_CHECK && BASE_64_LICENSE_KEY.length() == 0)
            Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this));

        PiracyChecker piracyChecker = new PiracyChecker(this);
        if (ENFORCE_GOOGLE_PLAY_INSTALL) piracyChecker.enableInstallerId(InstallerID.GOOGLE_PLAY);
        if (ENFORCE_AMAZON_APP_STORE_INSTALL)
            piracyChecker.enableInstallerId(InstallerID.AMAZON_APP_STORE);
        piracyChecker.callback(new PiracyCheckerCallback() {
            @Override
            public void allow() {
                beginSubstratumLaunch();
            }

            @Override
            public void dontAllow(PiracyCheckerError error, PirateApp pirateApp) {
                String parse = String.format(getString(R.string.toast_unlicensed),
                        getString(R.string.ThemeName));
                Toast.makeText(SubstratumLauncher.this, parse, Toast.LENGTH_SHORT).show();
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

    private boolean isPackageInstalled(String package_name) {
        try {
            PackageManager pm = getPackageManager();
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
        } catch (Exception e) {
            return false;
        }
    }

    private void beginSubstratumLaunch() {
        // If Substratum is found, then launch it with specific parameters
        if (isPackageInstalled(SUBSTRATUM_PACKAGE_NAME)) {
            if (!isPackageEnabled(SUBSTRATUM_PACKAGE_NAME)) {
                Toast.makeText(this, getString(R.string.toast_substratum_frozen),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Substratum is found, launch it directly
            launchSubstratum();
        } else {
            getSubstratumFromPlayStore();
        }
    }

    private void getSubstratumFromPlayStore() {
        String playURL = "https://play.google.com/store/apps/details?id=projekt.substratum";
        Intent i = new Intent(Intent.ACTION_VIEW);
        Toast.makeText(this, getString(R.string.toast_substratum), Toast.LENGTH_SHORT).show();
        i.setData(Uri.parse(playURL));
        startActivity(i);
        finish();
    }

    private void showOutdatedSubstratumToast() {
        String parse = String.format(
                getString(R.string.outdated_substratum),
                getString(R.string.ThemeName),
                String.valueOf(MINIMUM_SUBSTRATUM_VERSION));
        Toast.makeText(this, parse, Toast.LENGTH_SHORT).show();
    }

    private void launchSubstratum() {
        if (ENABLE_BLACKLISTED_APPLICATIONS) {
            for (String blacklistedApplication : BLACKLISTED_APPLICATIONS)
                if (isPackageInstalled(blacklistedApplication)) {
                    Toast.makeText(this, R.string.unauthorized,
                            Toast.LENGTH_LONG).show();
                    return;
                }
        }
        if (ENFORCE_MINIMUM_SUBSTRATUM_VERSION) {
            try {
                PackageInfo packageInfo = getApplicationContext()
                        .getPackageManager().getPackageInfo(SUBSTRATUM_PACKAGE_NAME, 0);
                if (packageInfo.versionCode >= MINIMUM_SUBSTRATUM_VERSION) {
                    Intent intent = SubstratumLoader.launchThemeActivity(getApplicationContext(),
                            getIntent(), getString(R.string.ThemeName), getPackageName());
                    startActivity(intent);
                    finish();
                } else {
                    showOutdatedSubstratumToast();
                }
            } catch (Exception e) {
                showOutdatedSubstratumToast();
            }
        } else {
            Intent intent = SubstratumLoader.launchThemeActivity(getApplicationContext(),
                    getIntent(), getString(R.string.ThemeName), getPackageName());
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (ENFORCE_INTERNET_CHECK) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                if (THEME_READY_GOOGLE_APPS) {
                    detectThemeReady();
                } else {
                    launch();
                }
            } else {
                checkConnection();
            }
        } else {
            if (THEME_READY_GOOGLE_APPS) {
                detectThemeReady();
            } else {
                launch();
            }
        }
    }

    private void launch() {
        if (PIRACY_CHECK && !BuildConfig.DEBUG) {
            startAntiPiracyCheck();
        } else {
            beginSubstratumLaunch();
        }
    }

    private void detectThemeReady() {
        File addon = new File("/system/addon.d/80-ThemeReady.sh");
        if (addon.exists()) {
            ArrayList<String> apps = new ArrayList<>();
            boolean updated = false;
            String data_path = "/data/app/";
            String[] app_folder = {"com.google.android.gm",
                    "com.google.android.googlequicksearchbox",
                    "com.android.vending",
                    "com.google.android.apps.plus",
                    "com.google.android.talk",
                    "com.google.android.youtube",
                    "com.google.android.apps.photos",
                    "com.google.android.contacts",
                    "com.google.android.dialer",
                    "com.google.android.inputmethod.latin"};
            String folder1 = "-1";
            String folder2 = "-2";
            String apk_path = "/base.apk";
            StringBuilder app_name = new StringBuilder();

            for (String anApp_folder : app_folder) {
                File app1 = new File(data_path + anApp_folder + folder1 + apk_path);
                File app2 = new File(data_path + anApp_folder + folder2 + apk_path);
                if (app1.exists() || app2.exists()) {
                    try {
                        updated = true;
                        ApplicationInfo app =
                                this.getPackageManager().getApplicationInfo(anApp_folder, 0);
                        String label = getPackageManager().getApplicationLabel(app).toString();
                        apps.add(label);
                    } catch (Exception e) {
                        // Suppress warning
                    }
                }
            }

            for (int i = 0; i < apps.size(); i++) {
                app_name.append(apps.get(i));
                if (i <= apps.size() - 3) {
                    app_name.append(", ");
                } else if (i == apps.size() - 2) {
                    app_name.append(" ").append(getString(R.string.and)).append(" ");
                }
            }

            if (!updated) {
                launch();
            } else {
                String parse = String.format(getString(R.string.theme_ready_updated),
                        app_name);

                new AlertDialog.Builder(this, R.style.DialogStyle)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.ThemeName))
                        .setMessage(parse)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                launch();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        })
                        .show();
            }
        } else {
            new AlertDialog.Builder(this, R.style.DialogStyle)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.ThemeName))
                    .setMessage(getString(R.string.theme_ready_not_detected))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            launch();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, R.string.toast_internet,
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
            editor.putInt("last_version", BuildConfig.VERSION_CODE).apply();
            if (THEME_READY_GOOGLE_APPS) {
                detectThemeReady();
            } else {
                launch();
            }
        }
    }
}