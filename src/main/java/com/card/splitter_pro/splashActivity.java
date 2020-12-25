package com.card.splitter_pro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import fragments.loadingScreen;
import fragments.showPermissionAlert;

public class splashActivity extends AppCompatActivity {


    private Handler handler;
    boolean isStorageAccessible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStorageAccessible = checkPermission();
        setContentView(R.layout.activity_splash);

        showScreen(new loadingScreen());
        VerifyStorage();
    }


    public void VerifyStorage()
    {
       handler = new Handler();

       handler.postDelayed(new Runnable() {
           @Override
           public void run() {

               if (!isStorageAccessible)
               {
                   showScreen(new showPermissionAlert());
               }else {

                   showMainScreen();
               }
           }
       },2000);
    }


    public void showMainScreen()
    {
        startActivity(new Intent(splashActivity.this,MainActivity.class));
        finish();
    }


    public void showScreen(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainLayout,fragment);
        transaction.commit();
    }



    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isStorageAccessible = checkPermission();
        showScreen(new loadingScreen());
        VerifyStorage();
    }
}