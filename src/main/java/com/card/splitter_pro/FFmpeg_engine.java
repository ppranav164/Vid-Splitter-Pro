package com.card.splitter_pro;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;

public class FFmpeg_engine {


    public static  void getVideoProperties(ArrayList<String> commandLines,Context context)
    {
        String[] lines = new String[commandLines.size()];
        for (int i=0; i < commandLines.size(); i++)
        {
            lines[i] = commandLines.get(i);
        }
        Log.e("getvideoProperties",commandLines.toString());
       //start(lines,context);
        getInfo(context,lines);
    }


    public static  void start(String[] commannds,Context context)
    {
        FFmpeg ffmpeg = FFmpeg.getInstance(context);

        ffmpeg.execute(commannds,new ExecuteBinaryResponseHandler(){

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);

                Log.e("FFmpeg_engine","onSuccess > " + message);
            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);

                Log.e("FFmpeg_engine","onProgress > " + message);
            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);

                Log.e("FFmpeg_engine","onFailure > " + message);
            }

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onFinish() {
                super.onFinish();


            }


        });

    }



    public static void getInfo(Context context,String[] command)
    {
        FFprobe ffprobe = FFprobe.getInstance(context);
        try {
            // to execute "ffprobe -version" command you just need to pass "-version"
            ffprobe.execute(command, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onProgress(String message) {}

                @Override
                public void onFailure(String message) {}

                @Override
                public void onSuccess(String message) {

                    Log.e("ffprobe","onSuccess > " + message);
                }

                @Override
                public void onFinish() {}

            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }




}
