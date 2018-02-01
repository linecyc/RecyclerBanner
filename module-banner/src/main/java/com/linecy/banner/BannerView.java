package com.linecy.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.linecy.banner.adapter.BannerAdapter;
import com.linecy.banner.adapter.BannerCreator;
import com.linecy.banner.adapter.BannerViewHolder;
import com.linecy.banner.adapter.IndicatorAdapter;
import com.linecy.banner.listener.OnBannerClickListener;
import com.linecy.banner.listener.OnBannerScrollChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;

import static android.support.v7.widget.OrientationHelper.HORIZONTAL;
import static android.support.v7.widget.OrientationHelper.VERTICAL;

/**
 * @author by linecy.
 */

public class BannerView extends FrameLayout {

  //默认自动切换时间
  private static final int DEFAULT_DURATION = 3000;
  private static final int MSG_AUTO_PLAY = 1;

  private RecyclerView.LayoutManager layoutManager;
  private RecyclerView recyclerBanner;
  private RecyclerView recyclerIndicator;
  private BannerAdapter banneradapter;
  private IndicatorAdapter indicatorAdapter;
  private Context context;
  private SnapHelper snapHelper;
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

  //上一次触摸的位置
  private float[] oldLocation;

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
          bannerView.OnScrollToNext();
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
    currentPosition = Integer.MAX_VALUE / 2;
    isAutoPlay = true;
    isPlaying = false;
    a.recycle();
  }

  private void init(Context context) {
    this.context = context;
    this.oldLocation = new float[2];
    timeHandler = new TimeHandler(this);
    recyclerBanner = new RecyclerView(context);
    LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutManager = new LinearLayoutManager(context, orientation, false);
    recyclerBanner.setLayoutManager(layoutManager);
    addView(recyclerBanner, lp);

    snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(recyclerBanner);
    setupWithBannerCreator(null);
    initIndicatorController();
    addListenerForRecycler();
    recyclerBanner.scrollToPosition(currentPosition);
  }

  private void initIndicatorController() {
    recyclerIndicator = new RecyclerView(context);
    recyclerIndicator.setLayoutManager(new LinearLayoutManager(context, orientation, false));
    LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    addView(recyclerIndicator, lp);
    indicatorAdapter = new IndicatorAdapter();
    recyclerIndicator.setAdapter(indicatorAdapter);
  }

  private void addListenerForRecycler() {
    recyclerBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        int realPosition = -1;
        View childView = snapHelper.findSnapView(layoutManager);
        if (null != childView) {
          BannerViewHolder viewHolder =
              (BannerViewHolder) recyclerView.getChildViewHolder(childView);
          realPosition = viewHolder.getCurrentPosition();
        }
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
      }
    });
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {

    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        onStop();
        oldLocation[0] = ev.getX();
        oldLocation[1] = ev.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        onStop();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_OUTSIDE:
      case MotionEvent.ACTION_CANCEL:
        if (orientation == HORIZONTAL) {

          if (ev.getX() > oldLocation[0]) {
            currentPosition--;
          } else if (ev.getX() < oldLocation[0]) {
            currentPosition++;
          }
        } else if (orientation == VERTICAL) {
          if (ev.getY() > oldLocation[1]) {
            currentPosition--;
          } else if (ev.getY() < oldLocation[1]) {
            currentPosition++;
          }
        }
        onStart();
        break;

      //default:
      //  onStart();
      //  break;
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
  private void OnScrollToNext() {
    ++currentPosition;
    recyclerBanner.smoothScrollToPosition(currentPosition);
    timeHandler.sendEmptyMessageDelayed(MSG_AUTO_PLAY, durationTime);
  }

  //Open api

  /**
   * 设置layoutManager
   *
   * @param layoutManager LayoutManager
   */
  public void setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    recyclerBanner.setLayoutManager(this.layoutManager);
  }

  /**
   * 自定义样式的时候需要调用此方法
   *
   * @param creator BannerCreator
   */
  public void setupWithBannerCreator(BannerCreator creator) {
    banneradapter = new BannerAdapter(context, creator);
    recyclerBanner.setAdapter(banneradapter);
  }

  /**
   * 刷新数据
   *
   * @param list data
   */
  public void onRefreshData(List list) {
    onStop();
    if (banneradapter != null) {
      banneradapter.refreshData(list);
      indicatorAdapter.setSize(list.size());
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

  public void setOrientation(int orientation) {
    this.orientation = orientation;
    layoutManager = new LinearLayoutManager(context, orientation, false);
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
    if (banneradapter != null) {
      banneradapter.setOnBannerClickListener(l);
    }
  }

  public void setOnBannerScrollChangeListener(OnBannerScrollChangeListener l) {
    this.onBannerScrollChangeListener = l;
  }
}
