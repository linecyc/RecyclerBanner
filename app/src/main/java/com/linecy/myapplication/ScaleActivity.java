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
    bannerView.setSpaceBetween(200);
    if (getIntent().getBooleanExtra(EXTRA_DATA, true)) {
      bannerView.setScaleCover(true);
      name.setText("展示上一个和下一个");
    } else {
      name.setText("展示中间的个有放大效果");
    }
    initBannerView();
  }

  void initBannerView() {
    textView.setText("第 1 个");
    bannerView.setOnBannerClickListener(new OnBannerClickListener() {
      @Override public void onBannerClick(Object data, int position) {
        Toast.makeText(ScaleActivity.this, "第 " + ((position + 1)) + " 个", Toast.LENGTH_SHORT)
            .show();
      }
    });
    bannerView.setOnBannerScrollChangeListener(new OnBannerScrollChangeListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState, int position) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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
