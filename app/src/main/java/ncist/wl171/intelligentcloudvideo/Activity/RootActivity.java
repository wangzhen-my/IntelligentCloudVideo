package ncist.wl171.intelligentcloudvideo.Activity;

import android.app.Activity;
import android.widget.Toast;

public class RootActivity extends Activity {
    protected void toast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
