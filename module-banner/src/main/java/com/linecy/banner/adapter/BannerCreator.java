package com.linecy.banner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现该类创建需要的布局
 *
 * @author by linecy.
 */

public interface BannerCreator {

  View onCreateView(Context context, ViewGroup parent, int viewType);

  void onBindData(Object data, int position);
}
