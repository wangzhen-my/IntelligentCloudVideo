package ncist.wl171.intelligentcloudvideo.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;

import ncist.wl171.intelligentcloudvideo.R;

public class EZDeviceSettingActivity extends RootActivity {

    private final String TAG = "EZDeviceSettingActivity";
    private static final int REQUEST_CODE_MODIFY_DEVICE_NAME = 2;
    private View.OnClickListener mOnClickListener;
    //修改设备名栏
    private LinearLayout mDeviceInfoLayout;
    //设置序列号
    private TextView mDeviceSerialTextView;
    //设备名称
    private TextView mDeviceNameView;
    //设备信息对象
    private EZDeviceInfo mEZDeviceInfo = null;
    //上方标题栏
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_setting_page);
        initView();
        initData();
        initClickListener();
    }

    private void initClickListener() {
        if (mEZDeviceInfo != null) {
            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch (v.getId()) {
                        case R.id.device_info_layout:
                            intent = new Intent(EZDeviceSettingActivity.this, ModifyDeviceNameActivity.class);
                            intent.putExtra(IntentConsts.EXTRA_NAME, mEZDeviceInfo.getDeviceName());
                            intent.putExtra(IntentConsts.EXTRA_DEVICE_ID,mEZDeviceInfo.getDeviceSerial());
                            startActivityForResult(intent, REQUEST_CODE_MODIFY_DEVICE_NAME);
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Bundle");
        mEZDeviceInfo = bundle.getParcelable(IntentConsts.EXTRA_DEVICE_INFO);
        if (mEZDeviceInfo == null){
            toast("设备未添加或已删除");
            finish();
        }
    }

    private void initView() {
        mDeviceInfoLayout = (LinearLayout) findViewById(R.id.device_info_layout);
        mDeviceSerialTextView = (TextView) findViewById(R.id.ez_device_serial);
        setStatusBarColor(this,R.color.c4);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.ez_setting);
        mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDeviceNameView = (TextView) findViewById(R.id.device_name);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setupDeviceInfo();
    }

    private void setupDeviceInfo() {
        if (mEZDeviceInfo != null) {
            String typeSn = mEZDeviceInfo.getDeviceName();
            mDeviceNameView.setText(TextUtils.isEmpty(typeSn) ? "" : typeSn);
            mDeviceSerialTextView.setText(mEZDeviceInfo.getDeviceSerial());
            mDeviceInfoLayout.setOnClickListener(mOnClickListener);
        }
    }
    //接收从修改设备名界面传回的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODIFY_DEVICE_NAME) {
            String name = data.getStringExtra(IntentConsts.EXTRA_NAME);
            if (!TextUtils.isEmpty(name)){
                mEZDeviceInfo.setDeviceName(name);
            }else{
                LogUtil.d(TAG,"modify device name is null");
            }
        }
    }
}