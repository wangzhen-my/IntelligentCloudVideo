package ncist.wl171.intelligentcloudvideo.Activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.R;

public class ModifyDeviceNameActivity extends RootActivity {

    private String mDeviceSerial;
    private Dialog mWaitDialog;
    private String mDeviceNameString;
    //设备名
    private String mDeviceName;
    //保存按钮
    private Button mSaveButton = null;
    //设备名输入框
    private EditText mNameText;
    //清空输入栏
    private ImageButton mNameDelButton;
    //标题栏
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_device_name_page);
        if (getIntent().hasExtra(IntentConsts.EXTRA_NAME)) {
            mDeviceName = getIntent().getStringExtra(IntentConsts.EXTRA_NAME);
        }
        if (getIntent().hasExtra(IntentConsts.EXTRA_DEVICE_ID)){
            mDeviceSerial = getIntent().getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
        }
        initView();
    }

    private void initView() {
        mWaitDialog = new Dialog(ModifyDeviceNameActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDialog.setCancelable(false);
        mWaitDialog.setContentView(R.layout.wait_dialog);
        mSaveButton = (Button) findViewById(R.id.btn_id_save_name);
        mNameText = (EditText) findViewById(R.id.name_text);
        setStatusBarColor(this,R.color.c4);
        mNameDelButton = (ImageButton) findViewById(R.id.name_del);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle("修改设备名");
        mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mNameDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameText.setText(null);
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mDeviceName)) {
                    return;
                }
                mDeviceNameString = mNameText.getText().toString().trim();
                if (TextUtils.isEmpty(mDeviceNameString)) {
                    toast("不能为空！");
                    return;
                }
                // 本地网络检测
                if (!ConnectionDetector.isNetworkAvailable(ModifyDeviceNameActivity.this)) {
                    toast("当前网络不可用，请检查您的网络");
                    return;
                }
                mWaitDialog.show();

                new Thread() {
                    @Override
                    public void run() {
                        int errorCode = 0;
                        try {
                            BaseApplication.getOpenSDK().setDeviceName(mDeviceSerial, mDeviceNameString);
                        } catch (BaseException e) {
                            e.printStackTrace();
                            ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                            errorCode = errorInfo.errorCode;
                            LogUtil.d("TAG", errorInfo.toString());
                        }
                        if (errorCode != 0) {
                            toast("修改失败！" + errorCode);
                        } else {
                            mWaitDialog.dismiss();
                            toast("修改成功！");
                            Intent intent = new Intent();
                            intent.putExtra(IntentConsts.EXTRA_NAME, mDeviceNameString);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }.start();
            }
        });
    }
}