package ncist.wl171.intelligentcloudvideo.Activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ExtraException;
import com.videogo.openapi.bean.EZProbeDeviceInfoResult;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LocalValidate;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.R;

public class SeriesNumSearchActivity extends RootActivity implements View.OnClickListener {

    private static final String TAG = "SeriesNumSearchActivity";
    //设备序列号
    private String mSerialNoStr = "";
    private Dialog mWaitDlg = null;
    //查询结果面板设备尚未联网文本
    private TextView mTvStatus;
    //最下方提示小字
    private TextView mConnectTip;
    //查询结果面板添加设备按钮
    private Button mAddButton = null;
    //查询结果面板中确定按钮
    private View mBtnNext;
    //查询结果面板设备序列号
    private TextView mDeviceName = null;
    //设备号查询所返回的对象
    private EZProbeDeviceInfoResult mEZProbeDeviceInfo = null;
    //查询时的加载面板
    private View mQueryingCameraRyt;
    //设备查询结果面板
    private View mCameraListLy;
    //序列号与验证码输入layout
    private View mInputLinearlayout;
    private LocalValidate mLocalValidate = null;
    //设备序列号输入框
    private EditText mSeriesNumberEt = null;
    //输入的验证码
    private String mVerifyCode = null;
    //验证码输入框
    private EditText mVerifyCodeEt;
    //状态栏标题
    private TextView mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camera_by_series_number_page);
        //设置activity状态栏颜色
        setStatusBarColor(this,R.color.c4);
        initTitleBar();
        initView();
    }
    private void initView() {
        mVerifyCodeEt = (EditText) findViewById(R.id.verifycodeEt);
        mSeriesNumberEt = (EditText) findViewById(R.id.seriesNumberEt);
        mInputLinearlayout = findViewById(R.id.inputLinearlayout);
        mCameraListLy = findViewById(R.id.cameraListLy);
        mQueryingCameraRyt = findViewById(R.id.queryingCameraRyt);
        mDeviceName = (TextView) findViewById(R.id.deviceName);
        mBtnNext = findViewById(R.id.btnNext);
        mAddButton = (Button) findViewById(R.id.addBtn);
        mConnectTip = (TextView) findViewById(R.id.connectTip);
        mTvStatus = (TextView) findViewById(R.id.tvStatus);
        mBtnNext.setOnClickListener(this);
        mAddButton.setOnClickListener(this);
        mWaitDlg = new Dialog(SeriesNumSearchActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDlg.setContentView(R.layout.wait_dialog);
        mWaitDlg.setCancelable(false);
    }

    //初始化顶部状态栏
    private void initTitleBar() {
        TitleBar mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitle = mTitleBar.setTitle("手动输入");
        mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onClick(View view) {
        mVerifyCode = mVerifyCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(mVerifyCode)){
            Toast.makeText(this,"验证码不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        switch (view.getId()) {
            case R.id.searchBtn:   //输入完设备序列号与验证码之后点击下一步
                searchCameraBySN();
                break;
            case R.id.btnNext:
                mCameraListLy.setVisibility(View.GONE);
                mQueryingCameraRyt.setVisibility(View.GONE);
                mInputLinearlayout.setVisibility(View.VISIBLE);
                mTitle.setText("手动输入");
                break;
            case R.id.addBtn:
                if(!TextUtils.isEmpty(mVerifyCode)){
                    // Local network detection
                    if (!ConnectionDetector.isNetworkAvailable(SeriesNumSearchActivity.this)) {
                        toast("添加失败，请检查您的网络");
                        return;
                    }
                    mWaitDlg.show();
                    new Thread() {
                        public void run() {
                            try {
                                /**添加设备 该接口为耗时操作，必须在线程中调用
                                 参数:
                                 deviceSerial - 设备序列号
                                 verifyCode - 设备验证码，验证码位于设备机身上，6位大写字母
                                 返回:
                                 true 表示成功， false 表示失败*/
                                boolean result = BaseApplication.getOpenSDK().addDevice(mSerialNoStr, mVerifyCode);

                                /***********If necessary, the developer needs to save this code***********/
                                // 添加成功过后
                                mWaitDlg.dismiss();
                                Intent intent = new Intent(SeriesNumSearchActivity.this, MainActivity.class);
                                startActivity(intent);
                            } catch (BaseException e) {
                                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                                LogUtil.d(TAG, errorInfo.toString());
                                toast("添加失败：" + errorInfo.errorCode);
                            }

                        }
                    }.start();
                } else {
                    mCameraListLy.setVisibility(View.GONE);
                    mQueryingCameraRyt.setVisibility(View.GONE);
                    mInputLinearlayout.setVisibility(View.VISIBLE);
                    toast("验证码为空");
                }
                break;
            default:
                break;
        }
    }
    //按序列号搜索相机
    public void searchCameraBySN() {
        hideKeyBoard();
        //获取输入的设备序列号
        final String serialNo = mSeriesNumberEt.getText().toString().trim();
        mSerialNoStr = serialNo;
        mLocalValidate = new LocalValidate();
        try {
            mLocalValidate.localValidatSerialNo(serialNo);
        } catch (BaseException e) {
            switch (e.getErrorCode()) {
                case ExtraException.SERIALNO_IS_NULL:
                    toast("序列号不能为空!");
                    break;
                case ExtraException.SERIALNO_IS_ILLEGAL:
                    toast("请输入正确的序列号!");
                    break;
                default:
                    toast("序列号错误" + e.getErrorCode());
                    break;
            }
            return;
        }
        if (!ConnectionDetector.isNetworkAvailable(SeriesNumSearchActivity.this)) {
            toast("查询失败，网络不给力");
            return;
        }
        showQueryingCamera();
        new Thread() {
            public void run() {
                /**probeDeviceInfo:尝试查询设备信息（用于添加设备之前, 简单查询设备信息，如是否在线，是否添加等） 该接口为耗时操作，必须在线程中调用
                 参数:
                 deviceSerial - 需要查询的设备序列号
                 deviceType - 设备型号 (设备型号和设备序列号不能均为空,优先按照设备序列号查询)
                 返回:
                 返回 EZProbeDeviceInfo 对象，包含设备简单信息，用于添加目的*/
                mEZProbeDeviceInfo = BaseApplication.getOpenSDK().probeDeviceInfo(serialNo,"");
                if (mEZProbeDeviceInfo != null){
                    if (mEZProbeDeviceInfo.getBaseException() == null){
                        // 设备查询成功，开始添加设备
                        if (mEZProbeDeviceInfo != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAddButton();
                                }
                            });
                        }
                    }else{
                        switch (mEZProbeDeviceInfo.getBaseException().getErrorCode()){
                            case 120023:
                                // TODO: 2018/6/25  设备不在线，未被用户添加 （这里需要网络配置）
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCameraList();
                                        mBtnNext.setVisibility(View.VISIBLE);
                                        mTvStatus.setVisibility(View.VISIBLE);
                                        mConnectTip.setVisibility(View.VISIBLE);
                                        mAddButton.setVisibility(View.GONE);
                                        mTvStatus.setTextColor(getResources().getColor(R.color.scan_yellow));
                                        mTvStatus.setText("尚未连接好网络");
                                    }
                                });
                                break;
                            case 120002:
                                // TODO: 2018/6/25  设备不存在，未被用户添加 （这里需要网络配置）
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCameraList();
                                        mBtnNext.setVisibility(View.VISIBLE);
                                        mTvStatus.setVisibility(View.VISIBLE);
                                        mConnectTip.setVisibility(View.GONE);
                                        mAddButton.setVisibility(View.GONE);
                                        mTvStatus.setTextColor(getResources().getColor(R.color.scan_yellow));
                                        mTvStatus.setText("设备不存在");
                                    }
                                });
                                break;
                            case 120029:
                                // TODO: 2018/6/25  设备不在线，已经被自己添加 (这里需要网络配置)
                            case 120020:
                                // TODO: 2018/6/25 设备在线，已经被自己添加 (给出提示)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCameraList();
                                        mTvStatus.setVisibility(View.VISIBLE);
                                        mTvStatus.setTextColor(getResources().getColor(R.color.common_text));
                                        mTvStatus.setText(getString(R.string.tip_of_added_by_yourself_and_online));
                                        mBtnNext.setVisibility(View.VISIBLE);
                                        mAddButton.setVisibility(View.GONE);
                                        mConnectTip.setVisibility(View.GONE);
                                    }
                                });
                                break;
                            case 120022:
                                // TODO: 2018/6/25  设备在线，已经被别的用户添加
                            case 120024:
                                // TODO: 2018/6/25  设备不在线，已经被别的用户添加
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCameraList();
                                        mTvStatus.setVisibility(View.VISIBLE);
                                        mTvStatus.setTextColor(getResources().getColor(R.color.common_text));
                                        mTvStatus.setText("此设备已被别人添加");
                                        mBtnNext.setVisibility(View.VISIBLE);
                                        mAddButton.setVisibility(View.GONE);
                                        mConnectTip.setVisibility(View.GONE);
                                    }
                                });
                                break;
                            default:
                                // TODO: 2018/6/25 请求异常
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast("请求失败 : " + mEZProbeDeviceInfo.getBaseException().getErrorCode());
                                    }
                                });
                                break;
                        }
                    }
                }else{
                    toast("查询失败，网络不给力");
                }
            }
        }.start();
    }
    //隐藏输入键盘
    private void hideKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSeriesNumberEt.getWindowToken(), 0);
    }
    //显示查询摄像头的加载动画
    private void showQueryingCamera() {
        mInputLinearlayout.setVisibility(View.GONE);
        mCameraListLy.setVisibility(View.GONE);
        mTitle.setText("设备搜索");
        mQueryingCameraRyt.setVisibility(View.VISIBLE);
    }
    //显示设备添加界面
    private void showAddButton() {
        showCameraList();
        mBtnNext.setVisibility(View.GONE);
        mAddButton.setVisibility(View.VISIBLE);
        mConnectTip.setVisibility(View.GONE);
        mTvStatus.setVisibility(View.GONE);
    }
    //展示设备查询结果界面
    private void showCameraList() {
        mTitle.setText("设备状态查询结果");
        mCameraListLy.setVisibility(View.VISIBLE);
        mQueryingCameraRyt.setVisibility(View.GONE);
        mInputLinearlayout.setVisibility(View.GONE);
        //设置设备名称
        mDeviceName.setText(mSeriesNumberEt.getText().toString().trim());
    }
}