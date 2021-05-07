package ncist.wl171.intelligentcloudvideo;

import android.app.Activity;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;

public class ActivityUtils {
    public static void handleSessionException(Activity activity) {
        goToLoginAgain(activity);
    }
    public static void goToLoginAgain(Activity activity) {
        BaseApplication.getOpenSDK().openLoginPage();
    }
}
