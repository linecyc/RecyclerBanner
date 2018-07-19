package com.linecy.banner;

import android.view.View;

/**
 * @author by linecy.
 */
public interface BannerCreator<T> {

  int getLayoutResId();

  void onBindData(View view, T data, int position);
}
