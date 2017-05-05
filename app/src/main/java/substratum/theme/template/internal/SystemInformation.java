package substratum.theme.template.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import static substratum.theme.template.ThemerConstants.ALLOW_OTHER_THEME_SYSTEMS;
import static substratum.theme.template.ThemerConstants.BLACKLISTED_APPLICATIONS;
import static substratum.theme.template.ThemerConstants.ENABLE_BLACKLISTED_APPLICATIONS;
import static substratum.theme.template.ThemerConstants.MINIMUM_SUBSTRATUM_VERSION;
import static substratum.theme.template.ThemerConstants.OTHER_THEME_SYSTEMS;

public class SystemInformation {

    public static final String SUBSTRATUM_PACKAGE_NAME = "projekt.substratum";

    public static Boolean checkNetworkConnection() {
        boolean isConnected = false;
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = process.waitFor();
            isConnected = (returnVal == 0);
        } catch (Exception e) {
            // Suppress error
        }
        return isConnected;
    }

    public static boolean isPackageInstalled(Context context, String package_name) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(package_name, 0);
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            return ai.enabled;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean getSubstratumUpdatedResponse(Context context) {
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager()
                    .getPackageInfo(SUBSTRATUM_PACKAGE_NAME, 0);
            if (packageInfo.versionCode >= MINIMUM_SUBSTRATUM_VERSION) {
                return true;
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return false;
    }

    public static Integer getSelfVerifiedIntentResponse(Context context) {
        if (ALLOW_OTHER_THEME_SYSTEMS) {
            return getSelfSignature(context);
        } else {
            return getSubstratumSignature(context);
        }
    }

    public static Boolean getSelfVerifiedPirateTools(Context context) {
        if (ENABLE_BLACKLISTED_APPLICATIONS) {
            for (String BLACKLISTED_APPLICATION : BLACKLISTED_APPLICATIONS) {
                if (isPackageInstalled(context, BLACKLISTED_APPLICATION)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean getSelfVerifiedThemeEngines(Context context) {
        Boolean isPermitted = false;
        for (String OTHER_THEME_SYSTEM : OTHER_THEME_SYSTEMS) {
            if (isPackageInstalled(context, OTHER_THEME_SYSTEM)) {
                isPermitted = true;
                break;
            }
        }
        if (ALLOW_OTHER_THEME_SYSTEMS) {
            return isPermitted;
        } else if (isPackageInstalled(context, SUBSTRATUM_PACKAGE_NAME)) {
            return !isPermitted;
        }
        return false;
    }

    @SuppressLint("PackageManagerGetSignatures")
    private static int getSubstratumSignature(Context context) {
        Signature[] sigs;
        try {
            sigs = context.getPackageManager().getPackageInfo(
                    SUBSTRATUM_PACKAGE_NAME,
                    PackageManager.GET_SIGNATURES
            ).signatures;
            return sigs[0].hashCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static int getSelfSignature(Context context) {
        Signature[] sigs;
        try {
            sigs = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES
            ).signatures;
            return sigs[0].hashCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}