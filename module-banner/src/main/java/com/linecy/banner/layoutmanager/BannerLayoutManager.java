package com.linecy.banner.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * 控制切换速度。
 *
 * @author by yang.chen on yang.chen@msxf.com.
 */
public class BannerLayoutManager extends LinearLayoutManager {

  //针对banner，默认横向
  public BannerLayoutManager(Context context) {
    this(context, HORIZONTAL, false);
  }

  public BannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  @Override public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
      int position) {
    BannerSmoothScroller scroller = new BannerSmoothScroller(recyclerView.getContext());
    scroller.setTargetPosition(position);
    startSmoothScroll(scroller);
  }

  private class BannerSmoothScroller extends LinearSmoothScroller {

    BannerSmoothScroller(Context context) {
      super(context);
    }

    @Nullable @Override public PointF computeScrollVectorForPosition(int targetPosition) {
      //return super.computeScrollVectorForPosition(targetPosition);
      return BannerLayoutManager.this.computeScrollVectorForPosition(targetPosition);
    }

    @Override public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd,
        int snapPreference) {
      //return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
      return (boxStart + boxEnd - viewStart - viewEnd) / 2;
    }

    @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
      //return super.calculateSpeedPerPixel(displayMetrics);
      return 0.2f;
    }

    @Override protected int getVerticalSnapPreference() {
      //return super.getVerticalSnapPreference();
      return LinearSmoothScroller.SNAP_TO_START;
    }
  }
}
