package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T>{
    public PullToRefreshBase(Context context) {
        super(context);
    }
}
