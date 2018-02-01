package com.linecy.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.linecy.banner.adapter.BannerCreator;

/**
 * @author by linecy.
 */

class NormalCreator implements BannerCreator {
  private ImageView imageView;
  private Context context;
  private TextView detail;

  @Override public View onCreateView(Context context, ViewGroup parent, int viewType) {
    this.context = context;
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_normal, parent, false);
    imageView = itemView.findViewById(R.id.iv);
    detail = itemView.findViewById(R.id.detail);
    return itemView;
  }

  @Override public void onBindData(Object data, int position) {
    Glide.with(context).load(data).into(imageView);
    detail.setText(String.valueOf(position));
  }
}