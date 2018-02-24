package substratum.theme.template

import substratum.theme.template.BuildConfig.SUPPORTS_THIRD_PARTY_THEME_SYSTEMS

object Constants {

    // Dynamic filter that only works on Substratum 627+
    // WARNING: Only enable if you are sure you want certification status to pass on Substratum
    //          before launching the theme, or else it will throw an unauthorized toast!
    const internal val SUBSTRATUM_FILTER_CHECK = false

    // Miscellaneous Checks
    const val ENFORCE_MINIMUM_SUBSTRATUM_VERSION = true
    const val MINIMUM_SUBSTRATUM_VERSION = 712 // 510 is the final MM build
    const val ENABLE_KNOWN_THIRD_PARTY_THEME_MANAGERS = SUPPORTS_THIRD_PARTY_THEME_SYSTEMS

    // Blacklisted APKs to prevent theme launching, these include simple regex formatting, without
    // full regex formatting (e.g. com.android. will block everything that starts with com.android.)
    val BLACKLISTED_APPLICATIONS = arrayOf(
            "cc.madkite.freedom",
            "zone.jasi2169.uretpatcher",
            "uret.jasi2169.patcher",
            "p.jasi2169.al3",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.forpda.lp",
            "com.android.vending.billing.InAppBillingService.LUCK",
            "com.android.vending.billing.InAppBillingService.CLON",
            "com.android.vending.billing.InAppBillingService.LOCK",
            "com.android.vending.billing.InAppBillingService.CRAC",
            "com.android.vending.billing.InAppBillingService.LACK",
            "com.android.vendinc",
            "com.appcake",
            "ac.market.store",
            "org.sbtools.gamehack",
            "com.zune.gamekiller",
            "com.aag.killer",
            "com.killerapp.gamekiller",
            "cn.lm.sq",
            "net.schwarzis.game_cih",
            "org.creeplays.hack",
            "com.baseappfull.fwd",
            "com.zmapp",
            "com.dv.marketmod.installer",
            "org.mobilism.android",
            "com.blackmartalpha",
            "org.blackmart.market"
    )

    // List of other theme systems that are officially unsupported by the team, but fully supported
    // by their corresponding organizations
    val OTHER_THEME_SYSTEMS = arrayOf(
            "com.slimroms.thememanager",
            "com.slimroms.omsbackend"
    )
}
