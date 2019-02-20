package com.hel.guessword.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hel.guessword.Constants;
import com.hel.guessword.MyApplication;
import com.hel.guessword.R;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class GetGoldActivity extends Activity implements View.OnClickListener {
    private TextView tv_user_gold, tv_pay_1, tv_pay_2, tv_pay_3;
    private Button back;
    private String format;
    private String format2;
    private boolean isPay1, isPay2, isPay3;
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold);
        initDataAd();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        //当前日期
        format = simpleDateFormat.format(date);
        format2 = (String) MyApplication.sp.getSharedPreference("time", "");
        //存储当前日期
        MyApplication.sp.put("time", format);
        tv_user_gold = findViewById(R.id.tv_user_gold);
        tv_pay_1 = findViewById(R.id.tv_pay_1);
        tv_pay_2 = findViewById(R.id.tv_pay_2);
        tv_pay_3 = findViewById(R.id.tv_pay_3);
        back = findViewById(R.id.back);

        tv_pay_1.setOnClickListener(this);
        tv_pay_2.setOnClickListener(this);
        tv_pay_3.setOnClickListener(this);
        back.setOnClickListener(this);

        tv_user_gold.setText(Constants.goldCount+"");

        isPay1 = (boolean) MyApplication.sp.getSharedPreference("isPay1",false);
        isPay2 = (boolean) MyApplication.sp.getSharedPreference("isPay2",false);
        isPay3 = (boolean) MyApplication.sp.getSharedPreference("isPay3",false);

        if (format.equals(format2)) {
            if (!isPay1) {
                tv_pay_1.setEnabled(true);
            } else {
                tv_pay_1.setText("已签到");
                tv_pay_1.setEnabled(false);
            }
            if (!isPay2) {
                tv_pay_2.setEnabled(true);
            } else {
                tv_pay_2.setText("已好评");
                tv_pay_2.setEnabled(false);
            }
            if (!isPay3) {
                tv_pay_3.setEnabled(true);
            } else {
                tv_pay_3.setText("已分享");
                tv_pay_3.setEnabled(false);
            }
        } else {
            tv_pay_1.setEnabled(true);
            tv_pay_2.setEnabled(true);
            tv_pay_3.setEnabled(true);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_pay_1:
                new AlertDialog.Builder(GetGoldActivity.this)
                        .setTitle("提示")
                        .setMessage("签到成功，金币+20")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Constants.goldCount += 20;
                                tv_user_gold.setText(Constants.goldCount+"");
                                tv_pay_1.setEnabled(false);
                                MyApplication.sp.put("isPay1", true);
                                tv_pay_1.setText("已签到");
                            }
                        }).show();
                break;
            case R.id.tv_pay_2:
                String mAddress = "market://details?id=" + getPackageName();
                Intent marketIntent = new Intent("android.intent.action.VIEW");
                marketIntent.setData(Uri.parse(mAddress));
                startActivityForResult(marketIntent, 0);
                break;
            case R.id.tv_pay_3:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String mAddress2 = "market://details?id=" + GetGoldActivity.this.getPackageName();
                shareIntent.putExtra(Intent.EXTRA_TEXT, "猜字闯关，乐趣无穷，点击进入" + mAddress2);
                shareIntent.setType("text/plain");

                //设置分享列表的标题，并且每次都显示分享列表
                startActivityForResult(Intent.createChooser(shareIntent, "分享到"), 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 || requestCode == 1) {
            if (requestCode == 0) {
                tv_pay_2.setEnabled(false);
                MyApplication.sp.put("isPay2", true);
                tv_pay_2.setText("已好评");
            }
            if (requestCode == 1) {
                tv_pay_3.setEnabled(false);
                MyApplication.sp.put("isPay3", true);
                tv_pay_3.setText("已分享");
            }
            new AlertDialog.Builder(GetGoldActivity.this)
                    .setTitle("提示")
                    .setCancelable(false)
                    .setMessage("感谢您的大力支持，金币+20")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Constants.goldCount += 20;
                            tv_user_gold.setText(Constants.goldCount+"");
                        }
                    }).show();
        }
    }
    public void initDataAd() {
        //全屏广告实例
        ad = new InterstitialAd(GetGoldActivity.this, Adwo_PID, false, new InterstitialAdListener() {
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
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.sp.put("gold", Constants.goldCount);
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
