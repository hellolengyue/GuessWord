package com.hel.guessword.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.guessword.R;
import com.hel.guessword.adapter.MyLevelAdapter;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hel.guessword.Constants.dbPath;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class LevelItemActivity extends Activity {
    private RecyclerView recyclerView;
    private List<Map<String, String>> mDatas = new ArrayList<>();
    private MyLevelAdapter recycleAdapter;
    private SQLiteDatabase database;
    private String level;
    private Button back;
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    private TextView level_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Intent intent = getIntent();
        level = intent.getStringExtra("level");
        recyclerView = findViewById(R.id.recyclerView);
        level_title = findViewById(R.id.level_title);
        level_title.setText("选择题目");
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        initDataAd();
        recycleAdapter = new MyLevelAdapter(this, mDatas);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(recycleAdapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 25);
            }
        });
        recycleAdapter.setOnItemClickListener(new MyLevelAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

                if (mDatas.get(position).get("ispass").equals("0")) {
                    if (level.equals("1") && mDatas.get(position).get("sub_level_no").equals("1")) {
                        Intent intent = new Intent(LevelItemActivity.this, GameActivity.class);
                        intent.putExtra("level_no", level);
                        intent.putExtra("sub_level_no", mDatas.get(position).get("sub_level_no"));
                        startActivity(intent);
                    } else {

                        Toast.makeText(LevelItemActivity.this, "请先完成前一道题目！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(LevelItemActivity.this, GameActivity.class);
                    intent.putExtra("level_no", level);
                    intent.putExtra("sub_level_no", mDatas.get(position).get("sub_level_no"));
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(int position) {

            }
        });

    }

    private void initData() {
        if (level != null) {
            database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            Cursor cursor = database.query("word_data", null, "level_no = ?", new String[]{level}, null, null, null);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                String level_no = cursor.getString(cursor.getColumnIndex("level_no"));
                String sub_level_no = cursor.getString(cursor.getColumnIndex("sub_level_no"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String word_desc = cursor.getString(cursor.getColumnIndex("word_desc"));
                String word_exp = cursor.getString(cursor.getColumnIndex("word_exp"));
                String ispass = cursor.getString(cursor.getColumnIndex("ispass"));
                map.put("level_no", level_no);
                map.put("sub_level_no", sub_level_no);
                map.put("word", word);
                map.put("word_desc", word_desc);
                map.put("word_exp", word_exp);
                map.put("ispass", ispass);
                mDatas.add(map);
            }
        }


    }
    public void initDataAd() {
        //全屏广告实例
        ad = new InterstitialAd(LevelItemActivity.this, Adwo_PID, false, new InterstitialAdListener() {
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
