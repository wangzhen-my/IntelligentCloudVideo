package ncist.wl171.intelligentcloudvideo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import ncist.wl171.intelligentcloudvideo.Enum.ValueKeys;

/**
 * SharedPreference工具类
 */
public class SpTool {

    //得到类的简写名称
    private final static String TAG = SpTool.class.getSimpleName();
    private final static String SP_FILE_NAME = "ICVcache";
    private static SharedPreferences mSP = null;

    /**
     * 初始化
     */
    public static void init(@NonNull Context context){
        mSP = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     *安全Key取
     */
    public static String obtainValue(ValueKeys key){
        return obtainValue(key.name());
    }

    /**
     * 取
     */
    public static String obtainValue(String key){
        if (mSP == null){
            Log.e(TAG, "SpTool is not init!!!");
            return null;
        }
        return mSP.getString(key, null);
    }

    /**
     * 存
     */
    public static void storeValue(ValueKeys key, String value){
        storeValue(key.name(), value);
    }

    /**
     * 安全Kye取
     */
    public static void storeValue(String key, String value){
        if (mSP == null){
            Log.e(TAG, "SpTool is not init!!!");
            return;
        }
        mSP.edit().putString(key, value).apply();
    }
}

