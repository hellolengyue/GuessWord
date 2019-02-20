package com.hel.guessword.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.guessword.MyApplication;
import com.hel.guessword.R;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.hel.guessword.Constants.dbPath;


/**
 * @author hel
 * @date 2018/2/26
 * 文件 TakeJoke
 * 描述
 */

public class ThinkActivity extends Activity implements View.OnClickListener {
    private Button back, share;
    private TextView level, ask, answer, next;
    private SQLiteDatabase database;
    private int count = 1;
    private boolean isGood = false;
    private ArrayList<Map<String, String>> contents = new ArrayList<>();
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ask.setText(contents.get(count - 1).get("content"));
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_think);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        back = findViewById(R.id.back);
//        share = findViewById(R.id.share);
        level = findViewById(R.id.level);
        ask = findViewById(R.id.ask);
        answer = findViewById(R.id.answer);
        next = findViewById(R.id.next);

        back.setOnClickListener(this);
//        share.setOnClickListener(this);
        answer.setOnClickListener(this);
        next.setOnClickListener(this);
        initData();
        //获取当前第几题
        count = (int) MyApplication.sp.getSharedPreference("count", 1);
        level.setText("第"+count + "题");
        geneItems();
        isGood = (boolean) MyApplication.sp.getSharedPreference("isGood",false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.share:
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                String mAddress = "market://details?id=" + ThinkActivity.this.getPackageName();
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "你知道答案吗：\n" + contents.get(count - 1).get("content"));
//                shareIntent.setType("text/plain");
//
//                //设置分享列表的标题，并且每次都显示分享列表
//                startActivity(Intent.createChooser(shareIntent, "分享到"));
//                break;
            case R.id.answer:
                if (!isGood){
                    new AlertDialog.Builder(ThinkActivity.this)
                            .setTitle("致谢")
                            .setMessage("好评可永久解锁显示答案！")
                            .setNegativeButton("稍后再说", null)
                            .setPositiveButton("去好评", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String mAddress = "market://details?id=" + getPackageName();
                                    Intent marketIntent = new Intent("android.intent.action.VIEW");
                                    marketIntent.setData(Uri.parse(mAddress));
                                    startActivityForResult(marketIntent,1);
                                }
                            }).show();
                }else{
                    answer.setText(contents.get(count - 1).get("answer"));
                }


                break;
            case R.id.next:
                count++;
                level.setText("第"+count + "题");
                ask.setText(contents.get(count - 1).get("content"));
                answer.setText("点击显示答案");
                break;
        }
    }

    /**
     * 初始化数据，刷新时添加数据
     */
    private void geneItems() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
                Cursor cursor = database.query("think", null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String answer = cursor.getString(cursor.getColumnIndex("answer"));
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    Map<String, String> map = new HashMap<>();
                    map.put("id", id + "");
                    map.put("content", content);
                    map.put("answer", answer);
                    contents.add(map);
                }
                cursor.close();
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    public void initData() {
        //全屏广告实例
        ad = new InterstitialAd(ThinkActivity.this, Adwo_PID, false, new InterstitialAdListener() {
            @Override
            public void onReceiveAd() {
                Log.e("hel", "onReceiveAd: " );
            }

            @Override
            public void onLoadAdComplete() {
                // 成功完成下载后，展示广告
                ad.displayAd();


            }

            @Override
            public void onFailedToReceiveAd(ErrorCode errorCode) {
                Log.e("hel", "onFailedToReceiveAd: "+errorCode );
            }

            @Override
            public void onAdDismiss() {
                Log.e("hel", "onAdDismiss: " );
            }

            @Override
            public void OnShow() {
                Log.e("hel", "OnShow: " );
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            answer.setText(contents.get(count - 1).get("answer"));
            MyApplication.sp.put("isGood",true);
            isGood = true;
            Toast.makeText(ThinkActivity.this,"感谢您的大力支持",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //存储count
        MyApplication.sp.put("count",count);

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
