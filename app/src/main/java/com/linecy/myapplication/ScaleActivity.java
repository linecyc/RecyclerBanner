package com.linecy.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import com.linecy.banner.BannerView;
import com.linecy.banner.listener.OnBannerClickListener;
import com.linecy.banner.listener.OnBannerScrollChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ScaleActivity extends AppCompatActivity {

  public static final String EXTRA_DATA = "extra_data";
  private TextView textView;
  private BannerView bannerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scale);

    TextView name = findViewById(R.id.textView);
    textView = findViewById(R.id.textView2);
    bannerView = findViewById(R.id.bannerView);
    bannerView.setupWithBannerCreator(new ScaleCreator());
    bannerView.setOrientation(BannerView.HORIZONTAL);
    bannerView.setScaleSize(0.75f);
    BannerView bannerView2 = findViewById(R.id.bannerView2);
    bannerView2.setupWithBannerCreator(new ScaleCreator());
    bannerView2.setOrientation(BannerView.VERTICAL);
    bannerView2.setScaleSize(0.75f);
    if (getIntent().getBooleanExtra(EXTRA_DATA, true)) {
      bannerView.setScaleCover(false);
      bannerView2.setScaleCover(false);
      name.setText("展示上一个和下一个");
    } else {
      bannerView.setScaleCover(true);
      bannerView.setShowIndicator(false);
      bannerView2.setScaleCover(true);
      bannerView2.setShowIndicator(false);
      name.setText("展示中间的个有放大效果");
    }
    initBannerView();
    bannerView2.onRefreshData(createData());
  }

  void initBannerView() {
    textView.setText("第 1 个");
    bannerView.setOnBannerClickListener(new OnBannerClickListener<Integer>() {
      @Override public void onBannerClick(Integer data, int position) {
        Toast.makeText(ScaleActivity.this, "第 " + ((position + 1)) + " 个", Toast.LENGTH_SHORT)
            .show();
      }
    });
    bannerView.setOnBannerScrollChangeListener(new OnBannerScrollChangeListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState, int position) {
        //如果根据newState判断的话，连续滑动时不会触发
        if (position != -1) {
          textView.setText(String.valueOf("第 " + (position + 1) + " 个"));
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

      }
    });
    bannerView.onRefreshData(createData());
  }

  List<Integer> createData() {
    List<Integer> list = new ArrayList<>();
    list.add(Color.RED);
    list.add(Color.YELLOW);
    list.add(Color.BLUE);
    list.add(Color.GREEN);
    return list;
  }
}
