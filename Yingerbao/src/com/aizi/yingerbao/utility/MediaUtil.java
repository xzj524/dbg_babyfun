package com.aizi.yingerbao.utility;

import java.io.IOException;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class MediaUtil {
    static MediaPlayer mMediaPlayer;
    /** single instance. */
    private static volatile MediaUtil mInstance; 
    Context mContext;
    
    protected MediaUtil(Context context) {
        mContext = context;
        mMediaPlayer = MediaPlayer.create(mContext, getSystemDefultRingtoneUri(context));  
        mMediaPlayer.setLooping(true);  
        try {  
            mMediaPlayer.prepare();  
        } catch (IllegalStateException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    
    public static MediaUtil getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MediaUtil(context);
            return mInstance;
        }
    }
 
    public void startAlarm() {  
        if (mMediaPlayer != null) {
            mMediaPlayer.start();  
        }
    }  
    
    public void stopAlarm() {  
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();  
        }
    }  
    
    //获取系统默认铃声的Uri  
    public Uri getSystemDefultRingtoneUri(Context context) {  
        return RingtoneManager.getActualDefaultRingtoneUri(context,  
                RingtoneManager.TYPE_ALL);  
    }  
    
}
