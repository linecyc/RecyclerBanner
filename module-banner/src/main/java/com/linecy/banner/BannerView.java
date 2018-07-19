package com.linecy.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
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
 * @author by linecy.
 */

public class BannerView extends FrameLayout {

  public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
  public static final int VERTICAL = OrientationHelper.VERTICAL;

  //默认自动切换时间
  private static final int DEFAULT_DURATION = 3000;
  private static final int MSG_AUTO_PLAY = 1;

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
  //是否在播放
  private boolean isPlaying;
  //定时器
  private Handler timeHandler;
  //滚动到哪一个
  private int currentPosition;
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

  private int currentOffset;
  //子view的宽或高
  private int childSize = 0;
  private int parentSize = 0;

  private OnBannerScrollChangeListener onBannerScrollChangeListener;

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
    isAutoPlay = a.getBoolean(R.styleable.BannerView_isAutoPlay, true);
    isScaleCover = a.getBoolean(R.styleable.BannerView_isScaleCover, false);
    setScaleSize(a.getFloat(R.styleable.BannerView_scaleSize, 0.9f));
    //开启展示中间放大时，需启用两端都展示
    isShowBothEnds = isScaleCover || a.getBoolean(R.styleable.BannerView_isShowBothEnds, false);
    //左右两边*2
    space = (int) a.getDimension(R.styleable.BannerView_BothEndsSpace, dip2px(context, 24) * 2);
    isPlaying = false;
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
      initIndicatorController();
    }
    addListenerForRecycler();
  }

  /**
   * 初始化指示器
   */
  private void initIndicatorController() {
    RecyclerView recyclerIndicator = new RecyclerView(context);
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
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        currentPosition = (first + last) / 2;
        int realPosition = bannerAdapter.getRealPosition(currentPosition);
        //&& newState == RecyclerView.SCROLL_STATE_IDLE
        //如果加上滚动结束的条件，对于连续滚动的话，指示器不更新
        //如果判断此时是否选中和indicatorAdapter已经选中是否是同一个的话，局部更新会感觉抖动
        if (isShowIndicator) {
          indicatorAdapter.setCurrentPosition(realPosition);
        }
        if (null != onBannerScrollChangeListener) {
          onBannerScrollChangeListener.onScrollStateChanged(recyclerView, newState, realPosition);
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (null != onBannerScrollChangeListener) {
          onBannerScrollChangeListener.onScrolled(recyclerView, dx, dy);
        }
        //缩放
        //if (isScaleCover) {
        //
        //}
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
   * 自定义样式的时候需要调用此方法
   *
   * @param creator BannerCreator
   */
  @SuppressWarnings("unchecked") public void setupWithBannerCreator(BannerCreator creator) {
    bannerAdapter = new BannerAdapter(creator);
    recyclerBanner.setAdapter(bannerAdapter);
  }

  public void setScaleCover(boolean isScaleCover) {
    this.isScaleCover = isScaleCover;
  }

  public void setScaleSize(float scaleSize) {
    if (scaleSize > 1) {
      throw new IllegalArgumentException("The scale size must be less than 1.");
    }
    this.scaleSize = scaleSize;
  }

  /**
   * 设置两端预留空间
   *
   * @param space 预留空间
   * @link isShowBothEnds == true才有效
   */
  public void setSpaceBetween(int space) {
    this.space = space * 2;
  }

  /**
   * 刷新数据
   *
   * @param list data
   */
  @SuppressWarnings("unchecked") public void onRefreshData(List list) {
    onStop();
    if (bannerAdapter != null) {
      bannerAdapter.refreshData(list, orientation == HORIZONTAL,
          this.isShowBothEnds ? this.space : 0);
      indicatorAdapter.setSize(list.size());
      currentPosition = list.size() * 1000;
      recyclerBanner.scrollToPosition(currentPosition);
      int realPosition = bannerAdapter.getRealPosition(currentPosition);
      indicatorAdapter.setCurrentPosition(realPosition);
    } else {
      indicatorAdapter.setSize(0);
    }
    onStart();
  }

  /**
   * 开始轮播
   */
  public synchronized void onStart() {
    if (isAutoPlay) {
      if (isPlaying) {
        onStop();
      }
      isPlaying = true;
      timeHandler.sendEmptyMessageDelayed(MSG_AUTO_PLAY, durationTime);
    }
  }

  /**
   * 停止轮播
   */
  public void onStop() {
    if (isPlaying) {
      timeHandler.removeMessages(MSG_AUTO_PLAY);
      isPlaying = false;
    }
  }

  /**
   * 设置banner 轮播方向
   *
   * @param orientation 方向
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("The orientation must be equals HORIZONTAL or VERTICAL.");
    }
    this.orientation = orientation;
    layoutManager = new LinearLayoutManager(context, orientation, false);
    recyclerBanner.setLayoutManager(layoutManager);
  }

  /**
   * 设置是否自动播放
   *
   * @param isAutoPlay flag
   */
  public void setIsAutoPlay(boolean isAutoPlay) {
    this.isAutoPlay = isAutoPlay;
  }

  /**
   * 设置自动播放的间隔时间，单位毫秒
   *
   * @param duration duration time
   */
  public void setDurationTime(long duration) {
    this.durationTime = duration;
  }

  /**
   * 返回是播放的状态
   *
   * @return isPlaying
   */
  public boolean getIsPlaying() {
    return isPlaying;
  }

  /**
   * 设置点击监听
   *
   * @param l OnBannerClickListener
   */
  public void setOnBannerClickListener(OnBannerClickListener l) {
    if (bannerAdapter != null) {
      bannerAdapter.setOnBannerClickListener(l);
    }
  }

  public void setOnBannerScrollChangeListener(OnBannerScrollChangeListener l) {
    this.onBannerScrollChangeListener = l;
  }

  public static int dip2px(Context context, float dpValue) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
        context.getResources().getDisplayMetrics());
  }
}
