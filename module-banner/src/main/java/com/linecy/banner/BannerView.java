package com.linecy.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.linecy.banner.adapter.BannerAdapter;
import com.linecy.banner.adapter.IndicatorAdapter;
import com.linecy.banner.listener.OnBannerClickListener;
import com.linecy.banner.listener.OnBannerScrollChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 轮播view
 *
 * @author by linecy.
 */

@SuppressWarnings({ "unchecked", "unused" }) public class BannerView extends FrameLayout {

  public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
  public static final int VERTICAL = OrientationHelper.VERTICAL;

  //默认自动切换时间
  private static final int DEFAULT_DURATION = 3000;
  private static final int MSG_AUTO_PLAY = 10;

  private LinearLayoutManager layoutManager;
  private RecyclerView recyclerBanner;
  private BannerAdapter bannerAdapter;
  private IndicatorAdapter indicatorAdapter;
  private Context context;
  //方向
  private int orientation;
  //自动切换时间
  private long durationTime;
  //是否自动轮动
  private boolean isAutoPlay;
  //定时器
  private Handler timeHandler;
  //滚动到哪一个
  private int currentPosition;
  //第一个
  private int firstPosition;
  //是否显示指示器
  private boolean isShowIndicator;
  //是否保持中间缩放
  private boolean isScaleCover;
  //缩放比例
  private float scaleSize;
  //是否展示两端的部分banner
  private boolean isShowBothEnds;
  //两端预留空间
  private int space;
  //是否刷新数据
  private boolean isRefresh = false;
  //当前偏移量
  private int currentOffset;
  //子view的宽或高
  private int childSize = 0;
  //bannerView的宽或高
  private int parentSize = 0;
  //是否启用轮播循环模式
  private boolean isLoop;
  //初始中心位置
  private int initCenter;

  private OnBannerScrollChangeListener onBannerScrollChangeListener;
  private RecyclerView recyclerIndicator;

  private static class TimeHandler extends Handler {
    private WeakReference<BannerView> bannerViewWeakReference;

    TimeHandler(BannerView bannerView) {
      bannerViewWeakReference = new WeakReference<>(bannerView);
    }

    @Override public void handleMessage(Message msg) {
      BannerView bannerView = bannerViewWeakReference.get();
      if (null != bannerView) {
        if (msg.what == MSG_AUTO_PLAY) {
          bannerView.onScrollToNext();
          sendEmptyMessageDelayed(MSG_AUTO_PLAY, bannerView.durationTime);
        }
      }
    }
  }

  public BannerView(@NonNull Context context) {
    this(context, null);
  }

  public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    readAttrs(context, attrs);
    init(context);
  }

  private void readAttrs(Context context, AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
    orientation = a.getInt(R.styleable.BannerView_orientation, HORIZONTAL);
    durationTime = a.getInt(R.styleable.BannerView_durationTime, DEFAULT_DURATION);
    isShowIndicator = a.getBoolean(R.styleable.BannerView_isShowIndicator, true);
    isLoop = a.getBoolean(R.styleable.BannerView_isLoop, true);
    isAutoPlay = a.getBoolean(R.styleable.BannerView_isAutoPlay, true);
    isScaleCover = a.getBoolean(R.styleable.BannerView_isScaleCover, false);
    setScaleSize(a.getFloat(R.styleable.BannerView_scaleSize, 0.9f));
    //开启展示中间放大时，需启用两端都展示
    isShowBothEnds = isScaleCover || a.getBoolean(R.styleable.BannerView_isShowBothEnds, false);
    //左右两边*2
    space = (int) a.getDimension(R.styleable.BannerView_BothEndsSpace, dip2px(context, 24) * 2);
    a.recycle();
  }

  private void init(Context context) {
    this.context = context;
    timeHandler = new TimeHandler(this);
    recyclerBanner = new RecyclerView(context);
    LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutManager = new LinearLayoutManager(context, orientation, false);
    recyclerBanner.setLayoutManager(layoutManager);
    addView(recyclerBanner, lp);

    SnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(recyclerBanner);
    if (isShowIndicator) {
      setIndicatorDrawableRes(R.drawable.selector_oval_indicator);
    }
    addListenerForRecycler();
  }

  /**
   * 初始化指示器
   */
  private void initIndicatorController() {
    recyclerIndicator = new RecyclerView(context);
    recyclerIndicator.setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
    LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    lp.bottomMargin = 20;
    addView(recyclerIndicator, lp);
    indicatorAdapter = new IndicatorAdapter();
    recyclerIndicator.setAdapter(indicatorAdapter);
  }

  /**
   * 初始化监听
   */
  private void addListenerForRecycler() {
    recyclerBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //最多同时显示3个子view，只有最中间的一个是完成的
        //如果能够同时显示3个以上的话，改成findFirstVisibleItemPosition 和last吧
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        if (-1 != pos) {
          currentPosition = pos;
        }
        int realPosition = bannerAdapter.getRealPosition(currentPosition);
        //非无限模式下，解决最后一个不更新问题
        indicatorAdapter.setCurrentPosition(realPosition);
        if (null != onBannerScrollChangeListener) {
          onBannerScrollChangeListener.onScrollStateChanged(recyclerView, newState, realPosition);
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (null != onBannerScrollChangeListener) {
          onBannerScrollChangeListener.onScrolled(recyclerView, dx, dy);
        }
        //&& newState == RecyclerView.SCROLL_STATE_IDLE
        //如果加上滚动结束的条件，对于连续滚动的话，指示器不更新
        //如果判断此时是否选中和indicatorAdapter已经选中是否是同一个的话，局部更新感觉会抖动
        //放到onScrollStateChanged里，连续滑动不会更新
        if (isShowIndicator) {
          int start = layoutManager.findFirstVisibleItemPosition();
          int end = layoutManager.findLastVisibleItemPosition();
          currentPosition = (start + end) / 2;
          int realPosition = bannerAdapter.getRealPosition(currentPosition);
          indicatorAdapter.setCurrentPosition(realPosition);
        }

        //缩放
        if (isScaleCover) {
          onScaleCallback(dx, dy);
        }
      }
    });
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (orientation == HORIZONTAL) {
      parentSize = w;
    } else {
      parentSize = h;
    }
    //子view的宽或高，包含外边距
    childSize = parentSize - space;
    //bannerView的中心，横向即为x，纵向即为y
    //如果初始时没有偏移量的话，初始中心为子view中心，否则为父view中心
    initCenter = isLoop ? parentSize / 2 : childSize / 2;
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        onStop();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_OUTSIDE:
      case MotionEvent.ACTION_CANCEL:
        onStart();
        break;
    }

    return super.dispatchTouchEvent(ev);
  }

  /**
   * 根据焦点判断是否进入后台，是的话就开始，不是的话就停止。
   *
   * @param hasWindowFocus 是否有焦点
   */
  @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);
    if (hasWindowFocus) {
      onStart();
    } else {
      onStop();
    }
  }

  /**
   * 每次刷新数据时，偏移中间的一个到中间
   */
  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (isLoop && isRefresh && isShowBothEnds) {
      //需要刷新数据，无限循环，展示两端，才初始化时偏移居中
      if (childSize != 0) {
        if (orientation == HORIZONTAL) {
          int offset = getPaddingLeft();
          int off = (parentSize - childSize) / 2 + offset;
          if (firstPosition == 0) {
            recyclerBanner.offsetChildrenHorizontal(off);
          } else {
            layoutManager.scrollToPositionWithOffset(firstPosition, off);
          }
          currentOffset = offset;
        } else {
          int offset = getPaddingTop();
          int off = (parentSize - childSize) / 2 + offset;
          if (firstPosition == 0) {
            recyclerBanner.offsetChildrenHorizontal(off);
          } else {
            layoutManager.scrollToPositionWithOffset(firstPosition, off);
          }
          currentOffset = offset;
        }
      } else {
        recyclerBanner.scrollToPosition(currentPosition);
      }
      isRefresh = false;
    }
  }

  //销毁
  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    onStop();
  }

  /**
   * 切换到下一个
   */
  private void onScrollToNext() {
    recyclerBanner.smoothScrollToPosition(++currentPosition);
  }

  /**
   * 缩放回调
   *
   * @param dx 横向偏移
   * @param dy 纵向偏移
   */
  private void onScaleCallback(int dx, int dy) {
    int start = layoutManager.findFirstVisibleItemPosition();
    int end = layoutManager.findLastVisibleItemPosition();
    int center = (start + end) / 2;
    View startView = layoutManager.findViewByPosition(start);
    View centerView = layoutManager.findViewByPosition(center);
    View endView = layoutManager.findViewByPosition(end);

    //计算偏移量
    if (layoutManager.canScrollHorizontally()) {
      currentOffset += dx;
    } else if (layoutManager.canScrollVertically()) {
      currentOffset += dy;
    }
    //保存上一屏的偏移量，避免偏移重置后无法确定上一次的偏移量，所以在2倍后重置
    if (currentOffset > childSize * 2) {
      currentOffset -= childSize;
    } else if (currentOffset + childSize * 2 < 0) {
      currentOffset += childSize;
    }

    boolean isPositive;//是否是手指正向滚动（从左到右，从下到上），自动滚动时一定true
    float scale;
    if (currentOffset > childSize) {
      scale = (1 - scaleSize) * Math.abs(currentOffset - childSize) / childSize;
      isPositive = true;
    } else if (currentOffset < -childSize) {
      scale = (1 - scaleSize) * Math.abs(currentOffset + childSize) / childSize;
      isPositive = false;
    } else {
      scale = (1 - scaleSize) * Math.abs(currentOffset) / childSize;
      isPositive = currentOffset >= 0;
    }
    scaleView(startView, centerView, endView, start, center, isPositive, scale);
  }

  /**
   * 缩放
   *
   * @param startView 起始的子view
   * @param centerView 中间的子view
   * @param endView 结束位置的子view
   * @param start 开始位置
   * @param center 中间位置
   * @param isPositive 是否正向滑动
   * @param scale scale比例
   */
  private void scaleView(View startView, View centerView, View endView, int start, int center,
      boolean isPositive, float scale) {
    if (startView != null && centerView != null && endView != null) {
      //子view的中心，横向即为x，纵向即为y
      int childCenter = orientation == HORIZONTAL ? centerView.getLeft() + centerView.getWidth() / 2
          : centerView.getTop() + centerView.getHeight() / 2;
      if (start != center) {//不是最后就是开始状态
        if (childCenter == initCenter) {//中间
          if (orientation == HORIZONTAL) {
            startView.setScaleY(scaleSize);
            centerView.setScaleY(1);
            endView.setScaleY(scaleSize);
          } else {
            startView.setScaleX(scaleSize);
            centerView.setScaleX(1);
            endView.setScaleX(scaleSize);
          }
        } else if ((isPositive && childCenter < initCenter)
            || !isPositive && childCenter > initCenter) {//起始位置的左边
          if (orientation == HORIZONTAL) {
            startView.setScaleY(scale + scaleSize);
            centerView.setScaleY(1 - scale);
            endView.setScaleY(scale + scaleSize);
          } else {
            startView.setScaleX(scale + scaleSize);
            centerView.setScaleX(1 - scale);
            endView.setScaleX(scale + scaleSize);
          }
        } else {//起始位置的右边
          if (orientation == HORIZONTAL) {
            startView.setScaleY(1 - scale);
            centerView.setScaleY(scale + scaleSize);
            endView.setScaleY(1 - scale);
          } else {
            startView.setScaleX(1 - scale);
            centerView.setScaleX(scale + scaleSize);
            endView.setScaleX(1 - scale);
          }
        }
      } else {//中间态 start==center
        if (orientation == HORIZONTAL) {
          if (isPositive) {//向左划
            centerView.setScaleY(1 - scale);
            endView.setScaleY(scale + scaleSize);
          } else {//向右划
            centerView.setScaleY(scale + scaleSize);
            endView.setScaleY(1 - scale);
          }
        } else {
          if (isPositive) {//向左划
            centerView.setScaleX(1 - scale);
            endView.setScaleX(scale + scaleSize);
          } else {//向右划
            centerView.setScaleX(scale + scaleSize);
            endView.setScaleX(1 - scale);
          }
        }
      }
    }
  }

  /**
   * 自定义样式的时候需要调用此方法
   *
   * @param creator BannerCreator
   */
  public BannerView setupWithBannerCreator(BannerCreator creator) {
    bannerAdapter = new BannerAdapter(creator);
    recyclerBanner.setAdapter(bannerAdapter);
    return this;
  }

  /**
   * 是否启用scaleCover 模式
   *
   * @param isScaleCover 是否
   */
  public BannerView setScaleCover(boolean isScaleCover) {
    this.isScaleCover = isScaleCover;
    if (isScaleCover) {
      this.isShowBothEnds = true;
    }
    return this;
  }

  /**
   * 设置缩放比例
   *
   * @param scaleSize 比例
   */
  public BannerView setScaleSize(float scaleSize) {
    if (scaleSize > 1) {
      throw new IllegalArgumentException("The scale size must be less than 1.");
    }
    this.scaleSize = scaleSize;
    return this;
  }

  /**
   * 设置两端预留空间，即（bannerView的宽 - 子view的宽）或者（bannerView的高 - 子view的高）
   *
   * @param space 预留空间
   * @link isShowBothEnds == true才有效
   */
  public BannerView setSpaceBetween(int space) {
    this.space = space * 2;
    return this;
  }

  /**
   * 是否展示两端
   *
   * @param isShowBothEnds 是否
   */
  public BannerView setShowBothEnds(boolean isShowBothEnds) {
    this.isShowBothEnds = isShowBothEnds;
    return this;
  }

  /**
   * 是否展示指示器
   *
   * @param isShowIndicator 是否
   */
  public BannerView setShowIndicator(boolean isShowIndicator) {
    this.isShowIndicator = isShowIndicator;
    if (this.isShowIndicator && recyclerIndicator == null) {
      initIndicatorController();
    } else if (!this.isShowIndicator) {
      removeView(recyclerIndicator);
    }
    return this;
  }

  /**
   * 设置指示器样式
   */

  public BannerView setIndicatorDrawableRes(@DrawableRes int indicatorRes) {
    if (indicatorAdapter == null) {
      initIndicatorController();
    }
    indicatorAdapter.setIndicatorDrawableRes(indicatorRes);
    return this;
  }

  public BannerView setIndicatorColorRes(@ColorInt int indicatorRes) {
    if (indicatorAdapter == null) {
      initIndicatorController();
    }
    indicatorAdapter.setIndicatorColorRes(indicatorRes);
    return this;
  }

  /**
   * 设置指示器样式
   *
   * @param orientation 指示器方向
   * @param gravity 位置
   * @param leftMargin 左边距
   * @param topMargin 顶部边距
   * @param rightMargin 有边距
   * @param bottomMargin 底部边距
   */
  public BannerView setIndicatorStyle(int orientation, int gravity, int leftMargin, int topMargin,
      int rightMargin, int bottomMargin) {
    if (recyclerIndicator != null) {
      removeView(recyclerIndicator);
    }
    recyclerIndicator = new RecyclerView(context);
    recyclerIndicator.setLayoutManager(new LinearLayoutManager(context, orientation, false));
    LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.leftMargin = leftMargin;
    lp.topMargin = topMargin;
    lp.rightMargin = rightMargin;
    lp.bottomMargin = bottomMargin;
    lp.gravity = gravity;
    addView(recyclerIndicator, lp);
    if (indicatorAdapter == null) {
      indicatorAdapter = new IndicatorAdapter();
    }
    recyclerIndicator.setAdapter(indicatorAdapter);
    return this;
  }

  /**
   * 设置banner 轮播方向
   *
   * @param orientation 方向
   */
  public BannerView setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("The orientation must be equals HORIZONTAL or VERTICAL.");
    }
    this.orientation = orientation;
    layoutManager = new LinearLayoutManager(context, orientation, false);
    recyclerBanner.setLayoutManager(layoutManager);
    return this;
  }

  /**
   * 设置是否自动播放
   *
   * 默认是的
   *
   * @param isAutoPlay flag
   */
  public BannerView setAutoPlay(boolean isAutoPlay) {
    this.isAutoPlay = isAutoPlay;
    return this;
  }

  /**
   * 设置是否循环播放
   *
   * 默认是的
   *
   * @param isLoop 是否
   */
  public BannerView setLoop(boolean isLoop) {
    this.isLoop = isLoop;
    return this;
  }

  /**
   * 为item 设置动画
   *
   * @param itemAnimator 动画
   */
  public BannerView setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
    recyclerBanner.setItemAnimator(itemAnimator);
    return this;
  }

  /**
   * 设置自动播放的间隔时间，单位毫秒
   *
   * @param duration duration time
   */
  public BannerView setDurationTime(long duration) {
    this.durationTime = duration;
    return this;
  }

  /**
   * 刷新数据
   *
   * @param list data
   */
  public void onRefreshData(List list) {
    onStop();
    currentOffset = 0;
    isRefresh = true;
    bannerAdapter.setLoop(isLoop);
    bannerAdapter.refreshData(list, orientation == HORIZONTAL,
        this.isShowBothEnds ? this.space : 0);
    if (list != null && list.size() > 0) {
      indicatorAdapter.setSize(list.size());
      if (list.size() < 1) {
        isAutoPlay = false;
      }
      firstPosition = list.size() > 1 ? list.size() * 1000 : 0;
      currentPosition = firstPosition;
      if (!isLoop || !isShowBothEnds) {
        recyclerBanner.scrollToPosition(currentPosition);
      }
      int realPosition = bannerAdapter.getRealPosition(currentPosition);
      indicatorAdapter.setCurrentPosition(realPosition);
    } else {
      firstPosition = -1;
      currentPosition = -1;
      indicatorAdapter.setSize(0);
      isAutoPlay = false;
    }
    onStart();
  }

  /**
   * 开始轮播
   */
  public synchronized void onStart() {
    if (isLoop && isAutoPlay) {
      onStop();
      timeHandler.sendEmptyMessageDelayed(MSG_AUTO_PLAY, durationTime);
    }
  }

  /**
   * 停止轮播
   */
  public void onStop() {
    timeHandler.removeMessages(MSG_AUTO_PLAY);
  }

  /**
   * 设置点击监听
   *
   * @param l OnBannerClickListener
   */
  public BannerView setOnBannerClickListener(OnBannerClickListener l) {
    if (bannerAdapter != null) {
      bannerAdapter.setOnBannerClickListener(l);
    }
    return this;
  }

  public BannerView setOnBannerScrollChangeListener(OnBannerScrollChangeListener l) {
    this.onBannerScrollChangeListener = l;
    return this;
  }

  public static int dip2px(Context context, float dpValue) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
        context.getResources().getDisplayMetrics());
  }
}
