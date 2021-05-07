package ncist.wl171.intelligentcloudvideo;

import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

public class EZUtils {

    //获取设备的对应通道
    public static EZCameraInfo getCameraInfoFromDevice(EZDeviceInfo deviceInfo,int camera_index) {
        if (deviceInfo == null) {
            return null;
        }
        //判断设备通道数量是否大于0 && 获取设备通道列表并不等于null && 设备通道列表长度大于camera_index
        if (deviceInfo.getCameraNum() > 0 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() > camera_index) {
            return deviceInfo.getCameraInfoList().get(camera_index);
        }
        return null;
    }
}
