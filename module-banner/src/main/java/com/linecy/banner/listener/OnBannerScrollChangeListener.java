package com.linecy.banner.listener;

import android.support.v7.widget.RecyclerView;

/**
 * @author by linecy.
 */

public interface OnBannerScrollChangeListener {

  void onScrollStateChanged(RecyclerView recyclerView, int newState, int position);

  void onScrolled(RecyclerView recyclerView, int dx, int dy);
}
