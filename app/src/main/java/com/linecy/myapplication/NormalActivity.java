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

public class NormalActivity extends AppCompatActivity {

  public static final String EXTRA_DATA = "extra_data";
  private TextView textView;
  private BannerView bannerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_normal);

    TextView name = findViewById(R.id.textView);
    textView = findViewById(R.id.textView2);
    bannerView = findViewById(R.id.bannerView);
    bannerView.setupWithBannerCreator(new NormalCreator());
    if (getIntent().getBooleanExtra(EXTRA_DATA, true)) {
      bannerView.setOrientation(BannerView.HORIZONTAL);
      name.setText("横向滚动");
    } else {
      bannerView.setOrientation(BannerView.VERTICAL);
      name.setText("纵向滚动");
    }
    initBannerView();
  }

  void initBannerView() {
    textView.setText("第 1 个");
    bannerView.setOnBannerClickListener(new OnBannerClickListener() {
      @Override public void onBannerClick(Object data, int position) {
        Toast.makeText(NormalActivity.this, "第 " + ((position + 1)) + " 个", Toast.LENGTH_SHORT)
            .show();
      }
    });
    bannerView.setOnBannerScrollChangeListener(new OnBannerScrollChangeListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState, int position) {
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
