package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.content.Context;
import android.widget.AbsListView;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> implements
        AbsListView.OnScrollListener {
    public PullToRefreshAdapterViewBase(Context context) {
        super(context);
    }
}
