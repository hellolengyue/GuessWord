package com.hel.guessword.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author hel
 * @date 2018/2/11
 * 文件 TakeJoke
 * 描述
 */

public class Utils {
    //路径例如： /SD卡/Android/data/程序的包名/cache/uniqueName
    public static String  getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator;
    }
}
