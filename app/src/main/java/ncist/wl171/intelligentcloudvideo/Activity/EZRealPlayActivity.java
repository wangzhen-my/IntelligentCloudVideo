package ncist.wl171.intelligentcloudvideo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.widget.CheckTextButton;
import com.videogo.widget.TitleBar;

import ncist.wl171.intelligentcloudvideo.Base.BaseApplication;
import ncist.wl171.intelligentcloudvideo.R;

import static com.videogo.openapi.EZConstants.MSG_VIDEO_SIZE_CHANGED;

public class EZRealPlayActivity extends RootActivity implements View.OnClickListener,Handler.Callback,SurfaceHolder.Callback {

    private SurfaceView mRealPlaySv = null;
    //播放器
    private SurfaceHolder mRealPlaySh = null;
    //声音按钮
    private ImageButton mRealPlaySoundBtn = null;
    //直播声音的开关（默认开）
    private Boolean isSoundOpen = true;
    private Handler mHandler = null;
    //对讲按钮
    private ImageButton mRealPlayTalkBtn = null;
    //云台按钮
    private ImageButton mRealPlayPtzBtn = null;
    //直播清晰度按钮
    private Button mRealPlayQualityBtn = null;
    //小框左下角的播放按钮
    private ImageButton mRealPlayBtn = null;
    //镜头隐蔽中提示组（一张图与一个text文本）
    private LinearLayout mRealPlayPlayPrivacyLy;
    //正在加载画面时“正在加载，请稍等...”提示框
    private TextView mRealPlayPlayLoading;
    //直播暂停时主画面上的播放按钮
    private ImageView mRealPlayPlayIv;
    //设备不在线文本提醒框
    private TextView mRealPlayTipTv;
    //整个加载页面的layout
    private RelativeLayout mRealPlayLoadingRl;
    private EZPlayer mEZPlayer = null;
    private static final String TAG = EZRealPlayActivity.class.getSimpleName();
    //一开始状态为初始化
    private int mStatus = RealPlayStatus.STATUS_INIT;
    //左上角返回图标
    private CheckTextButton mFullScreenTitleBarBackBtn;
    private TitleBar mPortraitTitleBar = null;
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
        initView();
    }

    private void initView() {
        initTitleBar();
        initLoadingUI();
        mRealPlayBtn = (ImageButton) findViewById(R.id.realplay_play_btn);
        //直播清晰度按钮
        mRealPlayQualityBtn = (Button) findViewById(R.id.realplay_quality_btn);
        mRealPlayPtzBtn = (ImageButton) findViewById(R.id.realplay_ptz_btn);
        //对讲按钮
        mRealPlayTalkBtn = (ImageButton) findViewById(R.id.realplay_talk_btn);
        mRealPlaySoundBtn = (ImageButton) findViewById(R.id.realplay_sound_btn);
        mRealPlaySv = (SurfaceView) findViewById(R.id.realplay_sv);
        mRealPlaySh = mRealPlaySv.getHolder();
        mRealPlaySh.addCallback(this);
    }

    private void initLoadingUI() {
        mRealPlayLoadingRl = (RelativeLayout) findViewById(R.id.realplay_loading_rl);
        mRealPlayTipTv = (TextView) findViewById(R.id.realplay_tip_tv);
        mRealPlayPlayIv = (ImageView) findViewById(R.id.realplay_play_iv);
        mRealPlayPlayLoading = findViewById(R.id.realplay_loading);
        mRealPlayPlayPrivacyLy = (LinearLayout) findViewById(R.id.realplay_privacy_ly);

        mRealPlayPlayIv.setOnClickListener(this);
    }

    private void initTitleBar() {
        mPortraitTitleBar = (TitleBar) findViewById(R.id.title_bar_portrait);
        mPortraitTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭云台弹出窗口
//                closePtzPopupWindow();
                //关闭对话弹出窗口
//                closeTalkPopupWindow(true, false);
//                if (mStatus != RealPlayStatus.STATUS_STOP) {
//                    stopRealPlay();
//                    setRealPlayStopUI();
//                }
                finish();
            }
        });
        mFullScreenTitleBarBackBtn = new CheckTextButton(this);
        mFullScreenTitleBarBackBtn.setBackground(getResources().getDrawable(R.drawable.common_title_back_selector));
    }

    private void initData() {
        mHandler = new Handler(this);
        Intent intent = getIntent();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.realplay_play_btn:
            case R.id.realplay_play_iv:
                //播放按钮，如果当前不是停止状态就停止播放
                if (mStatus != RealPlayStatus.STATUS_STOP) {
                    stopRealPlay();
                    setRealPlayStopUI();
                } else {
                    //如果当前播放状态为停止则开始播放
                    startRealPlay();
                }
                break;
//            case R.id.realplay_talk_btn:
//                //对讲一会自己试一试
//                selectTalkbackItems();
//                break;
//            case R.id.realplay_quality_btn:
//                //清晰度按钮
//                openQualityPopupWindow(mRealPlayQualityBtn);
//                break;
//            case R.id.realplay_ptz_btn:
//                //云台控制
//                openPtzPopupWindow(mRealPlayPlayRl);
//                break;
//            case R.id.realplay_sound_btn:
//                //静音按钮
//                onSoundBtnClick();
//                break;
//            default:
//                break;
        }
    }

    //停止播放
    private void stopRealPlay() {
        LogUtil.d(TAG, "stopRealPlay");
        //先把状态置为停止
        mStatus = RealPlayStatus.STATUS_STOP;
        if (mEZPlayer != null) {
            //停止播放
            mEZPlayer.stopRealPlay();
        }
    }

    //设置直播停止的UI
    private void setRealPlayStopUI() {
        //停止对讲
//        Stoptalking();
        //设置停止直播时页面的加载提示视图（有用）
        setStopLoading();
        //改变左下角播放按钮的状态
        mRealPlayBtn.setBackgroundResource(R.drawable.play_play_selector);
        if (mCameraInfo != null && mDeviceInfo != null) {
            if (mDeviceInfo.getStatus() == 1) {
                //如果设备在线，将清晰度按钮置为有效
                mRealPlayQualityBtn.setEnabled(true);
            } else {
                //如果设备不在线，将清晰度按钮置为无效
                mRealPlayQualityBtn.setEnabled(false);
            }
            //设置云台按钮为无效
            mRealPlayPtzBtn.setEnabled(false);
            //设置对讲按钮无效
            mRealPlayTalkBtn.setEnabled(false);
        }
    }

    public void setStopLoading() {
        mRealPlayLoadingRl.setVisibility(View.VISIBLE);
        //设备不在线的文本提示框
        mRealPlayTipTv.setVisibility(View.GONE);
        //正在加载画面时“正在加载，请稍等...”提示框
        mRealPlayPlayLoading.setVisibility(View.GONE);
        //直播暂停时主画面上的播放按钮
        mRealPlayPlayIv.setVisibility(View.VISIBLE);
        //镜头隐蔽中提示组（一张图与一个text文本）
        mRealPlayPlayPrivacyLy.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在onResume中初始化ui
        initUI();
        if (mCameraInfo != null && mDeviceInfo != null &&  mDeviceInfo.getStatus() != 1) {
            if (mStatus != RealPlayStatus.STATUS_STOP) {
                stopRealPlay();
            }
            setRealPlayFailUI("设备不在线");
        } else {
            if (mStatus == RealPlayStatus.STATUS_PAUSE || mStatus == RealPlayStatus.STATUS_DECRYPT) {
                // 开始播放
                startRealPlay();
            }
        }
    }
    //初始化UI
    private void initUI() {
        if (mCameraInfo != null) {
            //设置最上方状态栏的设备名称
            mPortraitTitleBar.setTitle(mCameraInfo.getCameraName());
            if (isSoundOpen) {
                //设置声音按钮
                mRealPlaySoundBtn.setBackgroundResource(R.drawable.ezopen_vertical_preview_sound_selector);
            } else {
                mRealPlaySoundBtn.setBackgroundResource(R.drawable.ezopen_vertical_preview_sound_off_selector);
            }
            //设置直播清晰度
            setVideoLevel();
        }
        //更新清晰切换按钮可见性
        updateQualityBtnVisibility();
    }
    //更新清晰切换按钮可见性
    private void updateQualityBtnVisibility(){
        // 获取不到清晰度数据时，不展示清晰度
        if (mCameraInfo != null && mCameraInfo.getVideoQualityInfos() != null && mCameraInfo.getVideoQualityInfos().size() > 0){
            mRealPlayQualityBtn.setVisibility(View.VISIBLE);
        }else{
            mRealPlayQualityBtn.setVisibility(View.INVISIBLE);
        }
    }
    //设置直播清晰度
    private void setVideoLevel() {
        if (mCameraInfo == null || mEZPlayer == null || mDeviceInfo == null) {
            return;
        }
        //如果设备在线则设置清晰度按钮有效
        if (mDeviceInfo.getStatus() == 1) {
            mRealPlayQualityBtn.setEnabled(true);
        } else {
            mRealPlayQualityBtn.setEnabled(false);
        }
        //设置直播清晰度
        mCameraInfo.setVideoLevel(mCurrentQulityMode.getVideoLevel());
        if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET.getVideoLevel()) {
            mRealPlayQualityBtn.setText("流畅");
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED.getVideoLevel()) {
            mRealPlayQualityBtn.setText("均衡");
        } else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD.getVideoLevel()) {
            mRealPlayQualityBtn.setText("高清");
        }else if (mCurrentQulityMode.getVideoLevel() == EZConstants.EZVideoLevel.VIDEO_LEVEL_SUPERCLEAR.getVideoLevel()){
            mRealPlayQualityBtn.setText("超清");
        }else{
            mRealPlayQualityBtn.setText("未知");
        }
    }

    //开始播放
    private void startRealPlay() {
        // 增加手机客户端操作信息记录
        LogUtil.d(TAG, "startRealPlay");
        //如果状态为开始或者正在播放则直接返回
        if (mStatus == RealPlayStatus.STATUS_START || mStatus == RealPlayStatus.STATUS_PLAY) {
            return;
        }
        // 检查网络是否可用，若不可用则提示信息并返回
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            setRealPlayFailUI(getString(R.string.realplay_play_fail_becauseof_network));
            return;
        }
        //设置状态为开始并设置正在加载的页面UI
        mStatus = RealPlayStatus.STATUS_START;
        //设置正在加载的界面
        setRealPlayLoadingUI();
        if (mCameraInfo != null) {
            mEZPlayer = BaseApplication.getOpenSDK().createPlayer(mCameraInfo.getDeviceSerial(), mCameraInfo.getCameraNo());
            if (mEZPlayer == null||mDeviceInfo == null)
                return;
            /**
             * 设备加密的需要传入密码
             * 传入视频加密密码，用于加密视频的解码，该接口可以在收到ERROR_INNER_VERIFYCODE_NEED或ERROR_INNER_VERIFYCODE_ERROR错误回调时调用
             * @param verifyCode 视频加密密码，默认为设备的6位验证码
             */
//            mEZPlayer.setPlayVerifyCode(DataManager.getInstance().getDeviceSerialVerifyCode(mCameraInfo.getDeviceSerial()));
            //设置Handler, 该handler将被用于从播放器向handler传递消息
            mEZPlayer.setHandler(mHandler);
            //设置播放器的显示Surface
            mEZPlayer.setSurfaceHold(mRealPlaySh);
            mEZPlayer.startRealPlay();
        }
    }

    //设置正在加载的页面UI
    private void setRealPlayLoadingUI() {
        setStartloading();
        mRealPlayBtn.setBackgroundResource(R.drawable.play_stop_selector);
        if (mCameraInfo != null  && mDeviceInfo != null) {
            if (mDeviceInfo.getStatus() == 1) {
                //如果设备在线设置清晰度按钮有效
                mRealPlayQualityBtn.setEnabled(true);
            } else {
                mRealPlayQualityBtn.setEnabled(false);
            }
            //云台按钮与对讲按钮设置无效
            mRealPlayPtzBtn.setEnabled(false);
            mRealPlayTalkBtn.setEnabled(false);
        }
    }

    //设置直播失败的UI
    private void setRealPlayFailUI(String errorStr) {
        setLoadingFail(errorStr);
        mRealPlayBtn.setBackgroundResource(R.drawable.play_play_selector);
        if (mCameraInfo != null && mDeviceInfo != null) {
            //如果设备在线并且mEZPlayer 不为null
            if (mDeviceInfo.getStatus() == 1 && (mEZPlayer != null)) {
                //直播清晰度按钮设置有效
                mRealPlayQualityBtn.setEnabled(true);
            } else {
                mRealPlayQualityBtn.setEnabled(false);
            }
            //云台按钮与对讲按钮设置无效
            mRealPlayPtzBtn.setEnabled(false);
            mRealPlayTalkBtn.setEnabled(false);
        }
    }

    //设置出错时加载页面的内容
    public void setLoadingFail(String errorStr) {
        mRealPlayLoadingRl.setVisibility(View.VISIBLE);
        //设置设备不在线的文本提示框可见并重新设置其文本内容
        mRealPlayTipTv.setVisibility(View.VISIBLE);
        mRealPlayTipTv.setText(errorStr);
        //隐藏“正在加载，请稍等...”提示框；主画面上的播放按钮；镜头隐蔽中提示组
        mRealPlayPlayLoading.setVisibility(View.GONE);
        mRealPlayPlayIv.setVisibility(View.GONE);
        mRealPlayPlayPrivacyLy.setVisibility(View.GONE);
    }

    //设置正在加载时的加载页面内容
    private void setStartloading() {
        //显示整个加载页面与“正在加载，请稍等...”提示框；
        mRealPlayPlayLoading.setVisibility(View.VISIBLE);
        mRealPlayLoadingRl.setVisibility(View.VISIBLE);
        //隐藏设备不在线文本提醒框；主画面上的播放按钮；镜头隐蔽中提示组
        mRealPlayTipTv.setVisibility(View.GONE);
        mRealPlayPlayIv.setVisibility(View.GONE);
        mRealPlayPlayPrivacyLy.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEZPlayer != null) {
            //释放资源
            mEZPlayer.release();
        }
        mHandler.removeMessages(EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL);
        mHandler.removeMessages(EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS);
        mHandler.removeMessages(MSG_VIDEO_SIZE_CHANGED);
        mHandler = null;
    }
    //设置handle消息处理
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (this.isFinishing()) {
            return false;
        }
        switch (msg.what) {
            case MSG_VIDEO_SIZE_CHANGED:
                //拿到画面分辨率
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                //播放成功
                handlePlaySuccess(msg);
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                //播放失败则跳出提示停止播放，传入的msg是Message类型的，后面要转成int型，所以这里以obj型传过去
                handlePlayFail(msg.obj);
                break;
            default:
                // do nothing
                break;
        }
        return false;
    }
    //处理播放失败的handle消息
    private void handlePlayFail(Object obj) {
        int errorCode = 0;
        if (obj != null) {
            ErrorInfo errorInfo = (ErrorInfo) obj;
            errorCode = errorInfo.errorCode;
            LogUtil.d(TAG, "handlePlayFail:" + errorInfo.errorCode);
        }
        stopRealPlay();
        updateRealPlayFailUI(errorCode);
    }
    //更新播放错误时的UI
    private void updateRealPlayFailUI(int errorCode) {
        String txt = null;
        LogUtil.i(TAG, "updateRealPlayFailUI: errorCode:" + errorCode);
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
                txt = "accesstoken无效请重新登录";
                return;
            case ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE:
                txt = "设备连接数过大，停止其他连接后再试试吧";
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                if (mCameraInfo != null) {
                    //设置分享状态为未分享
                    mCameraInfo.setIsShared(0);
                }
                txt = "设备不在线";
                break;
            case ErrorCode.ERROR_INNER_STREAM_TIMEOUT:
                txt = "播放失败，连接设备异常";
                break;
            case ErrorCode.ERROR_WEB_CODE_ERROR:
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                txt = "验证硬件特征码失败";
                break;
            case ErrorCode.ERROR_TRANSF_TERMINAL_BINDING:
                txt = "请在萤石客户端关闭终端绑定 ";
                break;
            // 收到这两个错误码，可以弹出对话框，让用户输入验证码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR:
                txt = "设备是加密设备无法播放 ";
            break;
            case ErrorCode.ERROR_EXTRA_SQUARE_NO_SHARING:
            default:
                txt = "视频播放失败";
                break;
        }
        if (!TextUtils.isEmpty(txt)) {
            setRealPlayFailUI(txt);
        } else {
            setRealPlayStopUI();
        }
    }

    //收到成功播放的消息
    private void handlePlaySuccess(Message msg) {
        LogUtil.d(TAG,"handlePlaySuccess");
        //将状态设置为正在播放
        mStatus = RealPlayStatus.STATUS_PLAY;
        // 声音处理，设置直播声音
        setRealPlaySound();
        //初始化UI
        initUI();
        //设置播放成功的UI
        setRealPlaySuccessUI();
        //如果设备支持对讲
        if (mDeviceInfo != null && mDeviceInfo.isSupportTalk() != EZConstants.EZTalkbackCapability.EZTalkbackNoSupport) {
            mRealPlayTalkBtn.setEnabled(true);
        }else{
            mRealPlayTalkBtn.setEnabled(false);
        }
    }
    //设置直播声音
    private void setRealPlaySound() {
        if (mEZPlayer != null) {
            if (isSoundOpen) {
                mEZPlayer.openSound();
            } else {
                mEZPlayer.closeSound();
            }
        }
    }
    //设置播放成功的UI
    private void setRealPlaySuccessUI() {
        //设置加载视图的播放成功界面
        setLoadingSuccess();
        //设置左下角播放按钮
        mRealPlayBtn.setBackgroundResource(R.drawable.play_stop_selector);
        if (mCameraInfo != null && mDeviceInfo != null) {
            if (mDeviceInfo.getStatus() == 1) {
                //设置直播清晰度按钮有效
                mRealPlayQualityBtn.setEnabled(true);
            } else {
                mRealPlayQualityBtn.setEnabled(false);
            }
            if (getSupportPtz() == 1) {
                //如果设备支持云台或者缩放则设置云台按钮有效
                mRealPlayPtzBtn.setEnabled(true);
            }
        }
    }
    //设备是否支持云台与缩放
    private int getSupportPtz() {
        if (mEZPlayer == null || mDeviceInfo == null) {
            return 0;
        }
        //如果设备支持云台控制则返回1
        if (mDeviceInfo.isSupportPTZ()) {
            return 1;
        } else {
            return 0;
        }
    }
    //设置加载视图的播放成功界面
    private void setLoadingSuccess() {
        mRealPlayLoadingRl.setVisibility(View.INVISIBLE);
        mRealPlayTipTv.setVisibility(View.GONE);
        mRealPlayPlayLoading.setVisibility(View.GONE);
        mRealPlayPlayIv.setVisibility(View.GONE);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        mRealPlaySh = holder;
        if (mStatus == RealPlayStatus.STATUS_INIT) {
            // 开始播放
            startRealPlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        mRealPlaySh = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(null);
        }
        mRealPlaySh = null;
    }
}