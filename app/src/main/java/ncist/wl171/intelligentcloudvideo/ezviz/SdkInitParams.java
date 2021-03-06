package ncist.wl171.intelligentcloudvideo.ezviz;

import com.google.gson.Gson;
/**Sdk初始化参数类*/
public class SdkInitParams {

    public String appKey;
    public String accessToken;
    public int serverAreaId;
    public String openApiServer;
    public String openAuthApiServer;
    //空的构造函数
    public SdkInitParams(){}

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static SdkInitParams createBy(){
        SdkInitParams sdkInitParams = new SdkInitParams();
            sdkInitParams.appKey = "26810f3acd794862b608b6cfbc32a6b8";
            sdkInitParams.serverAreaId = 0;
            sdkInitParams.openApiServer = "https://open.ys7.com";
            sdkInitParams.openAuthApiServer = "https://openauth.ys7.com";
        return sdkInitParams;
    }

}