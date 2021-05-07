package ncist.wl171.intelligentcloudvideo.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.videogo.constant.IntentConsts;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import ncist.wl171.intelligentcloudvideo.R;

public class EZRealPlayActivity extends RootActivity {

    //设备清晰度（初始值为高清）
    private EZConstants.EZVideoLevel mCurrentQulityMode = EZConstants.EZVideoLevel.VIDEO_LEVEL_HD;
    //设备信息对象
    private EZDeviceInfo mDeviceInfo = null;
    //通道信息对象
    private EZCameraInfo mCameraInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ez_realplay_page);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        //获取主页面传过来的值
        if (intent != null) {
            //获取主页面传入的设备信息对象
            mCameraInfo = intent.getParcelableExtra(IntentConsts.EXTRA_CAMERA_INFO);
            //获取主页面传入的通道信息对象
            mDeviceInfo = intent.getParcelableExtra(IntentConsts.EXTRA_DEVICE_INFO);
            if (mCameraInfo != null) {
                //获取清晰度
                mCurrentQulityMode = (mCameraInfo.getVideoLevel());
            }
        }
    }

    @Override
    public void finish() {
        //如果通道信息对象不为空，界面结束时向主页面返回数据
        if (mCameraInfo != null){
            Intent intent = new Intent();
            //发送camera对应的设备数字序列号
            intent.putExtra(IntentConsts.EXTRA_DEVICE_ID,mCameraInfo.getDeviceSerial());
            //发送camera在对应设备上的通道号
            intent.putExtra(IntentConsts.EXTRA_CAMERA_NO,mCameraInfo.getCameraNo());
            //发送清晰度
            intent.putExtra("video_level",mCameraInfo.getVideoLevel().getVideoLevel());
            setResult(MainActivity.RESULT_CODE, intent);
        }
        super.finish();
    }
}