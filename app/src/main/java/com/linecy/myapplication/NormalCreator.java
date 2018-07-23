package com.linecy.myapplication;

import android.view.View;
import android.widget.ImageView;
import com.linecy.banner.BannerCreator;

/**
 * @author by linecy.
 */

class NormalCreator implements BannerCreator<Integer> {

  @Override public int layoutResId() {
    return R.layout.item_normal;
  }

  @Override public void onBindData(View view, Integer data, int position) {
    ImageView imageView = view.findViewById(R.id.iv);
    imageView.setBackgroundColor(data);
  }
}