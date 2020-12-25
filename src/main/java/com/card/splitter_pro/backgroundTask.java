package com.card.splitter_pro;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;


public class backgroundTask extends AsyncTask<String,String,String>
{

    Context context;
    progressText progressText;
    VideoView videoView;

    SharedPreferences.Editor progresseditor;
    SharedPreferences getProgress;

    public backgroundTask(Context context, com.card.splitter_pro.progressText progressText, VideoView videoView) {
        this.context = context;
        this.progressText = progressText;
        this.videoView = videoView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        //pDialog.setMessage(values[0]);

        progressText.progressAt(values[0]);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    @Override
    protected String doInBackground(String... strings) {

        progresseditor = context.getSharedPreferences("progress",Context.MODE_PRIVATE).edit();
        getProgress = context.getSharedPreferences("progress",Context.MODE_PRIVATE);
        String data = "";
        try {

            while (true)
            {
                data = testtime(videoView.getCurrentPosition());
                publishProgress(data);
                if (getProgress.getBoolean("playing",false) == false)
                {
                    break;
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }



    public String testtime(double duration){

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((long) duration);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds((long) duration);
        int minsec = (int) (seconds-MinutestoSecs(minutes));
        String resu = minutes+"."+minsec;


        Log.i("minutes",""+minutes);
        Log.i("seconds",""+seconds);
        Log.i("minsec",""+minsec);
        Log.i("Dividing","Dividing");
        Log.i("Result",""+resu);

        return resu;
    }

    public double MinutestoSecs(double minutes) //add full duration
    {
        return minutes*60;
    }


}













