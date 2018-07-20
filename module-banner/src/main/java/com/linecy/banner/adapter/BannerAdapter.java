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
 * 轮播图适配器
 *
 * @author by linecy.
 */

public class BannerAdapter<T> extends RecyclerView.Adapter<BannerViewHolder<T>> {
  private List<T> bannerList;
  private BannerCreator<T> bannerCreator;
  private OnBannerClickListener onBannerClickListener;
  private int space;
  private boolean isHorizontal;
  private int viewSize;//itemView的宽或高
  private int listSize;//数据集合的大小
  private boolean isLoop;//是否循环播放

  public BannerAdapter(BannerCreator<T> creator) {
    this.bannerList = new ArrayList<>();
    this.bannerCreator = creator;
    this.isHorizontal = true;
    this.listSize = 0;
  }

  @Override public BannerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(bannerCreator.getLayoutResId(), parent, false);
    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
    if (isHorizontal) {
      lp.width = parent.getWidth() - lp.leftMargin - lp.rightMargin - space;
      viewSize = parent.getWidth() - space;
    } else {
      lp.height = parent.getHeight() - lp.topMargin - lp.bottomMargin - space;
      viewSize = parent.getHeight() - space;
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
    if (this.listSize > 1) {
      if (isLoop) {
        return Integer.MAX_VALUE;
      } else {
        return this.listSize;
      }
    } else {
      return this.listSize;
    }
  }

  public int getRealPosition(int position) {
    return this.listSize == 0 ? 0 : position % bannerList.size();
  }

  public void refreshData(List<T> list, boolean isHorizontal, int space) {
    this.bannerList.clear();
    this.isHorizontal = isHorizontal;
    this.space = space;
    if (list != null && list.size() > 0) {
      this.bannerList.addAll(list);
    }
    this.listSize = this.bannerList.size();
    notifyDataSetChanged();
  }

  public void setLoop(boolean isLoop) {
    this.isLoop = isLoop;
  }

  /**
   * 拿到子view的宽或高，如果横向就是宽度，反之就是高度
   *
   * @return 宽或高
   */
  public int getViewSize() {
    return viewSize;
  }

  public void setOnBannerClickListener(OnBannerClickListener l) {
    this.onBannerClickListener = l;
  }
}
