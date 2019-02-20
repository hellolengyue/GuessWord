package com.hel.guessword;

import android.app.Application;
import android.content.Context;

import com.hel.guessword.utils.SharedPreferencesHelper;

/**
 * @author hel
 * @date 2018/2/23
 * 文件 GuessWord
 * 描述
 */

public class MyApplication extends Application {
    public static SharedPreferencesHelper sp;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //存储数据
        sp = new SharedPreferencesHelper(context, "my_word");
    }
}
