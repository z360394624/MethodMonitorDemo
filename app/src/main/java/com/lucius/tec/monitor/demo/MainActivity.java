package com.lucius.tec.monitor.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  private int count = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }


  @Override
  protected void onStart() {
    super.onStart();
    for (int i = 0; i < 1000000; i++) {
      count += i;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    for (int i = 0; i < 10000000; i++) {
      count += i;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    for (int i = 0; i < 10000000; i++) {
      count -= i;
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    for (int i = 0; i < 10000000; i++) {
      count -= i;
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    for (int i = 0; i < 10000000; i++) {
      count += i;
    }
  }
}
