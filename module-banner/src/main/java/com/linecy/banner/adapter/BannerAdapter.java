package com.linecy.banner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.linecy.banner.BannerCreator;
import com.linecy.banner.listener.OnBannerClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by linecy.
 */

public class BannerAdapter<T> extends RecyclerView.Adapter<BannerViewHolder<T>> {
  private List<T> bannerList;
  private BannerCreator<T> bannerCreator;
  private OnBannerClickListener onBannerClickListener;
  private int space;
  private boolean isHorizontal;
  private int viewSize;

  public BannerAdapter(BannerCreator<T> creator) {
    this.bannerList = new ArrayList<>();
    this.bannerCreator = creator;
    this.isHorizontal = true;
  }

  @Override public BannerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(bannerCreator.getLayoutResId(), parent, false);
    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
    if (isHorizontal) {
      viewSize = lp.width = parent.getWidth() - lp.leftMargin - lp.rightMargin - space;
    } else {
      viewSize = lp.height = parent.getHeight() - lp.topMargin - lp.bottomMargin - space;
    }
    view.setLayoutParams(lp);
    BannerViewHolder<T> viewHolder = new BannerViewHolder<>(view, bannerCreator);
    viewHolder.setOnBannerClickListener(onBannerClickListener);

    return viewHolder;
  }

  @Override public void onBindViewHolder(BannerViewHolder<T> holder, int position) {
    int real = getRealPosition(position);
    holder.onBindData(bannerList.get(real), real);
  }

  @Override public int getItemCount() {
    if (bannerList.size() > 0) {
      if (bannerList.size() > 1) {
        return Integer.MAX_VALUE;
      } else {
        return 1;
      }
    } else {
      return 0;
    }
  }

  public int getRealPosition(int position) {
    return position % bannerList.size();
  }

  public void refreshData(List<T> list, boolean isHorizontal, int space) {
    this.bannerList.clear();
    this.isHorizontal = isHorizontal;
    this.space = space;
    if (list != null && list.size() > 0) {
      this.bannerList.addAll(list);
    }
    notifyDataSetChanged();
  }

  public int getViewSize() {
    return viewSize;
  }

  public void setOnBannerClickListener(OnBannerClickListener l) {
    this.onBannerClickListener = l;
  }
}
