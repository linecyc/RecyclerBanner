package com.linecy.banner.adapter;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 指示器适配器
 *
 * @author by linecy.
 */

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorViewHolder> {
  private int size = 0;
  private int currentPosition = 0;
  private int drawableRes;
  private int colorRes;

  @Override public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ImageView imageView = new ImageView(parent.getContext());
    imageView.setPadding(5, 5, 5, 5);
    if (colorRes == -1) {
      imageView.setImageResource(drawableRes);
    } else {
      imageView.setBackgroundColor(colorRes);
    }
    return new IndicatorViewHolder(imageView);
  }

  @Override public void onBindViewHolder(IndicatorViewHolder holder, int position) {
    ImageView imageView = (ImageView) holder.itemView;
    imageView.setSelected(position == currentPosition);
  }

  @Override public int getItemCount() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
    notifyDataSetChanged();
  }

  public void setCurrentPosition(int position) {
    if (-1 != position && this.currentPosition != position) {
      notifyItemChanged(this.currentPosition);
      notifyItemChanged(position);
      this.currentPosition = position;
    }
  }

  public void setIndicatorDrawableRes(@DrawableRes int indicatorRes) {
    this.drawableRes = indicatorRes;
    this.colorRes = -1;
  }

  public void setIndicatorColorRes(@ColorInt int indicatorRes) {
    this.drawableRes = -1;
    this.colorRes = indicatorRes;
  }
}
