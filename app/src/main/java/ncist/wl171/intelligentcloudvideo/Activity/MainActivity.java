package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import ncist.wl171.intelligentcloudvideo.R;

import static ncist.wl171.intelligentcloudvideo.Base.BaseApplication.getOpenSDK;

public class MainActivity extends RootActivity implements View.OnClickListener {

    private Button mUserBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
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
    }
}