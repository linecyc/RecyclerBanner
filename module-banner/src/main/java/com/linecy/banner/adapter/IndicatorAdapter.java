package com.linecy.banner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.linecy.banner.R;

/**
 * @author by linecy.
 */

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorViewHolder> {
  private int size = 0;
  private int currentPosition = 0;

  @Override public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ImageView imageView = new ImageView(parent.getContext());
    imageView.setPadding(5, 5, 5, 5);
    imageView.setImageResource(R.drawable.selector_oval_indicator);
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
    if (this.currentPosition != position) {
      notifyItemChanged(this.currentPosition);
      notifyItemChanged(position);
      this.currentPosition = position;
    }
  }
}
