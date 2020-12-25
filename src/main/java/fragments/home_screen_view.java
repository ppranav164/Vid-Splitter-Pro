package fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.card.splitter_pro.FFmpeg_engine;
import com.card.splitter_pro.FilePath;
import com.card.splitter_pro.Notifications;
import com.card.splitter_pro.R;
import com.card.splitter_pro.RunningInteraction;
import com.card.splitter_pro.backgroundTask;
import com.card.splitter_pro.progressText;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_screen_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_screen_view extends Fragment implements View.OnClickListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String FULL_FILE;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ScheduledExecutorService mScheduledExecutorService;


    FFmpeg ffmpeg;
    ImageView playBtn;
    Button option_2,option_3;
    TextView heading,statusView,descview,durationView,cdurationview;
    Button changeBtn;
    EditText option_custom;
    ImageView uploadView,controller_btn;
    LinearLayout controllerView;
    Button generateButton;
    ImageView uploadview;
    VideoView videoView;
    private Uri filePath;
    private String selectedFilePath;
    boolean isStorageAccessible = false;
    boolean isVideoSelected = false;
    int CURRENT_OPTION = 0;
    int MAX_LENGTH = 30;
    int nextSelection = 1;
    boolean isStopped = false;
    double MAX = 3.00;
    double VIDEO_DURATION;
    HashMap<Integer,Boolean> VIDEO_02 = new HashMap<>();
    private SharedPreferences config;


    //video duratio
    int MINUTES;
    int SECONDS;
    String DURATION;

    double SET_MINIMUM = 0.30;
    double GET_VIDEO_DURATION = 0.30;



    //BottomNavigation
    ImageView homeBtn,settingsBtn,FilesBtn,watermarkView;
    RelativeLayout main_home,main_settings,main_files;
    Spinner optionSpinner;

    ArrayList<RelativeLayout> bottombarItems = new ArrayList<>();
    String logo = "";
    String PRIVATE_DIR = "";
    ArrayList<String> extraOption = new ArrayList<>();

    ArrayList<Integer> extraOptionAsInteger = new ArrayList<>();

    RunningInteraction runningInteraction;
    private MenuItem menutabs;
    private MenuItem stopTab;

    //store completed task
    ArrayList<String> completedVideos = new ArrayList<>();
    String DIR_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Video Splitter";
    String DIR_TEMP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/.Temp";

    private Handler handler;
    private Runnable runnable;

    public home_screen_view() {
        // Required empty public constructor
    }

    SharedPreferences.Editor progresseditor;
    SharedPreferences getProgress;

    public home_screen_view( RunningInteraction runningInteraction) {

        this.runningInteraction = runningInteraction;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_screen_view.
     */
    // TODO: Rename and change types and number of parameters
    public static home_screen_view newInstance(String param1, String param2) {
        home_screen_view fragment = new home_screen_view();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        progresseditor = getContext().getSharedPreferences("progress",Context.MODE_PRIVATE).edit();
        getProgress = getContext().getSharedPreferences("progress",Context.MODE_PRIVATE);

        PRIVATE_DIR = getContext().getFilesDir().getAbsolutePath();

        File file = getContext().getFilesDir();
        String PATH = file.getAbsolutePath()+"/logo.png";
        logo = file.getAbsolutePath()+"/logo.png";
        File logoUri = new File(PATH);

        config = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        setHasOptionsMenu(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout._home_screen_view, container, false);


        videoView =  view.findViewById(R.id.videoView);

        option_2 =  view.findViewById(R.id.option_2);
        option_3 =  view.findViewById(R.id.option_3);
        videoView =  view.findViewById(R.id.videoView);
        changeBtn =  view.findViewById(R.id.change);
        statusView =  view.findViewById(R.id.status);
        generateButton =  view.findViewById(R.id.generate);
        controller_btn = view.findViewById(R.id.controller);
        controllerView = view.findViewById(R.id.controllerView);
        uploadView =  view.findViewById(R.id.uploader);
        playBtn =  view.findViewById(R.id.playButton);
        descview =  view.findViewById(R.id.desc);
        durationView = view.findViewById(R.id.duration);
        cdurationview = view.findViewById(R.id.cduration);
        option_custom = view.findViewById(R.id.option_custom);
        optionSpinner = view.findViewById(R.id.optionSpinner);
        watermarkView = view.findViewById(R.id.watermark);

        //BottomNavigation Objects
        homeBtn =  view.findViewById(R.id.main_home);
        settingsBtn =  view.findViewById(R.id.settings);
        FilesBtn =  view.findViewById(R.id.files);
        main_home =  view.findViewById(R.id.main_home_layout);
        main_files =  view.findViewById(R.id.main_files_layout);
        main_settings =  view.findViewById(R.id.main_settings_layout);




        option_2.setOnClickListener(this);
        option_3.setOnClickListener(this);
        generateButton.setOnClickListener(this);
        uploadView.setOnClickListener(this);
        videoView.setOnClickListener(this);
        changeBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        controller_btn.setOnClickListener(this);


        option_custom.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    setOption_custom();
                }

                return true;
            }
        });


        option_custom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > 0)
                {
                    option_custom.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    int value = Integer.parseInt(s.toString());

                    if (value > 10 || value == 0)
                    {
                        option_custom.setText("4");
                        option_custom.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }
                }else {

                    option_custom.setImeOptions(EditorInfo.IME_ACTION_NONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideo();
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                controller_btn.setBackgroundResource(R.drawable.ic_start);
            }
        });


        if (CURRENT_OPTION == 0)
        {
            setDefault();
        }

        showExtraOption();


        return  view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.dashboard,menu);

        final MenuItem menuItem = menu.findItem(R.id.action_settings);

        final MenuItem stopMenu = menu.findItem(R.id.stop);

        menutabs = menuItem;
        stopTab = stopMenu;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_settings : generateVideo();
            break;

            case R.id.stop : forceStop();
            break;
        }

        return super.onOptionsItemSelected(item);
    }



    public void showExtraOption()
    {
        extraOption.add("Add More");
        extraOptionAsInteger.add(0);

        for (int i=4; i < 11; i++)
        {
            extraOption.add(i+" Videos");
            extraOptionAsInteger.add(i);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.extra_option_layout,extraOption){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setGravity(Gravity.CENTER);
                return view;
            }
        };

        optionSpinner.setAdapter(arrayAdapter);

        optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int option = extraOptionAsInteger.get(position);

                if (position != 0)
                {
                    clearAllStates();
                    view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                    CURRENT_OPTION = option;
                    getSplitingDetails();
                }

                if (AllowedToSplit() == false && isVideoSelected == true)
                {
                    Toast.makeText(getContext(),"Video should be longer than "+SET_MINIMUM+" Duration",Toast.LENGTH_SHORT).show();
                    changeVideo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && data != null) {

            filePath = data.getData();
            selectedFilePath = FilePath.getPath(getContext(),filePath);

            showVideo();

            File file = new File(selectedFilePath);
            getVideoDuration(file);

            Log.e("selectedFilePath",selectedFilePath); //this works for uploading to server
            Log.e("selectedFileURI",filePath.toString()); //this works for displaying video to videoview using  videoView.setVideoURI(filePath);

            durationView.setText(DURATION);

            getSplitingDetails();

            if (!AllowedToSplit())
            {
                Toast.makeText(getContext(),"Video should be longer than "+SET_MINIMUM+" Duration",Toast.LENGTH_SHORT).show();
                changeVideo();
            }
        }

    }


    public void showVideo()
    {
        videoView.setVideoURI(filePath);
        videoView.setVisibility(View.VISIBLE);
        uploadView.setVisibility(View.INVISIBLE);
        isVideoSelected = true;
        changeBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        controllerView.setVisibility(View.VISIBLE);
    }


    public void getSplitingDetails()
    {
        Log.e(" Current Selected Video Option ",""+CURRENT_OPTION);
        Log.e(" Minimum Allowed Length ",""+ calCulateObjects());
        Log.e(" Selected Video Length ",""+GET_VIDEO_DURATION);
        SET_MINIMUM = calCulateObjects();
    }


    public static Uri getUrl(int res){

        return Uri.parse("android.resource://com.example.project/" + res);
    }


    public void showPermissionSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package",getContext().getPackageName(),null);
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
        commands[5] = "overlay=x=(main_w-overlay_w+2):y=(main_h-overlay_h)/(main_h-overlay_h+2)";

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

        Log.e("Option",""+CURRENT_OPTION);

        if (isVideoSelected == true)
        {
            if (CURRENT_OPTION != 0)
            {
                VIDEO_01(); // START THE PROCESS
            }else {

                Toast.makeText(getContext(),"Please select any option",Toast.LENGTH_SHORT).show();
            }

        }else {

            Toast.makeText(getContext(),"Please import a video",Toast.LENGTH_SHORT).show();
        }
    }


    public boolean AllowedToSplit()
    {
        if (GET_VIDEO_DURATION > SET_MINIMUM)
        {
            return true;
        }
        return false;
    }


    public double calCulateObjects()
    {
        float num1 = MAX_LENGTH;
        float num2 = CURRENT_OPTION;
        float minute = 60;
        double VIDEO_LENGTH = num2*num1/minute;
        return VIDEO_LENGTH;
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
        FFmpeg_engine.getVideoProperties(commandList,getContext());

        Log.e("getvideoProperties","Retrieving Vide Duration");
    }



    public void VIDEO_02()
    {

        if (CURRENT_OPTION == 2)
        {
            this.nextSelection = 0;
            // FFPMPEG IN will stop in onFinish() if user selected only for 2 video split
            //otherwise it could be 3 which is stated in the next condition
        }else if (CURRENT_OPTION > 2)
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

        if (CURRENT_OPTION == 3)
        {
            this.nextSelection = 0;
        }else if (CURRENT_OPTION > 3)
        {
            this.nextSelection = 4;
        }

        String[] commands = split(selectedFilePath,logo,"60","30");
        startEncoding(commands);
        descview.setText("Splitting 3rd video");
        Log.i("process_started","VIDEO_03");

    }

    public void VIDEO_04()
    {

        if (CURRENT_OPTION == 4)
        {
            this.nextSelection = 0;

        }else if (CURRENT_OPTION > 4)
        {
            this.nextSelection = 5;
        }

        String[] commands = split(selectedFilePath,logo,"90","30");
        startEncoding(commands);
        descview.setText("Splitting 4th video");
        Log.i("process_started","VIDEO_04");

    }


    public void VIDEO_05()
    {

        if (CURRENT_OPTION == 5)
        {
            this.nextSelection = 0;

        }else if (CURRENT_OPTION > 5 )
        {
            this.nextSelection = 6;
        }

        String[] commands = split(selectedFilePath,logo,"120","30");
        startEncoding(commands);
        descview.setText("Splitting 5th video");
        Log.i("process_started","VIDEO_05");

    }

    public void VIDEO_06()
    {

        if (CURRENT_OPTION == 6)
        {
            this.nextSelection = 0;

        }else if (CURRENT_OPTION > 6 )
        {
            this.nextSelection = 7;
        }

        String[] commands = split(selectedFilePath,logo,"150","30");
        startEncoding(commands);
        descview.setText("Splitting 6th video");
        Log.i("process_started","VIDEO_06");

    }


    public void VIDEO_07()
    {

        if (CURRENT_OPTION == 7)
        {
            this.nextSelection = 0;

        }else if (CURRENT_OPTION > 7)
        {
            this.nextSelection = 8;
        }

        String[] commands = split(selectedFilePath,logo,"180","30");
        startEncoding(commands);
        descview.setText("Splitting 7th video");
        Log.i("process_started","VIDEO_07");

    }


    public void VIDEO_08()
    {
        if (CURRENT_OPTION == 8)
        {
            this.nextSelection = 0;

        }else if (CURRENT_OPTION > 8)
        {
            this.nextSelection = 9;
        }

        String[] commands = split(selectedFilePath,logo,"210","30");
        startEncoding(commands);
        descview.setText("Splitting 8th video");
        Log.i("process_started","VIDEO_08");

    }


    public void VIDEO_09()
    {

        if (CURRENT_OPTION == 9)
        {
            this.nextSelection = 0;
        }else if (CURRENT_OPTION > 9)
        {
            this.nextSelection = 10;
        }
        String[] commands = split(selectedFilePath,logo,"240","30");
        startEncoding(commands);
        descview.setText("Splitting 9th video");
        Log.i("process_started","VIDEO_09");

    }


    public void VIDEO_10()
    {
        this.nextSelection = 0;
        String[] commands = split(selectedFilePath,logo,"270","30");
        startEncoding(commands);
        descview.setText("Splitting 10th video");
        Log.i("process_started","VIDEO_10");
    }


    public void showCurrentDuration()
    {
        new backgroundTask(getContext(), new progressText() {
            @Override
            public void progressAt(String data) {
                Log.i("progressAt",data);
                cdurationview.setText(data);
                setCurrentDuration();

            }
        },videoView).execute();
    }

    public void setCurrentDuration()
    {
        double cdur = Double.parseDouble(cdurationview.getText().toString());

        Log.i("setCurrentDuration",""+cdur);

        if (cdur == GET_VIDEO_DURATION)
        {
            invalidateVideoPlay();
        }
    }


    public void invalidateVideoPlay()
    {
        Log.i("setCurrentDuration","STOP");
        progresseditor.clear();
        progresseditor.putBoolean("playing",false);
        progresseditor.commit();
        progresseditor.apply();
    }

    public double formatDouble(String times)
    {
        String time = times.replace(":",".");
        double value = Double.parseDouble(time);
        return value;
    }


    public void showCurrentDurationss()
    {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("Video Handler >>","Handler running");
                int count = videoView.getCurrentPosition();
                if (videoView.isPlaying())
                {
                    Log.i("Video Playing >>",""+count);
                    cdurationview.setText(testtime(count));
                    handler.postDelayed(runnable,0);
                }
            }
        };
        handler.postDelayed(runnable,0);
    }


    public void splitThreeVideos(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        optionSpinner.setSelection(0);
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_3.setTextColor(getResources().getColor(R.color.white));

    }


    public void splitTwoVideos(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        optionSpinner.setSelection(0);
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
        option_2.setTextColor(getResources().getColor(R.color.white));
    }

    public void splitOneVideo(View view)
    {
        int tag = Integer.parseInt(view.getTag().toString());
        CURRENT_OPTION = tag;
        clearAllStates();
        view.setBackground(getResources().getDrawable(R.drawable.button_wide_active));
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

        option_2.setTextColor(getResources().getColor(R.color.black));
        option_3.setTextColor(getResources().getColor(R.color.black));


        option_2.setBackground(getResources().getDrawable(R.drawable.button_wide_default));
        option_3.setBackground(getResources().getDrawable(R.drawable.button_wide_default));
    }


    public void setOption_custom()
    {
        if (option_custom.getText().toString().length() > 0)
        {
            CURRENT_OPTION = Integer.parseInt(option_custom.getText().toString());;
            clearAllStates();
        }else {

            option_custom.setText("4");
            option_custom.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }
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
            int result = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }

        return false;
    }


    public void grantPermission() throws Exception {

        try {
            ActivityCompat.requestPermissions( (Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    public void startEncoding(String[] commannds)
    {
         ffmpeg = FFmpeg.getInstance(getContext());
         ffmpeg.execute(commannds,new ExecuteBinaryResponseHandler(){

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);

                Log.e("Video Success","Success");
                Log.e("Video Success",message);

                runningInteraction.isRunning(false);
                enableInteraction();
            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);
                Log.e("Video Processing",message);

                statusView.setText("Splitting ");
                changeBtn.setVisibility(View.INVISIBLE);
                generateButton.setText("Generating...");
                runningInteraction.isRunning(true);
                disableInteraction();

                if (isStopped)
                {
                    forceStop();
                    reset();
                }

            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
                Log.e("Video Failure",message);

                statusView.setText("Failed to split");
                runningInteraction.isRunning(false);
                enableInteraction();
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.e("Video Processing","Started");

                generateButton.setClickable(false);
                statusView.setText("Splitting started");
                generateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
                runningInteraction.isRunning(true);
                disableInteraction();
                completedVideos.add(FULL_FILE);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                Log.e("Video Processing","Finished");
                Log.i("Who is next"," > "+nextSelection);

                if (!isStopped)
                {
                    splitQues();

                }else {

                    forceStop();
                }

            }
        });

    }




    public void removeUnnecessaryVideos(){

        Log.i("Menu","deleting_stopped_video");
        for (int i=0; i < completedVideos.size(); i++)
        {
            String video = completedVideos.get(i);
            File filename = new File(video);
            onDeletionConfirm(filename.getName());
            Log.e("removed",filename.getName());
        }
    }


    public void onDeletionConfirm(String video)
    {
        File file = new File(DIR_OUTPUT+"/"+video);
        File tempFile = new File(DIR_TEMP+"/"+video+".png");
        tempFile.delete();
        file.delete();
        reset();
    }


    public void reset()
    {
        enableInteraction();
        statusView.setText(null);
        changeBtn.setVisibility(View.VISIBLE);
        generateButton.setText(null);
        runningInteraction.isRunning(false);
    }


    public void splitQues()
    {

        getThumblineImage(FULL_FILE);
        if (nextSelection == 2)
        {
            VIDEO_02();

        }else if (nextSelection == 3)
        {
            VIDEO_03();
        }else if (nextSelection == 4)
        {
            VIDEO_04();
        }else if (nextSelection == 5)
        {
            VIDEO_05();
        }else if (nextSelection == 6)
        {
            VIDEO_06();
        }else if (nextSelection == 7)
        {
            VIDEO_07();
        }else if (nextSelection == 8)
        {
            VIDEO_08();
        }else if (nextSelection == 9)
        {
            VIDEO_09();
        }else if (nextSelection == 10)
        {
            VIDEO_10();
        }else {
            processComplete();
        }
    }



    public void disableInteraction()
    {
        videoView.setClickable(false);
        uploadView.setClickable(false);
        menutabs.setIcon(R.drawable.ic_split_disabled);
        menutabs.setEnabled(false);
        stopTab.setVisible(true);
    }

    public void enableInteraction()
    {
        videoView.setClickable(true);
        uploadView.setClickable(true);
        menutabs.setIcon(R.drawable.ic_split);
        menutabs.setEnabled(true);
        stopTab.setVisible(false);
    }


    public void forceStop()
    {
        Log.e("Abandon Message","Stopping split task");
        isStopped = true;
        nextSelection = 0;
        removeUnnecessaryVideos();
    }


    public void processComplete()
    {
        nextSelection = 0;
        statusView.setText("Saved to SDcard");
        descview.setText(null);
        changeBtn.setVisibility(View.VISIBLE);
        generateButton.setClickable(true);
        generateButton.setBackgroundColor(getResources().getColor(R.color.indigo));
        generateButton.setText("GENERATE SPLIT");
        runningInteraction.isRunning(false);
        enableInteraction();

        if (config.getBoolean("alert_me", false))
        {
            Notifications.showNotification(getContext(),"Task Completed","Saved to SDCARD",R.drawable.logo);
        }

    }



    public void openFileExplorer()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivity(intent);
    }



    public  String[] split(String videoPath,String logoPath,String start,String end)
    {
        String DIR_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Video Splitter";
        String output = DIR_OUTPUT+"/"+System.currentTimeMillis()+"video_splitter.mp4";

        FULL_FILE = output;

        Log.e("Message","Current out file set to "+FULL_FILE);

        Log.e("OUT_DIR",DIR_OUTPUT);
        Log.e("FILE_",output);

        File file = new File(DIR_OUTPUT);

        if (!file.exists())
        {
            file.mkdir();
        }

        String[] commands = new String[10];
        commands[0] = "-i";
        commands[1] =  videoPath;
        commands[2] = "-ss";
        commands[3] = start;
        commands[4] = "-t";
        commands[5] = end;
        commands[6] = "-async";
        commands[7] = "1";
        commands[8] = "-y";
        commands[9] =  output;
        return commands;

    }


    public void VideoPlayBack()
    {
        if (videoView.isPlaying())
        {
            videoView.pause();
            playBtn.setVisibility(View.VISIBLE);
            controller_btn.setBackgroundResource(R.drawable.ic_start);
            stopProgress();
        }else {

            videoView.start();
            playBtn.setVisibility(View.INVISIBLE);
            controller_btn.setBackgroundResource(R.drawable.ic_stop_playback);
            startProgress();
        }
    }



    public void changeVideo()
    {
        stopProgress();
        videoView.pause();
        videoView.setVideoURI(null);
        videoView.setVisibility(View.GONE);
        watermarkView.setVisibility(View.INVISIBLE);
        uploadView.setVisibility(View.VISIBLE);
        changeBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        controller_btn.setBackgroundResource(R.drawable.ic_start);
        controllerView.setVisibility(View.INVISIBLE);
        isVideoSelected = false;
    }


    public void stopProgress()
    {
        progresseditor.clear();
        progresseditor.putBoolean("playing",false);
        progresseditor.commit();
        progresseditor.apply();
    }

    public void startProgress()
    {
        progresseditor.clear();
        progresseditor.putBoolean("playing",true);
        progresseditor.commit();
        progresseditor.apply();
        showCurrentDuration();
    }


    public void uploadVideo()
    {
        Log.e("storage_",""+isStorageAccessible);
        isStorageAccessible = true;
        if (isVideoSelected == false)
        {
            openGallery();
        }
    }

    public void openGallery()
    {
        Intent intent  = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),1);
    }

    public void getVideoDuration(File uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getContext(), Uri.fromFile(uri));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        double timeInMillisec = getMinutes(Long.parseLong(time));
        retriever.release();
        Log.i("DurationRaw",">>"+time);

        testtime(Long.parseLong(time));
    }


    public double getMinutes(double millisec)
    {
        double res = millisec/60000;
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.parseDouble(df.format(res));
    }


    public boolean validateDuration()
    {
        int MIN_SEC = 30;

        return false;
    }


    public String testtime(double duration){

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((long) duration);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds((long) duration);
        int minsec = (int) (seconds-MinutestoSecs(minutes));
        String resu = minutes+"."+minsec;

        MINUTES = minutes;
        SECONDS = seconds;
        DURATION = resu;

        GET_VIDEO_DURATION = Double.parseDouble(DURATION);

        Log.i("minutes",""+minutes);
        Log.i("seconds",""+seconds);
        Log.i("minsec",""+minsec);
        Log.i("Dividing","Dividing");
        Log.i("Result",""+resu);

        return resu;
    }


    public double toMinutes(double duration)
    {
        return duration/60000;
    }

    public double toSeconds(double duration)
    {
        return duration/1000;
    }

    public double MinutestoSecs(double minutes) //add full duration
    {
        return minutes*60;
    }



    public  Bitmap getThumblineImage(String videoPath) {

        Log.e("Video",videoPath);
        Log.e("Video","Converting to Bitmap");
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        StoreTemps(bitmap);
        return bitmap;
    }



    public void StoreTemps(Bitmap bitmap)
    {

        File exportDir = new File(DIR_TEMP);

        if (!exportDir.exists())
        {
            exportDir.mkdir();
        }

        try {
            Bitmap bms = Bitmap.createBitmap(bitmap);
            FileOutputStream outStream;
            File getFileName = new File(FULL_FILE);
            File file = new File(DIR_TEMP,getFileName.getName()+".png");
            outStream = new FileOutputStream(file);
            bms.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

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

            case R.id.playButton : VideoPlayBack();
                break;

            case R.id.controller : VideoPlayBack();
                break;

            case R.id.option_custom : setOption_custom();
            break;

        }

    }



}