package substratum.theme.template;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import static substratum.theme.template.ThemerConstants.APK_SIGNATURE_PRODUCTION;
import static substratum.theme.template.ThemerConstants.BASE_64_LICENSE_KEY;
import static substratum.theme.template.ThemerConstants.ENFORCE_AMAZON_APP_STORE_INSTALL;
import static substratum.theme.template.ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL;
import static substratum.theme.template.ThemerConstants.ENFORCE_INTERNET_CHECK;
import static substratum.theme.template.ThemerConstants.ENFORCE_MINIMUM_SUBSTRATUM_VERSION;
import static substratum.theme.template.ThemerConstants.MINIMUM_SUBSTRATUM_VERSION;
import static substratum.theme.template.ThemerConstants.PIRACY_CHECK;
import static substratum.theme.template.ThemerConstants.SUBSTRATUM_FILTER_CHECK;
import static substratum.theme.template.ThemerConstants.THEME_READY_GOOGLE_APPS;
import static substratum.theme.template.internal.SystemInformation.SUBSTRATUM_PACKAGE_NAME;
import static substratum.theme.template.internal.SystemInformation.checkNetworkConnection;
import static substratum.theme.template.internal.SystemInformation.getSelfSignature;
import static substratum.theme.template.internal.SystemInformation.getSelfVerifiedIntentResponse;
import static substratum.theme.template.internal.SystemInformation.getSelfVerifiedPirateTools;
import static substratum.theme.template.internal.SystemInformation.getSelfVerifiedThemeEngines;
import static substratum.theme.template.internal.SystemInformation.getSubstratumUpdatedResponse;
import static substratum.theme.template.internal.SystemInformation.isPackageInstalled;
import static substratum.theme.template.internal.TBOConstants.EXTRA_PACKAGE_NAMES;
import static substratum.theme.template.internal.TBOConstants.THEME_READY_PACKAGES;

public class SubstratumLauncher extends Activity {

    private Boolean mVerified = false;
    private PiracyChecker piracyChecker;
    private String mModeLaunch = "";

    private void calibrateSystem() {
        if (PIRACY_CHECK && !BuildConfig.DEBUG) {
            startAntiPiracyCheck();
        } else {
            quitSelf();
        }
    }

    private void startAntiPiracyCheck() {
        if (piracyChecker != null) {
            piracyChecker.start();
        } else {
            if (PIRACY_CHECK && BASE_64_LICENSE_KEY.length() == 0)
                Log.e("SubstratumAntiPiracyLog", PiracyCheckerUtils.getAPKSignature(this));

            piracyChecker = new PiracyChecker(this);
            if (ENFORCE_GOOGLE_PLAY_INSTALL)
                piracyChecker.enableInstallerId(InstallerID.GOOGLE_PLAY);
            if (ENFORCE_AMAZON_APP_STORE_INSTALL)
                piracyChecker.enableInstallerId(InstallerID.AMAZON_APP_STORE);

            piracyChecker.callback(new PiracyCheckerCallback() {
                @Override
                public void allow() {
                    quitSelf();
                }

                @Override
                public void dontAllow(@NonNull PiracyCheckerError error, PirateApp pirateApp) {
                    String parse = String.format(
                            getString(R.string.toast_unlicensed),
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
    }

    private void getSubstratumFromPlayStore() {
        String playURL = "https://play.google.com/store/apps/details?id=projekt.substratum";
        Intent i = new Intent(Intent.ACTION_VIEW);
        Toast.makeText(this, getString(R.string.toast_substratum), Toast.LENGTH_SHORT).show();
        i.setData(Uri.parse(playURL));
        startActivity(i);
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean quitSelf() {
        if (!isPackageInstalled(getApplicationContext(), SUBSTRATUM_PACKAGE_NAME)) {
            getSubstratumFromPlayStore();
            return false;
        }

        if (ENFORCE_MINIMUM_SUBSTRATUM_VERSION &&
                !getSubstratumUpdatedResponse(getApplicationContext())) {
            String parse = String.format(
                    getString(R.string.outdated_substratum),
                    getString(R.string.ThemeName),
                    String.valueOf(MINIMUM_SUBSTRATUM_VERSION));
            Toast.makeText(this, parse, Toast.LENGTH_SHORT).show();
            return false;
        }

        Intent returnIntent = new Intent();

        String theme_name = getString(R.string.ThemeName);
        String theme_author = getString(R.string.ThemeAuthor);
        String theme_pid = getPackageName();
        String theme_mode = mModeLaunch;
        returnIntent.putExtra("theme_name", theme_name);
        returnIntent.putExtra("theme_author", theme_author);
        returnIntent.putExtra("theme_pid", theme_pid);
        returnIntent.putExtra("theme_mode", theme_mode);

        Integer theme_hash = getSelfSignature(getApplicationContext());
        Boolean theme_launch_type = getSelfVerifiedThemeEngines(getApplicationContext());
        if (!theme_launch_type) {
            Toast.makeText(this, R.string.unauthorized_theme_client, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        Boolean theme_debug = BuildConfig.DEBUG;
        if (!theme_debug && PIRACY_CHECK) {
            Toast.makeText(this, R.string.unauthorized_debug, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        Boolean theme_piracy_check = getSelfVerifiedPirateTools(getApplicationContext());
        if (!theme_piracy_check || (SUBSTRATUM_FILTER_CHECK && !mVerified)) {
            Toast.makeText(this, R.string.unauthorized, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        returnIntent.putExtra("theme_hash", theme_hash);
        returnIntent.putExtra("theme_launch_type", theme_launch_type);
        returnIntent.putExtra("theme_debug", theme_debug);
        returnIntent.putExtra("theme_piracy_check", theme_piracy_check);

        setResult(getSelfVerifiedIntentResponse(getApplicationContext()), returnIntent);
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mVerified = intent.getBooleanExtra("certified", false);
        mModeLaunch = intent.getStringExtra("theme_mode");

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (ENFORCE_INTERNET_CHECK) {
            if (sharedPref.getInt("last_version", 0) == BuildConfig.VERSION_CODE) {
                if (THEME_READY_GOOGLE_APPS) {
                    detectThemeReady();
                } else {
                    calibrateSystem();
                }
            } else {
                checkConnection();
            }
        } else if (THEME_READY_GOOGLE_APPS) {
            detectThemeReady();
        } else {
            calibrateSystem();
        }
    }

    private Boolean checkConnection() {
        Boolean isConnected = checkNetworkConnection();
        if (!isConnected) {
            Toast.makeText(this, R.string.toast_internet, Toast.LENGTH_LONG).show();
            return false;
        } else {
            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
            editor.putInt("last_version", BuildConfig.VERSION_CODE).apply();
            if (THEME_READY_GOOGLE_APPS) {
                detectThemeReady();
            } else {
                calibrateSystem();
            }
            return true;
        }
    }

    private void detectThemeReady() {
        File addon = new File("/system/addon.d/80-ThemeReady.sh");
        if (addon.exists()) {
            ArrayList<String> apps = new ArrayList<>();
            boolean updated = false;
            boolean incomplete = false;
            PackageManager packageManager = this.getPackageManager();
            StringBuilder app_name = new StringBuilder();

            for (String packageName : EXTRA_PACKAGE_NAMES) {
                try {
                    ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        incomplete = true;
                        apps.add(packageManager.getApplicationLabel(appInfo).toString());
                    }
                } catch (Exception e) {
                    // Package not found
                }
            }

            if (!incomplete) {
                for (String packageName : THEME_READY_PACKAGES) {
                    try {
                        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                        if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                            updated = true;
                            apps.add(packageManager.getApplicationLabel(appInfo).toString());
                        }
                    } catch (Exception e) {
                        // Package not found
                    }
                }
                for (String packageName : EXTRA_PACKAGE_NAMES) {
                    try {
                        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                        if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                            updated = true;
                            apps.add(packageManager.getApplicationLabel(appInfo).toString());
                        }
                    } catch (Exception e) {
                        // Package not found
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

            if (!updated && !incomplete) {
                calibrateSystem();
            } else {
                int stringInt = incomplete ? R.string.theme_ready_incomplete :
                        R.string.theme_ready_updated;
                String parse = String.format(getString(stringInt),
                        app_name);

                new AlertDialog.Builder(this, R.style.DialogStyle)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.ThemeName))
                        .setMessage(parse)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                calibrateSystem();
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
                            calibrateSystem();
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
}