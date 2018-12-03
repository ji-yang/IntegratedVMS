package com.example.shayanmirjafari.integratedvms.prefrences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

/**
 * Created by shayan on 8/15/15.
 */
public class PrefrencesManager {
//    private Context mContext;
    SharedPreferences manager;
    private PrefrencesManager(Context context){
//        this.mContext = context;
        manager = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        
    }

    public static PrefrencesManager createPref(final Context context){
        return new PrefrencesManager(context);
    }

    public void saveAlbum(byte[] albumBuffer) {
        SharedPreferences.Editor editor = manager.edit();
        editor.putString("albumArray", Arrays.toString(albumBuffer));
        editor.commit();
    }

    public byte[] loadAlbum(){
        String arrayOfString = manager.getString("albumArray", null);

        byte[] albumArray = null;
        if (arrayOfString != null) {
            String[] splitStringArray = arrayOfString.substring(1,
                    arrayOfString.length() - 1).split(", ");

            albumArray = new byte[splitStringArray.length];
            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }

        }

        return albumArray;
    }

    public void setFirstTime(){
        SharedPreferences.Editor editor = manager.edit();
        editor.putBoolean("first", false);
        editor.commit();

    }

    public boolean isFirstTime(){
        return manager.getBoolean("first", true);
    }





}
