package ncist.wl171.intelligentcloudvideo.Base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.videogo.openapi.EZOpenSDK;

public class BaseApplication extends Application{


    public static EZOpenSDK getOpenSDK(){
        EZOpenSDK ezOpenSDK = EZOpenSDK.getInstance();
        return ezOpenSDK;
    }

//    /**
//     * 包名判断是否为主进程
//     */
//    private boolean isMainProcess() {
//        return getApplicationContext().getPackageName().equals(getCurrentProcessName());
//    }
//
//    /**
//     * 获取当前进程名
//     */
//    private String getCurrentProcessName() {
//        //获取当前进程ID
//        int pid = android.os.Process.myPid();
//        String processName = "";
//        //获取系统服务中管理应用程序的系统状态
//        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        //根据前面获得的进程ID遍历正在设备上运行的应用程序进程列表，来查询当前进程的进程名。
//        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
//            if (process.pid == pid) {
//                processName = process.processName;
//            }
//        }
//        return processName;
//    }
}
