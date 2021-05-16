package ncist.wl171.intelligentcloudvideo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EZCameraListAdapter extends BaseAdapter {

    private OnClickListener mListener;
    private Context mContext = null;
    public Map<String, EZDeviceInfo> mExecuteItemMap = null;
    private List<EZDeviceInfo> mCameraInfoList = null;

    public EZCameraListAdapter(Context context) {
        mContext = context;
        mCameraInfoList = new ArrayList<EZDeviceInfo>();
        mExecuteItemMap = new HashMap<String, EZDeviceInfo>();
    }

    //列表的长度
    @Override
    public int getCount() {
        return mCameraInfoList.size();
    }

    //获取并返回列表中对应顺序的设备信息对象
    @Override
    public EZDeviceInfo getItem(int position) {
        EZDeviceInfo item = null;
        //输入必须大于0并小于数组元素数量
        if (position >= 0 && getCount() > position) {
            item = mCameraInfoList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //在初始显示时；listview滚动时；notifyDataSetChanged处理时会调用此方法
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 此类中的自定义视图类
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cameralist_small_item, null);
            // 获取控件对象
            viewHolder.playBtn = (ImageView) convertView.findViewById(R.id.item_play_btn);
            viewHolder.offlineBtn = (ImageView) convertView.findViewById(R.id.item_offline);
            viewHolder.cameraNameTv = (TextView) convertView.findViewById(R.id.camera_name_tv);
            viewHolder.alarmListBtn = (ImageButton) convertView.findViewById(R.id.tab_alarmlist_btn);
            viewHolder.setDeviceBtn = (ImageButton) convertView.findViewById(R.id.tab_setdevice_btn);
            viewHolder.itemIconArea = convertView.findViewById(R.id.item_icon_area);

            // 设置点击图标的监听响应函数
            viewHolder.playBtn.setOnClickListener(mOnClickListener);

            // 设置报警列表的监听响应函数
            viewHolder.alarmListBtn.setOnClickListener(mOnClickListener);

            // 设置设备设置的监听响应函数
            viewHolder.setDeviceBtn.setOnClickListener(mOnClickListener);

            // 设置控件集到convertView，只进行一次的控件资源绑定避免每次都要绑定控件，下一次加载可以直接用getTag取出节省内存资源
            convertView.setTag(viewHolder);
        } else {
            //如果不为空就拿到此视图的标记
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置position
        viewHolder.playBtn.setTag(position);
        viewHolder.alarmListBtn.setTag(position);
        viewHolder.setDeviceBtn.setTag(position);

        //获取列表中对应顺序的设备信息对象
        final EZDeviceInfo deviceInfo = getItem(position);
        //获取设备的对应的通道信息列表中第一个通道信息
        final EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo,0);
        if (deviceInfo != null){
            //判断设备是否在线
            //在线状态，1-在线，2-不在线
            if (deviceInfo.getStatus() == 2) {
                //如果设备不在线，显示右上角不在线小标志
                viewHolder.offlineBtn.setVisibility(View.VISIBLE);
                //隐藏播放按钮
                viewHolder.playBtn.setVisibility(View.GONE);
            } else {
                viewHolder.offlineBtn.setVisibility(View.GONE);
                viewHolder.playBtn.setVisibility(View.VISIBLE);
            }
            //设置设备名称
            viewHolder.cameraNameTv.setText(deviceInfo.getDeviceName());
        }
        return convertView;
    }

    public void clearItem() {
        mCameraInfoList.clear();
    }

    public void addItem(EZDeviceInfo item) {
        mCameraInfoList.add(item);
    }

    public List<EZDeviceInfo> getDeviceInfoList() {
        return mCameraInfoList;
    }

    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }

    public interface OnClickListener {

        public void onPlayClick(BaseAdapter adapter, View view, int position);

        public void onAlarmListClick(BaseAdapter adapter, View view, int position);

        public void onSetDeviceClick(BaseAdapter adapter, View view, int position);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = (Integer) v.getTag();
                switch (v.getId()) {
                    case R.id.item_play_btn:
                        mListener.onPlayClick(EZCameraListAdapter.this, v, position);
                        break;

                    case R.id.tab_alarmlist_btn:
                        mListener.onAlarmListClick(EZCameraListAdapter.this, v, position);
                        break;

                    case R.id.tab_setdevice_btn:
                        mListener.onSetDeviceClick(EZCameraListAdapter.this, v, position);
                        break;
                }
            }
        }
    };

    public static class ViewHolder {

        public ImageView playBtn;

        public ImageView offlineBtn;

        public TextView cameraNameTv;

        public ImageButton alarmListBtn;

        public ImageButton setDeviceBtn;

        public View itemIconArea;
    }
}
