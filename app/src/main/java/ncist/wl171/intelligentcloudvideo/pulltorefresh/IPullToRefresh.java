package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.view.View;

public interface IPullToRefresh<T extends View> {

    public void setOnRefreshListener(OnRefreshListener<T> listener);

    public void setRefreshing();

    public static interface OnRefreshListener<V extends View> {

        public void onRefresh(final PullToRefreshBase<V> refreshView, boolean headerOrFooter);

    }
}
