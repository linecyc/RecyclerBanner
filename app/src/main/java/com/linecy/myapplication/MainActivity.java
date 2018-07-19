package com.linecy.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.button).setOnClickListener(this);
    findViewById(R.id.button2).setOnClickListener(this);
    findViewById(R.id.button3).setOnClickListener(this);
    findViewById(R.id.button4).setOnClickListener(this);
  }

  @Override public void onClick(View v) {

    switch (v.getId()) {
      case R.id.button:
        Intent intent = new Intent(this, NormalActivity.class);
        intent.putExtra(NormalActivity.EXTRA_DATA, true);
        startActivity(intent);
        break;

      case R.id.button2:
        Intent intent2 = new Intent(this, NormalActivity.class);
        intent2.putExtra(NormalActivity.EXTRA_DATA, false);
        startActivity(intent2);
        break;

      case R.id.button3:
        Intent intent3 = new Intent(this, ScaleActivity.class);
        intent3.putExtra(ScaleActivity.EXTRA_DATA, true);
        startActivity(intent3);
        break;
      case R.id.button4:
        Intent intent4 = new Intent(this, ScaleActivity.class);
        intent4.putExtra(ScaleActivity.EXTRA_DATA, false);
        startActivity(intent4);
        break;
    }
  }
}
