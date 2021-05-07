package ncist.wl171.intelligentcloudvideo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EZCameraListAdapter extends BaseAdapter {

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

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void clearItem() {
        mCameraInfoList.clear();
    }

    public void addItem(EZDeviceInfo item) {
        mCameraInfoList.add(item);
    }
}
