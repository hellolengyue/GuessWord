package com.hel.guessword.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hel.guessword.MyApplication;
import com.hel.guessword.R;
import com.hel.guessword.service.AudioService;
import com.sixth.adwoad.AdListener;
import com.sixth.adwoad.AdwoAdView;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import static com.hel.guessword.Constants.hasMusic;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView start, select, think, set, help, more;
    private ImageView music, music_bg;
    private boolean isPlaying = true;
    private AudioService audioService;
    public static RelativeLayout layout;
    static AdwoAdView adView = null;
    String Adwo_PID = "12fc807dca2c4fbbafa2a99e6eb07dee";
    private InterstitialAd ad;
    RelativeLayout.LayoutParams params = null;
    //使用ServiceConnection来监听Service状态的变化
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            audioService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //这里我们实例化audioService,通过binder来实现
            audioService = ((AudioService.AudioBinder) binder).getService();

        }
    };
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (TextView) findViewById(R.id.start);
        select = (TextView) findViewById(R.id.select);
        think = (TextView) findViewById(R.id.think);
        set = (TextView) findViewById(R.id.set);
        help = (TextView) findViewById(R.id.help);
        more = (TextView) findViewById(R.id.more);
        start.setOnClickListener(this);
        select.setOnClickListener(this);
        think.setOnClickListener(this);
        set.setOnClickListener(this);
        help.setOnClickListener(this);
        more.setOnClickListener(this);

        //启动Service，然后绑定该Service，这样我们可以在同时销毁该Activity，看看歌曲是否还在播放
        intent = new Intent();
        intent.setClass(this, AudioService.class);
        layout = (RelativeLayout) findViewById(R.id.layout);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		当不设置广告条充满屏幕宽时建议放置在父容器中间
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // 实例化广告对象
        adView = new AdwoAdView(this, Adwo_PID, false, 20);
        // 设置广告监听回调
        adView.setListener(new AdListener() {
            @Override
            public void onReceiveAd(Object arg0) {
            }

            @Override
            public void onFailedToReceiveAd(View adView, ErrorCode errorCode) {
            }


            @Override
            public void onDismissScreen() {
            }

            @Override
            public void onPresentScreen() {
            }
        });
        isPlaying = (boolean) MyApplication.sp.getSharedPreference("playBg", true);
        if (isPlaying) {
            //启动Service，然后绑定该Service，这样我们可以在同时销毁该Activity，看看歌曲是否还在播放
            startService(intent);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
        layout.addView(adView, params);
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                openActivity(GameActivity.class);
                break;
            case R.id.select:
                openActivity(LevelActivity.class);
                break;
            case R.id.think:
                openActivity(ThinkActivity.class);
                break;
            case R.id.set:
                showDialog(0, R.layout.dialog_set);
                break;
            case R.id.help:
                showDialog(1, R.layout.dialog_help);
                break;
            case R.id.more:
                showDialog(2, R.layout.dialog_more);
                break;
        }
    }

    public void initData() {
        //全屏广告实例
        ad = new InterstitialAd(MainActivity.this, Adwo_PID, false, new InterstitialAdListener() {
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

    public void showDialog(int type, int res) {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(res, null);

        Dialog dialog = new Dialog(MainActivity.this, R.style.DiyDialog);
        dialog.setContentView(view);
        if (type == 0) {
            music = (ImageView) view.findViewById(R.id.music);
            music_bg = (ImageView) view.findViewById(R.id.music_bg);

            if (isPlaying) {
                music_bg.setBackgroundResource(R.drawable.btn_music_on);
                startService(intent);
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
            } else {
                music_bg.setBackgroundResource(R.drawable.btn_music_off);
            }

            music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasMusic) {
                        hasMusic = false;
                        music.setBackgroundResource(R.drawable.btn_music_off);
                    } else {
                        hasMusic = true;
                        music.setBackgroundResource(R.drawable.btn_music_on);
                    }
                }
            });
            music_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        isPlaying = false;
                        //结束Service
                        unbindService(conn);
                        stopService(intent);
                        music_bg.setBackgroundResource(R.drawable.btn_music_off);
                    } else {
                        isPlaying = true;
                        //启动Service，然后绑定该Service，这样我们可以在同时销毁该Activity，看看歌曲是否还在播放
                        startService(intent);
                        bindService(intent, conn, Context.BIND_AUTO_CREATE);
                        music_bg.setBackgroundResource(R.drawable.btn_music_on);
                    }
                }
            });
        }


        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = MainActivity.this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("退出应用？")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    public void openActivity(Class<?> clazz) {
        Intent intent = new Intent(MainActivity.this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束Service
        try {
            if (isPlaying) {
                unbindService(conn);
                stopService(intent);
            }
        }catch (Exception e){
            Log.e("hel", "onDestroy: "+e );
        }

        MyApplication.sp.put("playBg", isPlaying);

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

