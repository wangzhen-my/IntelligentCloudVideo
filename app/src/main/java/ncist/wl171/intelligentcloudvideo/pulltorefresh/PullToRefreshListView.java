package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {
    public PullToRefreshListView(Context context) {
        super(context);
    }
    // 如果View是在.xml里声明的而不是在java里new的，则调用此构造函数
    // 自定义属性是从AttributeSet参数传进来的
    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
