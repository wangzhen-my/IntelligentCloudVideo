package ncist.wl171.intelligentcloudvideo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.videogo.exception.BaseException;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;

import ncist.wl171.intelligentcloudvideo.Activity.RootActivity;
import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.Enum.ValueKeys;
import ncist.wl171.intelligentcloudvideo.ezviz.SdkInitParams;
import ncist.wl171.intelligentcloudvideo.ezviz.SdkInitTool;

import static com.videogo.constant.Constant.OAUTH_SUCCESS_ACTION;
import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;



public class LoginActivity extends RootActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private BroadcastReceiver mLoginResultReceiver = null;
    private SdkInitParams mSdkInitParams = null;
    public static SdkInitParams mInitParams;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        initData();
        Checkloginstatus();
    }

    //检查登录状态
    private void Checkloginstatus() {
        //如果未登录则直接返回，如果已经登录则验证登录有效性
        if (LocalInfo.getInstance().getEZAccesstoken() == null || LocalInfo.getInstance().getEZAccesstoken().getAccessToken() == null){
            String tip = "AccessToken is empty!";
            LogUtil.i(TAG, tip);
            toast(tip);
            return;
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startCheckLoginValidity();
                }
            }).start();
        }
    }

    /**
     * 通过萤石账号进行体验
     */
    public void onClickLoginByEzvizAccount(View view) {
        //注册广播接收器
        mSdkInitParams = SdkInitParams.createBy();
        registerLoginResultReceiver();
        getOpenSDK().openLoginPage();
    }

    //注册广播接收器，接收萤石账号登录成功的广播
    private void registerLoginResultReceiver(){
        if (mLoginResultReceiver == null){
            mLoginResultReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "login success by h5 page");
                    unregisterLoginResultReceiver();
                    //若登录成功则保存SDK的配置至缓存中
                    saveLastSdkInitParams(mSdkInitParams);
                    textView.setText("已登录");
                    toast("登录成功！");
//                    jumpToCameraListActivity();
//                    finish();
                }
            };
            //登录成功EZSDK会发送OAUTH_SUCCESS_ACTION广播，设置接收器接收OAUTH_SUCCESS_ACTION广播
            IntentFilter filter = new IntentFilter(OAUTH_SUCCESS_ACTION);
            registerReceiver(mLoginResultReceiver, filter);
            Log.i(TAG, "registered login result receiver");
        }
    }

    /**
     * 保存上次sdk初始化的参数
     */
    private void saveLastSdkInitParams(SdkInitParams sdkInitParams) {
        // 不保存AccessToken
        sdkInitParams.accessToken = null;
        SpTool.storeValue(ValueKeys.SDK_INIT_PARAMS, sdkInitParams.toString());
    }

    //注销之前注册的广播接收器并重新赋值为null
    private void unregisterLoginResultReceiver(){
        if (mLoginResultReceiver != null){
            unregisterReceiver(mLoginResultReceiver);
            mLoginResultReceiver = null;
            Log.i(TAG, "unregistered login result receiver");
        }
    }

    //初始化
    private void initData() {
        //初始化SharedPreference工具类
        SpTool.init(getApplicationContext());
        //检查缓存中是否有SDK配置的缓存，若有则取出并给mInitParams赋值，若没有则直接给mInitParams赋值
        if (!loadLastSdkInitParams()){
            LoadDefaultSdkInitParams();
        }
        mInitParams.accessToken = null;
        //传入SDK参数类的变量，对萤石的SDK进行初始化参数设置
        SdkInitTool.initSdk(getApplication(), mInitParams);
    }

    //检查缓存中是否已经有SDK相关配置
    private boolean loadLastSdkInitParams() {
        //从缓存中取出sdk初始化参数并检查是否为空
        String sdkInitParamStr = SpTool.obtainValue(ValueKeys.SDK_INIT_PARAMS);
        if (sdkInitParamStr != null){
            mInitParams = new Gson().fromJson(sdkInitParamStr, SdkInitParams.class);
            return mInitParams != null && mInitParams.appKey != null;
        }
        return false;
    }

    //若缓存中没有对应的SDK配置，则初始化配置
    private void LoadDefaultSdkInitParams(){
//        mInitParams = SdkInitParams.createBy(null);
        mInitParams = new SdkInitParams();
        mInitParams.appKey = "26810f3acd794862b608b6cfbc32a6b8";
        mInitParams.openApiServer = "https://open.ys7.com";
        mInitParams.openAuthApiServer = "https://openauth.ys7.com";
    }

    /**
     * 检查AccessToke是否有效
     */
    private void startCheckLoginValidity(){
        if (checkAppKeyAndAccessToken()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("已登录");
                }
            });
            //若有效则直接跳转主界面，跳过登陆界面，直接finish
//            jumpToCameraListActivity();
//            finish();
        }
    }

    /**
     * 通过调用服务接口判断AppKey和AccessToken且有效
     * @return 是否依旧有效
     */
    private boolean checkAppKeyAndAccessToken() {
        boolean isValid = false;
        try {
            /**  EZOpenSDK.getDeviceList
             * 获取用户的设备列表，返回EZDeviceInfo的对象数组，只提供设备基础数据 该接口为耗时操作，必须在线程中调用
             * 参数:
             * pageIndex - 查询页index，从0开始
             * pageSize - 每页数量（建议20以内）
             * */
            BaseApplication.getOpenSDK().getDeviceList(0, 1);
            isValid = true;
        } catch (BaseException e) {
            textView.setText("未登录");
            //输出异常的栈跟踪轨迹
            e.printStackTrace();
            Log.e(TAG, "Error code is " + e.getErrorCode());
            int errCode = e.getErrorCode();
            String errMsg;
            switch (errCode){
                case 400031:
                    errMsg = getApplicationContext().getString(R.string.tip_of_bad_net);
                    break;
                default:
                    errMsg = getApplicationContext().getString(R.string.login_expire);
                    break;
            }
            toast(errMsg);
        }
        return isValid;
    }
}