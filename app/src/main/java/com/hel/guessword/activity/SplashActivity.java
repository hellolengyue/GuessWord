package com.hel.guessword.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.hel.guessword.Constants;
import com.hel.guessword.MyApplication;
import com.hel.guessword.R;
import com.hel.guessword.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.hel.guessword.Constants.dbPath;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class SplashActivity extends Activity {
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        getWindow().getDecorView().setFitsSystemWindows(true);
        //透明状态栏 @顶部
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_splash);
        dbPath = Utils.getDiskCacheDir(this) + "word.db";
        //复制数据库
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            if (!(new File(dbPath).exists())) {
                is = this.getResources().openRawResource(R.raw.word); // 你Raw的那个db索引
                fos = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

            }

        } catch (Exception e) {
            Log.e("DB_ERROR", "数据文件读写失败");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e("DB_ERROR", "数据文件读写失败");
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("DB_ERROR", "数据文件读写失败");
                }
            }
        }
        //初始化金币
        Constants.goldCount = (int) MyApplication.sp.getSharedPreference("gold", 0);
        handler.sendEmptyMessageDelayed(0, 2000);
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
