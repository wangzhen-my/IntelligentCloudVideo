package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import ncist.wl171.intelligentcloudvideo.R;

public class RootActivity extends Activity {
    protected void toast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    //设置状态栏颜色
    protected void setStatusBarColor(@NonNull Activity activity,@ColorRes int id){
        Window window;
        window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, id));
    }

    // 隐藏状态栏
    protected void settFullscreen(@NonNull Activity activity){
        Window window;
        window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
