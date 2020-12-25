package com.card.splitter_pro;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import fragments.settings_view;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;


public class DashboardActivity extends AppCompatActivity implements View.OnClickListener , CompleteCallback {

    ImageView playBtn;
    Button option_1,option_2,option_3;
    TextView heading,statusView,descview;
    Button changeBtn;
    ImageView uploadView;
    Button generateButton;
    ImageView uploadview;
    VideoView videoView;
    private Uri filePath;
    private String selectedFilePath;
    boolean isStorageAccessible = false;
    boolean isVideoSelected = false;
    int CURRENT_OPTION = 0;
    int nextSelection = 0;
    HashMap<Integer,Boolean> VIDEO_02 = new HashMap<>();

    //BottomNavigation
    ImageView homeBtn,settingsBtn,FilesBtn;
    RelativeLayout main_home,main_settings,main_files;

    ArrayList<RelativeLayout> bottombarItems = new ArrayList<>();


    String logo = "";
    String PRIVATE_DIR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_view);

        isStorageAccessible = checkPermission();

        Log.e("storage_",""+isStorageAccessible);

        if (FFmpeg.getInstance(this).isSupported()) {
            Log.e("Message","FFMpeg is supported");
        } else {

            Log.e("Message","FFMpeg is not supported");
        }


        PRIVATE_DIR = this.getFilesDir().getAbsolutePath();

        File file = this.getFilesDir();
        String PATH = file.getAbsolutePath()+"/logo.png";
        logo = file.getAbsolutePath()+"/logo.png";
        File logoUri = new File(PATH);

        if (!logoUri.exists())
        {
            MoveFileToInternal();
            Log.e("Logo","Not Exist");
        }else {

            Log.e("Logo","Exists at"+logo);
        }


        videoView = findViewById(R.id.videoView);
        uploadview = findViewById(R.id.uploader);


        heading = findViewById(R.id.heading);
        heading.setText("Video Splitter");

        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        option_3 = findViewById(R.id.option_3);
        videoView = findViewById(R.id.videoView);
        changeBtn = findViewById(R.id.change);
        statusView = findViewById(R.id.status);
        generateButton = findViewById(R.id.generate);
        uploadView = findViewById(R.id.uploader);
        playBtn = findViewById(R.id.playButton);
        descview = findViewById(R.id.desc);

        //BottomNavigation Objects

        homeBtn = findViewById(R.id.main_home);
        settingsBtn = findViewById(R.id.settings);
        FilesBtn = findViewById(R.id.files);
        main_home = findViewById(R.id.main_home_layout);
        main_files = findViewById(R.id.main_files_layout);
        main_settings = findViewById(R.id.main_settings_layout);

        uploadview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideo();
            }
        });


        if (CURRENT_OPTION == 0)
        {
            setDefault();
        }


        if (!isStorageAccessible)
        {
            showPermissionSettings();
        }



        option_1.setOnClickListener(this);
        option_2.setOnClickListener(this);
        option_3.setOnClickListener(this);
        generateButton.setOnClickListener(this);
        uploadView.setOnClickListener(this);
        videoView.setOnClickListener(this);
        changeBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);


        main_home.setOnClickListener(this);
        main_settings.setOnClickListener(this);
        main_files.setOnClickListener(this);


        bottombarItems.add(main_home);
        bottombarItems.add(main_settings);
        bottombarItems.add(main_files);








    }



    public void MoveFileToInternal()
    {
        try {
            Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.logo);
            FileOutputStream outStream;
            File file = new File(PRIVATE_DIR, "logo.png");
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, Bitmap.DENSITY_NONE, outStream);
            outStream.flush();
            outStream.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getVideo()
    {

       Intent intent  = new Intent();
       intent.setType("video/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent,"Select Video"),1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {

            filePath = data.getData();
            selectedFilePath = FilePath.getPath(getApplicationContext(),filePath);
            videoView.setVideoURI(filePath);

            videoView.setVisibility(View.VISIBLE);

            changeBtn.setVisibility(View.VISIBLE);

            uploadView.setVisibility(View.INVISIBLE);

            isVideoSelected = true;

            playBtn.setVisibility(View.VISIBLE);

            Log.e("selectedFilePath",selectedFilePath); //this works for uploading to server
            Log.e("selectedFileURI",filePath.toString()); //this works for displaying video to videoview using  videoView.setVideoURI(filePath);

        }


    }




    public void showPermissionSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }


    public static String[] cropOut(String videoPath,String logoPath)
    {

        String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+System.currentTimeMillis()+"cardmaker.mp4";
        String[] commands = new String[14];

        commands[0] = "-i";
        commands[1] =  videoPath;

        commands[2] = "-i";
        commands[3] = logoPath;

        Log.e("Logo",logoPath);

        //ADD LOGO ON TOP RIGHT
        commands[4] = "-filter_complex";
        commands[5] = "overlay=x=(main_w-overlay_w):y=(main_h-overlay_h)/(main_h-overlay_h)";

        commands[6] = "-ss";
        commands[7] = "00:00:28";
        commands[8] = "-t";
        commands[9] = "00:00:58";
        commands[10] = "-async";
        commands[11] = "1";
        commands[12] = "-y";
        commands[13] =  output;

        return commands;

    }


    public void generateVideo()
    {

       if (isVideoSelected == true)
       {
           VIDEO_01(); // START THE PROCESS
           Log.e("Option",""+CURRENT_OPTION);
       }else {

           Toast.makeText(getApplicationContext(),"Please select a video",Toast.LENGTH_SHORT).show();
       }
    }




    public void VIDEO_01()
    {

        if (CURRENT_OPTION > 1)
        {
            this.nextSelection = 2;
        }



        String[] commands = split(selectedFilePath,logo,"0","30");

        Log.e("COMMANDS",commands.toString());

        startEncoding(commands);
        Log.i("process_started","VIDEO_01");
        VIDEO_02.put(0,true);

    }


    public  void getvideoProperties(String video_url)
    {
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add("-i");
        commandList.add(video_url);
        FFmpeg_engine.getVideoProperties(commandList,getApplicationContext());

        Log.e("getvideoProperties","Retrieving Vide Duration");
    }



    public void VIDEO_02()
    {

        if (CURRENT_OPTION == 2)
        {
            this.nextSelection = 0;
            // FFPMPEG IN will stop in onFinish() if user selected only for 2 video split
            //otherwise it could be 3 which is stated in the next condition
        }else if (CURRENT_OPTION == 3)
        {
            this.nextSelection = 3;
        }

        String[] commands = split(selectedFilePath,logo,"30","30");
        startEncoding(commands);
        Log.i("process_started","VIDEO_02");
        descview.setText("Splitting 2nd Video");

    }


    public void VIDEO_03()
    {
        String[] commands = split(selectedFilePath,logo,"60","30");
        startEncoding(commands);
        descview.setText("Splitting 3rd video");
        Log.i("process_started","VIDEO_03");
        nextSelection = 0;
    }



    public void splitThreeVideos(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_3.setTextColor(getResources().getColor(R.color.white));

    }


    public void splitTwoVideos(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_2.setTextColor(getResources().getColor(R.color.white));
    }

    public void splitOneVideo(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_1.setTextColor(getResources().getColor(R.color.white));
    }


    public void setDefault()
    {
        CURRENT_OPTION = 2;
        clearAllStates();
        option_2.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_2.setTextColor(getResources().getColor(R.color.white));
    }


    public void clearAllStates()
    {
        option_1.setTextColor(getResources().getColor(R.color.black));
        option_2.setTextColor(getResources().getColor(R.color.black));
        option_3.setTextColor(getResources().getColor(R.color.black));


        option_1.setBackground(getResources().getDrawable(R.drawable.button_wide_default));
        option_2.setBackground(getResources().getDrawable(R.drawable.button_wide_default));
        option_3.setBackground(getResources().getDrawable(R.drawable.button_wide_default));
    }



    public void animateButton(View view)
    {

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        view.setAnimation(animation);

    }




    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }

        return false;
    }


    public void grantPermission() throws Exception {

        try {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    public void startEncoding(String[] commannds)
    {
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        ffmpeg.execute(commannds,new ExecuteBinaryResponseHandler(){

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);

                Log.e("Video Success","Success");
                Log.e("Video Success",message);


            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);
                Log.e("Video Processing",message);

                statusView.setText("Splitting ");
                changeBtn.setVisibility(View.INVISIBLE);
                generateButton.setText("Generating...");

            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
                Log.e("Video Failure",message);

                statusView.setText("Failed to split");
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.e("Video Processing","Started");

                generateButton.setClickable(false);
                statusView.setText("Splitting started");
                generateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("Video Processing","Finished");
                Log.i("Who is next"," > "+nextSelection);

                if (nextSelection == 2)
                {
                    VIDEO_02();

                }else if (nextSelection == 3)
                {
                    VIDEO_03();
                }else {

                    nextSelection = 0;
                    statusView.setText("Saved to SDcard");
                    descview.setText(null);
                    Snackbar.make(statusView,"Splitting Completed",Snackbar.LENGTH_LONG)
                            .setAction("View File", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openFileExplorer();
                                }
                            }).show();
                    changeBtn.setVisibility(View.VISIBLE);

                    generateButton.setClickable(true);
                    generateButton.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                    generateButton.setText("GENERATE SPLIT");

                }

            }
        });

    }



    public void openFileExplorer()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivity(intent);
    }



    public static String[] split(String videoPath,String logoPath,String start,String end)
    {

        String DIR_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Video Splitter";
        String output = DIR_OUTPUT+"/"+System.currentTimeMillis()+"cardmaker.mp4";

        Log.e("OUT_DIR",DIR_OUTPUT);
        Log.e("FILE_",output);

        File file = new File(DIR_OUTPUT);

        if (!file.exists())
        {
            file.mkdir();
        }

        String[] commands = new String[14];
        commands[0] = "-i";
        commands[1] =  videoPath;
        commands[2] = "-i";
        commands[3] = logoPath;
        Log.e("Logo",logoPath);
        //ADD LOGO ON TOP RIGHT
        commands[4] = "-filter_complex";
        commands[5] = "overlay=x=(main_w-overlay_w):y=(main_h-overlay_h)/(main_h-overlay_h)";
        commands[6] = "-ss";
        commands[7] = start;
        commands[8] = "-t";
        commands[9] = end;
        commands[10] = "-async";
        commands[11] = "1";
        commands[12] = "-y";
        commands[13] =  output;
        return commands;

    }


    public void VideoPlayBack()
    {
        if (videoView.isPlaying())
        {
            videoView.pause();
            playBtn.setVisibility(View.VISIBLE);

        }else {

            videoView.start();
            playBtn.setVisibility(View.INVISIBLE);
        }
    }



    public void changeVideo()
    {
        videoView.setVideoURI(null);
        videoView.setVisibility(View.GONE);
        uploadView.setVisibility(View.VISIBLE);
        changeBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        isVideoSelected = false;
    }


    public void uploadVideo()
    {
        Log.e("storage_",""+isStorageAccessible);
        isStorageAccessible = true;
        openGallery();
    }



    public void openGallery()
    {
        changeBtn.setVisibility(View.VISIBLE);
        Intent intent  = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),1);
    }





    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.option_1 : splitOneVideo(v);
                break;

            case R.id.option_2 : splitTwoVideos(v);
                break;

            case R.id.option_3 : splitThreeVideos(v);
                break;

            case R.id.generate : generateVideo();
                break;

            case R.id.uploader : uploadVideo();
                break;

            case R.id.videoView : VideoPlayBack();
                break;

            case  R.id.change : changeVideo();
                break;

            case R.id.playButton : playVideo();
            break;

            case R.id.main_home_layout : BottomNavigationBar(R.id.main_home_layout);
            break;

            case R.id.main_settings_layout : BottomNavigationBar(R.id.main_settings_layout);
            break;

            case R.id.main_files_layout : BottomNavigationBar(R.id.main_files_layout);
            break;

        }

    }


    public void BottomNavigationBar(int relativelayouts)
    {

        BottomBarClearActives();
        RelativeLayout relativeLayout = findViewById(relativelayouts);
        ImageView icon = (ImageView) relativeLayout.getChildAt(0);
        TextView icon_text = (TextView) relativeLayout.getChildAt(1);
        icon.setColorFilter(getResources().getColor(R.color.primary_dark));
        icon_text.setTextColor(getResources().getColor(R.color.primary_dark));
        BottomNavigateView(relativelayouts);

    }


    public void BottomBarClearActives()
    {
        for (int i=0; i < bottombarItems.size(); i++)
        {
            RelativeLayout layout = bottombarItems.get(i);
            ImageView icons = (ImageView) layout.getChildAt(0);
            TextView icon_text = (TextView) layout.getChildAt(1);
            icons.setColorFilter(getResources().getColor(R.color.grey));
            icon_text.setTextColor(getResources().getColor(R.color.grey));
        }
    }


    public void BottomNavigateView(int whichlayout)
    {
        switch (whichlayout)
        {
            case R.id.main_settings_layout : showDashboard();
            break;
        }
    }


    public void showDashboard()
    {
        LinearLayout mainscren = findViewById(R.id.main_screen);
        //mainscren.removeAllViews();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_screen,new settings_view());
        transaction.commit();
    }

    public void playVideo()
    {
        if (!videoView.isPlaying())
        {
            videoView.start();
            playBtn.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void nextVidedo(int video_screen) {

    }

}