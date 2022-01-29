package ncist.wl171.intelligentcloudvideo.Activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.R;

import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;

public class EZDeviceSettingActivity extends RootActivity {

    /* 设备删除 */
    private View mDeviceDeleteView;
    //活动检测开关
    private Button mDefenceToggleButton;
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
                        case R.id.defence_toggle_button:
                            /**获取设备布防状态
                            * 返回:
                            * 具有防护能力的设备布撤防状态：0-睡眠，8-在家，16-外出，普通IPC布撤防状态：0-撤防，1-布防*/
                            new SetDefenceTask().execute(mEZDeviceInfo.getDefence() == 0);
                            break;
                        case R.id.device_delete:
                            showdeleteDialog();
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }
    //显示确定删除设备的提示框
    private void showdeleteDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(EZDeviceSettingActivity.this);
        //设置提示框图标
        exitDialog.setIcon(R.drawable.tips);
        //设置提示框的内容
        exitDialog.setMessage("确定要删除设备？");
        //如果点击确定则启动删除设备的线程
        exitDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), getString(R.string.tip_login_out), Toast.LENGTH_LONG).show();
                new DeleteDeviceTask().execute();
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
    //删除设备的子线程
    private class DeleteDeviceTask extends AsyncTask<Void, Void, Boolean> {
        private Dialog mWaitDialog;
        private int mErrorCode = 0;
        //执行线程任务前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitDialog = new Dialog(EZDeviceSettingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            mWaitDialog.setContentView(R.layout.wait_dialog);
            mWaitDialog.setCancelable(false);
            mWaitDialog.show();
        }
        //执行任务中的耗时操作
        @Override
        protected Boolean doInBackground(Void... params) {
            //判断网络是否可用
            if (!ConnectionDetector.isNetworkAvailable(EZDeviceSettingActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return false;
            }
            //删除当前账号的设备,传入设备序列号
            try {
                BaseApplication.getOpenSDK().deleteDevice(mEZDeviceInfo.getDeviceSerial());
                return true;
            } catch (BaseException e) {
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.d(TAG, errorInfo.toString());
                return false;
            }
        }
        //线程任务结束时自动调用
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mWaitDialog.dismiss();
            if (result) {
                toast("删除成功!");
                Intent intent = new Intent(EZDeviceSettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                switch (mErrorCode) {
                    case ErrorCode.ERROR_WEB_SESSION_ERROR:
                    case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                        toast("删除失败，请重新登录！");
                        break;
                    case ErrorCode.ERROR_WEB_NET_EXCEPTION:
                        toast("删除失败，请检查您的网络");
                        break;
                    case ErrorCode.ERROR_WEB_DEVICE_VALICATECODE_ERROR:
                        toast("验证码错误，请核对");
                    default:
                        toast("删除失败" + mErrorCode);
                        break;
                }
            }
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
        mDeviceDeleteView = findViewById(R.id.device_delete);
        mDefenceToggleButton = (Button) findViewById(R.id.defence_toggle_button);
        mDeviceSerialTextView = (TextView) findViewById(R.id.ez_device_serial);
        mDeviceInfoLayout = (LinearLayout) findViewById(R.id.device_info_layout);
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
    //初始化视图与UI
    private void setupDeviceInfo() {
        if (mEZDeviceInfo != null) {
            String typeSn = mEZDeviceInfo.getDeviceName();
            mDeviceNameView.setText(TextUtils.isEmpty(typeSn) ? "" : typeSn);
            mDeviceSerialTextView.setText(mEZDeviceInfo.getDeviceSerial());
            mDeviceInfoLayout.setOnClickListener(mOnClickListener);
            //获取设备活动检测是否开启的状态
            boolean isDefenceEnable = (mEZDeviceInfo.getDefence() != 0);
            mDefenceToggleButton.setBackgroundResource(isDefenceEnable ? R.drawable.autologin_on
                    : R.drawable.autologin_off);
            mDefenceToggleButton.setOnClickListener(mOnClickListener);
            mDeviceDeleteView.setOnClickListener(mOnClickListener);
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
    //更改活动检测开关
    private class SetDefenceTask extends AsyncTask<Boolean, Void, Boolean> {
        private Dialog mWaitDialog;
        private int mErrorCode = 0;
        boolean bSetDefence;
        //执行线程任务前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitDialog = new Dialog(EZDeviceSettingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            mWaitDialog.setContentView(R.layout.wait_dialog);
            //设置返回键和点击其他区域不会使对话框消失
            mWaitDialog.setCancelable(false);
            mWaitDialog.show();
        }
        //执行任务中的耗时操作
        @Override
        protected Boolean doInBackground(Boolean... params) {
            bSetDefence = (Boolean) params[0];
            Boolean result = false;
            try {
                result = BaseApplication.getOpenSDK().setDefence(mEZDeviceInfo.getDeviceSerial(), bSetDefence? EZConstants.EZDefenceStatus.EZDefence_IPC_OPEN:
                        EZConstants.EZDefenceStatus.EZDefence_IPC_CLOSE);
            } catch (BaseException e) {
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.d(TAG, errorInfo.toString());
                e.printStackTrace();
            }
            return result;
        }
        //线程任务结束时自动调用
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mWaitDialog.dismiss();
            if(result) {
                mEZDeviceInfo.setDefence(bSetDefence ? 1 : 0);
                setupDeviceInfo();
            } else {
                switch (mErrorCode) {
                    case ErrorCode.ERROR_WEB_NET_EXCEPTION:
                        toast("操作失败，请检查您的网络！");
                        break;
                    case ErrorCode.ERROR_WEB_SESSION_ERROR:
                        toast("操作失败，请重新登录！");
                        break;
                    default:
                        toast("操作失败！" + mErrorCode);
                        break;
                }
            }
        }

    }
}