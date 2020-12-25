package com.card.splitter_pro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

import fragments.FileManager;
import fragments.InfoScreen;
import fragments.home_screen_view;
import fragments.settings_view;
import nl.bravobit.ffmpeg.FFmpeg;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , RunningInteraction  {



    boolean isStorageAccessible = false;
    //BottomNavigation
    ImageView homeBtn,settingsBtn,FilesBtn;
    RelativeLayout main_home,main_settings,main_files,main_about;
    ArrayList<RelativeLayout> bottombarItems = new ArrayList<>();
    String logo = "";
    String PRIVATE_DIR = "";
    int SCREEN = R.id.main_home_layout; //Default Home button
    LinearLayout bottom_bar_layout;
    home_screen_view homeFragment;
    private SharedPreferences config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showDashboard();

        isStorageAccessible = checkPermission();

        Log.e("storage_",""+isStorageAccessible);

        if (FFmpeg.getInstance(this).isSupported()) {
            Log.e("Message","FFMpeg is supported");
        } else {

            Log.e("Message","FFMpeg is not supported");
        }

        String libraryPath = getApplicationContext().getApplicationInfo().dataDir;

        File files = new File(libraryPath);
        File[] contents = files.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Log.i("LIB PATH",dir.getName());
                return false;
            }
        });

        //Log.i("LIB PATH",libraryPath);

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


        config = getSharedPreferences("config", Context.MODE_PRIVATE);

        isNightMode();

        //BottomNavigation Objects
        homeBtn = findViewById(R.id.main_home);
        settingsBtn = findViewById(R.id.settings);
        FilesBtn = findViewById(R.id.files);
        main_home = findViewById(R.id.main_home_layout);
        main_files = findViewById(R.id.main_files_layout);
        main_settings = findViewById(R.id.main_settings_layout);
        main_about = findViewById(R.id.main_about_layout);
        bottom_bar_layout = findViewById(R.id.bottombar);


        main_home.setOnClickListener(this);
        main_settings.setOnClickListener(this);
        main_files.setOnClickListener(this);
        main_about.setOnClickListener(this);


        bottombarItems.add(main_home);
        bottombarItems.add(main_settings);
        bottombarItems.add(main_files);
        bottombarItems.add(main_about);


        if (!isStorageAccessible)
        {
            showPermissionSettings();
        }

        setActiveDefaultBottomNav();


        getHardwareSpec();

    }



    public void showPermissionSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }


    public void MoveFileToInternal()
    {
        try {
            Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.logo);
            Bitmap bms = Bitmap.createScaledBitmap(bm,140,42,true);
            FileOutputStream outStream;
            File file = new File(PRIVATE_DIR, "logo.png");
            outStream = new FileOutputStream(file);
            bms.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void showDashboard()
    {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainview,new home_screen_view(this));
        transaction.commit();
        SCREEN = R.id.main_home_layout;
    }


    public void showSettings()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainview,new settings_view());
        transaction.commit();
        SCREEN = R.id.main_settings_layout;
    }


    public void showFileManager()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainview,new FileManager());
        transaction.commit();
        SCREEN = R.id.main_files_layout;
    }


    public void showInfoScreen()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainview,new InfoScreen());
        transaction.commit();
        SCREEN = R.id.main_about_layout;
    }



    public void BottomNavigationBar(int relativelayouts)
    {

       if (SCREEN != relativelayouts)
       {
           BottomBarClearActives();
           RelativeLayout relativeLayout = findViewById(relativelayouts);
           ImageView icon = (ImageView) relativeLayout.getChildAt(0);
           TextView icon_text = (TextView) relativeLayout.getChildAt(1);
           icon.setColorFilter(getResources().getColor(R.color.indigo));
           icon_text.setTextColor(getResources().getColor(R.color.indigo));
           BottomNavigateView(relativelayouts);
       }

    }


    public void setActiveDefaultBottomNav()
    {
        BottomBarClearActives();
        RelativeLayout relativeLayout = findViewById(SCREEN);
        ImageView icon = (ImageView) relativeLayout.getChildAt(0);
        TextView icon_text = (TextView) relativeLayout.getChildAt(1);
        icon.setColorFilter(getResources().getColor(R.color.indigo));
        icon_text.setTextColor(getResources().getColor(R.color.indigo));
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
            case R.id.main_home_layout : showDashboard();
                break;

            case R.id.main_settings_layout : showSettings();
            break;

            case R.id.main_files_layout : showFileManager();
            break;

            case R.id.main_about_layout : showInfoScreen();
            break;
        }
    }


    public void isNightMode()
    {
        if (config.getBoolean("night_mode",false))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.main_home_layout : BottomNavigationBar(R.id.main_home_layout);
                break;

            case R.id.main_settings_layout : BottomNavigationBar(R.id.main_settings_layout);
                break;

            case R.id.main_files_layout : BottomNavigationBar(R.id.main_files_layout);
                break;

            case R.id.main_about_layout : BottomNavigationBar(R.id.main_about_layout);
                break;
        }
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


    @Override
    public void isRunning(boolean running) {

        if (running)
        {
            disableInteraction();
        }else {

            enableInteraction();
        }
    }


    //prevent user clicking on bottom bar icons during splitting process
    public void disableInteraction()
    {
       for (int i=0; i < bottombarItems.size(); i++)
       {
           bottombarItems.get(i).setClickable(false);
       }
    }


    //enable user clicking on bottom bar icons after splitting process is done
    public void enableInteraction()
    {
        for (int i=0; i < bottombarItems.size(); i++)
        {
            bottombarItems.get(i).setClickable(true);
        }
    }


    public void getHardwareSpec()
    {
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG","brand: " + Build.BRAND);
    }



}