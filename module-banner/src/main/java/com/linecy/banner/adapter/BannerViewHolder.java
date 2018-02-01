package com.linecy.banner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.linecy.banner.listener.OnBannerClickListener;

/**
 * @author by linecy.
 */

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  private OnBannerClickListener onBannerClickListener;
  private Object data;
  private int position;

  BannerViewHolder(View itemView, OnBannerClickListener listener) {
    super(itemView);
    this.onBannerClickListener = listener;
    itemView.setOnClickListener(this);
    setIsRecyclable(false);
  }

  void onBindData(Object data, int position) {
    this.data = data;
    this.position = position;
  }

  @Override public void onClick(View v) {
    if (null != onBannerClickListener) {
      onBannerClickListener.onBannerClick(data, position);
    }
  }

  public int getCurrentPosition() {
    return position;
  }
}
