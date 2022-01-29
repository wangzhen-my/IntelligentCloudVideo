package ncist.wl171.intelligentcloudvideo.ezviz;

import android.app.Application;

import androidx.annotation.NonNull;

import com.videogo.debug.TestParams;
import com.videogo.openapi.EZOpenSDK;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;

public class SdkInitTool {
    public static void initSdk(@NonNull Application application, @NonNull SdkInitParams sdkInitParams){
        TestParams.setUse(true);
        // sdk日志开关，正式发布需要去掉
        EZOpenSDK.showSDKLog(false);
        // 设置是否支持P2P取流,详见api
        EZOpenSDK.enableP2P(true);
        // APP_KEY请替换成自己申请的
        EZOpenSDK.initLib(application, sdkInitParams.appKey);

        EZOpenSDK ezvizSDK = BaseApplication.getOpenSDK();
        if (sdkInitParams.accessToken != null){
            ezvizSDK.setAccessToken(sdkInitParams.accessToken);
        }
        if (sdkInitParams.openApiServer != null && sdkInitParams.openAuthApiServer != null){
            ezvizSDK.setServerUrl(sdkInitParams.openApiServer, sdkInitParams.openAuthApiServer);
        }
    }
}
