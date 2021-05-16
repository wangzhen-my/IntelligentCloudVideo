package ncist.wl171.intelligentcloudvideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.videogo.openapi.bean.EZAlarmInfo;

import java.util.List;

public class EZMessageListAdapter extends BaseAdapter {

    private List<EZAlarmInfo> mObjects;
    private Context mContext;

    public EZMessageListAdapter(Context context, List<EZAlarmInfo> list) {
        mContext = context;
        setList(list);
    }

    public void setList(List<EZAlarmInfo> list) {
        mObjects = list;
    }

    private class ViewHolder {
        TextView from;
        TextView type;
        TextView timeview;
    }
    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public EZAlarmInfo getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ez_message_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.from = (TextView) convertView.findViewById(R.id.message_from);
            viewHolder.type = (TextView) convertView.findViewById(R.id.message_type);
            viewHolder.timeview = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EZAlarmInfo item = getItem(position);
        EZAlarmInfo alarmLogInfo = (EZAlarmInfo) item;
        //获取告警名称
        viewHolder.from.setText(alarmLogInfo.getAlarmName());
        viewHolder.type.setText("人体感应报警");
        viewHolder.timeview.setText(alarmLogInfo.getAlarmStartTime());

        return convertView;
    }
}
