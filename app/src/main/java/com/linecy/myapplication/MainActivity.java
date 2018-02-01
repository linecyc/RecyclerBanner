package com.linecy.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.linecy.banner.BannerView;
import com.linecy.banner.listener.OnBannerClickListener;
import com.linecy.banner.listener.OnBannerScrollChangeListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  String p1 = "http://img1.imgtn.bdimg.com/it/u=1891468773,1102023310&fm=27&gp=0.jpg";
  String p2 = "http://img2.91.com/uploads/allimg/140328/32-14032QP519.jpg";
  String p3 = "http://imgsrc.baidu.com/imgad/pic/item/b812c8fcc3cec3fdc02b17a1dc88d43f869427c8.jpg";
  String p4 = "http://img15.3lian.com/2015/a1/15/d/1.jpg";
  private TextView textView;
  private BannerView bannerView;
  private TextView textView2;
  private BannerView bannerView2;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = findViewById(R.id.text);
    bannerView = findViewById(R.id.bannerView);
    bannerView.setupWithBannerCreator(new NormalCreator());
    textView2 = findViewById(R.id.text2);
    bannerView2 = findViewById(R.id.bannerView2);
    bannerView2.setupWithBannerCreator(new NormalCreator());
    initHorizontal();
    initVertical();
  }

  void initHorizontal() {
    textView.setText("第 0 个");
    bannerView.setOnBannerClickListener(new OnBannerClickListener() {
      @Override public void onBannerClick(Object data, int position) {
        Log.i("click", "--------------->>" + position);
        Toast.makeText(MainActivity.this, "第 " + (position) + " 个", Toast.LENGTH_SHORT).show();
      }
    });
    bannerView.setOnBannerScrollChangeListener(new OnBannerScrollChangeListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState, int position) {
        textView.setText("第 " + (position) + " 个");
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

      }
    });

    bannerView.onRefreshData(createData());
  }

  void initVertical() {
    textView2.setText("第 0 个");
    bannerView2.setOnBannerClickListener(new OnBannerClickListener() {
      @Override public void onBannerClick(Object data, int position) {
        Log.i("click", "--------------->>" + position);
        Toast.makeText(MainActivity.this, "第 " + (position) + " 个", Toast.LENGTH_SHORT).show();
      }
    });
    bannerView2.setOnBannerScrollChangeListener(new OnBannerScrollChangeListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState, int position) {
        textView2.setText("第 " + (position) + " 个");
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

      }
    });

    bannerView2.onRefreshData(createData());
  }

  List<String> createData() {
    List<String> list = new ArrayList<>();
    list.add(p1);
    list.add(p2);
    list.add(p3);
    list.add(p4);
    return list;
  }
}
