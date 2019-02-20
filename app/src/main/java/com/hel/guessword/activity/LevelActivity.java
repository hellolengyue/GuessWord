package com.hel.guessword.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hel.guessword.R;
import com.hel.guessword.adapter.MyRecyclerAdapter;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class LevelActivity extends Activity {
    private RecyclerView recyclerView;
    private List<String> mDatas = new ArrayList<String>();
    private MyRecyclerAdapter recycleAdapter;
    private SQLiteDatabase database;
    private Button back;
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    private TextView level_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        level_title = findViewById(R.id.level_title);
        level_title.setText("选择章节");
        initData();
        initDataAd();
        recycleAdapter = new MyRecyclerAdapter(this, mDatas);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置Adapter
        recyclerView.setAdapter(recycleAdapter);
//        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0,0,0,25);
            }
        });
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recycleAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(LevelActivity.this,LevelItemActivity.class);
                intent.putExtra("level",mDatas.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 13; i++) {
            mDatas.add(i+1+"");
        }

    }
    public void initDataAd() {
        //全屏广告实例
        ad = new InterstitialAd(LevelActivity.this, Adwo_PID, false, new InterstitialAdListener() {
            @Override
            public void onReceiveAd() {
                Log.e("hel", "onReceiveAd: ");
            }

            @Override
            public void onLoadAdComplete() {
                // 成功完成下载后，展示广告
                ad.displayAd();


            }

            @Override
            public void onFailedToReceiveAd(ErrorCode errorCode) {
                Log.e("hel", "onFailedToReceiveAd: " + errorCode);
            }

            @Override
            public void onAdDismiss() {
                Log.e("hel", "onAdDismiss: ");
            }

            @Override
            public void OnShow() {
                Log.e("hel", "OnShow: ");
            }
        });
        // 设置全屏格式
        ad.setDesireAdForm(InterstitialAd.ADWO_INTERSTITIAL);
        // 设置请求广告类型 可选。
        ad.setDesireAdType((byte) 0);
        // 开始请求全屏广告
        ad.prepareAd();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
