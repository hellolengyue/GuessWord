package com.hel.guessword.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.guessword.Constants;
import com.hel.guessword.MyApplication;
import com.hel.guessword.R;
import com.hel.guessword.adapter.GridViewAdapter;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.hel.guessword.Constants.dbPath;
import static com.hel.guessword.Constants.hasMusic;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class GameActivity extends Activity {
    private List<Map<String, String>> mDatas = new ArrayList<>();
    private SQLiteDatabase database;
    private String level_no;
    private String sub_level_no;
    private TextView problem, level, right, ask_desc, gold_count;
    private GridView gridView;
    private List<Map<String, String>> list = new ArrayList<>();
    private ImageView isRight;
    private GridViewAdapter adapter;
    private Button next, back;
    private List<Map<String, String>> randomList1;
    private String word;
    private LinearLayout add_coin;
    private ImageView coin_img;
    private TextView coin_add1;
    private MediaPlayer mp;
    private int countClick = 0;
    private LinearLayout get_answer;
    private TextView need;
    private String word_exp;
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        problem = findViewById(R.id.problem);
        level = findViewById(R.id.level);
        right = findViewById(R.id.right);
        gridView = findViewById(R.id.ask);
        isRight = findViewById(R.id.isRight);
        ask_desc = findViewById(R.id.ask_desc);
        next = findViewById(R.id.next);
        next.setEnabled(false);
        back = findViewById(R.id.back);
        add_coin = findViewById(R.id.add_coin);
        gold_count = findViewById(R.id.gold_count);
        coin_img = findViewById(R.id.coin_img);
        coin_add1 = findViewById(R.id.coin_add1);
        get_answer = findViewById(R.id.get_answer);
        need = findViewById(R.id.need);
        mp = MediaPlayer.create(this, R.raw.click);
        countClick = (int) MyApplication.sp.getSharedPreference("countClick", 0);

        gold_count.setText(Constants.goldCount + "");
        final Intent intent = getIntent();
        level_no = intent.getStringExtra("level_no");
        sub_level_no = intent.getStringExtra("sub_level_no");
        if (level_no == null) {
            level_no = (String) MyApplication.sp.getSharedPreference("level_no", "1");
            sub_level_no = (String) MyApplication.sp.getSharedPreference("sub_level_no", "1");
        }
        need.setText("-" + Integer.decode(level_no) * 10);
        initData(level_no);
        randomList1 = setData(Integer.decode(level_no), Integer.decode(sub_level_no));
        initData();
        adapter = new GridViewAdapter(this, R.layout.ask_item, randomList1);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("hel", "onItemClick: " + countClick);
                countClick++;
                if (countClick > 5) {
                    new AlertDialog.Builder(GameActivity.this)
                            .setTitle("提示")
                            .setMessage("是否花费1金币购买一次机会？")
                            .setCancelable(false)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("购买", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Constants.goldCount > 0) {
                                        Constants.goldCount--;
                                        countClick--;
                                        countClick--;
                                        gold_count.setText(Constants.goldCount + "");
                                    } else {
                                        Toast.makeText(GameActivity.this, "金币不足！", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(GameActivity.this, GetGoldActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }).show();
                } else {
                    right.setText(randomList1.get(i).get("word1"));
                    isRight.setVisibility(View.VISIBLE);
                    if (randomList1.get(i).get("word1").equals(word)) {
                        //回答正确
                        countClick = 0;
                        isRight.setImageResource(R.drawable.ask_right);
                        coin_img.setVisibility(View.VISIBLE);
                        coin_add1.setVisibility(View.VISIBLE);
                        ask_desc.setVisibility(View.VISIBLE);
                        ask_desc.setText(randomList1.get(i).get("word_exp"));
                        gridView.setEnabled(false);
                        next.setEnabled(true);
                        get_answer.setEnabled(false);

                        Constants.goldCount += 1;
                        gold_count.setText(Constants.goldCount + "");
                        //修改数据库
                        ContentValues values = new ContentValues();
                        values.put("ispass", 1);
                        database.update("word_data", values, "level_no=? and sub_level_no=?", new String[]{level_no, sub_level_no});
                        if (hasMusic) {
                            try {
                                mp.reset();
                                mp = MediaPlayer.create(GameActivity.this, R.raw.coin);//重新设置要播放的音频
                                mp.start();//开始播放
                            } catch (Exception e) {
                                e.printStackTrace();//输出异常信息
                            }
                        }
                        Integer decode = Integer.decode(level_no);
                        Integer decode2 = Integer.decode(sub_level_no);
                        if (decode == 13 && decode2 == mDatas.size()) {
                            return;
                        }
                        if (decode2 >= mDatas.size()) {
                            decode++;
                            decode2 = 1;
                            level_no = decode + "";
                            sub_level_no = decode2 + "";
                            need.setText("-" + Integer.decode(level_no) * 10);
                        } else {
                            decode2++;
                            sub_level_no = decode2 + "";

                        }
                    } else {
                        Toast.makeText(GameActivity.this, "还可以尝试" + (5 - countClick) + "次", Toast.LENGTH_SHORT).show();
                        isRight.setImageResource(R.drawable.ask_error);
                        if (hasMusic) {
                            try {
                                mp.reset();
                                mp = MediaPlayer.create(GameActivity.this, R.raw.click_error_1);//重新设置要播放的音频
                                mp.start();//开始播放
                            } catch (Exception e) {
                                e.printStackTrace();//输出异常信息
                            }
                        }


                    }
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasMusic) {
                    try {
                        mp.reset();
                        mp = MediaPlayer.create(GameActivity.this, R.raw.click);//重新设置要播放的音频
                        mp.start();//开始播放
                    } catch (Exception e) {
                        e.printStackTrace();//输出异常信息
                    }
                }
                get_answer.setEnabled(true);
                MyApplication.sp.put("gold", Constants.goldCount);
                isRight.setVisibility(View.INVISIBLE);
                ask_desc.setVisibility(View.INVISIBLE);
                right.setText("");
                gridView.setEnabled(true);
                coin_img.setVisibility(View.INVISIBLE);
                coin_add1.setVisibility(View.INVISIBLE);
                next.setEnabled(false);
                Integer decode = Integer.decode(level_no);
                Integer decode2 = Integer.decode(sub_level_no);
//                if (decode == 13 && decode2 == mDatas.size()) {
//                    return;
//                }
//                if (decode2 >= mDatas.size()) {
//                    decode++;
//                    decode2 = 1;
//                    level_no = decode + "";
//                    sub_level_no = decode2 + "";
//                } else {
//                    decode2++;
//                    sub_level_no = decode2 + "";
//
//                }
                randomList1 = setData(decode, decode2);
                adapter.setGridData(randomList1);

            }
        });
        add_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, GetGoldActivity.class);
                startActivity(intent);
            }
        });
        get_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(GameActivity.this)
                        .setTitle("提示")
                        .setMessage("是否花费" + Integer.decode(level_no) * 10 + "金币直接获取答案？")
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Constants.goldCount >= Integer.decode(level_no) * 10) {
                                    Constants.goldCount -= Integer.decode(level_no) * 10;
                                    MyApplication.sp.put("gold", Constants.goldCount);
                                    gold_count.setText(Constants.goldCount + "");
                                    right.setText(word);
                                    //回答正确
                                    countClick = 0;
                                    isRight.setImageResource(R.drawable.ask_right);
                                    coin_img.setVisibility(View.VISIBLE);
                                    coin_add1.setVisibility(View.VISIBLE);
                                    ask_desc.setVisibility(View.VISIBLE);
                                    ask_desc.setText(word_exp);
                                    gridView.setEnabled(false);
                                    next.setEnabled(true);
                                    get_answer.setEnabled(false);

                                    Constants.goldCount += 1;
                                    gold_count.setText(Constants.goldCount + "");
                                    //修改数据库
                                    ContentValues values = new ContentValues();
                                    values.put("ispass", 1);
                                    database.update("word_data", values, "level_no=? and sub_level_no=?", new String[]{level_no, sub_level_no});
                                    if (hasMusic) {
                                        try {
                                            mp.reset();
                                            mp = MediaPlayer.create(GameActivity.this, R.raw.coin);//重新设置要播放的音频
                                            mp.start();//开始播放
                                        } catch (Exception e) {
                                            e.printStackTrace();//输出异常信息
                                        }
                                    }
                                    Integer decode = Integer.decode(level_no);
                                    Integer decode2 = Integer.decode(sub_level_no);
                                    if (decode == 13 && decode2 == mDatas.size()) {
                                        return;
                                    }
                                    if (decode2 >= mDatas.size()) {
                                        decode++;
                                        decode2 = 1;
                                        level_no = decode + "";
                                        sub_level_no = decode2 + "";
                                        need.setText("-" + Integer.decode(level_no) * 10);
                                    } else {
                                        decode2++;
                                        sub_level_no = decode2 + "";

                                    }
                                } else {
                                    Toast.makeText(GameActivity.this, "金币不足！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(GameActivity.this, GetGoldActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).show();
            }
        });
    }

    /**
     * 设置关卡数据
     *
     * @param level_no
     * @param sub_level_no
     * @return
     */
    public List<Map<String, String>> setData(int level_no, int sub_level_no) {
        level.setText("第" + level_no + "-" + sub_level_no + "题");
        //存储关卡
        MyApplication.sp.put("level_no", level_no);
        MyApplication.sp.put("sub_level_no", sub_level_no);
        problem.setText(mDatas.get(sub_level_no - 1).get("word_desc"));
        //正确答案
        word = mDatas.get(sub_level_no - 1).get("word");
        word_exp = mDatas.get(sub_level_no - 1).get("word_exp");

        for (int i = 0; i < mDatas.size(); i++) {
            Map<String, String> map = new HashMap<>();
            String word1 = mDatas.get(i).get("word");
            String word_exp1 = mDatas.get(i).get("word_exp");
            if (word1.equals(word)) {
                continue;
            }
            map.put("word1", word1);
            map.put("word_exp", word_exp1);
            list.add(map);
        }
        List<Map<String, String>> randomList = getRandomList(list, 20);
        Map<String, String> map = new HashMap<>();
        map.put("word1", word);
        map.put("word_exp", word_exp);
        randomList.add(map);
        List<Map<String, String>> randomList1 = getRandomList(randomList, 21);
        return randomList1;
    }

    /**
     * @param paramList:被抽取list
     * @param count:抽取元素的个数
     * @function:从list中随机抽取若干不重复元素
     * @return:由抽取元素组成的新list
     */
    public static List<Map<String, String>> getRandomList(List<Map<String, String>> paramList, int count) {
        if (paramList.size() < count) {
            return paramList;
        }
        Random random = new Random();
        List<Integer> tempList = new ArrayList<Integer>();
        List<Map<String, String>> newList = new ArrayList<>();
        int temp = 0;
        for (int i = 0; i < count; i++) {
            temp = random.nextInt(paramList.size());//将产生的随机数作为被抽list的索引
            if (!tempList.contains(temp)) {
                tempList.add(temp);
                newList.add(paramList.get(temp));
            } else {
                i--;
            }
        }
        return newList;
    }

    private void initData(String level_no) {
        if (level_no != null) {
            mDatas.clear();
            database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            Cursor cursor = database.query("word_data", null, "level_no = ?", new String[]{level_no}, null, null, null);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                String sub_level_no = cursor.getString(cursor.getColumnIndex("sub_level_no"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String word_desc = cursor.getString(cursor.getColumnIndex("word_desc"));
                String word_exp = cursor.getString(cursor.getColumnIndex("word_exp"));
                String ispass = cursor.getString(cursor.getColumnIndex("ispass"));
                map.put("sub_level_no", sub_level_no);
                map.put("word", word);
                map.put("word_desc", word_desc);
                map.put("word_exp", word_exp);
                map.put("ispass", ispass);
                mDatas.add(map);
            }
            cursor.close();
        }


    }
    public void initData() {
        //全屏广告实例
        ad = new InterstitialAd(GameActivity.this, Adwo_PID, false, new InterstitialAdListener() {
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
        if (countClick >= 5) {
            countClick = 5;
        }
        gold_count.setText(Constants.goldCount + "");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //存储关卡
        MyApplication.sp.put("level_no", level_no);
        MyApplication.sp.put("sub_level_no", sub_level_no);
        MyApplication.sp.put("gold", Constants.goldCount);
        if (countClick >= 5) {
            countClick = 5;
        }
        MyApplication.sp.put("countClick", countClick);
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp.release();//释放资源
    }
}
