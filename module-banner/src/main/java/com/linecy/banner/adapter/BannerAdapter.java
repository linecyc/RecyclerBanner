package com.linecy.banner.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.linecy.banner.listener.OnBannerClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by linecy.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerViewHolder> {
  private List<Object> bannerList;
  private Context context;
  private BannerCreator bannerCreator;
  private OnBannerClickListener onBannerClickListener;

  public BannerAdapter(@NonNull Context context) {
    new BannerAdapter(context, null);
  }

  public BannerAdapter(@NonNull Context context, BannerCreator bannerCreator) {
    this.context = context;
    this.bannerList = new ArrayList<>();
    this.bannerCreator = bannerCreator;
  }

  @Override public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (null == bannerCreator) {
      throw new RuntimeException("You must init you layout or use default banner creator");
    }
    View view = bannerCreator.onCreateView(context, parent, viewType);
    if (view != null) {
      return new BannerViewHolder(view, onBannerClickListener);
    } else {
      throw new RuntimeException("You must init you layout or use default banner creator");
    }
  }

  @Override public void onBindViewHolder(BannerViewHolder holder, int position) {
    int real = getRealPosition(position);
    Object obj = bannerList.get(real);
    holder.onBindData(obj, real);
    bannerCreator.onBindData(obj, real);
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

  public int getRealCount() {
    return bannerList.size();
  }

  public int getRealPosition(int position) {
    return position % bannerList.size();
  }

  public void refreshData(List list) {
    this.bannerList.clear();
    if (list != null && list.size() > 0) {
      this.bannerList.addAll(list);
    }

    notifyDataSetChanged();
  }

  public void setOnBannerClickListener(OnBannerClickListener l) {
    this.onBannerClickListener = l;
  }
}
