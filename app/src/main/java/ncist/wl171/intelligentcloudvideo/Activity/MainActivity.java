package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ez.stream.EZStreamClientManager;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.util.List;

import ncist.wl171.intelligentcloudvideo.EZCameraListAdapter;
import ncist.wl171.intelligentcloudvideo.EZUtils;
import ncist.wl171.intelligentcloudvideo.R;

import static com.ez.stream.EZError.EZ_OK;
import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;

public class MainActivity extends RootActivity implements View.OnClickListener {

    //添加设备按钮
    private Button mAddBtn;
    public final static int RESULT_CODE = 101;
    public final static int REQUEST_CODE = 100;

    private boolean bIsFromSetting = false;

    private EZCameraListAdapter mAdapter = null;

    protected static final String TAG = "MainActivity";

    private LinearLayout mNoCameraTipLy = null;

    private ListView mListView = null;

    private Button mUserBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mAddBtn = (Button) findViewById(R.id.btn_add);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SeriesNumSearchActivity.class);
                startActivity(intent);

            }
        });
        mNoCameraTipLy = (LinearLayout) findViewById(R.id.no_camera_tip_ly);
        mListView = (ListView) findViewById(R.id.camera_listview);
        mAdapter = new EZCameraListAdapter(this);
        mAdapter.setOnClickListener(new EZCameraListAdapter.OnClickListener() {
            /**
             * 根据设备型号判断是否是HUB设备
             */
            private boolean isHubDevice(String deviceType){
                if (TextUtils.isEmpty(deviceType)){
                    return false;
                }
                switch (deviceType){
                    case "CASTT":
                    case "CAS_HUB_NEW":
                        return true;
                    default:
                        //比较字符串前缀是否是CAS_WG_TEST
                        return deviceType.startsWith("CAS_WG_TEST");
                }
            }

            //点击查看监控视频图标
            @Override
            public void onPlayClick(BaseAdapter adapter, View view, int position) {
                //获取列表中设备信息对象
                final EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                //获取设备类型并判断否是HUB设备
                if (isHubDevice(deviceInfo.getDeviceType())){
                    toast("无法查看HUB设备！");
                    return;
                }
                if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
                    LogUtil.d(TAG, "cameralist is null or cameralist size is 0");
                    toast("如果设备通道数量小于0 || 设备通道列表等于null || 设备通道列表长度小于等于0");
                    return;
                }
                /*单通道设备*/
                if (deviceInfo.getCameraNum() == 1 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() == 1) {
                    LogUtil.d(TAG, "cameralist have one camera");
                    //获取设备的对应通道
                    final EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
                    if (cameraInfo == null) {
                        toast("获取的通道为null！");
                        return;
                    }
                    int ret = EZStreamClientManager.create(getApplication().getApplicationContext()).clearTokens();
                    if (EZ_OK == ret){
                        Log.i(TAG, "clearTokens: ok");
                    }else{
                        Log.e(TAG, "clearTokens: fail");
                    }
                    Intent intent = new Intent(MainActivity.this, EZRealPlayActivity.class);
                    //向下一个activity传入设备通道信息
                    intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                    //向下一个activity传入设备信息对象
                    intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                    startActivityForResult(intent, REQUEST_CODE);
                    return;
                }
            }
            //点击设置图标
            @Override
            public void onSetDeviceClick(BaseAdapter adapter, View view, int position) {
                EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, EZDeviceSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(IntentConsts.EXTRA_DEVICE_INFO,deviceInfo);
                intent.putExtra("Bundle",bundle);
                startActivity(intent);
                bIsFromSetting = true;
            }
            //点击消息通知图标
            @Override
            public void onAlarmListClick(BaseAdapter adapter, View view, int position) {
                final EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                LogUtil.d(TAG, "cameralist is null or cameralist size is 0");
                Intent intent = new Intent(MainActivity.this, EZMessageActivity2.class);
                //获取设备序列号并传递给下一个界面
                intent.putExtra(IntentConsts.EXTRA_DEVICE_ID, deviceInfo.getDeviceSerial());
                startActivity(intent);
            }
        });
        mListView.setAdapter(mAdapter);
        //设置activity状态栏颜色
        setStatusBarColor(this,R.color.c4);
        mUserBtn = (Button) findViewById(R.id.btn_user);
        mUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popLogoutDialog();
            }
        });
    }

    private void getCameraInfoList() {
        if (this.isFinishing()) {
            return;
        }
        //启动刷新列表的子线程
        new GetCamersInfoListTask().execute();
    }

    private void popLogoutDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
        //设置提示框的标题
        exitDialog.setTitle(R.string.exit);
        //设置提示框图标
        exitDialog.setIcon(R.drawable.tips);
        //设置提示框的内容
        exitDialog.setMessage(R.string.exit_tip);
        //如果点击确定则退出应用
        exitDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.tip_login_out), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getOpenSDK().logout();
                    }
                }).start();
                finish();
            }
        });
        //若点击取消则什么都不做
        exitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        exitDialog.show();
    }

    @Override
    public void onClick(View view) {
                refreshButtonClicked();
    }

    private void refreshButtonClicked() {
        mListView.setVisibility(View.VISIBLE);
        mNoCameraTipLy.setVisibility(View.GONE);
        //如果activity不处于finish，则启动刷新列表子线程
        getCameraInfoList();
    }

    private class GetCamersInfoListTask extends AsyncTask<Void,Void, List<EZDeviceInfo>> {
        private int mErrorCode = 0;
        public GetCamersInfoListTask(){}

        //执行任务中的耗时操作
        @Override
        protected List<EZDeviceInfo> doInBackground(Void... params) {
            //如果activity正在finish就直接返回
            if (MainActivity.this.isFinishing()) {
                return null;
            }
            //判断网络是否可用
            if (!ConnectionDetector.isNetworkAvailable(MainActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }
            try {
                List<EZDeviceInfo> result = null;
                /**
                 * 获取用户的设备列表，返回EZDeviceInfo的对象数组，只提供设备基础数据 该接口为耗时操作，必须在线程中调用
                 * 参数:
                 * pageIndex - 查询页index，从0开始
                 * pageSize - 每页数量（建议20以内）*/
                result = getOpenSDK().getDeviceList(0, 10);
                return result;
            } catch (BaseException e) {
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.d(TAG, errorInfo.toString());
                return null;
            }
        }

        //线程任务结束时自动调用
        @Override
        protected void onPostExecute(List<EZDeviceInfo> result) {
            super.onPostExecute(result);
            if (MainActivity.this.isFinishing()) {
                return;
            }
            //如果获取到的设备列表不为null
            if (result != null) {
                //清空原来的适配器
                mAdapter.clearItem();
                //如果获取到的列表长度为0则将提示显示出来
                if (mAdapter.getCount() == 0 && result.size() == 0) {
                    mListView.setVisibility(View.GONE);
                    mNoCameraTipLy.setVisibility(View.VISIBLE);
                }
                addCameraList(result);
                toast("刷新设备列表成功！");
                //通知附加的观察者基础数据已更改，任何反映数据集的视图都应刷新自身。
                mAdapter.notifyDataSetChanged();
            }
            if (mErrorCode != 0) {
                onError(mErrorCode);
            }
        }

        protected void onError(int errorCode) {
            switch (errorCode) {
                case ErrorCode.ERROR_WEB_SESSION_ERROR:
                case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
                    Utils.showToast(MainActivity.this, R.string.get_camera_list_fail2, errorCode);
                    break;
                default:
                    if (mAdapter.getCount() == 0) {
                        //如果列表还是没有设备则显示未找到设备
                        mListView.setVisibility(View.GONE);
                        mNoCameraTipLy.setVisibility(View.VISIBLE);
                        Utils.showToast(MainActivity.this, R.string.get_camera_list_fail, errorCode);
                    } else {
                        Utils.showToast(MainActivity.this, R.string.get_camera_list_fail, errorCode);
                    }
                    break;
            }
        }
    }

    private void addCameraList(List<EZDeviceInfo> result) {
        int count = result.size();
        EZDeviceInfo item = null;
        for (int i = 0; i < count; i++) {
            item = result.get(i);
            mAdapter.addItem(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果从设置界面切回则刷新列表，因为有可能有设备被删除
        //或者设备列表配置器中的设备数量为空则刷新界面要显示“没有找到任何设备”
        //因为在oncreat方法中初始化了mAdapter，所以第一次启动activity也会刷新列表
        if (bIsFromSetting || (mAdapter != null && mAdapter.getCount() == 0)) {
            refreshButtonClicked();
            bIsFromSetting = false;
        }
    }

    /**  接收子activity（上一个activity）结束之后返回的值
     * requestCode:请求码，用于启动子Activity
     * resultCode:子Activity设置的结果码，用于指示操作结果。可以是任何整数值，但通常是resultCode = =
     * RESULT_OK或resultCode==RESULT_CANCELED
     * Data:用于打包返回数据的Intent,可以包括用于表示所选内容的URI。子Activity也可以在返回数据Intent时，添加一些附加消息
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //若返回的是结果码
        if (resultCode == RESULT_CODE && requestCode == REQUEST_CODE){
            //设备序列号
            String deviceSerial = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
            //获取camera在对应设备上的通道号
            int cameraNo = intent.getIntExtra(IntentConsts.EXTRA_CAMERA_NO,-1);
            //通道清晰度
            int videoLevel = intent.getIntExtra("video_level",-1);
            if (TextUtils.isEmpty(deviceSerial)||videoLevel == -1 || cameraNo == -1){
                return;
            }
            //如果设备列表不为空
            if (mAdapter.getDeviceInfoList() != null){
                for (EZDeviceInfo deviceInfo:mAdapter.getDeviceInfoList()){
                    //从设备列表中找到对应设备号的设备 && 获取该设备的通道列表并对比是否为null
                    if (deviceInfo.getDeviceSerial().equals(deviceSerial) && deviceInfo.getCameraInfoList() != null){
                        for (EZCameraInfo cameraInfo:deviceInfo.getCameraInfoList()){
                            //找到设备通道列表中对应的通道号
                            if (cameraInfo.getCameraNo() == cameraNo){
                                //设置通道清晰度
                                cameraInfo.setVideoLevel(videoLevel);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }
}