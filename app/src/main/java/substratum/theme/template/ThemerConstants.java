package substratum.theme.template;

public class ThemerConstants {
    // Simple AntiPiracy Configuration
    public static final Boolean PIRACY_CHECK = false;

    // Miscellaneous Checks
    public static final Boolean ALLOW_DEBUG_SUBSTRATUM_BUILDS = PIRACY_CHECK;
    public static final Boolean ALLOW_OTHER_THEME_SYSTEMS = false;
    public static final Boolean ENFORCE_MINIMUM_SUBSTRATUM_VERSION = true;
    public static final int MINIMUM_SUBSTRATUM_VERSION = 712; // 510 is the final MM build
    // Blacklisted APKs to prevent theme launching
    public static final Boolean ENABLE_BLACKLISTED_APPLICATIONS = false;
    public static final String[] BLACKLISTED_APPLICATIONS = new String[]{
            "com.android.vending.billing.InAppBillingService.LOCK",
            "com.android.vending.billing.InAppBillingService.LACK",
            "cc.madkite.freedom",
            "zone.jasi2169.uretpatcher",
            "uret.jasi2169.patcher",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.forpda.lp",
            "com.android.vending.billing.InAppBillingService.LUCK",
            "com.android.protips",
    };
    public static final String[] OTHER_THEME_SYSTEMS = new String[]{
            "com.slimroms.thememanager"
    };
    static final Boolean THEME_READY_GOOGLE_APPS = false;
    // Dynamic filter that only works on Substratum 627+
    // WARNING: Only enable if you are sure you want certification status to pass on Substratum
    //          before launching the theme, or else it will throw an unauthorized toast!
    static final Boolean SUBSTRATUM_FILTER_CHECK = false;
    // Play Store AntiPiracy LVL configurations (Relies on PIRACY_CHECK)
    static final String BASE_64_LICENSE_KEY = "";
    static final String APK_SIGNATURE_PRODUCTION = "";
    // AntiPiracy Library Configurations (Relies on PIRACY_CHECK)
    static final Boolean ENFORCE_INTERNET_CHECK = false;
    static final Boolean ENFORCE_GOOGLE_PLAY_INSTALL = true;
    static final Boolean ENFORCE_AMAZON_APP_STORE_INSTALL = false;
}
