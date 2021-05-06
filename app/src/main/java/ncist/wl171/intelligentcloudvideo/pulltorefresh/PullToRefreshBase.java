package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.videogo.util.LogUtil;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T>{

    public PullToRefreshBase(Context context) {
        super(context);
    }

    // 如果View是在.xml里声明的而不是在java里new的，则调用此构造函数
    // 自定义属性是从AttributeSet参数传进来的
    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
    }
}
