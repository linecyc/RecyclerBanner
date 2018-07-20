package com.linecy.banner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.linecy.banner.BannerCreator;
import com.linecy.banner.listener.OnBannerClickListener;

/**
 * @author by linecy.
 */

public class BannerViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

  private OnBannerClickListener<T> onBannerClickListener;
  private T data;
  private int position;
  private BannerCreator<T> bannerCreator;
  private View view;

  BannerViewHolder(View itemView, BannerCreator<T> creator) {
    super(itemView);
    this.view = itemView;
    itemView.setOnClickListener(this);
    this.bannerCreator = creator;
  }

  void onBindData(T data, int position) {
    this.data = data;
    this.position = position;
    if (this.bannerCreator != null) {
      this.bannerCreator.onBindData(view, data, position);
    }
  }

  @Override public void onClick(View v) {
    if (null != onBannerClickListener) {
      onBannerClickListener.onBannerClick(data, position);
    }
  }

  public void setOnBannerClickListener(OnBannerClickListener<T> l) {
    this.onBannerClickListener = l;
  }
}
