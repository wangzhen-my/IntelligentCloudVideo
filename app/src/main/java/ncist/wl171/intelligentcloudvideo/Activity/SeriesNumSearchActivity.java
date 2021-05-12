package ncist.wl171.intelligentcloudvideo.Activity;


import android.os.Bundle;

import ncist.wl171.intelligentcloudvideo.R;

public class SeriesNumSearchActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camera_by_series_number_page);
        //设置activity状态栏颜色
        setStatusBarColor(this,R.color.c4);
    }
}