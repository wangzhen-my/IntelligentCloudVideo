package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.util.List;

import ncist.wl171.intelligentcloudvideo.ActivityUtils;
import ncist.wl171.intelligentcloudvideo.EZCameraListAdapter;
import ncist.wl171.intelligentcloudvideo.R;
import ncist.wl171.intelligentcloudvideo.pulltorefresh.PullToRefreshListView;

import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;

public class MainActivity extends RootActivity implements View.OnClickListener {

    private EZCameraListAdapter mAdapter = null;

    protected static final String TAG = "MainActivity";

    private LinearLayout mNoCameraTipLy = null;

    private PullToRefreshListView mListView = null;

    private Button mUserBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mAdapter = new EZCameraListAdapter(this);
        mNoCameraTipLy = (LinearLayout) findViewById(R.id.no_camera_tip_ly);
        mListView = (PullToRefreshListView) findViewById(R.id.camera_listview);
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
        private boolean mHeaderOrFooter;

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
                toast("获取设备列表成功！");
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
                    ActivityUtils.handleSessionException(MainActivity.this);
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
}