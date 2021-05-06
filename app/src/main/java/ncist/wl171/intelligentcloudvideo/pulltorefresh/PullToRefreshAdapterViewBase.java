package ncist.wl171.intelligentcloudvideo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;



/** OnScrollListener
 * 为了在列表或网格滚动时执行回调函数而定义的接口。用以实现下滑刷新列表
 * */

/** 继承OnScrollListener接口必须要实现的方法
 * 滚动列表或网格时要调用的回调方法。初始化absListview的时候也会调用此方法，并且滚动时会多次调用所以需要加上别的判断条件
 * 当列表或网格的滚动已经完成时调用的回调函数。会在滚动完成后调用。
 * 各个参数的意义：
 * firstVisibleItem：第一个可见单元格的索引（如果 visibleItemCount == 0 则忽略该参数），
 * 注意如果有header，那么header也会算在里面，因为是从最上面的子view到当前view的顺序。
 * visibleItemCount:当前可见的view的数量.
 * totalItemCount:所有的项数，包含header和footer。*/

/** 继承OnScrollListener接口必须要实现的方法
 * 滚动状态发生变化时，系统会回调这个方法。滚动状态会被赋值到State，State的值如下：
 * SCROLL_STATE_TOUCH_SCROLL	正在滚动的状态；SCROLL_STATE_IDLE	不滚动时的状态，通常会在滚动停止时监听到此状态
 * 如果视图正在滚动该方法会在渲染下一帧之前调用该方法。
 * 就是说会在调用任何 getView(int,View,ViewGroup)方法之前调用。
 * ScrollStateChanged 在滚动时也会被调用多次，但可以通过判断 State 来判断当前滚动的状态，防止多次调用。*/
public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> {
    public PullToRefreshAdapterViewBase(Context context) {
        super(context);
    }

    // 如果View是在.xml里声明的而不是在java里new的，则调用此构造函数
    // 自定义属性是从AttributeSet参数传进来的
    public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mRefreshableView.setOnScrollListener(this);
    }
}
