package ncist.wl171.intelligentcloudvideo.Base;

import android.app.Application;

import com.videogo.openapi.EZOpenSDK;

public class BaseApplication extends Application{


    public static EZOpenSDK getOpenSDK(){
        EZOpenSDK ezOpenSDK = EZOpenSDK.getInstance();
        return ezOpenSDK;
    }
}
