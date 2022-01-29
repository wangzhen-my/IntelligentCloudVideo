package ncist.wl171.intelligentcloudvideo.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.EZMessageListAdapter;
import ncist.wl171.intelligentcloudvideo.R;

public class EZMessageActivity2 extends RootActivity {

    //没有数据提示信息框
    private LinearLayout mNoMessageLayout;
    //数据为空或不存在
    public static final int ERROR_WEB_NO_DATA = 100000 - 2;
    //查询告警信息的设备序列号，为null时查询整个账号下的告警信息
    private String mDeviceSerial;
    //告警信息列表
    private List<EZAlarmInfo> mMessageList;
    private EZMessageListAdapter mAdapter;
    private ListView mMessageListView;
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ez_message_page);
        //设置状态栏颜色
        setStatusBarColor(this,R.color.c4);
        initView();
        initData();
    }

    private void initData() {
        mMessageList = new ArrayList<EZAlarmInfo>();
        mAdapter = new EZMessageListAdapter(this,mMessageList);
        mMessageListView.setAdapter(mAdapter);
        //从上一个页面获取传入的设备序列号
        mDeviceSerial = getIntent().getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
    }

    private void initView() {
        mNoMessageLayout = findViewById(R.id.no_message_layout);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle("消息");
        mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mMessageListView = (ListView) findViewById(R.id.message_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动获取告警信息的线程
        new GetAlarmMessageTask().execute();
    }
    private class GetAlarmMessageTask extends AsyncTask<Void, Void, List<EZAlarmInfo>> {
        private int mErrorCode = 100000;// ErrorCode.ERROR_WEB_NO_ERROR;
        //执行任务中的耗时操作
        @Override
        protected List<EZAlarmInfo> doInBackground(Void... voids) {
            //判断网络是否连接
            if (!ConnectionDetector.isNetworkAvailable(EZMessageActivity2.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }
            List<EZAlarmInfo> result = null;
            try {
                /**获取告警信息列表 该接口为耗时操作，必须在线程中调用
                 参数:
                 deviceSerial - 设备序列号，为null时查询整个账户下的告警信息列表
                 pageIndex - 查询页index，从0开始
                 pageSize - 每页数量（建议20以内）
                 beginTime - 搜索时间范围开始时间，开始时间和结束时间可以同时为空
                 endTime - 搜索时间范围结束时间
                 返回:
                 EZAlarmInfo对象列表*/
                result = BaseApplication.getOpenSDK().getAlarmList(mDeviceSerial, 0, 20,null,
                        null);
            }
            catch (BaseException e) {
                e.printStackTrace();
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.d("EM", errorInfo.toString());
            }
            return result;
        }
        //线程任务结束时自动调用
        @Override
        protected void onPostExecute(List<EZAlarmInfo> result) {
            super.onPostExecute(result);
            int pageSize = 10;
            if (result == null && mErrorCode != 100000){
                    onError(mErrorCode);
                return;
            }
            if (result != null && result.size() > 0) {
                mMessageList.addAll(result);
                mAdapter.setList(mMessageList);
                mAdapter.notifyDataSetChanged();
            } else {
                mErrorCode = ERROR_WEB_NO_DATA;
            }
            //如果有数据则设置"没有数据"提示信息不可见
            if (mMessageList.size() > 0) {
                mNoMessageLayout.setVisibility(View.GONE);
            }
            if (mErrorCode != 100000)
                onError(mErrorCode);
        }
        protected void onError(int errorCode) {
            switch (errorCode) {
                case ERROR_WEB_NO_DATA:
                    if (mMessageList.size() == 0) {
                        mNoMessageLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    toast("获取事件消息失败" + errorCode);
                    break;
            }
        }
    }
}