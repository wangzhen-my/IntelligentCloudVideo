package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;

import ncist.wl171.intelligentcloudvideo.pulltorefresh.IPullToRefresh.OnRefreshListener;

import ncist.wl171.intelligentcloudvideo.R;
import ncist.wl171.intelligentcloudvideo.pulltorefresh.PullToRefreshBase;
import ncist.wl171.intelligentcloudvideo.pulltorefresh.PullToRefreshListView;

import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;

public class MainActivity extends RootActivity implements View.OnClickListener {

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
        mNoCameraTipLy = (LinearLayout) findViewById(R.id.no_camera_tip_ly);
        mListView = (PullToRefreshListView) findViewById(R.id.camera_listview);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView, boolean headerOrFooter) {
                //获取CamersInfo列表任务，先判断activity是否正在finish，如果不在则执行子线程更新
                getCameraInfoList(headerOrFooter);
            }
        });
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

    private void getCameraInfoList(boolean headerOrFooter) {
        if (this.isFinishing()) {
            return;
        }
        new GetCamersInfoListTask(headerOrFooter).execute();
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
        //设置列表重新刷新
        mListView.setRefreshing();
        toast("刷新列表");
    }

    private class GetCamersInfoListTask extends AsyncTask<Void,Void, List<EZDeviceInfo>> {
        private boolean mHeaderOrFooter;

        public GetCamersInfoListTask(boolean headerOrFooter) {
            mHeaderOrFooter = headerOrFooter;
        }

        @Override
        protected List<EZDeviceInfo> doInBackground(Void... voids) {
            toast("启动子线程！");
            return null;
        }
    }
}